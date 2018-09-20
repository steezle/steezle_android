package com.steezle.e_com.view;

import android.support.multidex.MultiDexApplication;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import com.steezle.e_com.R;

public class MyApplication extends MultiDexApplication {
    private Tracker mTracker;

    private Activity mCurrentActivity = null;

    public void setCurrentActivity(Activity mCurrectActivity) {
        this.mCurrentActivity = mCurrectActivity;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    private static Context mAppContext;

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
        this.setAppContext(getApplicationContext());
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.string.app_name);
        }
        return mTracker;
    }
}