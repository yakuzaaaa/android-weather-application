package com.example.nilarnab.mystats.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by nilarnab on 14/8/16.
 */
public class WeatherProvider extends ContentProvider {
    //content://com.yakuza.content.weather/weather
    public static final int WEATHER = 1001;
    //content://com.yakuza.content.weather/weather/712235
    public static final int WEATHER_WITH_LOCATION = 1002;
    //content://com.yakuza.content.weather/weather/712235?date="123123"
    public static final int WEATHER_WITH_LOCATION_AND_DATE = 1003;
    //content://com.yakuza.content.weather/weather
    public static final int LOCATION = 1004;
    //location.location_setting = ?
    private static final String sLocationSettingSelection =
            WeatherContract.LocationTable.TABLE_NAME +
                    "." + WeatherContract.LocationTable.COLUMN_LOCATION_SETTINGS + " = ? ";
    //location.location_setting = ? AND date >= ?
    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationTable.TABLE_NAME +
                    "." + WeatherContract.LocationTable.COLUMN_LOCATION_SETTINGS + " = ? AND " +
                    WeatherContract.WeatherTable.COLUMN_DATE + " >= ? ";
    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationTable.TABLE_NAME +
                    "." + WeatherContract.LocationTable.COLUMN_LOCATION_SETTINGS + " = ? AND " +
                    WeatherContract.WeatherTable.COLUMN_DATE + " = ? ";
    private static UriMatcher mMatcher = buildUriMatcher();
    private static SQLiteQueryBuilder mQueryBuilder;

    static {
        mQueryBuilder = new SQLiteQueryBuilder();
        //here we will join weather table and location table
        mQueryBuilder.setTables(
                WeatherContract.WeatherTable.TABLE_NAME +
                        " INNER JOIN " +
                        WeatherContract.LocationTable.TABLE_NAME +
                        " ON " +
                        WeatherContract.WeatherTable.TABLE_NAME + "." + WeatherContract.WeatherTable.COLUMN_LOCATION_ID +
                        " = " +
                        WeatherContract.LocationTable.TABLE_NAME + "." + WeatherContract.LocationTable._ID
        );
    }

    private WeatherDatabaseOpenHelper mDataBaseHelper;

    public static UriMatcher buildUriMatcher() {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, WEATHER);
        mMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        mMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        mMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_LOCATION, LOCATION);

        return mMatcher;
    }

    @Override
    public boolean onCreate() {
        mDataBaseHelper = new WeatherDatabaseOpenHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor resultCursor;
        switch (mMatcher.match(uri)) {
            case WEATHER_WITH_LOCATION_AND_DATE: {
                resultCursor = getWeatherByLocationAndStartDate(uri, projection, sortOrder);
                break;
            }
            case WEATHER_WITH_LOCATION: {
                resultCursor = getWeatherByLocationOrDate(uri, projection, sortOrder);
                break;
            }
            case WEATHER: {
                resultCursor = mDataBaseHelper.getReadableDatabase().query(WeatherContract.WeatherTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case LOCATION: {
                resultCursor = mDataBaseHelper.getReadableDatabase().query(WeatherContract.LocationTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return resultCursor;
    }

    private Cursor getWeatherByLocationOrDate(Uri uri, String[] projection, String sortOrder) {
        String locationSettings = WeatherContract.WeatherTable.getLocationFromUri(uri);
        String selection = sLocationSettingSelection;
        String selectionArgs[] = new String[]{locationSettings};
        long startDate = WeatherContract.WeatherTable.getStartDateFromUri(uri);

        if (startDate > 0) {
            selection = sLocationSettingAndDaySelection;
            selectionArgs = new String[]{locationSettings, String.valueOf(startDate)};
        }

        return mQueryBuilder.query(mDataBaseHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getWeatherByLocationAndStartDate(Uri uri, String[] projection, String sortOrder) {
        String locationSettings = WeatherContract.WeatherTable.getLocationFromUri(uri);
        String selection = sLocationSettingAndDaySelection;
        long startDate = WeatherContract.WeatherTable.getStartDateFromUri(uri);
        String selectionArgs[] = new String[]{locationSettings, String.valueOf(startDate)};

        return mQueryBuilder.query(mDataBaseHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int code = mMatcher.match(uri);
        switch (code) {
            case WEATHER:
                return WeatherContract.WeatherTable.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationTable.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherTable.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (mMatcher.match(uri)) {
            case WEATHER: {
                long id = mDataBaseHelper.getWritableDatabase().insertWithOnConflict(WeatherContract.WeatherTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    return WeatherContract.WeatherTable.buildWeatherUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            }
            case LOCATION: {
                long id = mDataBaseHelper.getWritableDatabase().insertWithOnConflict(WeatherContract.LocationTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    return WeatherContract.LocationTable.buildLocationUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            }
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        final int match = mMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case WEATHER:
                rowsDeleted = db.delete(
                        WeatherContract.WeatherTable.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        WeatherContract.LocationTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        final int match = mMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case WEATHER:
                rowsUpdated = db.update(WeatherContract.WeatherTable.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(WeatherContract.LocationTable.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        final int match = mMatcher.match(uri);
        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WeatherContract.WeatherTable.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
