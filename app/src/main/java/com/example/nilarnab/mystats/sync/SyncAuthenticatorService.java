package com.example.nilarnab.mystats.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by nilarnab on 27/8/16 and it is made of each and everyone of you people to see, judge and advice :-).
 */
public class SyncAuthenticatorService extends Service {
    SyncAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        mAuthenticator = new SyncAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
