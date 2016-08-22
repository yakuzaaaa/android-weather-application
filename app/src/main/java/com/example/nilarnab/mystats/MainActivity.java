package com.example.nilarnab.mystats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nilarnab.mystats.background.FetchWeatherTask;
import com.example.nilarnab.mystats.fragments.WeatherDetailsFragment;
import com.example.nilarnab.mystats.fragments.WeatherForecastFragment;
import com.example.nilarnab.mystats.utility.Utility;

public class MainActivity extends AppCompatActivity
        implements WeatherForecastFragment.listener {
    private static final String FORECAST_FRAGMENT = WeatherForecastFragment.class.getSimpleName();

    private boolean mTwoPaneMode = false;
    private FetchWeatherTask mWeatherFetchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_container, WeatherForecastFragment.newInstance(), FORECAST_FRAGMENT)
                .commit();
        if (findViewById(R.id.details_activity_container) != null) {
            mTwoPaneMode = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_activity_container, WeatherDetailsFragment.newInstance(null))
                    .commit();
        } else {
            mTwoPaneMode = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);

            return true;
        }

        if (item.getItemId() == R.id.action_refresh) {
            if (mWeatherFetchTask == null) {
                executeNewTask();
            } else {
                mWeatherFetchTask.cancel(false);
                if (mWeatherFetchTask.isCancelled()) {
                    executeNewTask();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void executeNewTask() {
        mWeatherFetchTask = new FetchWeatherTask();
        String pinCode = Utility.getPreferredLocation();
        String unit = Utility.getPreferredUnit();
        if (pinCode != null || unit != null) {
            mWeatherFetchTask.execute(pinCode, unit);
        } else {
            Toast.makeText(this, "General weather settings error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClicked(Uri uri) {
        if (mTwoPaneMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_activity_container,
                            WeatherDetailsFragment.newInstance(uri.toString()))
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .setData(uri);
            intent.setAction(DetailsActivity.ACTION_WEATHER);
            startActivity(intent);
        }
    }
}
