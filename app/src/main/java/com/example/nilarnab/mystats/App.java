package com.example.nilarnab.mystats;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nilarnab on 6/8/16 and it is made of each and everyone of you people to see, judge and advice :-).
 */
public class App extends Application {
    public static final String TAG = "MyStats";

    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    public static SharedPreferences getAppPreference() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
