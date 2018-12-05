package com.welcare.test.app;

import android.app.Application;

import com.welcare.hjkblelibrary.utile.BleServiceManager;
import com.welcare.hjkblelibrary.utile.LogUtile;

/**
 * Created by admin on 2018/11/30.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BleServiceManager.getInstance(this).init();
        LogUtile.setOpen(false);
    }
}
