package com.example.nilarnab.mystats.utility;

import com.example.nilarnab.mystats.App;
import com.example.nilarnab.mystats.R;

/**
 * Created by nilarnab on 6/8/16.
 */
public class DeviceUtils {

    public void storeDeviceLocation(float latitude, float longitude) {
        Utility.storeFloatPreference(App.getAppContext().getString(R.string.pref_lat), latitude);
        Utility.storeFloatPreference(App.getAppContext().getString(R.string.pref_lng), longitude);
    }

    public float getDeviceLatitude() {
        return Utility.getFloatPreference(App.getAppContext().getResources().getString(R.string.pref_lat), 0);
    }

    public float getDeviceLongitude() {
        return Utility.getFloatPreference(App.getAppContext().getResources().getString(R.string.pref_lng), 0);
    }
}
