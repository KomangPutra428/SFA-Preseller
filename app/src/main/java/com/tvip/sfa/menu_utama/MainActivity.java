package com.tvip.sfa.menu_utama;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.ServerError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tvip.sfa.AppUtils;
import com.tvip.sfa.BuildConfig;
import com.tvip.sfa.Perangkat.GPSTracker;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tvip.sfa.menu_mulai_perjalanan.mulai_perjalanan;
import com.tvip.sfa.menu_selesai_perjalanan.detail_selesai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    private String basicAuthCredentials = "admin:Databa53";
    String nama_file;
    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";

    public static String Tunda;
    public static TextView txt_nama, txt_depo, istirahat_text, laporan_cpr;
    LinearLayout persiapan, istirahat, mulaiperjalanan, selesai_perjalanan;
    LocationManager locationManager;
    ArrayList<String> Istirahat = new ArrayList<>();

    String TokenDevice;
    ImageButton biodata, setting;

    public static String longitude, latitude, kodelokasi, conditional;
    String id_istirahat;
    Context context;

    SweetAlertDialog pDialog, success;
    private static final byte[] reserve = new byte[1024 * 1024]; // Reserves 1MB.

    ImageView button_persiapan, image_mulaiperjalanan, image_istirahat, button_selesai_perjalanan;

    private RequestQueue requestQueue;

    GPSTracker gpsTracker;

    private long downloadID;
    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadID) {
                pDialog.dismissWithAnimation();
                installApk();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_main);

        button_persiapan = findViewById(R.id.button_persiapan);
        image_mulaiperjalanan = findViewById(R.id.image_mulaiperjalanan);
        image_istirahat = findViewById(R.id.image_istirahat);
        button_selesai_perjalanan = findViewById(R.id.button_selesai_perjalanan);
        mulaiperjalanan = findViewById(R.id.mulaiperjalanan);
        selesai_perjalanan = findViewById(R.id.selesai_perjalanan);
        laporan_cpr = findViewById(R.id.laporan_cpr);

        txt_nama = findViewById(R.id.txt_nama);
        txt_depo = findViewById(R.id.txt_depo);
        biodata = findViewById(R.id.biodata);
        persiapan = findViewById(R.id.persiapan);
        istirahat = findViewById(R.id.istirahat);
        istirahat_text = findViewById(R.id.istirahat_text);
        setting = findViewById(R.id.setting);

        createNotificationChannel();
        getToken();

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));



        // Check for write external storage permission




//        FirebaseMessaging.getInstance().subscribeToTopic("system").addOnCompleteListener(task -> {
//            FirebaseMessaging.getInstance().subscribeToTopic("system");
//            String msg = getString(R.string.msg_subscribed); //==> msg_subscribed = "subscribed"
//            if (!task.isSuccessful()) {
//                msg = getString(R.string.msg_subscribe_failed); //==> msg_subscribe_failed = "failed"
//            }
//        });





        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        getLocation();

        final String lokasi = sharedPreferences.getString("lokasi", null);
        final String nama = sharedPreferences.getString("nama", null);
        final String id_employee = sharedPreferences.getString("szDocCall", null);
        String[] parts = id_employee.split("-");
        String result = parts[0];

        if(result.equals("336") || result.equals("321") || result.equals("324")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("link", "rest_server_sfa_asa_dummy");
            editor.apply();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("link", "rest_server_sfa_tvip_dummy");
            editor.apply();
        }

        txt_nama.setText(nama);
        txt_depo.setText("Depo "+lokasi);

        System.out.println("Total Memory " + java.lang.Runtime.getRuntime().maxMemory());

        boolean isOutOfMemory = false;
        try {

        }
        catch (OutOfMemoryError ex) {
            System.gc();
            isOutOfMemory = true;
        }

        selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_selesai_perjalanan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.selesai_perjalanan_hitam).getConstantState()) {
                    Toast.makeText(context, "True", Toast.LENGTH_SHORT).show();
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), detail_selesai.class);
                    startActivity(intent);

                }



            }
        });

        biodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentbiodata = new Intent(getBaseContext(), com.tvip.sfa.menu_biodata.biodata.class);
                startActivity(intentbiodata);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentbiodata = new Intent(getBaseContext(), com.tvip.sfa.menu_setting.setting.class);
                startActivity(intentbiodata);
            }
        });

        persiapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((button_persiapan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.persiapan_hitam).getConstantState())) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                } else {
                    Intent biodata = new Intent(getBaseContext(), com.tvip.sfa.menu_persiapan.persiapan.class);
                    startActivity(biodata);
                }

            }
        });


        mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((image_mulaiperjalanan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.mulai_perjalanan_hitam).getConstantState())) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                } else {
                    Intent mulaiperjalanan = new Intent(getBaseContext(), mulai_perjalanan.class);
                    startActivity(mulaiperjalanan);
                }

            }
        });

        istirahat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Istirahat.clear();
                if (image_mulaiperjalanan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.mulai_perjalanan_hitam).getConstantState()) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.popup_selesai_istirahat);
                    Button tidak = dialog.findViewById(R.id.tidak);
                    Button ya = dialog.findViewById(R.id.ya);
                    dialog.setCancelable(false);

                    dialog.show();

                    tidak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    ya.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Harap Menunggu");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_istirahat",
                                    new Response.Listener<String>() {

                                        @Override
                                        public void onResponse(String response) {
                                            pDialog.dismissWithAnimation();
                                            success = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                            success.setContentText("Data Sudah Disimpan");
                                            success.setCancelable(false);
                                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                            pDialog.setTitleText("Harap Menunggu");
                                                            pDialog.setCancelable(false);
                                                            pDialog.show();
                                                            updateUI();
                                                            button_persiapan.setImageDrawable(getResources().getDrawable(R.drawable.button_persiapan));
                                                            image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan));
                                                            image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.button_istirahat));
                                                            button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.button_selesai_perjalanan));

                                                        }
                                                    });
                                            success.show();
                                            pDialog.cancel();

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
                                    String nik_baru = sharedPreferences.getString("szDocCall", null);


                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                    String currentDateandTime2 = sdf2.format(new Date());

                                    params.put("iInternalId", id_istirahat);

                                    params.put("dtmFinished", currentDateandTime2);
                                    params.put("szUserUpdatedId", nik_baru);
                                    params.put("dtmLastUpdated", currentDateandTime2);


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
                            RequestQueue requestQueue2 = Volley.newRequestQueue(MainActivity.this);
                            requestQueue2.getCache().clear();
                            requestQueue2.add(stringRequest2);

                        }
                    });

                } else {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.topup_istirahat);
                    dialog.setCancelable(false);
                    Istirahat.clear();

                    final AutoCompleteTextView istirahat = dialog.findViewById(R.id.editpilihistirahat);
                    Button tidak = dialog.findViewById(R.id.tidak);
                    Button ya = dialog.findViewById(R.id.ya);

                    StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_JenisIstirahat",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getString("status").equals("true")) {
                                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                String id = jsonObject1.getString("szId");
                                                String jenis_istirahat = jsonObject1.getString("szName");
                                                Istirahat.add(id + "-" + jenis_istirahat);

                                            }
                                        }
                                        istirahat.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, Istirahat));
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
                    rest.setRetryPolicy(new DefaultRetryPolicy(
                            5000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestkota = Volley.newRequestQueue(MainActivity.this);
                    requestkota.getCache().clear();
                    requestkota.add(rest);

                    dialog.show();

                    istirahat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            istirahat.showDropDown();
                        }
                    });
                    istirahat.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            istirahat.showDropDown();
                            return false;
                        }
                    });

                    tidak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ya.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (istirahat.getText().toString().length() == 0) {
                                istirahat.setError("Pilih Jenis Istirahat");
                            } else if(longitude == null || longitude.equals("0.0")){
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Lokasi belum ditemukan")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            } else {
                                dialog.cancel();
                                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Harap Menunggu");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                String nik_baru = sharedPreferences.getString("szDocCall", null);
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru=" + nik_baru,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {
                                                    JSONObject obj = new JSONObject(response);
                                                    final JSONArray movieArray = obj.getJSONArray("data");
                                                    for (int i = 0; i < movieArray.length(); i++) {
                                                        final JSONObject movieObject = movieArray.getJSONObject(i);

                                                        postIstirahat(movieObject.getString("szDocId"), istirahat.getText().toString());

                                                    }


                                                } catch (JSONException e) {
                                                    e.printStackTrace();

                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                pDialog.dismissWithAnimation();
                                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setContentText("Surat Tugas Belum Ada")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismissWithAnimation();
                                                            }
                                                        })
                                                        .show();
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

                                stringRequest.setRetryPolicy(
                                        new DefaultRetryPolicy(
                                                5000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                        ));
                                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                requestQueue.getCache().clear();
                                requestQueue.add(stringRequest);
                            }

                        }
                    });


                }
            }
        });



        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server/master/lokasi/index_kode?namadepo=" + lokasi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {
                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                    kodelokasi = movieObject.getString("kode_dms");


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
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }



    private void postIstirahat(String szDocId, String istirahat) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_istirahat",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        success = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        success.setContentText("Data Sudah Disimpan");
                        success.setCancelable(false);
                        success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Harap Menunggu");
                                pDialog.setCancelable(false);
                                pDialog.show();
                                updateUI();
                            }
                        });
                        success.show();


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
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentDateandTime = sdf.format(new Date());

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                String currentDateandTime2 = sdf2.format(new Date());

                String[] parts = istirahat.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");

                params.put("szDocId", nik_baru + "_" + currentDateandTime);
                params.put("szDocCallId", szDocId);
                params.put("dtmStarted", currentDateandTime2);

                params.put("szAttendanceTypeId", restnomorbaru);
                params.put("szLongitude", latitude);

                params.put("szLangitude", longitude);
                params.put("szUserCreatedId", nik_baru);
                params.put("dtmCreated", currentDateandTime2);



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
        RequestQueue requestQueue2 = Volley.newRequestQueue(MainActivity.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);


    }


    private void getLocation() {
        gpsTracker = new GPSTracker(MainActivity.this);

        if(gpsTracker.canGetLocation()){
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());
        }else{
            gpsTracker.showSettingsAlert();

        }


    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakah anda yakin?")
                .setContentText("Anda akan keluar dari aplikasi ini")
                .setConfirmText("Yes")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        finishAffinity();
                        finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        updateUIFinished();
        updateStart();
        Runtime.getRuntime().gc();

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
//        } else {
//            downloadApk();
//        }
    }

    private void updateDeviceId(String token) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Notification/index_cekDeviceId",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

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
                params.put("preseller_name", txt_nama.getText().toString());
                params.put("preseller_branch", restnomor2);

                params.put("preseller_location", sharedPreferences.getString("lokasi", null));
                params.put("device_brand", Build.BRAND);

                params.put("device_model", Build.MODEL);
                params.put("device_sdk", String.valueOf(Build.VERSION.SDK_INT));
                params.put("device_version", Build.VERSION.RELEASE);
                params.put("apps_version", BuildConfig.VERSION_NAME);

                params.put("apps_last_open", currentDateandTime2);
                params.put("device_token", token);
                params.put("status_user", "1");




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
        RequestQueue requestQueue2 = Volley.newRequestQueue(MainActivity.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void updateStart() {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       pDialog.dismissWithAnimation();
//                        Toast.makeText(getApplicationContext(), "Loading Berhasil", Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                getTunda(movieObject.getString("szDocId"));

                                if(movieObject.getString("bFinished").equals("1")){
                                    //getUpdate();
                                    button_persiapan.setImageDrawable(getResources().getDrawable(R.drawable.persiapan_hitam));
                                    image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan_hitam));
                                    image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.istirahat_hitam));
                                    conditional = "finish";
                                    laporan_cpr.setText("Laporan CPR/ECR");
                                    button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.button_selesai_perjalanan));

                                    persiapan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Sudah Diselesaikan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Sudah Diselesaikan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    istirahat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Sudah Diselesaikan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(), detail_selesai.class);
                                            startActivity(intent);
                                        }
                                    });

                                }else if(movieObject.getString("bStarted").equals("0")){
                                    image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan_hitam));
                                    image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.istirahat_hitam));
                                    button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.selesai_perjalanan_hitam));

                                    mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Belum Dijalankan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    istirahat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Belum Dijalankan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Belum Dijalankan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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
                                } else {
                                    updateUI();
                                    image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan));
                                    image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.button_istirahat));
                                    button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.button_selesai_perjalanan));

                                    mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if ((image_mulaiperjalanan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.mulai_perjalanan_hitam).getConstantState())) {
                                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismissWithAnimation();
                                                            }
                                                        })
                                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.cancel();
                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                Intent mulaiperjalanan = new Intent(getBaseContext(), mulai_perjalanan.class);
                                                startActivity(mulaiperjalanan);
                                            }

                                        }
                                    });

                                    istirahat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Istirahat.clear();
                                            if (image_mulaiperjalanan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.mulai_perjalanan_hitam).getConstantState()) {
                                                final Dialog dialog = new Dialog(MainActivity.this);
                                                dialog.setContentView(R.layout.popup_selesai_istirahat);
                                                dialog.setCancelable(false);
                                                Button tidak = dialog.findViewById(R.id.tidak);
                                                Button ya = dialog.findViewById(R.id.ya);

                                                dialog.show();

                                                tidak.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.cancel();
                                                    }
                                                });

                                                ya.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.cancel();
                                                        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                        pDialog.setTitleText("Harap Menunggu");
                                                        pDialog.setCancelable(false);
                                                        pDialog.show();

                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_istirahat",
                                                                new Response.Listener<String>() {

                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        pDialog.dismissWithAnimation();
                                                                        pDialog.dismissWithAnimation();
                                                                        success = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                                                        success.setContentText("Data Sudah Disimpan");
                                                                        success.setCancelable(false);
                                                                        success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                sDialog.dismissWithAnimation();
                                                                                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                                                pDialog.setTitleText("Harap Menunggu");
                                                                                pDialog.setCancelable(false);
                                                                                pDialog.show();
                                                                                updateUI();
                                                                                button_persiapan.setImageDrawable(getResources().getDrawable(R.drawable.button_persiapan));
                                                                                image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan));
                                                                                image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.button_istirahat));
                                                                                button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.button_selesai_perjalanan));
                                                                            }
                                                                        });
                                                                        success.show();

                                                                        pDialog.cancel();

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
                                                                String nik_baru = sharedPreferences.getString("szDocCall", null);


                                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                                                String currentDateandTime2 = sdf2.format(new Date());

                                                                params.put("iInternalId", id_istirahat);

                                                                params.put("dtmFinished", currentDateandTime2);
                                                                params.put("szUserUpdatedId", nik_baru);
                                                                params.put("dtmLastUpdated", currentDateandTime2);


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
                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(MainActivity.this);
                                                        requestQueue2.add(stringRequest2);

                                                    }
                                                });

                                            } else {
                                                Istirahat.clear();
                                                final Dialog dialog = new Dialog(MainActivity.this);
                                                dialog.setContentView(R.layout.topup_istirahat);
                                                dialog.setCancelable(false);

                                                final AutoCompleteTextView istirahat = dialog.findViewById(R.id.editpilihistirahat);
                                                Button tidak = dialog.findViewById(R.id.tidak);
                                                Button ya = dialog.findViewById(R.id.ya);

                                                StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_JenisIstirahat",
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(response);
                                                                    if (jsonObject.getString("status").equals("true")) {
                                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                            String id = jsonObject1.getString("szId");
                                                                            String jenis_istirahat = jsonObject1.getString("szName");
                                                                            Istirahat.add(id + "-" + jenis_istirahat);

                                                                        }
                                                                    }
                                                                    istirahat.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, Istirahat));
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
                                                rest.setRetryPolicy(new DefaultRetryPolicy(
                                                        5000,
                                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                RequestQueue requestkota = Volley.newRequestQueue(MainActivity.this);
                                                requestkota.add(rest);

                                                dialog.show();

                                                istirahat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                    @Override
                                                    public void onFocusChange(View v, boolean hasFocus) {
                                                        istirahat.showDropDown();
                                                    }
                                                });
                                                istirahat.setOnTouchListener(new View.OnTouchListener() {
                                                    @Override
                                                    public boolean onTouch(View v, MotionEvent event) {
                                                        istirahat.showDropDown();
                                                        return false;
                                                    }
                                                });

                                                tidak.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                ya.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        if (istirahat.getText().toString().length() == 0) {
                                                            istirahat.setError("Pilih Jenis Istirahat");
                                                        } else if(longitude == null || longitude.equals("0.0")){
                                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                                    .setTitleText("Lokasi belum ditemukan")
                                                                    .setConfirmText("OK")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                            sDialog.dismissWithAnimation();
                                                                        }
                                                                    })
                                                                    .show();
                                                        } else {
                                                            dialog.cancel();
                                                            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                            pDialog.setTitleText("Harap Menunggu");
                                                            pDialog.setCancelable(false);
                                                            pDialog.show();

                                                            sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                            String nik_baru = sharedPreferences.getString("szDocCall", null);
                                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru=" + nik_baru,
                                                                    new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {

                                                                            try {
                                                                                JSONObject obj = new JSONObject(response);
                                                                                final JSONArray movieArray = obj.getJSONArray("data");
                                                                                for (int i = 0; i < movieArray.length(); i++) {
                                                                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                                                                    postIstirahat(movieObject.getString("szDocId"), istirahat.getText().toString());

                                                                                }


                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();

                                                                            }
                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            pDialog.dismissWithAnimation();
                                                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                                    .setContentText("Surat Tugas Belum Ada")
                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                        @Override
                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                            sDialog.dismissWithAnimation();
                                                                                        }
                                                                                    })
                                                                                    .show();
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

                                                            stringRequest.setRetryPolicy(
                                                                    new DefaultRetryPolicy(
                                                                            5000,
                                                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                                    ));
                                                            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                                            requestQueue.add(stringRequest);
                                                        }

                                                    }
                                                });


                                            }
                                        }
                                    });

                                    mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if ((image_mulaiperjalanan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.mulai_perjalanan_hitam).getConstantState())) {
                                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismissWithAnimation();
                                                            }
                                                        })
                                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.cancel();
                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                Intent mulaiperjalanan = new Intent(getBaseContext(), mulai_perjalanan.class);
                                                startActivity(mulaiperjalanan);
                                            }

                                        }
                                    });

                                    selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if ((button_selesai_perjalanan.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.selesai_perjalanan_hitam).getConstantState())) {
                                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismissWithAnimation();
                                                            }
                                                        })
                                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.cancel();
                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), detail_selesai.class);
                                                startActivity(intent);

                                            }

                                        }
                                    });

                                }

                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
//                        Toast.makeText(getApplicationContext(), "Loading Gagal", Toast.LENGTH_SHORT).show();
                        if (error instanceof ServerError) {
                            pDialog.dismissWithAnimation();
                            image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan_hitam));
                            image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.istirahat_hitam));
                            button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.selesai_perjalanan_hitam));

                            mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Surat Tugas Belum Tersedia")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
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

                            istirahat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Surat Tugas Belum Tersedia")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
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

                            selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Surat Tugas Belum Tersedia")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
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
                        } else {
                            pDialog.dismissWithAnimation();
                            image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan_hitam));
                            image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.istirahat_hitam));
                            button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.selesai_perjalanan_hitam));

                            mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Koneksi Tidak Stabil")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
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

                            istirahat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Koneksi Tidak Stabil")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
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

                            selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Koneksi Tidak Stabil")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
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


                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }

    private void getTunda(String szDocId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_tunda?szDocId="+ szDocId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Tunda = "Ada";
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tunda = "Tidak Ada";
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void getUpdate() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        System.out.println("Link CallPlan = " + "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                String szDocId = movieObject.getString("szDocId");
                                getSurat(szDocId);
                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);


    }

    private void getSurat(String szDocId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_AOPerformance?surat_tugas=" + szDocId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            int number1 = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                                String currentDateandTime2 = sdf2.format(new Date());


                                if(movieObject.getString("bSuccess").equals("1")){
                                    updateDraft(movieObject.getString("szDocSO"));
                                }


                            }

                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void updateUIFinished() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                if(movieObject.getString("bFinished").equals("1")){
                                    button_persiapan.setImageDrawable(getResources().getDrawable(R.drawable.persiapan_hitam));
                                    image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan_hitam));
                                    image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.istirahat_hitam));

                                    persiapan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Sudah Diselesaikan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Sudah Diselesaikan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    istirahat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Surat Tugas Sudah Diselesaikan")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(), detail_selesai.class);
                                            startActivity(intent);
                                        }
                                    });
                                }

                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        } else {
            requestQueue.add(stringRequest);
        }

    }

    private void updateUI() {

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_Istirahat_szId?szId="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                id_istirahat = movieObject.getString("iInternalId");
                                if(movieObject.getString("bFinished").equals("0")){
                                    button_persiapan.setImageDrawable(getResources().getDrawable(R.drawable.persiapan_hitam));
                                    image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan_hitam));
                                    button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.selesai_perjalanan_hitam));

                                    selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismissWithAnimation();
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

                                    mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    persiapan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Anda Masih Menggunakan Mode Istirahat")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
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

                                    istirahat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Istirahat.clear();
                                                final Dialog dialog = new Dialog(MainActivity.this);
                                                dialog.setContentView(R.layout.popup_selesai_istirahat);
                                                dialog.setCancelable(false);
                                                Button tidak = dialog.findViewById(R.id.tidak);
                                                Button ya = dialog.findViewById(R.id.ya);


                                                dialog.show();

                                                tidak.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.cancel();
                                                    }
                                                });

                                                ya.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.cancel();
                                                        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                        pDialog.setTitleText("Harap Menunggu");
                                                        pDialog.setCancelable(false);
                                                        pDialog.show();

                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_istirahat",
                                                                new Response.Listener<String>() {

                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        pDialog.dismissWithAnimation();
                                                                        success = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                                                        success.setContentText("Data Sudah Disimpan");
                                                                        success.setCancelable(false);
                                                                        success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                sDialog.dismissWithAnimation();
                                                                                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                                                pDialog.setTitleText("Harap Menunggu");
                                                                                pDialog.setCancelable(false);
                                                                                pDialog.show();
                                                                                updateUI();
                                                                                button_persiapan.setImageDrawable(getResources().getDrawable(R.drawable.button_persiapan));
                                                                                image_mulaiperjalanan.setImageDrawable(getResources().getDrawable(R.drawable.mulai_perjalanan));
                                                                                image_istirahat.setImageDrawable(getResources().getDrawable(R.drawable.button_istirahat));
                                                                                button_selesai_perjalanan.setImageDrawable(getResources().getDrawable(R.drawable.button_selesai_perjalanan));

                                                                            }
                                                                        });
                                                                        success.show();
                                                                        pDialog.cancel();

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
                                                                String nik_baru = sharedPreferences.getString("szDocCall", null);


                                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                                                String currentDateandTime2 = sdf2.format(new Date());

                                                                params.put("iInternalId", id_istirahat);

                                                                params.put("dtmFinished", currentDateandTime2);
                                                                params.put("szUserUpdatedId", nik_baru);
                                                                params.put("dtmLastUpdated", currentDateandTime2);


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
                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(MainActivity.this);
                                                        requestQueue2.add(stringRequest2);

                                                    }
                                                });


                                        }
                                    });


                                } else {

                                    selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(), detail_selesai.class);
                                            startActivity(intent);
                                        }
                                    });

                                    persiapan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent biodata = new Intent(getBaseContext(), com.tvip.sfa.menu_persiapan.persiapan.class);
                                            startActivity(biodata);

                                        }
                                    });

                                    mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent mulaiperjalanan = new Intent(getBaseContext(), mulai_perjalanan.class);
                                            startActivity(mulaiperjalanan);
                                        }
                                    });

                                    istirahat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            {
                                                Istirahat.clear();
                                                final Dialog dialog = new Dialog(MainActivity.this);
                                                dialog.setContentView(R.layout.topup_istirahat);
                                                dialog.setCancelable(false);

                                                final AutoCompleteTextView istirahat = dialog.findViewById(R.id.editpilihistirahat);
                                                Button tidak = dialog.findViewById(R.id.tidak);
                                                Button ya = dialog.findViewById(R.id.ya);

                                                StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_JenisIstirahat",
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(response);
                                                                    if (jsonObject.getString("status").equals("true")) {
                                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                            String id = jsonObject1.getString("szId");
                                                                            String jenis_istirahat = jsonObject1.getString("szName");
                                                                            Istirahat.add(id + "-" + jenis_istirahat);

                                                                        }
                                                                    }
                                                                    istirahat.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, Istirahat));
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
                                                rest.setRetryPolicy(new DefaultRetryPolicy(
                                                        5000,
                                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                RequestQueue requestkota = Volley.newRequestQueue(MainActivity.this);
                                                requestkota.add(rest);

                                                dialog.show();

                                                istirahat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                    @Override
                                                    public void onFocusChange(View v, boolean hasFocus) {
                                                        istirahat.showDropDown();
                                                    }
                                                });
                                                istirahat.setOnTouchListener(new View.OnTouchListener() {
                                                    @Override
                                                    public boolean onTouch(View v, MotionEvent event) {
                                                        istirahat.showDropDown();
                                                        return false;
                                                    }
                                                });

                                                tidak.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                ya.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        if (istirahat.getText().toString().length() == 0) {
                                                            istirahat.setError("Pilih Jenis Istirahat");
                                                        } else if(longitude == null || longitude.equals("0.0")){
                                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                                    .setTitleText("Lokasi belum ditemukan")
                                                                    .setConfirmText("OK")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                            sDialog.dismissWithAnimation();
                                                                        }
                                                                    })
                                                                    .show();
                                                        } else {
                                                            dialog.cancel();
                                                            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                            pDialog.setTitleText("Harap Menunggu");
                                                            pDialog.setCancelable(false);
                                                            pDialog.show();

                                                            sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                            String nik_baru = sharedPreferences.getString("szDocCall", null);
                                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru=" + nik_baru,
                                                                    new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {

                                                                            try {
                                                                                JSONObject obj = new JSONObject(response);
                                                                                final JSONArray movieArray = obj.getJSONArray("data");
                                                                                for (int i = 0; i < movieArray.length(); i++) {
                                                                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                                                                    postIstirahat(movieObject.getString("szDocId"), istirahat.getText().toString());

                                                                                }


                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();

                                                                            }
                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            pDialog.dismissWithAnimation();
                                                                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                                    .setContentText("Surat Tugas Belum Ada")
                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                        @Override
                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                            sDialog.dismissWithAnimation();
                                                                                        }
                                                                                    })
                                                                                    .show();
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

                                                            stringRequest.setRetryPolicy(
                                                                    new DefaultRetryPolicy(
                                                                            5000,
                                                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                                    ));
                                                            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                                            requestQueue.add(stringRequest);
                                                        }

                                                    }
                                                });


                                            }


                                        }
                                    });
                                }
                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
                        selesai_perjalanan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), detail_selesai.class);
                                startActivity(intent);
                            }
                        });

                        persiapan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent biodata = new Intent(getBaseContext(), com.tvip.sfa.menu_persiapan.persiapan.class);
                                startActivity(biodata);

                            }
                        });

                        mulaiperjalanan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent mulaiperjalanan = new Intent(getBaseContext(), mulai_perjalanan.class);
                                startActivity(mulaiperjalanan);
                            }
                        });

                        istirahat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                {
                                    Istirahat.clear();
                                    final Dialog dialog = new Dialog(MainActivity.this);
                                    dialog.setContentView(R.layout.topup_istirahat);


                                    final AutoCompleteTextView istirahat = dialog.findViewById(R.id.editpilihistirahat);
                                    Button tidak = dialog.findViewById(R.id.tidak);
                                    Button ya = dialog.findViewById(R.id.ya);

                                    StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_JenisIstirahat",
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        if (jsonObject.getString("status").equals("true")) {
                                                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                String id = jsonObject1.getString("szId");
                                                                String jenis_istirahat = jsonObject1.getString("szName");
                                                                Istirahat.add(id + "-" + jenis_istirahat);

                                                            }
                                                        }
                                                        istirahat.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, Istirahat));
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
                                    rest.setRetryPolicy(new DefaultRetryPolicy(
                                            5000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    RequestQueue requestkota = Volley.newRequestQueue(MainActivity.this);
                                    requestkota.add(rest);

                                    dialog.show();

                                    istirahat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View v, boolean hasFocus) {
                                            istirahat.showDropDown();
                                        }
                                    });
                                    istirahat.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            istirahat.showDropDown();
                                            return false;
                                        }
                                    });

                                    tidak.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    ya.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (istirahat.getText().toString().length() == 0) {
                                                istirahat.setError("Pilih Jenis Istirahat");
                                            } else if(longitude == null || longitude.equals("0.0")){
                                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Lokasi belum ditemukan")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                sDialog.dismissWithAnimation();
                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                dialog.cancel();
                                                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                pDialog.setTitleText("Harap Menunggu");
                                                pDialog.setCancelable(false);
                                                pDialog.show();

                                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                String nik_baru = sharedPreferences.getString("szDocCall", null);
                                                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru=" + nik_baru,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {

                                                                try {
                                                                    JSONObject obj = new JSONObject(response);
                                                                    final JSONArray movieArray = obj.getJSONArray("data");
                                                                    for (int i = 0; i < movieArray.length(); i++) {
                                                                        final JSONObject movieObject = movieArray.getJSONObject(i);

                                                                        postIstirahat(movieObject.getString("szDocId"), istirahat.getText().toString());

                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();

                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                pDialog.dismissWithAnimation();
                                                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                        .setContentText("Surat Tugas Belum Ada")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                sDialog.dismissWithAnimation();
                                                                            }
                                                                        })
                                                                        .show();
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

                                                stringRequest.setRetryPolicy(
                                                        new DefaultRetryPolicy(
                                                                5000,
                                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                        ));
                                                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                                requestQueue.add(stringRequest);
                                            }

                                        }
                                    });


                                }


                            }
                        });

                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    private void updateDraft(String szDocSO) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToApplied",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("szDocId", szDocSO);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(MainActivity.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.deleteCache(getApplicationContext());
       // clearApplicationData();

    }

    public void clearApplicationData()
    {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir)
    {
        if (dir != null & dir.isDirectory()) {
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            boolean success = deleteDir(new File(dir, children[i]));
            if (!success) {
                return false;
            } else {
                System.out.println("Success");

            }
        }
    }
        return dir.delete();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed to get the Token");
                }

                //Token
                String token = task.getResult();
                updateDeviceId(token);
                Log.d(TAG, "onComplete: " + token);
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Receve Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_STORAGE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                downloadApk();
//            } else {
//                Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    private void downloadApk() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Download?nama_aplikasi="+ "SFA Preseller",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                if(!movieObject.getString("versi").equals(BuildConfig.VERSION_NAME)){
                                    nama_file =movieObject.getString("nama_file");
                                    SweetAlertDialog updateaplikasi = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                                    updateaplikasi.setTitleText("Update Version " + movieObject.getString("versi"));
                                    updateaplikasi.setContentText("Versi baru sekarang telah tersedia. Silahkan update versi anda terlebih dahulu.");
                                    updateaplikasi.setCancelable(false);
                                    updateaplikasi.setConfirmText("Download");
                                    updateaplikasi.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                            pDialog.setTitleText("Harap Menunggu");
                                            pDialog.setCancelable(false);
                                            pDialog.show();

                                            DownloadManager.Request request = null;
                                            try {
                                                request = new DownloadManager.Request(Uri.parse(movieObject.getString("link_download")));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                            request.setTitle("Your App Title");
                                            request.setDescription("Downloading APK");
                                            try {
                                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, movieObject.getString("nama_file"));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                            request.setAllowedOverMetered(true);
                                            request.setAllowedOverRoaming(true);

                                            // Set Basic Authentication header
                                            try {
                                                String auth = "Basic " + Base64.encodeToString(basicAuthCredentials.getBytes("UTF-8"), Base64.NO_WRAP);
                                                request.addRequestHeader("Authorization", auth);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                            downloadID = downloadManager.enqueue(request);
                                        }
                                    });
                                    updateaplikasi.show();

                                } else {

                                }



                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DownloadImmediately();
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    private void DownloadImmediately() {
        {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Download_force?nama_aplikasi="+ "SFA Preseller",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject obj = new JSONObject(response);
                                final JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {
                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                    if(!movieObject.getString("versi").equals(BuildConfig.VERSION_NAME)){
                                        nama_file =movieObject.getString("nama_file");
                                        SweetAlertDialog updateaplikasi = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                                        updateaplikasi.setTitleText("Update Version " + movieObject.getString("versi"));
                                        updateaplikasi.setContentText("Batas update version telah habis. Harap hubungi ICT.");
                                        updateaplikasi.setCancelable(false);
                                        updateaplikasi.setConfirmText("Keluar");
                                        updateaplikasi.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                finish();
                                                finishAffinity();
                                            }
                                        });
                                        updateaplikasi.show();

                                    } else {

                                    }



                                }


                            } catch(JSONException e){
                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    String creds = String.format("%s:%s", "admin", "Databa53");
                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                    params.put("Authorization", auth);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    ));
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        }
    }

    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nama_file);
        Uri apkUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", apkFile);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }





}