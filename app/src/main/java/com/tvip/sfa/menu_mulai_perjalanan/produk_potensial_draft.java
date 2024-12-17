package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.pelanggan_baru_outlet.fullsegment;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.sfa.R;
import com.tvip.sfa.pojo.data_product_potensial_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class produk_potensial_draft extends AppCompatActivity {
    ListView productsDraft;
    ListViewAdapteProductDraft adapter;
    Button batal, selanjutnya;
    SharedPreferences sharedPreferences;
    String new_id, number;

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_produk_potensial_draft);
        productsDraft = findViewById(R.id.productsDraft);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        batal = findViewById(R.id.batal);
        selanjutnya = findViewById(R.id.selanjutnya);


        adapter = new ListViewAdapteProductDraft(produk_potensial.dataDraft, getApplicationContext());
        productsDraft.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        });

        selanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastData();
            }
        });
    }

    private void getLastData() {
        pDialog = new SweetAlertDialog(produk_potensial_draft.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String[] parts = nik_baru.split("-");
        String restnomor = parts[0];

        StringRequest biodata = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LastId?depo=" + restnomor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getRouteItem();
                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                new_id = restnomor + "-" + movieObject.getString("data_id");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getRouteItem();
                        new_id = restnomor + "-" + "0000001";
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

        biodata.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue biodataQueue = Volley.newRequestQueue(this);
        biodataQueue.getCache().clear();
        biodataQueue.add(biodata);
    }

    private void getRouteItem() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String[] parts = nik_baru.split("-");
        String restnomor = parts[0];

        StringRequest biodata = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LastRouteItem?employeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        postSegment();
                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                int count = Integer.parseInt(movieObject.getString("intItemNumber")) + 1;

                                number = String.valueOf(count);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        postSegment();
                        number = "0";
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

        biodata.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue biodataQueue = Volley.newRequestQueue(this);
        biodataQueue.getCache().clear();
        biodataQueue.add(biodata);
    }

    private void postSegment() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CreateCustomer",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        postAlamat();

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


                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szId", new_id);
                params.put("szName", pelanggan_baru_outlet.namaoutlet.getText().toString());
                params.put("szHierarchyId", pelanggan_baru_outlet.segment.getText().toString());
                params.put("szHierarchyFull", fullsegment);
                params.put("user_id", nik_baru);
                params.put("datetime", currentDateandTime3);



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
        RequestQueue requestQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void postAlamat() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_AlamatCustomer",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        postRoute();

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

                String[] parts = pelanggan_baru_outlet.segment.getText().toString().split("-");
                String restnomor = parts[0];

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szId", new_id);
                params.put("szAddress", pelanggan_baru_alamat_outlet.alamatoutlet.getText().toString().toUpperCase());
                params.put("szProvince", pelanggan_baru_outlet.propinsi.toUpperCase());
                params.put("szCity", pelanggan_baru_alamat_outlet.editkotaoutlet.getText().toString());
                params.put("szDistrict", pelanggan_baru_alamat_outlet.editkecamatanoutlet.getText().toString());
                params.put("szSubDistrict", pelanggan_baru_alamat_outlet.editkelurahanoutlet.getText().toString());

                params.put("szZipCode", pelanggan_baru_alamat_outlet.editKodePosoutlet.getText().toString());
                params.put("szPhone1", pelanggan_baru_pemilik.editteleponowner.getText().toString());
                params.put("szContactPerson1", pelanggan_baru_alamat_outlet.editteleponoutlet.getText().toString());
                params.put("szLongitude", pelanggan_baru_outlet.longitude);
                params.put("szLatitude", pelanggan_baru_outlet.langitude);
                params.put("user_id", nik_baru);
                params.put("datetime", currentDateandTime3);




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
        RequestQueue requestQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void postRoute() {
        StringRequest routeinfo = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_RouteInfoCustomer",
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
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szId", new_id);
                params.put("szEmployeeId", nik_baru);

                if(pelanggan_baru_jadwal_kunjungan.day1.isChecked()){
                    params.put("bMon", "1");
                } else {
                    params.put("bMon", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day2.isChecked()){
                    params.put("bTue", "1");
                } else {
                    params.put("bTue", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day3.isChecked()){
                    params.put("bWed", "1");
                } else {
                    params.put("bWed", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day4.isChecked()){
                    params.put("bThu", "1");
                } else {
                    params.put("bThu", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day5.isChecked()){
                    params.put("bFri", "1");
                } else {
                    params.put("bFri", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day6.isChecked()){
                    params.put("bSat", "1");
                } else {
                    params.put("bSat", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day7.isChecked()){
                    params.put("bSun", "1");
                } else {
                    params.put("bSun", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week1.isChecked()){
                    params.put("bWeek1", "1");
                } else {
                    params.put("bWeek1", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week2.isChecked()){
                    params.put("bWeek2", "1");
                } else {
                    params.put("bWeek2", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week3.isChecked()){
                    params.put("bWeek3", "1");
                } else {
                    params.put("bWeek3", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week4.isChecked()){
                    params.put("bWeek4", "1");
                } else {
                    params.put("bWeek4", "0");
                }

                params.put("user_id", nik_baru);
                params.put("datetime", currentDateandTime3);

                return params;
            }

        };
        routeinfo.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue routeinfoQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        routeinfoQueue2.getCache().clear();
        routeinfoQueue2.add(routeinfo);

        StringRequest routeitem = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_RouteItemCustomer",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        PostSales();
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

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szId", nik_baru);
                params.put("intItemNumber", number);
                params.put("szCustomerId", new_id);




                if(pelanggan_baru_jadwal_kunjungan.day1.isChecked()){
                    params.put("intDay1", "1");
                } else {
                    params.put("intDay1", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day2.isChecked()){
                    params.put("intDay2", "1");
                } else {
                    params.put("intDay2", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day3.isChecked()){
                    params.put("intDay3", "1");
                } else {
                    params.put("intDay3", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day4.isChecked()){
                    params.put("intDay4", "1");
                } else {
                    params.put("intDay4", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day5.isChecked()){
                    params.put("intDay5", "1");
                } else {
                    params.put("intDay5", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day6.isChecked()){
                    params.put("intDay6", "1");
                } else {
                    params.put("intDay6", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.day7.isChecked()){
                    params.put("intDay7", "1");
                } else {
                    params.put("intDay7", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week1.isChecked()){
                    params.put("intWeek1", "1");
                } else {
                    params.put("intWeek1", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week2.isChecked()){
                    params.put("intWeek2", "1");
                } else {
                    params.put("intWeek2", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week3.isChecked()){
                    params.put("intWeek3", "1");
                } else {
                    params.put("intWeek3", "0");
                }

                if(pelanggan_baru_jadwal_kunjungan.week4.isChecked()){
                    params.put("intWeek4", "1");
                } else {
                    params.put("intWeek4", "0");
                }



                return params;
            }

        };
        routeitem.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue routeitemQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        routeitemQueue2.getCache().clear();
        routeitemQueue2.add(routeitem);
    }

    private void PostSales() {
        StringRequest topRequest = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SalesInfoCustomer",
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
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                String[] parts = pelanggan_baru_outlet.segment.getText().toString().split("-");
                String restnomor = parts[0];

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szId", new_id);
                params.put("szPaymetTermId", pelanggan_baru_pembayaran.top.getText().toString()+"HARI");

                if(pelanggan_baru_pembayaran.pembayaran.getText().toString().equals("Tunai")){
                    params.put("bAllowToCredit", "0");
                } else {
                    params.put("bAllowToCredit", "1");
                }
                params.put("datetime", currentDateandTime3);
                params.put("user_id", nik_baru);

                return params;
            }

        };
        topRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue topRequestQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        topRequestQueue2.getCache().clear();
        topRequestQueue2.add(topRequest);

        StringRequest StructureRequest = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_StructureCustomer",
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
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                String[] parts = pelanggan_baru_outlet.segment.getText().toString().split("-");
                String restnomor = parts[0];

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szId", new_id);
                params.put("depo", pelanggan_baru_pembayaran.kodedms);
                params.put("datetime", currentDateandTime3);
                params.put("doccal_id", nik_baru);

                return params;
            }

        };
        StructureRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue StructureRequestQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        StructureRequestQueue2.getCache().clear();
        StructureRequestQueue2.add(StructureRequest);

        StringRequest TaxInfoCustomer = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_TaxInfoCustomer",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        ProdukPotensial();

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

                String[] parts = pelanggan_baru_outlet.segment.getText().toString().split("-");
                String restnomor = parts[0];

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szId", new_id);
                params.put("datetime", currentDateandTime3);
                params.put("doccal_id", nik_baru);

                return params;
            }

        };
        TaxInfoCustomer.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue TaxInfoCustomerQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        TaxInfoCustomerQueue2.getCache().clear();
        TaxInfoCustomerQueue2.add(TaxInfoCustomer);


    }

    private void ProdukPotensial() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSOBaru();
                }
            }, 5000);

        for(int i = 0; i < produk_potensial.dataDraft.size(); i ++){
            int finalI = i;
            StringRequest ProductPotential = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ProductPotential",
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

                    params.put("szDocId", new_id);
                    params.put("decQty", adapter.getItem(finalI).getQty());
                    params.put("szProductId", adapter.getItem(finalI).getSzId());

                    return params;
                }

            };
            ProductPotential.setRetryPolicy(
                    new DefaultRetryPolicy(
                            5000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            RequestQueue ProductPotentialQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
            ProductPotentialQueue2.getCache().clear();
            ProductPotentialQueue2.add(ProductPotential);
        }
    }

    private void getSOBaru() {
        String[] parts = sharedPreferences.getString("no_surat", null).split("-");
        String restnomor = parts[0];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_NoSoPelanggan?depo=" + restnomor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                getJumlah(restnomor + "-" +movieObject.getString("data_id"));


                            }
                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        pDialog.dismissWithAnimation();
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

    private void getJumlah(String data_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Dalam_Pelanggan?surat_tugas=" + mulai_perjalanan.id_pelanggan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            postPelangganBaru(String.valueOf(movieArray.length()), data_id);
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

    private void postPelangganBaru(String valueOf, String data_id) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, " https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_PelangganLuar",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        new SweetAlertDialog(produk_potensial_draft.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Pelanggan Sudah Disimpan")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                })
                                .show();

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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("intItemNumber", valueOf);
                params.put("szCustomerId", new_id);

                params.put("dtmStart", currentDateandTime2);
                params.put("dtmFinish", currentDateandTime2);

                params.put("dtmLastCheckin", currentDateandTime2);
                params.put("szDocSO", data_id);


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
        RequestQueue requestQueue2 = Volley.newRequestQueue(produk_potensial_draft.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);

    }

    public class ListViewAdapteProductDraft extends ArrayAdapter<data_product_potensial_pojo> {
        private final List<data_product_potensial_pojo> totalPenjualanPojos;

        private final Context context;

        public ListViewAdapteProductDraft(List<data_product_potensial_pojo> totalPenjualanPojos, Context context) {
            super(context, R.layout.list_potensial_draft, totalPenjualanPojos);
            this.totalPenjualanPojos = totalPenjualanPojos;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_potensial_draft, null, true);

            TextView namaproduk = listViewItem.findViewById(R.id.namaproduk);
            TextView qty = listViewItem.findViewById(R.id.qty);

            data_product_potensial_pojo data = totalPenjualanPojos.get(position);


            namaproduk.setText(data.getSzName());
            qty.setText("Order : " + data.getQty() + " Qty");

            return listViewItem;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}