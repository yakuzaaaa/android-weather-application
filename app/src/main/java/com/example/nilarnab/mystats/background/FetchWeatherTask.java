package com.example.nilarnab.mystats.background;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.nilarnab.mystats.App;
import com.example.nilarnab.mystats.Constants;
import com.example.nilarnab.mystats.events.WeatherFetchedEvent;
import com.example.nilarnab.mystats.utility.WeatherUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nilarnab on 4/8/16.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
    public static final int DAYS_COUNT = 14;

    private static final String REQUEST_SCHEME = "http";
    private static final String BASE_URL = "api.openweathermap.org";
    private static final String PATH_DATA = "data";
    private static final String PATH_VERSION = "2.5";
    private static final String PATH_FORECAST = "forecast";
    private static final String QUERY_PIN_CODE = "q";
    private static final String QUERY_MODE_FORMAT = "mode";
    private static final String QUERY_UNIT = "units";
    private static final String QUERY_DAYS_COUNT = "cnt";
    private static final String QUERY_API_KEY = "appid";
    private static final String FORMAT_JSON = "json";
    private static final String PATH_DAILY = "daily";

    @Override
    protected Void doInBackground(String... strings) {
        String resultJSON = "";
        HttpURLConnection connection = null;
        BufferedReader br = null;

        Uri uri =
                new Uri.Builder()
                        .scheme(REQUEST_SCHEME)
                        .authority(BASE_URL)
                        .appendEncodedPath(PATH_DATA)
                        .appendEncodedPath(PATH_VERSION)
                        .appendEncodedPath(PATH_FORECAST)
                        .appendEncodedPath(PATH_DAILY)
                        .appendQueryParameter(QUERY_PIN_CODE, strings[0])
                        .appendQueryParameter(QUERY_MODE_FORMAT, FORMAT_JSON)
                        .appendQueryParameter(QUERY_DAYS_COUNT, String.valueOf(DAYS_COUNT))
                        .appendQueryParameter(QUERY_UNIT, strings[1])
                        .appendQueryParameter(QUERY_API_KEY, Constants.OPEN_WEATHER_API_KEY)
                        .build();
        Log.d(App.TAG, uri.toString());

        try {
            //Making a request
            URL openWetherMapUrl = new URL(uri.toString());
            connection = (HttpURLConnection) openWetherMapUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            //now to parse the result
            InputStream inputStream = connection.getInputStream();

            if (inputStream != null) {
                br = new BufferedReader(new InputStreamReader(inputStream));
            }
            //now we will use stringBuffer as it is thread safe
            StringBuilder buffer = new StringBuilder();
            String line;
            if (br != null) {
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
            }

            resultJSON = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
            WeatherUtils.saveResultsToDb(resultJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(App.getAppContext(), "Refreshed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        WeatherFetchedEvent event = new WeatherFetchedEvent();
        event.setSuccess(false);

        EventBus.getDefault().postSticky(event);
    }
}
