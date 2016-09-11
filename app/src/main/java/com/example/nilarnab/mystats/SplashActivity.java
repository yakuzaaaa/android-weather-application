package com.example.nilarnab.mystats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.nilarnab.mystats.events.DataUpdatedEvent;
import com.example.nilarnab.mystats.events.LocationFetchedEvent;
import com.example.nilarnab.mystats.services.LocationListenerService;
import com.example.nilarnab.mystats.sync.SyncAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
    boolean mTimerExpired = false;
    Handler mHandler;

    @BindView(R.id.loading_image) ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        RotateAnimation rotateAnimation =
                new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new AccelerateInterpolator(1.0f));
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        mImageView.startAnimation(rotateAnimation);



        if (savedInstanceState != null) {
            mTimerExpired = true;
            switchActivity();
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(this);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, LocationListenerService.class));
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMIT);
                }
            }
            startService(new Intent(this, LocationListenerService.class));

            //todo:start a time limit handler here
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTimerExpired = true;
                    switchActivity();
                }
            }, Constants.SPALSH_TIME_LIMIT_MILLIS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.LOCATION_PERMIT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, LocationListenerService.class));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void locationFetchedEvent(LocationFetchedEvent event) {
        if (event.isChangedLocation()) {
            SyncAdapter.initWeatherSync();
        } else {
            switchActivity();
        }

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataUpdatedEvent(DataUpdatedEvent event) {
        switchActivity();
    }

    private void switchActivity() {
        if (mTimerExpired) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
