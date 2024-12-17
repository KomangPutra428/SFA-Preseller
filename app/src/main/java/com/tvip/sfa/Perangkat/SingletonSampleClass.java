package com.tvip.sfa.Perangkat;

import android.content.Context;

public class SingletonSampleClass {
    private Context context;
    private static SingletonSampleClass instance;

    private SingletonSampleClass(Context context) {
        this.context = context;
    }

    public synchronized static SingletonSampleClass getInstance(Context context) {
        if (instance == null) instance = new SingletonSampleClass(context);
        return instance;
    }

    public void onDestroy() {
        if(context != null) {
            context = null;
        }
    }
}
