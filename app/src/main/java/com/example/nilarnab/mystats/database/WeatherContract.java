package com.example.nilarnab.mystats.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nilarnab on 14/8/16.
 */
public class WeatherContract {
    public static final String CONTENT_AUTHORITY = "com.yakuza.content.weather";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    public static class WeatherTable implements BaseColumns {
        //content://com.yakuza.content.weather/weather
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather_table";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MIN_TEMP = "min_temp";
        public static final String COLUMN_MAX_TEMP = "max_temp";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_DEGREES = "degrees";
        public static final String COLUMN_CONDITION = "condition";
        public static final String COLUMN_WEATHER_CONDITION_ID = "weather_id";

        public static final String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WEATHER_CONDITION_ID + " INTEGER NOT NULL, " +
                COLUMN_DATE + " INTEGER NOT NULL, " +
                COLUMN_MAX_TEMP + " REAL NOT NULL, " +
                COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                COLUMN_HUMIDITY + " REAL NOT NULL, " +
                COLUMN_PRESSURE + " REAL NOT NULL, " +
                COLUMN_DEGREES + " REAL NOT NULL, " +
                COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                COLUMN_CONDITION + " TEXT NOT NULL, " +
                COLUMN_LOCATION_ID + " INTEGER, " +

                "FOREIGN KEY (" + COLUMN_LOCATION_ID + ") REFERENCES " + LocationTable.TABLE_NAME + " (" + LocationTable._ID + "), " +
                "UNIQUE ( " + COLUMN_LOCATION_ID + ", " + COLUMN_DATE + " ) ON CONFLICT REPLACE );";


        public static Uri buildWeatherWithLocationUri(String locationSettings) {
            return CONTENT_URI.buildUpon().appendPath(locationSettings).build();
        }


        public static Uri buildWeatherWithLocationAndDateUri(String locationSettings, long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(locationSettings)
                    .appendPath(String.valueOf(date))
                    .build();
        }

        public static Uri buildWeatherWithLocationAndStartDateUri(String locationSettings, long startDate) {
            return CONTENT_URI.buildUpon()
                    .appendPath(locationSettings)
                    .appendQueryParameter(COLUMN_DATE, String.valueOf(startDate))
                    .build();
        }

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getStartDateFromUri(Uri uri) {
            String date = uri.getQueryParameter(COLUMN_DATE);
            if (date != null && date.length() > 0) {
                return Long.parseLong(date);
            } else {
                return 0;
            }
        }

        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromPath(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    public static class LocationTable implements BaseColumns {
        //content://com.yakuza.content.weather/weather
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTET_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location_table";
        public static final String COLUMN_LOCATION_SETTINGS = "location_settings";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LNG = "coor_lng";

        public static final String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOCATION_SETTINGS + " TEXT UNIQUE NOT NULL, " +
                COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                COLUMN_COORD_LNG + " TEXT NOT NULL, " +
                COLUMN_COORD_LAT + " TEXT NOT NULL );";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
