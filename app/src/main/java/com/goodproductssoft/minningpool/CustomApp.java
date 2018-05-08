package com.goodproductssoft.minningpool;

import android.app.Activity;
import android.app.Application;

import com.goodproductssoft.minningpool.activitys.MainActivity;

/**
 * Created by user on 5/2/2018.
 */

public class CustomApp extends Application {
    private static CustomApp instance = null;

    public final static CustomApp getInstance() {
        return instance;
    }
    public MainActivity mainActivity;
    private Activity currentActivity = null;

    public Activity getCurrentActivity() {
        return currentActivity;
    }
}
