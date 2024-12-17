package com.tvip.sfa.menu_setting;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tvip.sfa.BuildConfig;
import com.tvip.sfa.Perangkat.HttpsTrustManager;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tvip.sfa.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class tentang_perangkat extends AppCompatActivity {
    TextView deviceid, version;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_tentang_perangkat);
        deviceid = findViewById(R.id.deviceid);
        version = findViewById(R.id.version);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String rest = info.getMacAddress().toUpperCase(Locale.ROOT);
        rest = rest.replace(":", "");
        rest = rest.replace(" ", "");

        deviceid.setText(rest.toUpperCase(Locale.ROOT));

        version.setText(BuildConfig.VERSION_NAME);


    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }
}