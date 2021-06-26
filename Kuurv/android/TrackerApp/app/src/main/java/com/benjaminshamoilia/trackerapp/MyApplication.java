package com.benjaminshamoilia.trackerapp;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Created by Jaydeep on 21-12-2017.
 */

public class MyApplication extends Application{

    private static int activityVisible = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static int isActivityVisible() {

        return activityVisible;
    }

    public static void activityResumed() {
        ++activityVisible;
    }

    public static void activityPaused() { --activityVisible; }



    //Vinay Code
    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }
    //Vinay Code


}