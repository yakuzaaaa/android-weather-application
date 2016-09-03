package com.example.nilarnab.mystats.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.nilarnab.mystats.App;
import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.database.WeatherContract;
import com.example.nilarnab.mystats.services.DataFetchUtil;
import com.example.nilarnab.mystats.utility.Utility;
import com.example.nilarnab.mystats.utility.WeatherUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by nilarnab on 27/8/16 and it is made of each and everyone of you people to see, judge and advice :-).
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final long SYNC_INTERVAL_SECONDS = 5 * 3600 ;// * 3600 * 3;
    private static final long FLEX_INTERVAL_SECONDS = 3 * 3600;//* 3600 * 2;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherTable.TABLE_NAME + "." + WeatherContract.WeatherTable._ID,
            WeatherContract.WeatherTable.COLUMN_CONDITION,
            WeatherContract.WeatherTable.COLUMN_WEATHER_CONDITION_ID,
            WeatherContract.WeatherTable.COLUMN_DATE,
    };
//    private static final String SELECTION = WeatherContract.WeatherTable.

    // these indices must match the projection
    private static final int INDEX_ID = 0;
    private static final int INDEX_SHORT_DESC = 1;
    private static final int INDEX_WEATHER_CONDITION_ID = 2;
    private static final int INDEX_DATE = 3;

    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    private static void performImmediateSync(Account account) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account,
                App.getAppContext().getString(R.string.weather_content_authority), bundle);
    }

    private static void startPeriodicSync(Account account) {
        String authority = App.getAppContext().getString(R.string.weather_content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(SYNC_INTERVAL_SECONDS, FLEX_INTERVAL_SECONDS).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), SYNC_INTERVAL_SECONDS);
        }
        performImmediateSync(account);
    }

    private static Account getSyncAccount() {
        AccountManager accountManager =
                (AccountManager) App.getAppContext().getSystemService(Context.ACCOUNT_SERVICE);

        Account account = new Account(
                App.getAppContext().getString(R.string.app_name),
                App.getAppContext().getString(R.string.sync_account_type)
        );

        if (accountManager.getPassword(account) == null) {
            //this is a new account
            if(!accountManager.addAccountExplicitly(account,App.getAppContext().getString(R.string.app_name),null)) {
                //some error occurred
                return null;
            }
            onAccountCreated(account);
        }

        startPeriodicSync(account);

        return account;
    }

    private static void onAccountCreated(Account account) {
        startPeriodicSync(account);

        ContentResolver
                .setSyncAutomatically(
                        account,
                        App.getAppContext().getString(R.string.weather_content_authority),
                        true
                );
    }

    public static void initWeatherSync() {
        getSyncAccount();
    }

    public static void doPerformDataSync(Context context) {
        DataFetchUtil.fetchDataNow();
        String locationSetting = Utility.getUserLocation();

        Calendar tempUtilCalendar = new GregorianCalendar();
        tempUtilCalendar.add(Calendar.DAY_OF_MONTH,1);
        //this gives time for tomorrow
        String selection = WeatherContract.WeatherTable.COLUMN_DATE + " < "+tempUtilCalendar.getTimeInMillis();
        Uri weatherUri = WeatherContract.WeatherTable.buildWeatherWithLocationAndDateUri(locationSetting,tempUtilCalendar.getTimeInMillis());
        Cursor cursor = context.getContentResolver().query(weatherUri, FORECAST_COLUMNS, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int weatherId = cursor.getInt(INDEX_WEATHER_CONDITION_ID);
            String desc = cursor.getString(INDEX_SHORT_DESC);

            Resources resources = context.getResources();
            Bitmap largeIcon = BitmapFactory.decodeResource(resources,
                    WeatherUtils.getArtResourceForWeatherCondition(weatherId));
            String title = context.getString(R.string.app_name);
            Utility.showWeatherNotification(
                    desc,
                    largeIcon,
                    WeatherContract.WeatherTable.buildWeatherWithLocationAndStartDateUri(locationSetting,cursor.getInt(INDEX_DATE)),
                    title);
            cursor.close();
        }

        //Now Lets remove data which are like two weeks or so old
        tempUtilCalendar = new GregorianCalendar();
        tempUtilCalendar.setTime(new Date());
        //time in millis for two days
        long twoDays = 1000 * 3600 * 48;

        context.getContentResolver().delete(
                WeatherContract.WeatherTable.CONTENT_URI,
                WeatherContract.WeatherTable.COLUMN_DATE + " < ?",
                new String[]{String.valueOf(tempUtilCalendar.getTimeInMillis() - twoDays)}
        );
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        doPerformDataSync(getContext());
    }
}
