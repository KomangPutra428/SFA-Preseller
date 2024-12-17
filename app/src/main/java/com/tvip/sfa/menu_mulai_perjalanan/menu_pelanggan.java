package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_utama.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menu_pelanggan extends AppCompatActivity {
    LinearLayout info, penjualan, feedback, kompetitor, promo, survey, pengajuankredit, infopengiriman;
    public static String no_surat;
    static MaterialToolbar pengaturanBar;
    Button kunjungan;
    static String noSO;
    String kondisi;
    SharedPreferences sharedPreferences;
    static String tax;
    ArrayList<String> failed = new ArrayList<>();
    SweetAlertDialog pDialog;
    private static final byte[] reserve = new byte[1024 * 1024];
    private RequestQueue requestQueue, requestQueue3,
            requestQueue2, requestQueue4, requestQueue5, requestQueue6,
            requestQueue7, requestQueue8, requestQueue9, requestQueue10;

    static String failreason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_menu_pelanggan);
        getCacheDir().delete();

        infopengiriman = findViewById(R.id.infopengiriman);
        info = findViewById(R.id.info);
        penjualan = findViewById(R.id.penjualan);
        feedback = findViewById(R.id.feedback);
        kompetitor = findViewById(R.id.kompetitor);
        promo = findViewById(R.id.promo);
        survey = findViewById(R.id.survey);
        pengajuankredit = findViewById(R.id.pengajuankredit);

        pengaturanBar = findViewById(R.id.pengaturanBar);
        kunjungan = findViewById(R.id.kunjungan);


        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME",MODE_PRIVATE).edit();
        editor.putString("nosurat", "no_surat");
        editor.apply();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String name= sharedPreferences.getString("nosurat", getIntent().getStringExtra("kode"));

        no_surat = name;

        boolean isOutOfMemory = false;
        try {

        }
        catch (OutOfMemoryError ex) {
            System.gc();
            isOutOfMemory = true;
        }


        pengajuankredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), pengajuan_kredit.class);
                startActivity(intent);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), menu_info.class);
                startActivity(intent);

            }
        });

        penjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noSO == null){
                    new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Dokumen Tidak Ditemukan")
                            .setConfirmText("OK")
                            .show();
                } else {
                    Intent intent = new Intent(getBaseContext(), product_penjualan.class);
                    startActivity(intent);
                }

            }
        });

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), posm.class);
                startActivity(intent);
            }
        });

        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), survey_input.class);
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), feedback.class);
                startActivity(intent);
            }
        });

        kompetitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), kompetitor.class);
                startActivity(intent);
            }
        });

        if(kondisi == null){
            StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Scan_Dalam_Pelanggan?szCustomerId=" + no_surat  + "&szDocId=" + mulai_perjalanan.id_pelanggan,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equals("true")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        if(jsonObject1.getString("bOutOfRoute").equals("1")){
                                            pengaturanBar.setTitle("Pelanggan Luar Rute");
                                            kondisi = "Pelanggan Luar Rute";
                                        } else {
                                            pengaturanBar.setTitle("Pelanggan Dalam Rute");
                                            kondisi = "Pelanggan Dalam Rute";
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
                            pengaturanBar.setTitle("Pelanggan Luar Rute");
                            kondisi = "Pelanggan Luar Rute";
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
            RequestQueue requestkota = Volley.newRequestQueue(menu_pelanggan.this);
            requestkota.getCache().clear();
            requestkota.add(rest);
        } else {
            pengaturanBar.setTitle(kondisi);
        }

        kunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failed.clear();
                final Dialog dialogstatus = new Dialog(menu_pelanggan.this);
                dialogstatus.setCancelable(false);
                dialogstatus.setContentView(R.layout.pilih_status_kunjungan);

                Button batal = dialogstatus.findViewById(R.id.batal);
                Button ok = dialogstatus.findViewById(R.id.ok);
                final AutoCompleteTextView statuskunjungan = dialogstatus.findViewById(R.id.statuskunjungan);

                String[] data = {"Selesai Kunjungan", "Tunda Kunjungan"};
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, data);
                statuskunjungan.setAdapter(adapter);
                statuskunjungan.setThreshold(1);
                statuskunjungan.dismissDropDown();

                statuskunjungan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus)
                            statuskunjungan.showDropDown();
                    }
                });

                statuskunjungan.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        statuskunjungan.showDropDown();
                        return false;
                    }
                });

                batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogstatus.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLonglat();
                        if(statuskunjungan.getText().toString().length() == 0){
                            statuskunjungan.setError("Isi Status");
                        } else if(statuskunjungan.getText().toString().equals("Tunda Kunjungan")){
                            new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Kunjungan Ditunda")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            pDialog = new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.PROGRESS_TYPE);
                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                            pDialog.setTitleText("Harap Menunggu");
                                            pDialog.setCancelable(false);
                                            pDialog.show();
                                            // tunda();
                                            getCompare("1", "0", "1", "", noSO);
                                            getRekapPenjualan(noSO);
                                            StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CekSO?szDocId="+ mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                JSONObject jsonObject = new JSONObject(response);
                                                                if (jsonObject.getString("status").equals("true")) {
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                        String so = jsonObject1.getString("szDocSO");
                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
                                                                                    new Response.Listener<String>() {

                                                                                        @Override
                                                                                        public void onResponse(String response) {
                                                                                            getCompare("1", "0", "1", "", so);
                                                                                            getRekapPenjualan(so);

                                                                                        }
                                                                                    },
                                                                                    new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            getCompare("1", "0", "1", "", noSO);
                                                                                            getRekapPenjualan(noSO);

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
                                                                                    params.put("szCustomerId", no_surat);
                                                                                    params.put("szDocSO", so);
                                                                                    params.put("bPostPone", "1");
                                                                                    params.put("bFinisihed", "1");
                                                                                    params.put("bSuccess", "0");
                                                                                    params.put("bVisited" , "1");
                                                                                    params.put("dtmFinish", currentDateandTime2);
                                                                                    params.put("szFailReason", "");


                                                                                    System.out.println("Params 1 =  " + params);

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
                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                            requestQueue2.getCache().clear();
                                                                            requestQueue2.add(stringRequest2);


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
                                                            getCompare("1", "0", "1", "", noSO);
                                                            getRekapPenjualan(noSO);

                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
                                                                    new Response.Listener<String>() {

                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            getCompare("1", "0", "1", "", noSO);
                                                                            getRekapPenjualan(noSO);

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
                                                                    params.put("szCustomerId", no_surat);
                                                                    params.put("szDocSO", noSO);
                                                                    params.put("bPostPone", "1");
                                                                    params.put("bFinisihed", "1");
                                                                    params.put("bSuccess", "0");
                                                                    params.put("bVisited" , "1");
                                                                    params.put("dtmFinish", currentDateandTime2);
                                                                    params.put("szFailReason", "");

                                                                    System.out.println("Params 2 = " + params);
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
                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                            requestQueue2.getCache().clear();
                                                            requestQueue2.add(stringRequest2);

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
                                            RequestQueue requestkota = Volley.newRequestQueue(menu_pelanggan.this);
                                            requestkota.getCache().clear();
                                            requestkota.add(rest);
//

                                        }
                                    })
                                    .show();
                        } else if(statuskunjungan.getText().toString().equals("Selesai Kunjungan")) {
                            dialogstatus.dismiss();
                            pDialog = new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Harap Menunggu");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CekSO?szDocId="+ mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // selesai();

                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                if (jsonObject.getString("status").equals("true")) {
                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                        String so = jsonObject1.getString("szDocSO");

                                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LastNumbers?szDocId=" + so,
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        getCompare("1", "1", "0", "", noSO);
                                                                        try {
                                                                            int number = 0;
                                                                            JSONObject obj = new JSONObject(response);
                                                                            final JSONArray movieArray = obj.getJSONArray("data");
                                                                            for (int i = 0; i < movieArray.length(); i++) {
                                                                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                                                                StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
                                                                                        new Response.Listener<String>() {
                                                                                            @Override
                                                                                            public void onResponse(String response) {



                                                                                                updateDraft();
                                                                                                updateDraftMDBA();



                                                                                                updateDraftDoccall();
                                                                                                updateDraftDocSo();

                                                                                                updateSO();

                                                                                            }

                                                                                            private void updateDraftDocSo() {
                                                                                                StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToAppliedDocSo",
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

                                                                                                        params.put("szDocId", so);

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
                                                                                                RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                                requestQueue2.getCache().clear();
                                                                                                requestQueue2.add(stringRequest2);
                                                                                            }

                                                                                            private void updateDraftDoccall() {
                                                                                                StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToAppliedDoccall",
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

                                                                                                        params.put("szDocId", mulai_perjalanan.id_pelanggan);

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
                                                                                                RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                                requestQueue2.getCache().clear();
                                                                                                requestQueue2.add(stringRequest2);
                                                                                            }

                                                                                            private void updateDraftMDBA() {
                                                                                                StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToAppliedMDBA",
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

                                                                                                        params.put("szDocId", so);

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
                                                                                                RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                                requestQueue2.getCache().clear();
                                                                                                requestQueue2.add(stringRequest2);
                                                                                            }

                                                                                            private void updateDraft() {
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

                                                                                                        params.put("szDocId", so);

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
                                                                                                if (requestQueue6 == null) {
                                                                                                    requestQueue6 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                                    requestQueue6.add(stringRequest2);
                                                                                                } else {
                                                                                                    requestQueue6.add(stringRequest2);
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

                                                                                    @Override
                                                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                                                        Map<String, String> params = new HashMap<String, String>();
                                                                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                        String currentDateandTime2 = sdf2.format(new Date());

                                                                                        params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                                                                        params.put("szCustomerId", no_surat);
                                                                                        params.put("szDocSO", so);

                                                                                        params.put("bPostPone", "0");
                                                                                        params.put("bFinisihed", "1");
                                                                                        params.put("bSuccess", "1");
                                                                                        params.put("szFailReason", "");
                                                                                        params.put("bVisited", "1");

                                                                                        params.put("dtmFinish", currentDateandTime2);

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

                                                                                if (requestQueue5 == null) {
                                                                                    requestQueue5 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                    requestQueue5.add(stringRequest2);
                                                                                } else {
                                                                                    requestQueue5.add(stringRequest2);
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
                                                                        {
                                                                            pDialog.dismissWithAnimation();
                                                                            final Dialog dialog = new Dialog(menu_pelanggan.this);
                                                                            dialog.setContentView(R.layout.topup_reason);
                                                                            dialog.setCancelable(false);
                                                                            dialog.show();

                                                                            // selesai();



                                                                            final AutoCompleteTextView editpilihalasan = dialog.findViewById(R.id.editpilihalasan);
                                                                            Button tidak = dialog.findViewById(R.id.tidak);
                                                                            Button ya = dialog.findViewById(R.id.ya);

                                                                            tidak.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            });

                                                                            ya.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    editpilihalasan.setError(null);
                                                                                    if(editpilihalasan.getText().toString().length() == 0){
                                                                                        editpilihalasan.setError("Pilih Alasan");
                                                                                    } else {
                                                                                        new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                                .setContentText("Kunjungan Selesai")
                                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                                                        sDialog.dismissWithAnimation();
                                                                                                        pDialog = new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                                                                        pDialog.setTitleText("Harap Menunggu");
                                                                                                        pDialog.setCancelable(false);
                                                                                                        pDialog.show();
                                                                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
                                                                                                                new Response.Listener<String>() {

                                                                                                                    @Override
                                                                                                                    public void onResponse(String response) {
                                                                                                                        String[] parts = editpilihalasan.getText().toString().split("-");
                                                                                                                        String restnomor = parts[0];
                                                                                                                        getCompare("1", "0", "0", restnomor, so);
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

                                                                                                                String[] parts = editpilihalasan.getText().toString().split("-");
                                                                                                                String restnomor = parts[0];
                                                                                                                String restnomorbaru = restnomor.replace(" ", "");
                                                                                                                failreason = restnomorbaru;

                                                                                                                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                                                                                                params.put("szCustomerId", no_surat);
                                                                                                                params.put("szDocSO", so);
                                                                                                                params.put("bPostPone", "0");
                                                                                                                params.put("bFinisihed", "1");
                                                                                                                params.put("bVisited", "1");
                                                                                                                params.put("bSuccess", "0");
                                                                                                                params.put("szFailReason", restnomorbaru);


                                                                                                                params.put("dtmFinish", currentDateandTime2);


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
                                                                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                                        requestQueue2.getCache().clear();
                                                                                                        requestQueue2.add(stringRequest2);



                                                                                                    }
                                                                                                })
                                                                                                .show();

                                                                                    }
                                                                                }
                                                                            });

                                                                            StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_FailedVisit",
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
                                                                                                        failed.add(id + "-" + jenis_istirahat);

                                                                                                    }
                                                                                                }
                                                                                                editpilihalasan.setAdapter(new ArrayAdapter<String>(menu_pelanggan.this, android.R.layout.simple_expandable_list_item_1, failed));
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
                                                                            RequestQueue requestkota = Volley.newRequestQueue(menu_pelanggan.this);
                                                                            requestkota.getCache().clear();
                                                                            requestkota.add(rest);
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
                                                        if (requestQueue == null) {
                                                            requestQueue = Volley.newRequestQueue(menu_pelanggan.this);
                                                            requestQueue.add(stringRequest);
                                                        } else {
                                                            requestQueue.add(stringRequest);
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

                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LastNumbers?szDocId=" + noSO,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            pDialog.dismissWithAnimation();

                                                            try {
                                                                int number = 0;
                                                                JSONObject obj = new JSONObject(response);
                                                                final JSONArray movieArray = obj.getJSONArray("data");
                                                                for (int i = 0; i < movieArray.length(); i++) {
                                                                    final JSONObject movieObject = movieArray.getJSONObject(i);
                                                                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
                                                                            new Response.Listener<String>() {
                                                                                @Override
                                                                                public void onResponse(String response) {

                                                                                    updateDraft();
                                                                                    updateDraftMDBA();

                                                                                    updateDraftDoccall();
                                                                                    updateDraftDocSo();

                                                                                    updateSO();

                                                                                }

                                                                                private void updateDraftDocSo() {
                                                                                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToAppliedDocSo",
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

                                                                                            params.put("szDocId", noSO);

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
                                                                                    RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                    requestQueue2.getCache().clear();
                                                                                    requestQueue2.add(stringRequest2);
                                                                                }

                                                                                private void updateDraftDoccall() {
                                                                                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToAppliedDoccall",
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

                                                                                            params.put("szDocId", mulai_perjalanan.id_pelanggan);

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
                                                                                    RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                    requestQueue2.getCache().clear();
                                                                                    requestQueue2.add(stringRequest2);
                                                                                }

                                                                                private void updateDraftMDBA() {
                                                                                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToAppliedMDBA",
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

                                                                                            params.put("szDocId", noSO);

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
                                                                                    RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                    requestQueue2.getCache().clear();
                                                                                    requestQueue2.add(stringRequest2);
                                                                                }

                                                                                private void updateDraft() {
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

                                                                                            params.put("szDocId", noSO);

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
                                                                                    if (requestQueue6 == null) {
                                                                                        requestQueue6 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                        requestQueue6.add(stringRequest2);
                                                                                    } else {
                                                                                        requestQueue6.add(stringRequest2);
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

                                                                        @Override
                                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                                            Map<String, String> params = new HashMap<String, String>();
                                                                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                            String currentDateandTime2 = sdf2.format(new Date());

                                                                            params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                                                            params.put("szCustomerId", no_surat);
                                                                            params.put("szDocSO", noSO);

                                                                            params.put("bPostPone", "0");
                                                                            params.put("bFinisihed", "1");
                                                                            params.put("bSuccess", "1");
                                                                            params.put("szFailReason", "");
                                                                            params.put("bVisited", "1");

                                                                            params.put("dtmFinish", currentDateandTime2);


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

                                                                    if (requestQueue5 == null) {
                                                                        requestQueue5 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                        requestQueue5.add(stringRequest2);
                                                                    } else {
                                                                        requestQueue5.add(stringRequest2);
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
                                                            {
                                                                pDialog.dismissWithAnimation();
                                                                final Dialog dialog = new Dialog(menu_pelanggan.this);
                                                                dialog.setContentView(R.layout.topup_reason);
                                                                dialog.setCancelable(false);
                                                                dialog.show();

                                                                // selesai();



                                                                final AutoCompleteTextView editpilihalasan = dialog.findViewById(R.id.editpilihalasan);
                                                                Button tidak = dialog.findViewById(R.id.tidak);
                                                                Button ya = dialog.findViewById(R.id.ya);

                                                                tidak.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });

                                                                ya.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        editpilihalasan.setError(null);
                                                                        if(editpilihalasan.getText().toString().length() == 0){
                                                                            editpilihalasan.setError("Pilih Alasan");
                                                                        } else {
                                                                            new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                    .setContentText("Kunjungan Selesai")
                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                        @Override
                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                            sDialog.dismissWithAnimation();
                                                                                            pDialog = new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                                                            pDialog.setTitleText("Harap Menunggu");
                                                                                            pDialog.setCancelable(false);
                                                                                            pDialog.show();
                                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
                                                                                                    new Response.Listener<String>() {

                                                                                                        @Override
                                                                                                        public void onResponse(String response) {
                                                                                                            String[] parts = editpilihalasan.getText().toString().split("-");
                                                                                                            String restnomor = parts[0];
                                                                                                            getCompare("1", "0", "0", restnomor, noSO);
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

                                                                                                    String[] parts = editpilihalasan.getText().toString().split("-");
                                                                                                    String restnomor = parts[0];
                                                                                                    String restnomorbaru = restnomor.replace(" ", "");
                                                                                                    failreason = restnomorbaru;

                                                                                                    params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                                                                                    params.put("szCustomerId", no_surat);
                                                                                                    params.put("szDocSO", noSO);
                                                                                                    params.put("bPostPone", "0");
                                                                                                    params.put("bFinisihed", "1");
                                                                                                    params.put("bVisited", "1");
                                                                                                    params.put("bSuccess", "0");
                                                                                                    params.put("szFailReason", restnomorbaru);


                                                                                                    params.put("dtmFinish", currentDateandTime2);


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
                                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                                                                                            requestQueue2.getCache().clear();
                                                                                            requestQueue2.add(stringRequest2);



                                                                                        }
                                                                                    })
                                                                                    .show();

                                                                        }
                                                                    }
                                                                });

                                                                StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_FailedVisit",
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
                                                                                            failed.add(id + "-" + jenis_istirahat);

                                                                                        }
                                                                                    }
                                                                                    editpilihalasan.setAdapter(new ArrayAdapter<String>(menu_pelanggan.this, android.R.layout.simple_expandable_list_item_1, failed));
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
                                                                RequestQueue requestkota = Volley.newRequestQueue(menu_pelanggan.this);
                                                                requestkota.getCache().clear();
                                                                requestkota.add(rest);
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
                                            if (requestQueue == null) {
                                                requestQueue = Volley.newRequestQueue(menu_pelanggan.this);
                                                requestQueue.add(stringRequest);
                                            } else {
                                                requestQueue.add(stringRequest);
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
                                    5000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            RequestQueue requestkota = Volley.newRequestQueue(menu_pelanggan.this);
                            requestkota.getCache().clear();
                            requestkota.add(rest);
//


                        }
                    }
                });
                dialogstatus.show();
            }
        });

        infopengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pengiriman_info.class);
                startActivity(intent);
            }
        });
    }

    private void tunda() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
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
                params.put("szCustomerId", no_surat);
                params.put("szDocSO", noSO);
                params.put("bPostPone", "1");
                params.put("bFinisihed", "1");
                params.put("bSuccess", "0");
                params.put("bVisited" , "1");
                params.put("dtmFinish", currentDateandTime2);
                params.put("szFailReason", "");

                System.out.println("Params 3 = " + params);
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
        RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);

    }

    private void updateSO() {
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO2?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                updateSOApplied(biodatas.getString("szDocSO"));






                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void updateSOApplied(String szDocSO) {
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
                        if (requestQueue8 == null) {
                            requestQueue8 = Volley.newRequestQueue(menu_pelanggan.this);
                            requestQueue8.add(stringRequest2);
                        } else {
                            requestQueue8.add(stringRequest2);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
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
        if (requestQueue3 == null) {
            requestQueue3 = Volley.newRequestQueue(menu_pelanggan.this);
            requestQueue3.add(channel_status);
        } else {
            requestQueue3.add(channel_status);
        }
    }

    private void getRekapPenjualanVT() {
        String[] parts = no_surat.split("-");
        String restnomor = parts[0];
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_TotalHargaRekap?szCustomerId=" + no_surat + "&szBranchId="+restnomor+"&szDocSO="+noSO+"VT",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int totalharga = 0;
                        int totalpajak = 0;
                        double totaldpp = 0;
                        int totaldiskon = 0;

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                final JSONArray movieArray = obj.getJSONArray("data");

                                JSONObject biodatas = null;
                                for (int i = 0; i < movieArray.length(); i++) {

                                    biodatas = movieArray.getJSONObject(i);

                                    String[] decAmount = biodatas.getString("decAmount").split("\\.");
                                    String harga = decAmount[0];

                                    String[] decTax = biodatas.getString("decTax").split("\\.");
                                    String pajak = decTax[0];

                                    String[] decDpp = biodatas.getString("decDpp").split("\\.");
                                    String dpp = decDpp[0];

                                    String[] Discount = biodatas.getString("decDiscount").split("\\.");
                                    String diskon = Discount[0];

                                    totalharga+=Integer.parseInt(harga);
                                    totalpajak+=Integer.parseInt(pajak);
                                    totaldpp+=Integer.parseInt(dpp);
                                    totaldiskon+=Integer.parseInt(diskon);

                                }





                                PostTotalHargaDms(totalharga);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void PostTotalHargaDms(int totalharga) {
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

        channel_status.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue channel_statusQueue = Volley.newRequestQueue(this);
        channel_statusQueue.add(channel_status);
    }

    private void getRekapPenjualanAQ() {
        String[] parts = no_surat.split("-");
        String restnomor = parts[0];
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_TotalHargaRekap?szCustomerId=" + no_surat + "&szBranchId="+restnomor+"&szDocSO="+noSO +"AQ",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int totalharga = 0;
                        int totalpajak = 0;
                        double totaldpp = 0;
                        int totaldiskon = 0;

                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                final JSONArray movieArray = obj.getJSONArray("data");

                                JSONObject biodatas = null;
                                for (int i = 0; i < movieArray.length(); i++) {

                                    biodatas = movieArray.getJSONObject(i);

                                    String[] decAmount = biodatas.getString("decAmount").split("\\.");
                                    String harga = decAmount[0];

                                    String[] decTax = biodatas.getString("decTax").split("\\.");
                                    String pajak = decTax[0];

                                    String[] decDpp = biodatas.getString("decDpp").split("\\.");
                                    String dpp = decDpp[0];

                                    String[] Discount = biodatas.getString("decDiscount").split("\\.");
                                    String diskon = Discount[0];

                                    totalharga+=Integer.parseInt(harga);
                                    totalpajak+=Integer.parseInt(pajak);
                                    totaldpp+=Integer.parseInt(dpp);
                                    totaldiskon+=Integer.parseInt(diskon);

                                }





                                PostTotalHargaDms(totalharga);

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void PostTotalHargaDms(int totalharga) {
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

        channel_status.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue channel_statusQueue = Volley.newRequestQueue(this);
        channel_statusQueue.add(channel_status);
    }

    private void getRekapPenjualan(String so) {
        String[] parts = no_surat.split("-");
        String restnomor = parts[0];
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_TotalHargaRekap?szCustomerId=" + no_surat + "&szBranchId="+restnomor+"&szDocSO="+noSO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int totalharga = 0;
                        int totalpajak = 0;
                        double totaldpp = 0;
                        int totaldiskon = 0;

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                final JSONArray movieArray = obj.getJSONArray("data");

                                JSONObject biodatas = null;
                                for (int i = 0; i < movieArray.length(); i++) {

                                    biodatas = movieArray.getJSONObject(i);

                                    String[] decAmount = biodatas.getString("decAmount").split("\\.");
                                    String harga = decAmount[0];

                                    String[] decTax = biodatas.getString("decTax").split("\\.");
                                    String pajak = decTax[0];

                                    String[] decDpp = biodatas.getString("decDpp").split("\\.");
                                    String dpp = decDpp[0];

                                    String[] Discount = biodatas.getString("decDiscount").split("\\.");
                                    String diskon = Discount[0];

                                    totalharga+=Integer.parseInt(harga);
                                    totalpajak+=Integer.parseInt(pajak);
                                    totaldpp+=Integer.parseInt(dpp);
                                    totaldiskon+=Integer.parseInt(diskon);

                                }


                                PostTotalHargaDms(totalharga);
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void PostTotalHargaDms(int totalharga) {
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

        channel_status.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue channel_statusQueue = Volley.newRequestQueue(this);
        channel_statusQueue.add(channel_status);
    }

    private void getLonglat() {
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Longlat?szDocId="+mulai_perjalanan.id_pelanggan+"&szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                if(biodatas.getString("szLatitude").equals("")){
                                    putRadiusLongLat(0);
                                } else {
                                    Location startPoint=new Location("locationA");
                                    startPoint.setLatitude(Double.valueOf(MainActivity.latitude));
                                    startPoint.setLongitude(Double.valueOf(MainActivity.longitude));

                                    Location endPoint=new Location("locationA");
                                    endPoint.setLatitude(Double.valueOf(biodatas.getString("szLatitude")));
                                    endPoint.setLongitude(Double.valueOf(biodatas.getString("szLongitude")));
                                    double distance2 = startPoint.distanceTo(endPoint);
                                    int value = (int) distance2;

                                    System.out.println("Hasil Radius = " + value);

                                    putRadiusLongLat(value);
                                }





                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void putRadiusLongLat(int value) {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DoccallItemSFA3",
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
                                params.put("szCustomerId", no_surat);

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
                        if (requestQueue2 == null) {
                            requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                            requestQueue2.add(stringRequest2);
                        } else {
                            requestQueue2.add(stringRequest2);
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

        channel_status.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(menu_pelanggan.this);
            requestQueue4.add(channel_status);
        } else {
            requestQueue4.add(channel_status);
        }
    }


    private void getCompare(String bVisited, String bSuccess, String bPostPone, String szFailReason, String so) {

        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                String startDate = biodatas.getString("dtmStart");
                                String stopDate = biodatas.getString("dtmFinish");

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                                LocalDateTime dateTime1= LocalDateTime.parse(startDate, formatter);
                                LocalDateTime dateTime2= LocalDateTime.parse(stopDate, formatter);

                                long diffInSeconds = java.time.Duration.between(dateTime1, dateTime2).getSeconds();

                                UpdatesfaDoccallItem(bVisited, bSuccess, bPostPone, szFailReason, String.valueOf(diffInSeconds), so);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        UpdatesfaDoccallItem(bVisited, bSuccess, bPostPone, szFailReason, "0", so);
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
        RequestQueue channel_statusQueue = Volley.newRequestQueue(menu_pelanggan.this);
        channel_statusQueue.add(channel_status);
    }

    private void UpdateDecDuration(String valueOf, String so) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DecDuration",
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

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("szDocSO", so);
                params.put("szCustomerId", no_surat);
                params.put("decDuration", valueOf);



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
        RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
        requestQueue2.add(stringRequest2);
    }

    private void getNoSO() {


        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(0);

                                getNoSO2(biodatas.getString("szDocSO"));






                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void getNoSO2(String szDocSO) {
                        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        pDialog.dismissWithAnimation();

                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            final JSONArray movieArray = obj.getJSONArray("data");

                                            JSONObject biodatas = null;
                                            for (int i = 0; i < movieArray.length(); i++) {

                                                biodatas = movieArray.getJSONObject(0);
                                                noSO = biodatas.getString("szDocSO");

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
                        if (requestQueue7 == null) {
                            requestQueue7 = Volley.newRequestQueue(menu_pelanggan.this);
                            requestQueue7.add(channel_status);
                        } else {
                            requestQueue7.add(channel_status);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
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
        if (requestQueue3 == null) {
            requestQueue3 = Volley.newRequestQueue(menu_pelanggan.this);
            requestQueue3.add(channel_status);
        } else {
            requestQueue3.add(channel_status);
        }


    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();

        pDialog = new SweetAlertDialog(menu_pelanggan.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNoSO();
            }
        }, 5000);

        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Taxtype",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);
                                tax = biodatas.getString("decTax");

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

        channel_status.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue channel_statusQueue = Volley.newRequestQueue(menu_pelanggan.this);
        channel_statusQueue.add(channel_status);
    }

//    $start_sfa = $this->M_Mulai_Perjalanan->getDtmStart($szDocId, $szCustomerId);
//    foreach($start_sfa as $row) {
//        $start = $row['dtmStart'];
//        $langitude = $row['szLangitude'];
//        $longitude = $row['szLongitude'];
//    }
    private void UpdatesfaDoccallItem(String bVisited, String bSuccess, String bPostPone, String szFailReason, String s, String so) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DoccallItemSFA2",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        UpdateDecDuration(s, noSO);
                        UpdateDecDuration(s, so);

                        if(bPostPone.equals("1")){
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Sedang Memuat...", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(menu_pelanggan.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    System.exit(0);
                                }
                            }, 2000);
                        } else {
                            Intent intent = new Intent(getBaseContext(), FotoSelesai.class);
                            startActivity(intent);
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        UpdateDecDuration(s, noSO);
                        UpdateDecDuration(s, so);

                        if(bPostPone.equals("1")){
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Sedang Memuat...", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(menu_pelanggan.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    System.exit(0);
                                }
                            }, 2000);
                        } else {
                            Intent intent = new Intent(getBaseContext(), FotoSelesai.class);
                            startActivity(intent);
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

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("szCustomerId", no_surat);

                params.put("dtmFinish", currentDateandTime2);

                params.put("bVisited", bVisited);
                params.put("bSuccess", bSuccess);
                params.put("bPostPone", bPostPone);
                params.put("szFailReason", szFailReason);
                params.put("decDuration", s);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
        requestQueue2.add(stringRequest2);
    }

    private void selesai() {

                StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Status_Kunjungan",
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
                        params.put("szCustomerId", no_surat);
                        params.put("szDocSO", noSO);
                        params.put("bPostPone", "1");
                        params.put("bFinisihed", "1");
                        params.put("bSuccess", "0");
                        params.put("bVisited" , "1");
                        params.put("dtmFinish", currentDateandTime2);
                        params.put("szFailReason", "");

                        System.out.println("Params 4 = " + params);

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
                RequestQueue requestQueue2 = Volley.newRequestQueue(menu_pelanggan.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);

    }

}