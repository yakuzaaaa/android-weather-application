package com.example.nilarnab.mystats;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.nilarnab.mystats.database.WeatherContract;
import com.example.nilarnab.mystats.database.WeatherProvider;

import java.util.Date;

/**
 * Created by nilarnab on 15/8/16.
 */
public class TestContentProvider extends AndroidTestCase {
    public void testInserts() {
        mContext.getContentResolver();
    }

    public void testRegistery() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), WeatherProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals(providerInfo.authority, WeatherContract.CONTENT_AUTHORITY, providerInfo.authority);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(WeatherContract.WeatherTable.buildWeatherWithLocationUri("712235"));
        assertEquals(type, type, WeatherContract.WeatherTable.CONTENT_TYPE);

        type = mContext.getContentResolver().getType(WeatherContract.WeatherTable.buildWeatherWithLocationAndDateUri("712235", 1223123L));
        assertEquals(type, type, WeatherContract.WeatherTable.CONTENT_ITEM_TYPE);

        type = mContext.getContentResolver().getType(WeatherContract.WeatherTable.buildWeatherWithLocationAndStartDateUri("712235", new Date().getTime()));
        assertEquals(type, type, WeatherContract.WeatherTable.CONTENT_TYPE);
    }

    public void testInsert() {
        ContentValues cv = new ContentValues();
        cv.put(WeatherContract.LocationTable.COLUMN_CITY_NAME, "Konnagar");
        cv.put(WeatherContract.LocationTable.COLUMN_LOCATION_SETTINGS, "712235");
        cv.put(WeatherContract.LocationTable.COLUMN_COORD_LNG, 22.34);
        cv.put(WeatherContract.LocationTable.COLUMN_COORD_LAT, 22.3);

        Uri row = mContext.getContentResolver().insert(WeatherContract.LocationTable.CONTENT_URI, cv);

        assertEquals(row.toString(), row, WeatherContract.LocationTable.buildLocationUri(2));
    }

    public void testQuery() {
        Cursor c = mContext.getContentResolver().query(WeatherContract.LocationTable.CONTENT_URI, null, null, null, null);

        assertNotNull(c);
        assertEquals("" + c.getCount(), c.getCount(), 2);

        c.close();
    }
}
