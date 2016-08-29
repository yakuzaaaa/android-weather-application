package com.example.nilarnab.mystats;

import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nilarnab.mystats.fragments.WeatherDetailsFragment;
import com.example.nilarnab.mystats.fragments.WeatherForecastFragment;
import com.example.nilarnab.mystats.sync.SyncAdapter;
import com.example.nilarnab.mystats.utility.Utility;

public class MainActivity extends AppCompatActivity
        implements WeatherForecastFragment.listener {
    private static final String FORECAST_FRAGMENT = WeatherForecastFragment.class.getSimpleName();

    private boolean mTwoPaneMode = false;

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
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_activity_container, WeatherDetailsFragment.newInstance(null))
                        .commit();
            }
        } else {
            mTwoPaneMode = false;
        }
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(Constants.APP_NOTIFICATION_ID);
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
            SyncAdapter.initWeatherSync();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClicked(Uri uri, ImageView icon, TextView desc, int position, View max, View min) {
        if (mTwoPaneMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_activity_container,
                            WeatherDetailsFragment.newInstance(uri.toString()))
                    .commit();
        } else {
            ActivityOptionsCompat activityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(icon,getString(R.string.detail_icon_transition_name)),
                            new Pair<View,String>(desc,getString(R.string.detail_desc_transition_name)),
                            new Pair<View,String>(max,getString(R.string.detail_max_transition_name)),
                            new Pair<View,String>(min,getString(R.string.detail_min_transition_name))
                    );

            ActivityCompat.startActivity(this,Utility.getWeatherDetailsIntent(uri),activityOptionsCompat.toBundle());
        }
    }
}
