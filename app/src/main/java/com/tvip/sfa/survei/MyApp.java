package com.tvip.sfa.survei;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static MyApp instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApp getsInstance(){
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static Context getAppContext(){
        return instance.getApplicationContext();
    }
}
