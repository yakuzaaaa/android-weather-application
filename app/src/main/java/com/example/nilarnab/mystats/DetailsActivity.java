package com.example.nilarnab.mystats;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.nilarnab.mystats.fragments.WeatherDetailsFragment;

public class DetailsActivity extends AppCompatActivity {
    public static final String ACTION_WEATHER = "weather_details_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getAction() != null) {
            checkAction(getIntent());
        }
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(Constants.APP_NOTIFICATION_ID);
    }

    private void checkAction(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_WEATHER: {
                    if (intent.getData() != null) {
                        supportStartPostponedEnterTransition();
                        showFragment(WeatherDetailsFragment.newInstance(intent.getDataString()));
                    }
                }
                break;
                default: {
                    this.finish();
                    break;
                }
            }
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.details_activity_container, fragment).commit();
    }
}
