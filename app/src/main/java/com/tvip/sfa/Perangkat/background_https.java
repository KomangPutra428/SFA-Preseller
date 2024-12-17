package com.tvip.sfa.Perangkat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class background_https extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HttpsTrustManager.allowAllSSL();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
