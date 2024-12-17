package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_utama.MainActivity.latitude;
import static com.tvip.sfa.menu_utama.MainActivity.longitude;

import androidx.appcompat.app.AppCompatActivity;

import com.tvip.sfa.Perangkat.GPSTracker;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_persiapan.outlet_kritis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class mulai_perjalanan extends AppCompatActivity {
    MaterialCardView dalamrute, luarrute, summarykunjungan, laporanpenjualan, pelangganbaru, outletkritis;
    static String pelanggan;
    SharedPreferences sharedPreferences;
    public static String id_pelanggan;
    SweetAlertDialog pDialog;
    ArrayList<String> GagalCheckin = new ArrayList<>();
    static String lastitem;
    static String outofroute;
    static String noSOInduk;
    int nomor;
    String customer, szDocSO;

    String alasan;

    int radius;
    GPSTracker gpsTracker;

    private Handler mRepeatHandler;
    private Runnable mRepeatRunnable;
    private final static int UPDATE_INTERVAL = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_mulai_perjalanan);

        mRepeatHandler = new Handler();
        mRepeatRunnable = new Runnable() {
            @Override
            public void run() {
                getLocation();

                mRepeatHandler.postDelayed(mRepeatRunnable, UPDATE_INTERVAL);
            }};

        mRepeatHandler.postDelayed(mRepeatRunnable, UPDATE_INTERVAL);


        dalamrute = findViewById(R.id.dalamrute);
        luarrute = findViewById(R.id.luarrute);
        summarykunjungan = findViewById(R.id.summarykunjungan);
        laporanpenjualan = findViewById(R.id.laporanpenjualan);
        pelangganbaru = findViewById(R.id.pelangganbaru);
        outletkritis = findViewById(R.id.outletkritis);

        pDialog = new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        outletkritis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), outlet_kritis.class);
                startActivity(intent);
            }
        });
        pelangganbaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pelanggan_baru_outlet.class);
                startActivity(intent);
            }
        });

        laporanpenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), laporan_penjualan.class);
                startActivity(intent);
            }
        });

        summarykunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), summary_penjualan.class);
                startActivity(intent);
            }
        });

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

                                id_pelanggan = movieObject.getString("szDocId");
                                getLastData(id_pelanggan);


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
        RequestQueue requestQueue = Volley.newRequestQueue(mulai_perjalanan.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

        luarrute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outofroute = "1";
                if(longitude == null || longitude.equals("0.0")){
                    new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
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
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDateandTime2 = sdf2.format(new Date());

                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);
                    String[] parts = nik_baru.split("-");
                    String restnomor = parts[0];



                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_NoSoInduk?tgl="+currentDateandTime2+"&depo=" + restnomor,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        final JSONArray movieArray = obj.getJSONArray("data");
                                        for (int i = 0; i < movieArray.length(); i++) {
                                            final JSONObject movieObject = movieArray.getJSONObject(i);

                                            noSOInduk = movieObject.getString("iniso_induk");

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
                    RequestQueue requestQueue = Volley.newRequestQueue(mulai_perjalanan.this);
                    requestQueue.getCache().clear();
                    requestQueue.add(stringRequest);

                    pelanggan = "luar";


                    IntentIntegrator intentIntegrator = new IntentIntegrator(mulai_perjalanan.this);
                    intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                    intentIntegrator.initiateScan();

                }


                final Dialog dialog = new Dialog(mulai_perjalanan.this);
                dialog.setContentView(R.layout.pilih_metode);
                dialog.setCancelable(false);


                Button manual = dialog.findViewById(R.id.manual);
                Button scan = dialog.findViewById(R.id.scan);

//                manual.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//
//                        Intent dalam_rute = new Intent(getBaseContext(), scangagal_luarrute.class);
//                        startActivity(dalam_rute);
//                    }
//                });

                scan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        pelanggan = "luar";

                        IntentIntegrator intentIntegrator = new IntentIntegrator(mulai_perjalanan.this);
                        intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                        intentIntegrator.initiateScan();
                    }
                });
//                dialog.show();

            }
        });


        dalamrute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outofroute = "0";
                if(longitude == null || longitude.equals("0.0")){
                    new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
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
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDateandTime2 = sdf2.format(new Date());

                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);
                    String[] parts = nik_baru.split("-");
                    String restnomor = parts[0];

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_NoSoInduk?tgl="+currentDateandTime2+"&depo=" + restnomor,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        final JSONArray movieArray = obj.getJSONArray("data");
                                        for (int i = 0; i < movieArray.length(); i++) {
                                            final JSONObject movieObject = movieArray.getJSONObject(i);

                                            noSOInduk = movieObject.getString("iniso_induk");

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
                    RequestQueue requestQueue = Volley.newRequestQueue(mulai_perjalanan.this);
                    requestQueue.getCache().clear();
                    requestQueue.add(stringRequest);

                    pelanggan = "dalam";
                    IntentIntegrator intentIntegrator = new IntentIntegrator(mulai_perjalanan.this);
                    intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                    intentIntegrator.initiateScan();
                }

            }
        });
    }

    private void getLastData(String id_pelanggan) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LastItem?szDocId=" + id_pelanggan
                ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);


                                int item = Integer.parseInt(movieObject.getString("intItemNumber")) + 1;
                                pDialog.dismissWithAnimation();

                                lastitem = String.valueOf(item);

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
        RequestQueue requestQueue = Volley.newRequestQueue(mulai_perjalanan.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                if(mulai_perjalanan.pelanggan.equals("dalam")){
                    Intent intent = new Intent(mulai_perjalanan.this, scangagal_dalamrute.class); // Replace with your target activity
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mulai_perjalanan.this, scangagal_luarrute.class); // Replace with your target activity
                    startActivity(intent);
                }
            } else {
                try {
                    JSONObject object = new JSONObject(result.getContents());
                    System.out.println(object.getString("nik"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    customer = result.getContents();
                    pDialog = new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    if (pelanggan.equals("dalam")) {
                        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Comparing_Pelanggan?surat_tugas="+mulai_perjalanan.id_pelanggan+"&szCustomerId=" + result.getContents(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        getData();
                                    }

                                    private void getData() {
                                        StringRequest rest2 = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Scan_Dalam_Pelanggan?szCustomerId=" + result.getContents() + "&szDocId=" + id_pelanggan,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            if (jsonObject.getString("status").equals("true")) {
                                                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                for (int i = 0; i < 1; i++) {
                                                                    pDialog.dismissWithAnimation();
                                                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                    String szRefDocId = jsonObject1.getString("szRefDocId");
                                                                    String intItemNumber = jsonObject1.getString("intItemNumber");
                                                                    String mulai = jsonObject1.getString("bStarted");

                                                                    if(jsonObject1.getString("bPostPone").equals("1")){
                                                                        String idcustomer = result.getContents();
                                                                        Intent ok = new Intent(getBaseContext(), menu_pelanggan.class);
                                                                        ok.putExtra("kode", idcustomer);
                                                                        startActivity(ok);
                                                                    } else if(jsonObject1.getString("bFinisihed").equals("1")){
                                                                        new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                                .setTitleText("Toko Sudah Selesai Dikunjungi")
                                                                                .setConfirmText("OK")
                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                    @Override
                                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                                        sDialog.dismissWithAnimation();
                                                                                        pDialog.dismissWithAnimation();
                                                                                    }
                                                                                })
                                                                                .show();
                                                                    } else if(!jsonObject1.getString("latitude_now").equals("")){
                                                                        Location startPoint=new Location("locationA");
                                                                        startPoint.setLatitude(Double.valueOf(latitude));
                                                                        startPoint.setLongitude(Double.valueOf(longitude));
//
                                                                        Location endPoint=new Location("locationA");
                                                                        endPoint.setLatitude(Double.valueOf(jsonObject1.getString("latitude_now")));
                                                                        endPoint.setLongitude(Double.valueOf(jsonObject1.getString("longitude_now")));
                                                                        double distance2 = startPoint.distanceTo(endPoint);
                                                                        int value = (int) distance2;
                                                                        System.out.println("Radius = " + value);


                                                                        if(value > 30) {
                                                                            final Dialog dialog = new Dialog(mulai_perjalanan.this);
                                                                            dialog.setContentView(R.layout.pilih_checkin);
                                                                            dialog.setCancelable(false);
                                                                            GagalCheckin.clear();

                                                                            AutoCompleteTextView pilihalasan = dialog.findViewById(R.id.pilihalasan);
                                                                            Button batal = dialog.findViewById(R.id.batal);
                                                                            Button ok = dialog.findViewById(R.id.ok);

                                                                            StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Check_In_Failed",
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
                                                                                                        GagalCheckin.add(id + "-" + jenis_istirahat);

                                                                                                    }
                                                                                                }
                                                                                                pilihalasan.setAdapter(new ArrayAdapter<String>(mulai_perjalanan.this, android.R.layout.simple_expandable_list_item_1, GagalCheckin));
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
                                                                            RequestQueue requestkota = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                            requestkota.getCache().clear();
                                                                            requestkota.add(rest);

                                                                            batal.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    dialog.dismiss();
                                                                                    pDialog.dismissWithAnimation();
                                                                                }
                                                                            });

                                                                            ok.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    pilihalasan.setError(null);
                                                                                    if(pilihalasan.getText().toString().length() == 0){
                                                                                        pilihalasan.setError("Pilih Alasan");
                                                                                    } else {
                                                                                        alasan = pilihalasan.getText().toString();
                                                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ScanBarcode",
                                                                                                new Response.Listener<String>() {

                                                                                                    @Override
                                                                                                    public void onResponse(String response) {
                                                                                                        if(!mulai.equals("1")){
                                                                                                            postSfaDoccallItem();
                                                                                                        } else {
                                                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DoccallItemSFA",
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

                                                                                                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                                    String currentDateandTime2 = sdf2.format(new Date());

                                                                                                                    String[] parts = pilihalasan.getText().toString().split("-");
                                                                                                                    String restnomor = parts[0];

                                                                                                                    params.put("szDocId", id_pelanggan);
                                                                                                                    params.put("szCustomerId", String.valueOf(result.getContents()));

                                                                                                                    params.put("szFailReason", restnomor);

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
                                                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                                                            requestQueue2.getCache().clear();
                                                                                                            requestQueue2.add(stringRequest2);
                                                                                                        }


                                                                                                    }

                                                                                                    private void postSfaDoccallItem() {
                                                                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                                                                                                new Response.Listener<String>() {

                                                                                                                    @Override
                                                                                                                    public void onResponse(String response) {
//                        DocSOMDBA(s);
                                                                                                                    }
                                                                                                                }, new Response.ErrorListener() {
                                                                                                            @Override
                                                                                                            public void onErrorResponse(VolleyError error) {
                                                                                                                NetworkResponse response = error.networkResponse;
                                                                                                                if(response != null && response.data != null){

                                                                                                                }else{
                                                                                                                    String errorMessage=error.getClass().getSimpleName();
                                                                                                                    if(!TextUtils.isEmpty(errorMessage)){

                                                                                                                    }
                                                                                                                }
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
                                                                                                                String[] parts = pilihalasan.getText().toString().split("-");
                                                                                                                String restnomor = parts[0];

                                                                                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                                String currentDateandTime2 = sdf2.format(new Date());

                                                                                                                params.put("szDocId", id_pelanggan);
                                                                                                                params.put("intItemNumber", intItemNumber);

                                                                                                                params.put("szCustomerId", result.getContents());

                                                                                                                params.put("dtmStart", currentDateandTime2);
                                                                                                                params.put("dtmFinish", currentDateandTime2);

                                                                                                                params.put("bVisited", "1");
                                                                                                                params.put("bSuccess", "0");


                                                                                                                params.put("szFailReason", "");
                                                                                                                params.put("szLangitude", latitude);
                                                                                                                params.put("szLongitude", longitude);
                                                                                                                params.put("bOutOfRoute", outofroute);
                                                                                                                params.put("szRefDocId", szRefDocId);

                                                                                                                params.put("bScanBarcode", "1");

                                                                                                                params.put("szReasonIdCheckin", restnomor);
                                                                                                                params.put("szReasonFailedScan", "");
                                                                                                                params.put("decRadiusDiff", String.valueOf(value));








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
                                                                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                                                        requestQueue2.getCache().clear();
                                                                                                        requestQueue2.add(stringRequest2);
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

                                                                                                String[] parts = pilihalasan.getText().toString().split("-");
                                                                                                String restnomor = parts[0];

                                                                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                String currentDateandTime2 = sdf2.format(new Date());

                                                                                                params.put("szDocId", id_pelanggan);
                                                                                                params.put("szCustomerId", result.getContents());
                                                                                                params.put("dtmStart", currentDateandTime2);

                                                                                                params.put("szLongitude", longitude);
                                                                                                params.put("szLatitude", latitude);
                                                                                                params.put("szReasonFailedScan", restnomor);




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
                                                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                                        requestQueue2.getCache().clear();
                                                                                        requestQueue2.add(stringRequest2);
                                                                                        String idcustomer = result.getContents();
                                                                                        Intent ok = new Intent(getBaseContext(), menu_pelanggan.class);
                                                                                        ok.putExtra("kode", idcustomer);
                                                                                        startActivity(ok);
                                                                                    }

                                                                                }
                                                                            });

                                                                            dialog.show();

                                                                        } else if(jsonObject1.getString("bFinisihed").equals("1") && jsonObject1.getString("bSuccess").equals("1")){
                                                                            new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                                    .setTitleText("Toko Sudah Selesai Dikunjungi")
                                                                                    .setConfirmText("OK")
                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                        @Override
                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                            sDialog.dismissWithAnimation();
                                                                                            pDialog.dismissWithAnimation();
                                                                                        }
                                                                                    })

                                                                                    .show();
                                                                        } else {
                                                                            String idcustomer = jsonObject1.getString("szCustomerId");
                                                                            Intent ok = new Intent(getBaseContext(), menu_pelanggan.class);
                                                                            ok.putExtra("kode", idcustomer);
                                                                            startActivity(ok);
                                                                            updatebScanBarcode(idcustomer);

                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                                                                    new Response.Listener<String>() {

                                                                                        @Override
                                                                                        public void onResponse(String response) {
//                        DocSOMDBA(s);
                                                                                        }
                                                                                    }, new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    NetworkResponse response = error.networkResponse;
                                                                                    if(response != null && response.data != null){

                                                                                    }else{
                                                                                        String errorMessage=error.getClass().getSimpleName();
                                                                                        if(!TextUtils.isEmpty(errorMessage)){

                                                                                        }
                                                                                    }
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


                                                                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                    String currentDateandTime2 = sdf2.format(new Date());

                                                                                    params.put("szDocId", id_pelanggan);
                                                                                    params.put("intItemNumber", intItemNumber);

                                                                                    params.put("szCustomerId", result.getContents());

                                                                                    params.put("dtmStart", currentDateandTime2);
                                                                                    params.put("dtmFinish", currentDateandTime2);

                                                                                    params.put("bVisited", "1");
                                                                                    params.put("bSuccess", "0");


                                                                                    params.put("szFailReason", "");
                                                                                    params.put("szLangitude", latitude);
                                                                                    params.put("szLongitude", longitude);
                                                                                    params.put("bOutOfRoute", outofroute);
                                                                                    params.put("szRefDocId", szRefDocId);

                                                                                    params.put("bScanBarcode", "1");

                                                                                    params.put("szReasonIdCheckin", "");
                                                                                    params.put("szReasonFailedScan", "");
                                                                                    params.put("decRadiusDiff", String.valueOf(value));








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
                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                            requestQueue2.getCache().clear();
                                                                            requestQueue2.add(stringRequest2);

                                                                        }

                                                                    } else {
                                                                        new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                                .setTitleText("Toko Tidak Ada Longlat")
                                                                                .setConfirmText("OK")
                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                    @Override
                                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                                        sDialog.dismissWithAnimation();
                                                                                    }
                                                                                })
                                                                                .show();
                                                                    }
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
                                                        StringRequest rest2 = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Customer?szId=" + result.getContents(),
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        pDialog.dismissWithAnimation();
                                                                        new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                                .setTitleText("Maaf, Toko sudah ada di luar rute")
                                                                                .setConfirmText("OK")
                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                    @Override
                                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                                        sDialog.dismissWithAnimation();
                                                                                    }
                                                                                })

                                                                                .show();

                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                            pDialog.dismissWithAnimation();
                                                                            Intent dalam_rute = new Intent(getBaseContext(), scangagal_dalamrute.class);
                                                                            startActivity(dalam_rute);

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
                                                        rest2.setRetryPolicy(new DefaultRetryPolicy(
                                                                5000,
                                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                        RequestQueue requestkota2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                        requestkota2.getCache().clear();
                                                        requestkota2.add(rest2);

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
                                        rest2.setRetryPolicy(new DefaultRetryPolicy(
                                                5000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        RequestQueue requestkota2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                        requestkota2.getCache().clear();
                                        requestkota2.add(rest2);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (error instanceof TimeoutError){
                                            new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Maaf, Jaringan sedang bermasalah")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            pDialog.dismissWithAnimation();
                                                        }
                                                    })
                                                    .show();


                                        } else {
                                            StringRequest rest2 = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Customer?szId=" + result.getContents(),
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            pDialog.dismissWithAnimation();
                                                            new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                    .setTitleText("Maaf, Toko sudah ada di luar rute")
                                                                    .setConfirmText("OK")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                            sDialog.dismissWithAnimation();
                                                                        }
                                                                    })

                                                                    .show();

                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            pDialog.dismissWithAnimation();
                                                            Intent dalam_rute = new Intent(getBaseContext(), scangagal_dalamrute.class);
                                                            startActivity(dalam_rute);

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
                                            rest2.setRetryPolicy(new DefaultRetryPolicy(
                                                    5000,
                                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                            RequestQueue requestkota2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                            requestkota2.getCache().clear();
                                            requestkota2.add(rest2);
                                        }

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
                                160000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueue requestkota = Volley.newRequestQueue(mulai_perjalanan.this);
                        requestkota.getCache().clear();
                        requestkota.add(rest);
                    } else {
                        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Scan_Dalam_Pelanggan?szCustomerId=" + result.getContents() + "&szDocId=" + id_pelanggan,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        pDialog.dismissWithAnimation();
                                        new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Toko Berada Di Dalam Rute")
                                                .setConfirmText("OK")
                                                .show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (error instanceof TimeoutError) {
                                            new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Maaf, Jaringan sedang bermasalah")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            pDialog.dismissWithAnimation();
                                                        }
                                                    })
                                                    .show();


                                        } else {
                                            sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                            String nik_baru = sharedPreferences.getString("szDocCall", null);
                                            StringRequest detail_pelanggan = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Luar_Pelanggan_detail?surat_tugas="+id_pelanggan+"&szId="+nik_baru+"&szCustomerId="+result.getContents(),
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {

                                                            try {
                                                                JSONObject obj = new JSONObject(response);
                                                                final JSONArray movieArray = obj.getJSONArray("data");
                                                                JSONObject biodatas = null;
                                                                for (int i = 0; i < movieArray.length(); i++) {

                                                                    biodatas = movieArray.getJSONObject(i);

                                                                    if(biodatas.getString("bPostPone").equals("1")){
                                                                        String idcustomer = result.getContents();
                                                                        Intent ok = new Intent(getBaseContext(), menu_pelanggan.class);
                                                                        ok.putExtra("kode", idcustomer);
                                                                        startActivity(ok);
                                                                    } else if(biodatas.getString("bFinisihed").equals("1")){
                                                                        pDialog.dismissWithAnimation();
                                                                        new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                                .setTitleText("Toko Sudah Selesai Dikunjungi")
                                                                                .setConfirmText("OK")
                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                    @Override
                                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                                        sDialog.dismissWithAnimation();
                                                                                        pDialog.dismissWithAnimation();
                                                                                    }
                                                                                })
                                                                                .show();

                                                                    } else if (biodatas.getString("szLatitude").equals("")) {
                                                                        pDialog.dismissWithAnimation();
                                                                        new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                                .setTitleText("Toko Tidak Ada Longlat")
                                                                                .setConfirmText("OK")
                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                    @Override
                                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                                        sDialog.dismissWithAnimation();
                                                                                    }
                                                                                })
                                                                                .show();
                                                                    } else {
                                                                        if(biodatas.getString("bStarted").equals("null")){

                                                                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                            String currentDateandTime2 = sdf2.format(new Date());
                                                                            getJumlah(result.getContents(), currentDateandTime2, currentDateandTime2, currentDateandTime2, "");
                                                                        }

                                                                        Location startPoint = new Location("locationA");
                                                                        startPoint.setLatitude(Double.valueOf(latitude));
                                                                        startPoint.setLongitude(Double.valueOf(longitude));
//
                                                                        Location endPoint = new Location("locationA");
                                                                        endPoint.setLatitude(Double.valueOf(biodatas.getString("szLatitude")));
                                                                        endPoint.setLongitude(Double.valueOf(biodatas.getString("szLongitude")));
                                                                        double distance2 = startPoint.distanceTo(endPoint);
                                                                        int value = (int) distance2;

                                                                        radius = (int) distance2;



                                                                        System.out.println("Radius = " + value);

                                                                        if (value > 30) {
                                                                            final Dialog dialog = new Dialog(mulai_perjalanan.this);
                                                                            dialog.setContentView(R.layout.pilih_checkin);
                                                                            dialog.setCancelable(false);
                                                                            GagalCheckin.clear();

                                                                            AutoCompleteTextView pilihalasan = dialog.findViewById(R.id.pilihalasan);
                                                                            Button batal = dialog.findViewById(R.id.batal);
                                                                            Button ok = dialog.findViewById(R.id.ok);

                                                                            StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Check_In_Failed",
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
                                                                                                        GagalCheckin.add(id + "-" + jenis_istirahat);

                                                                                                    }
                                                                                                }
                                                                                                pilihalasan.setAdapter(new ArrayAdapter<String>(mulai_perjalanan.this, android.R.layout.simple_expandable_list_item_1, GagalCheckin));
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
                                                                            RequestQueue requestkota = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                            requestkota.add(rest);

                                                                            batal.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    dialog.dismiss();
                                                                                    pDialog.dismissWithAnimation();
                                                                                }
                                                                            });

                                                                            ok.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    if(pilihalasan.getText().toString().length() == 0){
                                                                                        pilihalasan.setError("Pilih Alasan");
                                                                                    } else {
                                                                                        alasan = pilihalasan.getText().toString();
                                                                                        pDialog = new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                                                        pDialog.setTitleText("Harap Menunggu");
                                                                                        pDialog.setCancelable(false);
                                                                                        pDialog.show();

                                                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                                                                                new Response.Listener<String>() {

                                                                                                    @Override
                                                                                                    public void onResponse(String response) {
//                        DocSOMDBA(s);
                                                                                                    }
                                                                                                }, new Response.ErrorListener() {
                                                                                            @Override
                                                                                            public void onErrorResponse(VolleyError error) {
                                                                                                NetworkResponse response = error.networkResponse;
                                                                                                if (response != null && response.data != null) {

                                                                                                } else {
                                                                                                    String errorMessage = error.getClass().getSimpleName();
                                                                                                    if (!TextUtils.isEmpty(errorMessage)) {

                                                                                                    }
                                                                                                }
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
                                                                                                String[] parts = pilihalasan.getText().toString().split("-");
                                                                                                String restnomor = parts[0];

                                                                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                String currentDateandTime2 = sdf2.format(new Date());

                                                                                                params.put("szDocId", id_pelanggan);
                                                                                                params.put("intItemNumber", lastitem);

                                                                                                params.put("szCustomerId", result.getContents());

                                                                                                params.put("dtmStart", currentDateandTime2);
                                                                                                params.put("dtmFinish", currentDateandTime2);

                                                                                                params.put("bVisited", "1");
                                                                                                params.put("bSuccess", "0");


                                                                                                params.put("szFailReason", "");
                                                                                                params.put("szLangitude", latitude);
                                                                                                params.put("szLongitude", longitude);
                                                                                                params.put("bOutOfRoute", outofroute);
                                                                                                params.put("szRefDocId", "");

                                                                                                params.put("bScanBarcode", "1");

                                                                                                params.put("szReasonIdCheckin", restnomor);
                                                                                                params.put("szReasonFailedScan", "");
                                                                                                params.put("decRadiusDiff", String.valueOf(value));


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
                                                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                                        requestQueue2.add(stringRequest2);

                                                                                        new Handler().postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                String idcustomer = result.getContents();
                                                                                                Intent ok = new Intent(getBaseContext(), menu_pelanggan.class);
                                                                                                ok.putExtra("kode", idcustomer);
                                                                                                startActivity(ok);

                                                                                                StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ScanBarcode",
                                                                                                        new Response.Listener<String>() {

                                                                                                            @Override
                                                                                                            public void onResponse(String response) {

                                                                                                                getDetail();

                                                                                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                                String currentDateandTime2 = sdf2.format(new Date());
                                                                                                                getJumlah(result.getContents(), currentDateandTime2, currentDateandTime2, currentDateandTime2, "");

                                                                                                            }

                                                                                                            private void getDetail() {
                                                                                                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                                                                                String nik_baru = sharedPreferences.getString("szDocCall", null);
                                                                                                                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Luar_Pelanggan_detail?surat_tugas=" + id_pelanggan + "&szId=" + nik_baru + "&szCustomerId=" + result.getContents(),
                                                                                                                        new Response.Listener<String>() {
                                                                                                                            @Override
                                                                                                                            public void onResponse(String response) {

                                                                                                                                try {
                                                                                                                                    JSONObject obj = new JSONObject(response);
                                                                                                                                    final JSONArray movieArray = obj.getJSONArray("data");
                                                                                                                                    for (int i = 0; i < movieArray.length(); i++) {
                                                                                                                                        final JSONObject movieObject = movieArray.getJSONObject(i);

                                                                                                                                        if (movieObject.getString("bStarted").equals("null")) {
                                                                                                                                            postSfaDoccallItem();
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
                                                                                                                RequestQueue requestQueue = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                                                                requestQueue.add(stringRequest);

                                                                                                            }

                                                                                                            private void postSfaDoccallItem() {
                                                                                                                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                                                                                                        new Response.Listener<String>() {

                                                                                                                            @Override
                                                                                                                            public void onResponse(String response) {
//                        DocSOMDBA(s);
                                                                                                                            }
                                                                                                                        }, new Response.ErrorListener() {
                                                                                                                    @Override
                                                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                                                        NetworkResponse response = error.networkResponse;
                                                                                                                        if (response != null && response.data != null) {

                                                                                                                        } else {
                                                                                                                            String errorMessage = error.getClass().getSimpleName();
                                                                                                                            if (!TextUtils.isEmpty(errorMessage)) {

                                                                                                                            }
                                                                                                                        }
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
                                                                                                                        String[] parts = pilihalasan.getText().toString().split("-");
                                                                                                                        String restnomor = parts[0];

                                                                                                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                                        String currentDateandTime2 = sdf2.format(new Date());

                                                                                                                        params.put("szDocId", id_pelanggan);
                                                                                                                        params.put("intItemNumber", lastitem);

                                                                                                                        params.put("szCustomerId", result.getContents());

                                                                                                                        params.put("dtmStart", currentDateandTime2);
                                                                                                                        params.put("dtmFinish", currentDateandTime2);

                                                                                                                        params.put("bVisited", "1");
                                                                                                                        params.put("bSuccess", "0");


                                                                                                                        params.put("szFailReason", "");
                                                                                                                        params.put("szLangitude", latitude);
                                                                                                                        params.put("szLongitude", longitude);
                                                                                                                        params.put("bOutOfRoute", outofroute);
                                                                                                                        params.put("szRefDocId", "");

                                                                                                                        params.put("bScanBarcode", "1");

                                                                                                                        params.put("szReasonIdCheckin", restnomor);
                                                                                                                        params.put("szReasonFailedScan", "");
                                                                                                                        params.put("decRadiusDiff", String.valueOf(value));


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
                                                                                                                RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                                                                requestQueue2.add(stringRequest2);
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

                                                                                                        String[] parts = pilihalasan.getText().toString().split("-");
                                                                                                        String restnomor = parts[0];

                                                                                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                        String currentDateandTime2 = sdf2.format(new Date());

                                                                                                        params.put("szDocId", id_pelanggan);
                                                                                                        params.put("szCustomerId", result.getContents());
                                                                                                        params.put("dtmStart", currentDateandTime2);

                                                                                                        params.put("szLongitude", longitude);
                                                                                                        params.put("szLatitude", latitude);
                                                                                                        params.put("szReasonFailedScan", restnomor);


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
                                                                                                RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                                                requestQueue2.add(stringRequest2);
                                                                                            }
                                                                                        }, 7000);
                                                                                    }


                                                                                }
                                                                            });

                                                                            dialog.show();

                                                                        } else {
                                                                            String idcustomer = result.getContents();
                                                                            Intent ok = new Intent(getBaseContext(), menu_pelanggan.class);
                                                                            ok.putExtra("kode", idcustomer);
                                                                            startActivity(ok);
                                                                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                            String currentDateandTime2 = sdf2.format(new Date());
                                                                            getJumlah(result.getContents(), currentDateandTime2, currentDateandTime2, currentDateandTime2, "");

                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                                                                    new Response.Listener<String>() {

                                                                                        @Override
                                                                                        public void onResponse(String response) {
//                        DocSOMDBA(s);
                                                                                        }
                                                                                    }, new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    NetworkResponse response = error.networkResponse;
                                                                                    if (response != null && response.data != null) {

                                                                                    } else {
                                                                                        String errorMessage = error.getClass().getSimpleName();
                                                                                        if (!TextUtils.isEmpty(errorMessage)) {

                                                                                        }
                                                                                    }
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

                                                                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                    String currentDateandTime2 = sdf2.format(new Date());

                                                                                    params.put("szDocId", id_pelanggan);
                                                                                    params.put("intItemNumber", lastitem);

                                                                                    params.put("szCustomerId", result.getContents());

                                                                                    params.put("dtmStart", currentDateandTime2);
                                                                                    params.put("dtmFinish", currentDateandTime2);

                                                                                    params.put("bVisited", "1");
                                                                                    params.put("bSuccess", "0");


                                                                                    params.put("szFailReason", "");
                                                                                    params.put("szLangitude", latitude);
                                                                                    params.put("szLongitude", longitude);
                                                                                    params.put("bOutOfRoute", outofroute);
                                                                                    params.put("szRefDocId", "");

                                                                                    params.put("bScanBarcode", "1");

                                                                                    params.put("szReasonIdCheckin", "");
                                                                                    params.put("szReasonFailedScan", "");
                                                                                    params.put("decRadiusDiff", String.valueOf(value));


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
                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                                                                            requestQueue2.add(stringRequest2);


                                                                        }
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
                                                            if (error instanceof TimeoutError) {
                                                                new SweetAlertDialog(mulai_perjalanan.this, SweetAlertDialog.WARNING_TYPE)
                                                                        .setTitleText("Maaf, Jaringan sedang bermasalah")
                                                                        .setConfirmText("OK")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                sDialog.dismissWithAnimation();
                                                                                pDialog.dismissWithAnimation();
                                                                            }
                                                                        })
                                                                        .show();


                                                            } else {
                                                                pDialog.dismissWithAnimation();
                                                                Intent dalam_rute = new Intent(getBaseContext(), scangagal_luarrute.class);
                                                                startActivity(dalam_rute);
                                                            }


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

                                            detail_pelanggan.setRetryPolicy(
                                                    new DefaultRetryPolicy(
                                                            5000,
                                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                    ));
                                            RequestQueue detail_pelangganQueue = Volley.newRequestQueue(mulai_perjalanan.this);
                                            detail_pelangganQueue.add(detail_pelanggan);
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

                        channel_status.setRetryPolicy(
                                new DefaultRetryPolicy(
                                        160000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                ));
                        RequestQueue channel_statusQueue = Volley.newRequestQueue(this);
                        channel_statusQueue.add(channel_status);

                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void getJumlah(String szCustomerId, String currentDateandTime2, String currentDateandTime21, String currentDateandTime22, String szRefDocId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LastData?surat_tugas=" + mulai_perjalanan.id_pelanggan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                nomor = Integer.parseInt(biodatas.getString("intItemNumber")) + 1;
                                
                                getSoldtoBranch();




                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void getSoldtoBranch() {
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SoldToBranchId?szId="+ szCustomerId,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            final JSONArray movieArray = obj.getJSONArray("data");
                                            for (int i = 0; i < movieArray.length(); i++) {
                                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                                String branchid = movieObject.getString("szSoldToBranchId");

                                                getDetail(branchid);





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
                        RequestQueue requestQueue = Volley.newRequestQueue(mulai_perjalanan.this);
                        requestQueue.add(stringRequest);
                    }

                    private void getDetail(String branchid) {
                        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + szCustomerId,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        pDialog.dismissWithAnimation();
//                                        Intent ok = new Intent(getBaseContext(), menu_pelanggan.class);
//                                        ok.putExtra("kode", szCustomerId);
//                                        startActivity(ok);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                        String nik_baru = sharedPreferences.getString("szDocCall", null);

                                        String[] parts = nik_baru.split("-");
                                        String restnomor = parts[0];

                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                        String currentDateandTime2 = sdf2.format(new Date());

                                        postDocSO(branchid, szCustomerId);


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

                        channel_status.setRetryPolicy(
                                new DefaultRetryPolicy(
                                        5000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                ));
                        RequestQueue channel_statusQueue = Volley.newRequestQueue(mulai_perjalanan.this);
                        channel_statusQueue.getCache().clear();
                        channel_statusQueue.add(channel_status);
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
        requestQueue.add(stringRequest);
    }

    private void postDocSO(String szSoldToBranchId, String szCustomerId) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoData",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            String message = jsonObj.getString("message");
                            szDocSO = message;

                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateandTime2 = sdf2.format(new Date());

                            postPelangganLuar(szCustomerId, currentDateandTime2, String.valueOf(nomor), szSoldToBranchId);

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
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

                String[] parts = nik_baru.split("-");
                String branch = parts[0];

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("szMobileId", "");

                params.put("dtmDoc", currentDateandTime2);
                params.put("szCustomerId", szCustomerId);
                params.put("szEmployeeId", nik_baru);

                params.put("bCash", "1");

                params.put("szPaymentTermId", "0HARI");

                params.put("dtmPO", currentDateandTime2);
                params.put("dtmPOExpired", currentDateandTime2);
                params.put("szBranchId", szSoldToBranchId);
                params.put("szDocStatus", "Draft");

                params.put("szUserCreatedId", nik_baru);
                params.put("dtmCreated", currentDateandTime2);
                params.put("dtmReqDlvDate", currentDateandTime2);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void postPelangganLuar(String szCustomerId, String currentDateandTime2, String szRefDocId, String valueOf) {

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_PelangganLuar",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        updateLatLong(szCustomerId);
                        postSFA();
                    }

                    private void postSFA() {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
//                        DocSOMDBA(s);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse response = error.networkResponse;
                                if(response != null && response.data != null){

                                }else{
                                    String errorMessage=error.getClass().getSimpleName();
                                    if(!TextUtils.isEmpty(errorMessage)){

                                    }
                                }
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
                                String[] parts = alasan.split("-");
                                String restnomor = parts[0];

                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateandTime2 = sdf2.format(new Date());

                                params.put("szDocId", id_pelanggan);
                                params.put("intItemNumber", lastitem);

                                params.put("szCustomerId", szCustomerId);

                                params.put("dtmStart", currentDateandTime2);
                                params.put("dtmFinish", currentDateandTime2);

                                params.put("bVisited", "1");
                                params.put("bSuccess", "0");


                                params.put("szFailReason", "");
                                params.put("szLangitude", latitude);
                                params.put("szLongitude", longitude);
                                params.put("bOutOfRoute", outofroute);
                                params.put("szRefDocId", szRefDocId);

                                params.put("bScanBarcode", "1");

                                params.put("szReasonIdCheckin", restnomor);
                                params.put("szReasonFailedScan", "");
                                params.put("decRadiusDiff", String.valueOf(radius));

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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
                        requestQueue2.getCache().clear();
                        requestQueue2.add(stringRequest2);
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

                String nik_baru = sharedPreferences.getString("szDocCall", null);

                String[] parts = nik_baru.split("-");
                String restnomor = parts[0];

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("intItemNumber", lastitem);
                params.put("szCustomerId", szCustomerId);

                params.put("dtmStart", currentDateandTime2);
                params.put("dtmFinish", currentDateandTime2);

                params.put("bScanBarcode", "1");
                params.put("dtmLastCheckin", currentDateandTime2);
                params.put("szDocSO", szDocSO);
                params.put("szRefDocId", szDocSO);


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
        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
        requestQueue2.add(stringRequest2);
    }

    private void updateLatLong(String szCustomerId) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_latlong",
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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("szCustomerId", szCustomerId);
                params.put("bVisited", "0");
                params.put("szLangitude", latitude);
                params.put("szLongitude", longitude);

                params.put("dtmStart", currentDateandTime2);




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
        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
        requestQueue2.add(stringRequest2);
    }

    private void updatebScanBarcode(String idcustomer) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ScanBarcode",
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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("szDocId", id_pelanggan);
                params.put("szCustomerId", customer);
                params.put("dtmStart", currentDateandTime2);

                params.put("szLongitude", longitude);
                params.put("szLatitude", latitude);
                params.put("szReasonFailedScan", "");



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
        RequestQueue requestQueue2 = Volley.newRequestQueue(mulai_perjalanan.this);
        requestQueue2.add(stringRequest2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Runtime.getRuntime().gc();
    }

    private void getLocation() {
        gpsTracker = new GPSTracker(mulai_perjalanan.this);

        if(gpsTracker.canGetLocation()){
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

            System.out.println("Lokasi Longlat = " + latitude + ", " + longitude);
        }else{
            gpsTracker.showSettingsAlert();

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRepeatHandler.removeCallbacks(mRepeatRunnable);
    }
}