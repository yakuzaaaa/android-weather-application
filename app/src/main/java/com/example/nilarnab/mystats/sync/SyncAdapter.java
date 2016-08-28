package com.example.nilarnab.mystats.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
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

/**
 * Created by nilarnab on 27/8/16 and it is made of each and everyone of you people to see, judge and advice :-).
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final long SYNC_INTERVAL_SECONDS = 5 * 3600;// * 3600 * 3;
    private static final long FLEX_INTERVAL_SECONDS = 3 * 3600;//* 3600 * 2;

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[] {
            WeatherContract.WeatherTable.COLUMN_WEATHER_CONDITION_ID,
            WeatherContract.WeatherTable.COLUMN_CONDITION,
    };
//    private static final String SELECTION = WeatherContract.WeatherTable.

    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;
    private static final int INDEX_SHORT_DESC = 3;

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

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        DataFetchUtil.fetchDataNow();
        String locationSetting = Utility.getPreferredLocation();
        String contentText = "Just Updated WEather, Cahnge me to be more specific";
//        Uri weatherUri =
//                WeatherContract.WeatherTable.buildWeatherWithLocationAndStartDateUri(locationSetting, System.currentTimeMillis());
//        Cursor cursor = getContext().getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);
//
//        if (cursor.moveToFirst()) {
//            int weatherId = cursor.getInt(INDEX_WEATHER_ID);
//            double high = cursor.getDouble(INDEX_MAX_TEMP);
//            double low = cursor.getDouble(INDEX_MIN_TEMP);
//            String desc = cursor.getString(INDEX_SHORT_DESC);
//
//            int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
//            Resources resources = context.getResources();
//            Bitmap largeIcon = BitmapFactory.decodeResource(resources,
//                    Utility.getArtResourceForWeatherCondition(weatherId));
//            String title = context.getString(R.string.app_name);

        Utility.showWeatherNotification(contentText, null);
    }

    private static void performImmediateSync() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(),
                App.getAppContext().getString(R.string.weather_content_authority), bundle);
    }

    private static void startPeriodicSync() {
        Account account = getSyncAccount();
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

        return account;
    }

    private static void onAccountCreated(Account account) {
        startPeriodicSync();

        ContentResolver
                .setSyncAutomatically(
                        account,
                        App.getAppContext().getString(R.string.weather_content_authority),
                        true
                );

        performImmediateSync();
    }

    public static void initWeatherSync() {
        getSyncAccount();
    }
}
