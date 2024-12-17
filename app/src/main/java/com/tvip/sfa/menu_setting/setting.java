package com.tvip.sfa.menu_setting;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.sfa.BuildConfig;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;

import com.tvip.sfa.R;
import com.tvip.sfa.menu_login.login;
import com.tvip.sfa.menu_utama.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class setting extends AppCompatActivity {
    LinearLayout realisasipengiriman, informasi_device, konfigurasi, editposm, keluar;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_setting);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        informasi_device = findViewById(R.id.informasi_device);
        konfigurasi = findViewById(R.id.konfigurasi);
        keluar = findViewById(R.id.keluar);
        realisasipengiriman = findViewById(R.id.realisasipengiriman);
        editposm = findViewById(R.id.editposm);

        editposm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(setting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Fitur Ini Belum Tersedia")
                        .setConfirmText("OK")
                        .show();
            }
        });

        realisasipengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tentangperangkat = new Intent(getBaseContext(), realisasi_pengiriman.class);
                startActivity(tentangperangkat);
            }
        });


        informasi_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tentangperangkat = new Intent(getBaseContext(), tentang_perangkat.class);
                startActivity(tentangperangkat);
            }
        });

        konfigurasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent konfigurasi = new Intent(getBaseContext(), konfigurasi_device.class);
                startActivity(konfigurasi);

            }
        });


        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(setting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Apakah anda yakin?")
                        .setContentText("Anda akan keluar dari aplikasi ini")
                        .setConfirmText("Yes")
                        .setCancelText("Cancel")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Notification/index_cekDeviceId",
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.clear();
                                                editor.apply();
                                                Intent inetnt = new Intent(getBaseContext(), login.class);
                                                inetnt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(inetnt);

                                            }
                                        }, new Response.ErrorListener() {
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

                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                        String employeeid = sharedPreferences.getString("szDocCall", null);
                                        String nik_baru = sharedPreferences.getString("nik_baru", null);

                                        String[] parts2 = employeeid.split("-");
                                        String restnomor2 = parts2[0];

                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String currentDateandTime2 = sdf2.format(new Date());

                                        params.put("preseller_nik", nik_baru);
                                        params.put("preseller_employeeId", employeeid);
                                        params.put("preseller_name", MainActivity.txt_nama.getText().toString());
                                        params.put("preseller_branch", restnomor2);

                                        params.put("preseller_location", sharedPreferences.getString("lokasi", null));
                                        params.put("device_brand", Build.BRAND);

                                        params.put("device_model", Build.MODEL);
                                        params.put("device_sdk", String.valueOf(Build.VERSION.SDK_INT));
                                        params.put("device_version", Build.VERSION.RELEASE);
                                        params.put("apps_version", BuildConfig.VERSION_NAME);

                                        params.put("apps_last_open", currentDateandTime2);
                                        params.put("device_token", nik_baru);
                                        params.put("status_user", "0");




                                        return params;
                                    }

                                };
                                stringRequest2.setRetryPolicy(
                                        new DefaultRetryPolicy(
                                                5000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                        )
                                );
                                RequestQueue requestQueue2 = Volley.newRequestQueue(setting.this);
                                requestQueue2.getCache().clear();
                                requestQueue2.add(stringRequest2);

                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        refreshActivity();
        super.onBackPressed();
    }

    public void refreshActivity() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}