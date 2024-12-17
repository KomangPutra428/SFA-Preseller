package com.tvip.sfa.Perangkat;

import android.app.Activity;

public class ExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Activity myContext;
    private final String LINE_SEPARATOR = "\n";

    public ExceptionHandler(Activity context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {

        System.gc();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
