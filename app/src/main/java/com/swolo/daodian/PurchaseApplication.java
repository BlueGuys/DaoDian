package com.swolo.daodian;

import android.app.Application;
import android.content.Context;

/**
 * Created by hezhisu on 2016/12/20.
 */

public class PurchaseApplication extends Application{

    public static Context mAppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mAppContext;
    }

}
