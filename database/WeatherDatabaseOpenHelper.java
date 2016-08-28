package com.example.nilarnab.mystats.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nilarnab on 14/8/16.
 */
public class WeatherDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int WEATHER_DB_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";

    public WeatherDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, WEATHER_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WeatherContract.LocationTable.CREATE_QUERY);
        db.execSQL(WeatherContract.WeatherTable.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherTable.TABLE_NAME);

        onCreate(db);
    }
}
