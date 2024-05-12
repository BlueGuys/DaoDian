package com.swolo.daodian.business.main;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class PrintService extends Service {

    private MyBinder mBinder;

    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }

    public class MyBinder extends Binder {
        public void onDone() {
            Log.d("MyService", "onDone()");
        }
    }
}
