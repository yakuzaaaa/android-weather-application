package com.example.nilarnab.mystats.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.nilarnab.mystats.App;
import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.background.FetchWeatherTask;
import com.example.nilarnab.mystats.database.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by nilarnab on 6/8/16.
 */
public class WeatherUtils {
    public static final String DATE_FORMAT = "yyyyMMdd";
    private static final String KEY_LIST = "list";
    private static final String KEY_DT = "dt";
    private static final String KEY_TEMP = "temp";
    private static final String KEY_MAX = "max";
    private static final String KEY_MIN = "min";
    private static final String KEY_WEATHER = "weather";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_CITY = "city";
    private static final String KEY_WEATHER_ID = "id";
    private static final String[] DAYS_OF_WEEK = new String[]{
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
    };
    private static Context context = App.getAppContext();

    public static double getMaxTemperatureForDay(JSONObject day) throws JSONException {
        JSONObject temp = day.getJSONObject(KEY_TEMP);

        return temp.getDouble(KEY_MAX);
    }

    public static double getMinTemperatureForDay(JSONObject day) throws JSONException {
        JSONObject temp = day.getJSONObject(KEY_TEMP);

        return temp.getDouble(KEY_MIN);
    }

    public static String getReadableDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(time);

        return DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static Cursor saveResultsToDb(String rawJSON) throws JSONException {
        JSONObject mainObject = new JSONObject(rawJSON);
        JSONObject location = mainObject.getJSONObject(KEY_CITY);

        //Location related
        String cityName = location.getString("name");
        JSONObject coord = location.getJSONObject("coord");
        double lat = coord.getDouble("lat");
        double lng = coord.getDouble("lon");

        String locationPin = Utility.getPreferredLocation();

        long locationId = addLocation(locationPin, cityName, lat, lng);


        JSONArray dayDetailsList = mainObject.getJSONArray(KEY_LIST);
        Vector<ContentValues> vector = new Vector<>();

        for (int i = 0; i < FetchWeatherTask.DAYS_COUNT; i++) {
            JSONObject day = dayDetailsList.getJSONObject(i);

            JSONObject temp = day.getJSONArray(KEY_WEATHER).getJSONObject(0);
            String description = temp.getString(KEY_DESCRIPTION);
            int weatherId = temp.getInt(KEY_WEATHER_ID);

            ContentValues values = new ContentValues();
            values.put(WeatherContract.WeatherTable.COLUMN_LOCATION_ID, locationId);
            values.put(WeatherContract.WeatherTable.COLUMN_DATE, day.getLong(KEY_DT) * 1000L);
            values.put(WeatherContract.WeatherTable.COLUMN_CONDITION, description);
            values.put(WeatherContract.WeatherTable.COLUMN_PRESSURE, day.getDouble("pressure"));
            values.put(WeatherContract.WeatherTable.COLUMN_WIND_SPEED, day.getDouble("speed"));
            values.put(WeatherContract.WeatherTable.COLUMN_MAX_TEMP, getMaxTemperatureForDay(day));
            values.put(WeatherContract.WeatherTable.COLUMN_MIN_TEMP, getMinTemperatureForDay(day));
            values.put(WeatherContract.WeatherTable.COLUMN_WEATHER_CONDITION_ID, weatherId);
            values.put(WeatherContract.WeatherTable.COLUMN_DEGREES, day.getDouble("deg"));
            values.put(WeatherContract.WeatherTable.COLUMN_HUMIDITY, day.getDouble("humidity"));

            vector.add(values);
        }

        if (vector.size() > 0) {
            ContentValues arr[] = vector.toArray(new ContentValues[vector.size()]);
            App.getAppContext().getContentResolver().bulkInsert(WeatherContract.WeatherTable.CONTENT_URI, arr);
        }

        return App.getAppContext().getContentResolver()
                .query(WeatherContract.WeatherTable.CONTENT_URI, null, null, null, null);
    }

    private static long addLocation(String pin, String cityName, double lat, double lng) {
        ContentValues cv = new ContentValues();
        cv.put(WeatherContract.LocationTable.COLUMN_CITY_NAME, cityName);
        cv.put(WeatherContract.LocationTable.COLUMN_COORD_LAT, lat);
        cv.put(WeatherContract.LocationTable.COLUMN_COORD_LNG, lng);
        cv.put(WeatherContract.LocationTable.COLUMN_LOCATION_SETTINGS, pin);

        Uri uri = App.getAppContext().getContentResolver().insert(WeatherContract.LocationTable.CONTENT_URI, cv);

        return Long.parseLong(uri.getLastPathSegment());
    }

    public static String formatTemperature(double temperature) {
        return context.getString(R.string.format_temperature, temperature);
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     *
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                     in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(long dateInMillis) {
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(dateInMillis);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd");
        return sdf.format(now.getTime());
    }

    public static String getFormattedWind(double windSpeed, double degrees) {
        int windFormat;
        String unitSystem = Utility.getPreferredUnit();
        if (unitSystem.equals(context.getString(R.string.unit_metric))) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }
        return String.format(context.getString(windFormat), windSpeed, direction);
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(long weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_rain_and_lighting_48dp;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_rain_and_lighting_48dp;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain_and_lighting_48dp;
        } else if (weatherId == 511) {
            return R.drawable.ic_hails_and_snow_48dp;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain_48dp;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_hails_and_snow_48dp;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_cloudy_48dp;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_rain_and_lighting_48dp;
        } else if (weatherId == 800) {
            return R.drawable.ic_sunny_48dp;
        } else if (weatherId == 801) {
            return R.drawable.ic_partly_cloudy_48dp;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy_48dp;
        }
        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(long weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

    public static String getFriendlyDayString(long dateInMillis) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"
        Calendar current = new GregorianCalendar();

        Calendar param = new GregorianCalendar();
        param.setTimeInMillis(dateInMillis);

        int currentDay = current.get(Calendar.DAY_OF_MONTH);
        int currentMonth = current.get(Calendar.MONTH);
        int paramDay = param.get(Calendar.DAY_OF_MONTH);
        int paramMonth = param.get(Calendar.MONTH);


        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (currentDay == paramDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(dateInMillis));
        } else if (currentMonth == paramMonth && paramDay < currentDay + 7) {
            // If the input date is less than a week in the future, just return the day name.
            return getReadableDate(dateInMillis);
        } else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
        }
    }
}
