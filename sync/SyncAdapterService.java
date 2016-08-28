package com.example.nilarnab.mystats.sync;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.nilarnab.mystats.sync.SyncAdapter;

/**
 * Created by nilarnab on 27/8/16 and it is made of each and everyone of you people to see, judge and advice :-).
 */
public class SyncAdapterService extends Service {
    private static SyncAdapter mSyncAdapter = null;
    private final Object lockObject = new Object();

    @Override
    public void onCreate() {
        synchronized (lockObject) {
            if (mSyncAdapter == null) {
                mSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
