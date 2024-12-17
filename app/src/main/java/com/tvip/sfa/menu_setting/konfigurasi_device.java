package com.tvip.sfa.menu_setting;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_login.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class konfigurasi_device extends AppCompatActivity {
    EditText edituserid, editpassword, editemployeeid, editdepoid, editnamadepo, editipwifi, editportwifi, editipgprs;
    SharedPreferences sharedPreferences;
    EditText edittype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_konfigurasi_device);
        edituserid = findViewById(R.id.edituserid);
        editpassword = findViewById(R.id.editpassword);
        editemployeeid = findViewById(R.id.editemployeeid);
        editdepoid = findViewById(R.id.editdepoid);
        editnamadepo = findViewById(R.id.editnamadepo);
        editipwifi = findViewById(R.id.editipwifi);
        editportwifi = findViewById(R.id.editportwifi);
        editipgprs = findViewById(R.id.editipgprs);
        edittype = findViewById(R.id.edittype);

        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);
        editipwifi.setText(ipAddress);
        editportwifi.setText("8080");
        getLocalIpAddress();


        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        String lokasi = sharedPreferences.getString("lokasi", null);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String employee = sharedPreferences.getString("nik_baru", null);
        String password = sharedPreferences.getString("password", null);

        editpassword.setText(password);
        editemployeeid.setText(nik_baru);
        edituserid.setText(employee);
        editnamadepo.setText(lokasi);

        if(nik_baru.contains("D")){
            edittype.setText("CAN");
        } else if(nik_baru.contains("CRL")){
            edittype.setText("CRL");
        } else if(nik_baru.contains("C")){
            edittype.setText("COL");
        } else if(nik_baru.contains("PR")){
            edittype.setText("TKO");
        } else if(nik_baru.contains("RP")){
            edittype.setText("DEL");
        } else if(nik_baru.contains("STO")) {
            edittype.setText("MER");
        }

        StringRequest depo = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server/master/lokasi/index_kode?namadepo=" + lokasi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {
                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                    editdepoid.setText(movieObject.getString("kode_dms"));

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        depo.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue depoQueue = Volley.newRequestQueue(konfigurasi_device.this);
        depoQueue.getCache().clear();
        depoQueue.add(depo);

    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        editipgprs.setText(ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }


}