package com.tvip.sfa.menu_mulai_perjalanan;


import androidx.appcompat.app.AppCompatActivity;

import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import com.tvip.sfa.R;
import com.tvip.sfa.menu_utama.MainActivity;
import com.tvip.sfa.pojo.data_pelanggan_luar_rute_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class scangagal_luarrute extends AppCompatActivity {
    TabLayout tablayout;
    ListView list_luarrute;
    List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojos = new ArrayList<>();
    ListViewAdapterDaftarLuarPelanggan adapter;
    SweetAlertDialog pDialog;
    SharedPreferences sharedPreferences;
    SearchView caripelanggan;
    int nomor;
    int lastcounter;
    String docsonew;

    RequestQueue requestQueue, requestQueue2;

    String nama2, alamat2, langitude2, longitude2, szDocSO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_scangagal_luarrute);
        tablayout = findViewById(R.id.tablayout);
        caripelanggan = findViewById(R.id.caripelanggan);



        tablayout.getTabAt(0).getOrCreateBadge().setNumber(0);
        tablayout.getTabAt(1).getOrCreateBadge().setNumber(0);
        tablayout.getTabAt(2).getOrCreateBadge().setNumber(0);


        list_luarrute = findViewById(R.id.list_luarrute);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        getNumber(nik_baru);
        ListDatapelangganLuarRute("0", "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Luar_Pelanggan?surat_tugas=" + mulai_perjalanan.id_pelanggan + "&szId=" + nik_baru);
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                tablayout.setEnabled(false);
                caripelanggan.setQuery("", false);
                caripelanggan.clearFocus();
                if(position == 0){
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);
                    ListDatapelangganLuarRute("0", "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Luar_Pelanggan?surat_tugas=" + mulai_perjalanan.id_pelanggan + "&szId=" + nik_baru);
                } else if (position == 1){
                    ListDatapelangganLuarRute("1", "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ListLuarPelangganSuratTugas?surat_tugas=" + mulai_perjalanan.id_pelanggan);
                } else {
                    ListDatapelangganLuarRute("2", "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ListLuarPelangganSuratTugas?surat_tugas=" + mulai_perjalanan.id_pelanggan);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        list_luarrute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String nama = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzName();
                String kode = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzCustomerId();
                String alamat = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzAddress();

                String langitude = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzLatitude();
                String longitude = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzLongitude();

                nama2 = nama;
                alamat2 = alamat;

                langitude2 = langitude;
                longitude2 = longitude;
                if(longitude.equals("")){
                    new SweetAlertDialog(scangagal_luarrute.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Toko Tidak Ada Longlat")
                            .setConfirmText("OK")
                            .show();
                } else if(((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getbPostPone().equals("1")){
                    pDialog = new SweetAlertDialog(scangagal_luarrute.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_latlong_after",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    updateSFADoccall(kode);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    updateSFADoccall(kode);

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
                            params.put("szCustomerId", kode);
                            params.put("szLangitude", MainActivity.latitude);
                            params.put("szLongitude", MainActivity.longitude);

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
                    if (requestQueue == null) {
                        requestQueue = Volley.newRequestQueue(scangagal_luarrute.this);
                        requestQueue.getCache().clear();
                        requestQueue.add(stringRequest2);
                    } else {
                        requestQueue.add(stringRequest2);
                    }
                } else if(((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getBsuccess().equals("1")){
                    new SweetAlertDialog(scangagal_luarrute.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Toko Sudah Selesai Dikunjungi")
                            .setConfirmText("OK")
                            .show();
                } else if (((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getbStarted().equals("1")){
                    pDialog = new SweetAlertDialog(scangagal_luarrute.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_latlong_after",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    updateSFADoccall(kode);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    updateSFADoccall(kode);

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
                            params.put("szCustomerId", kode);
                            params.put("szLangitude", MainActivity.latitude);
                            params.put("szLongitude", MainActivity.longitude);

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
                    if (requestQueue == null) {
                        requestQueue = Volley.newRequestQueue(scangagal_luarrute.this);
                        requestQueue.getCache().clear();
                        requestQueue.add(stringRequest2);
                    } else {
                        requestQueue.add(stringRequest2);
                    }
                } else {
                    pDialog = new SweetAlertDialog(scangagal_luarrute.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Detail_Pelanggan?szCustomerId=" + kode,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        final JSONArray movieArray = obj.getJSONArray("data");

                                        JSONObject biodatas = null;
                                        for (int i = 0; i < movieArray.length(); i++) {

                                            biodatas = movieArray.getJSONObject(i);
                                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                            String currentDateandTime2 = sdf2.format(new Date());

                                            getJumlah(kode, currentDateandTime2, currentDateandTime2, '1', currentDateandTime2, biodatas.getString("szDocSO"), biodatas.getString("szRefDocId"));






                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }
                                }

                                private void getJumlah(String szCustomerId, String currentDateandTime2, String currentDateandTime21, char c, String currentDateandTime22, String szDocSO, String szRefDocId) {
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


                                                            getSoldtoBranch(szCustomerId);



                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();

                                                    }

                                                }

                                                private void getSoldtoBranch(String szCustomerId) {
                                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SoldToBranchId?szId="+ kode,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {

                                                                    try {
                                                                        JSONObject obj = new JSONObject(response);
                                                                        final JSONArray movieArray = obj.getJSONArray("data");
                                                                        for (int i = 0; i < movieArray.length(); i++) {
                                                                            final JSONObject movieObject = movieArray.getJSONObject(i);

                                                                            getDetail(movieObject.getString("szSoldToBranchId"), szCustomerId);

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
                                                    RequestQueue requestQueue = Volley.newRequestQueue(scangagal_luarrute.this);
                                                    requestQueue.getCache().clear();

                                                    requestQueue.add(stringRequest);

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
                                    RequestQueue requestQueue = Volley.newRequestQueue(scangagal_luarrute.this);
                                    requestQueue.getCache().clear();
                                    requestQueue.add(stringRequest);
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
                    RequestQueue channel_statusQueue = Volley.newRequestQueue(scangagal_luarrute.this);
                    channel_statusQueue.getCache().clear();
                    channel_statusQueue.add(channel_status);

                }






            }




        });
    }

    private void updateSFADoccall(String kode) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DoccallItem_longlat",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Intent i = new Intent(getBaseContext(), menu_pelanggan.class);
                        i.putExtra("kode", kode);
                        startActivity(i);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent i = new Intent(getBaseContext(), menu_pelanggan.class);
                        i.putExtra("kode", kode);
                        startActivity(i);
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
                params.put("szCustomerId", kode);
                params.put("szLangitude", MainActivity.latitude);
                params.put("szLongitude", MainActivity.longitude);

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
        if (requestQueue2 == null) {
            requestQueue2 = Volley.newRequestQueue(scangagal_luarrute.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        } else {
            requestQueue2.add(stringRequest2);
        }
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
                pDialog.dismissWithAnimation();
                System.out.println("Message = " + error.toString());

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_luarrute.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void getDetail(String szSoldToBranchId, String szCustomerId) {
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + szCustomerId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        System.out.println("Link SFA = " + "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + szCustomerId);
                        Intent i = new Intent(getBaseContext(), detailscangagal_dalamrute.class);

                        i.putExtra("nama", nama2);
                        i.putExtra("kode", szCustomerId);
                        i.putExtra("alamat", alamat2);
                        i.putExtra("langitude", langitude2);
                        i.putExtra("longitude", longitude2);
                        i.putExtra("jenispelanggan", "Pelanggan Luar Rute");

                        startActivity(i);

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

                        postDocSO(szSoldToBranchId, szCustomerId);


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
        RequestQueue channel_statusQueue = Volley.newRequestQueue(scangagal_luarrute.this);
        channel_statusQueue.getCache().clear();
        channel_statusQueue.add(channel_status);
    }

    private void getNumber(String nik_baru) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Luar_Pelanggan?surat_tugas=" + mulai_perjalanan.id_pelanggan + "&szId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tablayout.setEnabled(false);
                        try {
                            int number = 0;
                            int number1 = 0;
                            int number2 = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                if((movieObject.getString("bPostPone").equals("null") || movieObject.getString("bPostPone").equals("0")) && (movieObject.getString("bFinisihed").equals("null") || movieObject.getString("bFinisihed").equals("0"))) {
                                    number++;
                                    tablayout.getTabAt(0).getOrCreateBadge().setNumber(number);
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
                        tablayout.setEnabled(false);
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

    private void ListDatapelangganLuarRute(String s, String link) {
        pDialog = new SweetAlertDialog(scangagal_luarrute.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        adapter = new ListViewAdapterDaftarLuarPelanggan(dataPelangganLuarRutePojos, getApplicationContext());
        list_luarrute.setAdapter(adapter);
        adapter.clear();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, link,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tablayout.setEnabled(false);
                        try {
                            int number = 0;
                            int number1 = 0;
                            int number2 = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_pelanggan_luar_rute_pojo movieItem = new data_pelanggan_luar_rute_pojo(
                                        movieObject.getString("szCustomerId"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szAddress"),
                                        movieObject.getString("szLatitude"),
                                        movieObject.getString("szLongitude"),
                                        movieObject.getString("bVisited"),
                                        movieObject.getString("bFinisihed"),
                                        movieObject.getString("bScanBarcode"),
                                        movieObject.getString("bPostPone"));

                                dataPelangganLuarRutePojos.add(movieItem);

                                caripelanggan.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String text) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        adapter.getFilter().filter(newText);
                                        return true;
                                    }
                                });

                                if(movieObject.getString("bPostPone").equals("1")) {
                                    number1++;
                                    tablayout.getTabAt(1).getOrCreateBadge().setNumber(number1);
                                }

                                if(movieObject.getString("bFinisihed").equals("1") && movieObject.getString("bPostPone").equals("0")) {
                                    number2++;
                                    tablayout.getTabAt(2).getOrCreateBadge().setNumber(number2);
                                }


                                if(s.equals("0")){
                                    pDialog.dismissWithAnimation();
                                    if(movieObject.getString("bPostPone").equals("1") || movieObject.getString("bsuccess").equals("1") || movieObject.getString("bFinisihed").equals("1")){
                                        dataPelangganLuarRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if(s.equals("1")){
                                    pDialog.dismissWithAnimation();
                                    if(movieObject.getString("bPostPone").equals("0")) {
                                        dataPelangganLuarRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if(s.equals("2")){
                                    pDialog.dismissWithAnimation();
                                    if(!(movieObject.getString("bFinisihed").equals("1") && movieObject.getString("bPostPone").equals("0"))) {
                                        dataPelangganLuarRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }

                                }


                                adapter.notifyDataSetChanged();

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
                        tablayout.setEnabled(false);
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

    public class ListViewAdapterDaftarLuarPelanggan extends BaseAdapter implements Filterable {
        private final List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojos;
        private List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojosFiltered;

        private final Context context;

        public ListViewAdapterDaftarLuarPelanggan(List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojos, Context context) {
            this.dataPelangganLuarRutePojos = dataPelangganLuarRutePojos;
            this.dataPelangganLuarRutePojosFiltered = dataPelangganLuarRutePojos;
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataPelangganLuarRutePojosFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return dataPelangganLuarRutePojosFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void clear() {
            dataPelangganLuarRutePojosFiltered.clear();

        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View listViewItem = getLayoutInflater().inflate(R.layout.list_rute, null, true);

            TextView namatoko = listViewItem.findViewById(R.id.namatoko);
            TextView alamat = listViewItem.findViewById(R.id.alamat);
            TextView status = listViewItem.findViewById(R.id.status);
            TextView noSo = listViewItem.findViewById(R.id.noSo);
            MaterialCardView warna = listViewItem.findViewById(R.id.warna);


            namatoko.setText(dataPelangganLuarRutePojosFiltered.get(position).getSzName());
            alamat.setText(dataPelangganLuarRutePojosFiltered.get(position).getSzAddress());
            noSo.setText(dataPelangganLuarRutePojosFiltered.get(position).getSzCustomerId());

            if(tablayout.getSelectedTabPosition() == 1){
                status.setText("Ditunda");
                warna.setCardBackgroundColor(Color.parseColor("#A2C21D"));
            } else if(tablayout.getSelectedTabPosition() == 2){
                status.setText("Selesai");
                warna.setCardBackgroundColor(Color.parseColor("#1EB547"));
            }
            return listViewItem;
        }
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if(constraint == null || constraint.length() == 0){
                        filterResults.count = dataPelangganLuarRutePojos.size();
                        filterResults.values = dataPelangganLuarRutePojos;

                    }else{
                        List<data_pelanggan_luar_rute_pojo> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toUpperCase();

                        for(data_pelanggan_luar_rute_pojo itemsModel:dataPelangganLuarRutePojos){
                            if(itemsModel.getSzName().contains(searchStr) || itemsModel.getSzCustomerId().contains(searchStr)){
                                resultsModel.add(itemsModel);

                            }
                            filterResults.count = resultsModel.size();
                            filterResults.values = resultsModel;
                        }


                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    dataPelangganLuarRutePojosFiltered = (List<data_pelanggan_luar_rute_pojo>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }

    private void postPelangganLuar(String szCustomerId, String currentDateandTime2, String currentDateandTime21, String szSoldToBranchId) {

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_PelangganLuar",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        Intent i = new Intent(getBaseContext(), detailscangagal_dalamrute.class);

                        i.putExtra("nama", nama2);
                        i.putExtra("kode", szCustomerId);
                        i.putExtra("alamat", alamat2);
                        i.putExtra("langitude", langitude2);
                        i.putExtra("longitude", longitude2);
                        i.putExtra("jenispelanggan", "Pelanggan Luar Rute");

                        startActivity(i);
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

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("intItemNumber", String.valueOf(nomor));

                params.put("szCustomerId", szCustomerId);

                params.put("dtmStart", currentDateandTime2);
                params.put("dtmFinish", currentDateandTime2);

                params.put("bScanBarcode", "0");
                params.put("dtmLastCheckin", currentDateandTime2);
                params.put("szDocSO", szDocSO);
                params.put("szRefDocId", szDocSO);

                System.out.println("Params Returns = " + params);


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
        RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_luarrute.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }
}