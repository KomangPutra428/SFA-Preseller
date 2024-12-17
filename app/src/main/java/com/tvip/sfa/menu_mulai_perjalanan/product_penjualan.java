package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.noSO;
import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;
import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.pengaturanBar;
import static com.tvip.sfa.menu_mulai_perjalanan.sisa_stock.uang;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.TimeoutError;
import com.tvip.sfa.Perangkat.HttpsTrustManager;
import androidx.core.view.MenuItemCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_persiapan.daftar_kunjungan;
import com.tvip.sfa.menu_selesai_perjalanan.detail_selesai;
import com.tvip.sfa.pojo.data_pelanggan_pojo;
import com.tvip.sfa.pojo.data_posm_foto_pojo;
import com.tvip.sfa.pojo.data_produk_penjualan_pojo;
import com.tvip.sfa.pojo.total_penjualan_pojo;
import com.tvip.sfa.pojo.trend_produk_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class product_penjualan extends AppCompatActivity {
    static ListView listproductpenjualan;
    static List<data_produk_penjualan_pojo>data_produk_penjualan_pojos = new ArrayList<>();
    static List<total_penjualan_pojo>totalPenjualanPojos = new ArrayList<>();
    static ListViewAdapteProductPenjualan adapter;
    SweetAlertDialog pDialog;
    SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;

    static String soldtobranch;

    private CountDownTimer countDownTimer;
    private int click_duble = 1;


    int count;
    int count1;
    int count2;
    int count3;
    static int MaxQty;
    static AutoCompleteTextView pilihkategori;

    Button batal, lanjutkan;

    private SimpleDateFormat dateFormatter;
    private Calendar date;

    static SearchView cariproduct;

    static int lastnumber, lastremain;

    String[] jenis = {"BOX", "BOTOL"};

    int hargaproduk, discount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_product_penjualan);
        cariproduct = findViewById(R.id.cariproduct);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        pilihkategori = findViewById(R.id.pilihkategori);

        getNumber();
        getRemain();
        getConfig();

        enableSearchView(cariproduct, false);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, jenis);
        pilihkategori.setAdapter(adapter2);



        pilihkategori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                pDialog = new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Harap Menunggu");
                pDialog.setCancelable(false);
                pDialog.show();

                data_produk_penjualan_pojos.clear();
                totalPenjualanPojos.clear();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SoldToBranchId?szId="+ no_surat,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    final JSONArray movieArray = obj.getJSONArray("data");
                                    for (int i = 0; i < movieArray.length(); i++) {
                                        final JSONObject movieObject = movieArray.getJSONObject(i);

                                        getProduct(movieObject.getString("szSoldToBranchId"));
                                        soldtobranch = movieObject.getString("szSoldToBranchId");

                                    }


                                } catch(JSONException e){
                                    e.printStackTrace();

                                }
                            }

                            private void getProduct(String szSoldToBranchId) {
                                enableSearchView(cariproduct, true);

                                if(pilihkategori.getText().toString().equals("BOTOL")){
                                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                    String nik_baru = sharedPreferences.getString("szDocCall", null);
                                    String[] parts = nik_baru.split("-");
                                    String restnomor = parts[0];
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Product_Penjualan?depo=" + no_surat + "&szUomId1=BOX&szUomId2=PCS" + "&szId=" + restnomor,
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
                                                            final data_produk_penjualan_pojo movieItem = new data_produk_penjualan_pojo(
                                                                    movieObject.getString("szId"),
                                                                    movieObject.getString("szName"),
                                                                    movieObject.getString("szUomId"),
                                                                    movieObject.getString("decPrice"),
                                                                    movieObject.getString("szPriceSegmentId"),
                                                                    movieObject.getString("qty"));

                                                            data_produk_penjualan_pojos.add(movieItem);



                                                            adapter = new ListViewAdapteProductPenjualan(data_produk_penjualan_pojos, getApplicationContext());
                                                            listproductpenjualan.setAdapter(adapter);
                                                            listproductpenjualan.setVisibility(View.VISIBLE);







                                                        }


                                                        pDialog.dismissWithAnimation();

                                                    } catch(JSONException e){
                                                        e.printStackTrace();

                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pDialog.dismissWithAnimation();
                                                    listproductpenjualan.setVisibility(View.GONE);
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
                                    RequestQueue requestQueue = Volley.newRequestQueue(product_penjualan.this);
                                    requestQueue.add(stringRequest);

                                } else if(pilihkategori.getText().toString().equals("BOX")){
                                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                    String nik_baru = sharedPreferences.getString("szDocCall", null);
                                    String[] parts = nik_baru.split("-");
                                    String restnomor = parts[0];
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Product_Penjualan?depo=" + no_surat + "&szUomId1=LEMBAR&szUomId2=BOTOL" + "&szId=" + restnomor,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    try {
                                                        int number = 0;
                                                        JSONObject obj = new JSONObject(response);
                                                        final JSONArray movieArray = obj.getJSONArray("data");
                                                        for (int i = 0; i < movieArray.length(); i++) {
                                                            final JSONObject movieObject = movieArray.getJSONObject(i);
                                                            final data_produk_penjualan_pojo movieItem = new data_produk_penjualan_pojo(
                                                                    movieObject.getString("szId"),
                                                                    movieObject.getString("szName"),
                                                                    movieObject.getString("szUomId"),
                                                                    movieObject.getString("decPrice"),
                                                                    movieObject.getString("szPriceSegmentId"),
                                                                    movieObject.getString("qty"));

                                                            data_produk_penjualan_pojos.add(movieItem);

                                                            adapter = new ListViewAdapteProductPenjualan(data_produk_penjualan_pojos, getApplicationContext());
                                                            listproductpenjualan.setAdapter(adapter);
                                                            listproductpenjualan.setVisibility(View.VISIBLE);


                                                        }


                                                        pDialog.dismissWithAnimation();

                                                    } catch(JSONException e){
                                                        e.printStackTrace();

                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pDialog.dismissWithAnimation();
                                                    listproductpenjualan.setVisibility(View.GONE);
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
                                    RequestQueue requestQueue = Volley.newRequestQueue(product_penjualan.this);
                                    requestQueue.add(stringRequest);

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
                RequestQueue requestQueue = Volley.newRequestQueue(product_penjualan.this);
                requestQueue.add(stringRequest);


            }
        });


        listproductpenjualan = findViewById(R.id.listproductpenjualan);
        listproductpenjualan.setVisibility(View.GONE);
        dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanjutkan.setEnabled(false);

                    pDialog = new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CheckDraft?szDocId=" + noSO,
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
                                            System.out.println("Status = " + movieObject.getString("szDocStatus"));
                                            if(movieObject.getString("szDocStatus").equals("Applied")){
                                                if(pilihkategori.getText().toString().equals("BOX")){
                                                    new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.WARNING_TYPE)
                                                            .setTitleText("Dokumen Sudah Digunakan")
                                                            .setConfirmText("OK")
                                                            .show();
                                                } else {
                                                    pDialog.dismissWithAnimation();
                                                    Intent intent = new Intent(getBaseContext(), summary_order.class);
                                                    startActivity(intent);
                                                    for(int e = 0; e < data_produk_penjualan_pojos.size();e++){

                                                        if (!(adapter.getItem(e).getExpired_date() == null)){
                                                            Double harga = Double.valueOf(adapter.getItem(e).getDecPrice());
                                                            int total_harga = harga.intValue() * Integer.parseInt(adapter.getItem(e).getStock_qty());
                                                            totalPenjualanPojos.add(new total_penjualan_pojo(
                                                                    adapter.getItem(e).getSzId(),
                                                                    adapter.getItem(e).getSzName(),
                                                                    adapter.getItem(e).getDecPrice(),
                                                                    adapter.getItem(e).getStock_qty(),
                                                                    adapter.getItem(e).getDisplay(),
                                                                    String.valueOf(total_harga),
                                                                    adapter.getItem(e).getDisc_total(),
                                                                    adapter.getItem(e).getStock(),
                                                                    adapter.getItem(e).getExpired(),
                                                                    adapter.getItem(e).getExpired_date(),
                                                                    adapter.getItem(e).getSzUomId(),
                                                                    adapter.getItem(e).getDisc_distributor(),
                                                                    adapter.getItem(e).getDisc_internal(),
                                                                    adapter.getItem(e).getDisc_principle(),
                                                                    adapter.getItem(e).getSzPriceSegmentId()));

                                                            if(adapter.getItem(e).getSzId().equals("74559")){
                                                                totalPenjualanPojos.add(new total_penjualan_pojo(
                                                                        "19310",
                                                                        "AQ.TISSUE",
                                                                        "0.0000",
                                                                        adapter.getItem(e).getStock_qty(),
                                                                        adapter.getItem(e).getDisplay(),
                                                                        "0",
                                                                        adapter.getItem(e).getDisc_total(),
                                                                        adapter.getItem(e).getStock(),
                                                                        adapter.getItem(e).getExpired(),
                                                                        adapter.getItem(e).getExpired_date(),
                                                                        "LEMBAR",
                                                                        "0",
                                                                        "0",
                                                                        "0",
                                                                        adapter.getItem(e).getSzPriceSegmentId()));


                                                            } else if (adapter.getItem(e).getSzId().equals("74560")){
                                                                totalPenjualanPojos.add(new total_penjualan_pojo(
                                                                        "29310",
                                                                        "VT.TISSUE",
                                                                        "0.0000",
                                                                        adapter.getItem(e).getStock_qty(),
                                                                        adapter.getItem(e).getDisplay(),
                                                                        "0",
                                                                        adapter.getItem(e).getDisc_total(),
                                                                        adapter.getItem(e).getStock(),
                                                                        adapter.getItem(e).getExpired(),
                                                                        adapter.getItem(e).getExpired_date(),
                                                                        "LEMBAR",
                                                                        "0",
                                                                        "0",
                                                                        "0",
                                                                        adapter.getItem(e).getSzPriceSegmentId()));
                                                            }
                                                        }

                                                    }

                                                }

                                            } else {
                                                pDialog.dismissWithAnimation();
                                                Intent intent = new Intent(getBaseContext(), summary_order.class);
                                                startActivity(intent);
                                                for(int e = 0; e < data_produk_penjualan_pojos.size();e++){

                                                    if (!(adapter.getItem(e).getExpired_date() == null)){
                                                        Double harga = Double.valueOf(adapter.getItem(e).getDecPrice());
                                                        int total_harga = harga.intValue() * Integer.parseInt(adapter.getItem(e).getStock_qty());
                                                        totalPenjualanPojos.add(new total_penjualan_pojo(
                                                                adapter.getItem(e).getSzId(),
                                                                adapter.getItem(e).getSzName(),
                                                                adapter.getItem(e).getDecPrice(),
                                                                adapter.getItem(e).getStock_qty(),
                                                                adapter.getItem(e).getDisplay(),
                                                                String.valueOf(total_harga),
                                                                adapter.getItem(e).getDisc_total(),
                                                                adapter.getItem(e).getStock(),
                                                                adapter.getItem(e).getExpired(),
                                                                adapter.getItem(e).getExpired_date(),
                                                                adapter.getItem(e).getSzUomId(),
                                                                adapter.getItem(e).getDisc_distributor(),
                                                                adapter.getItem(e).getDisc_internal(),
                                                                adapter.getItem(e).getDisc_principle(),
                                                                adapter.getItem(e).getSzPriceSegmentId()));

                                                        if(adapter.getItem(e).getSzId().equals("74559")){
                                                            totalPenjualanPojos.add(new total_penjualan_pojo(
                                                                    "19310",
                                                                    "AQ.TISSUE",
                                                                    "0.0000",
                                                                    adapter.getItem(e).getStock_qty(),
                                                                    adapter.getItem(e).getDisplay(),
                                                                    "0",
                                                                    adapter.getItem(e).getDisc_total(),
                                                                    adapter.getItem(e).getStock(),
                                                                    adapter.getItem(e).getExpired(),
                                                                    adapter.getItem(e).getExpired_date(),
                                                                    "LEMBAR",
                                                                    "0",
                                                                    "0",
                                                                    "0",
                                                                    adapter.getItem(e).getSzPriceSegmentId()));


                                                        } else if (adapter.getItem(e).getSzId().equals("74560")){
                                                            totalPenjualanPojos.add(new total_penjualan_pojo(
                                                                    "29310",
                                                                    "VT.TISSUE",
                                                                    "0.0000",
                                                                    adapter.getItem(e).getStock_qty(),
                                                                    adapter.getItem(e).getDisplay(),
                                                                    "0",
                                                                    adapter.getItem(e).getDisc_total(),
                                                                    adapter.getItem(e).getStock(),
                                                                    adapter.getItem(e).getExpired(),
                                                                    adapter.getItem(e).getExpired_date(),
                                                                    "LEMBAR",
                                                                    "0",
                                                                    "0",
                                                                    "0",
                                                                    adapter.getItem(e).getSzPriceSegmentId()));
                                                        }
                                                    }

                                                }

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
                                    if (error instanceof TimeoutError){
                                        new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.WARNING_TYPE)
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
                                        Intent intent = new Intent(getBaseContext(), summary_order.class);
                                        startActivity(intent);
                                        for(int e = 0; e < data_produk_penjualan_pojos.size();e++) {

                                            if (!(adapter.getItem(e).getExpired_date() == null)) {
                                                Double harga = Double.valueOf(adapter.getItem(e).getDecPrice());
                                                int total_harga = harga.intValue() * Integer.parseInt(adapter.getItem(e).getStock_qty());
                                                totalPenjualanPojos.add(new total_penjualan_pojo(
                                                        adapter.getItem(e).getSzId(),
                                                        adapter.getItem(e).getSzName(),
                                                        adapter.getItem(e).getDecPrice(),
                                                        adapter.getItem(e).getStock_qty(),
                                                        adapter.getItem(e).getDisplay(),
                                                        String.valueOf(total_harga),
                                                        adapter.getItem(e).getDisc_total(),
                                                        adapter.getItem(e).getStock(),
                                                        adapter.getItem(e).getExpired(),
                                                        adapter.getItem(e).getExpired_date(),
                                                        adapter.getItem(e).getSzUomId(),
                                                        adapter.getItem(e).getDisc_distributor(),
                                                        adapter.getItem(e).getDisc_internal(),
                                                        adapter.getItem(e).getDisc_principle(),
                                                        adapter.getItem(e).getSzPriceSegmentId()));

                                                if(adapter.getItem(e).getSzId().equals("74559")){
                                                    totalPenjualanPojos.add(new total_penjualan_pojo(
                                                            "19310",
                                                            "AQ.TISSUE",
                                                            "0.0000",
                                                            adapter.getItem(e).getStock_qty(),
                                                            adapter.getItem(e).getDisplay(),
                                                            "0",
                                                            "0",
                                                            adapter.getItem(e).getStock(),
                                                            adapter.getItem(e).getExpired(),
                                                            adapter.getItem(e).getExpired_date(),
                                                            "LEMBAR",
                                                            "0",
                                                            "0",
                                                            "0",
                                                            adapter.getItem(e).getSzPriceSegmentId()));


                                                } else if (adapter.getItem(e).getSzId().equals("74560")){
                                                    totalPenjualanPojos.add(new total_penjualan_pojo(
                                                            "29310",
                                                            "VT.TISSUE",
                                                            "0.0000",
                                                            adapter.getItem(e).getStock_qty(),
                                                            adapter.getItem(e).getDisplay(),
                                                            "0",
                                                            "0",
                                                            adapter.getItem(e).getStock(),
                                                            adapter.getItem(e).getExpired(),
                                                            adapter.getItem(e).getExpired_date(),
                                                            "LEMBAR",
                                                            "0",
                                                            "0",
                                                            "0",
                                                            adapter.getItem(e).getSzPriceSegmentId()));
                                                }
                                            }

                                        }

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
                    RequestQueue requestQueue = Volley.newRequestQueue(product_penjualan.this);
                    requestQueue.add(stringRequest);



            }
        });

        cariproduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });




    }

    private void getConfig() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String[] parts = nik_baru.split("-");
        String restnomor = parts[0];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Config?szId=" + restnomor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);


                                MaxQty = Integer.parseInt(movieObject.getString("szValue"));





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
        RequestQueue requestQueue = Volley.newRequestQueue(product_penjualan.this);
        requestQueue.add(stringRequest);
    }

    private void enableSearchView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                enableSearchView(child, enabled);
            }
        }
    }

    private void getNumber() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LastNumbers?szDocId=" + noSO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);


                                if(pilihkategori.getText().toString().equals("BOTOL")){
                                    lastnumber = 0;
                                } else {
                                    lastnumber = Integer.parseInt(movieObject.getString("intnumber"));
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
                        lastnumber = 0;
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
        RequestQueue requestQueue = Volley.newRequestQueue(product_penjualan.this);
        requestQueue.add(stringRequest);

    }

    private void getRemain() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CekData?szCustomerId="+no_surat+"&szEmployeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                lastremain = Integer.parseInt(movieObject.getString("COUNT"));






                            }




                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lastremain = 0;
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
        RequestQueue requestQueue = Volley.newRequestQueue(product_penjualan.this);
        requestQueue.add(stringRequest);

    }

    public class ListViewAdapteProductPenjualan extends ArrayAdapter<data_produk_penjualan_pojo> {

        private class ViewHolder {
            TextView namaproduk, qty_order;
            ImageButton refresh;
            MaterialButton tambahproduk;
            ImageView icons;
        }

        List<data_produk_penjualan_pojo> data_produk_penjualan_pojos;
        ArrayList<data_produk_penjualan_pojo> data_produk_penjualanList;
        private final Context context;

        public ListViewAdapteProductPenjualan(List<data_produk_penjualan_pojo> data_produk_penjualan_pojos, Context context) {
            super(context, R.layout.list_penjualanproduct, data_produk_penjualan_pojos);
            this.data_produk_penjualan_pojos = data_produk_penjualan_pojos;
            this.data_produk_penjualanList = new ArrayList<data_produk_penjualan_pojo>();
            this.data_produk_penjualanList.addAll(data_produk_penjualan_pojos);
            this.context = context;

        }



        public int getCount() {
            return data_produk_penjualan_pojos.size();
        }

        public data_produk_penjualan_pojo getItem(int position) {
            return data_produk_penjualan_pojos.get(position);
        }


        @Override
        public int getViewTypeCount() {
            int count;
            if (data_produk_penjualan_pojos.size() > 0) {
                count = getCount();
            } else {
                count = 1;
            }
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public long getItemId(int position) {
            return 0;
        }




        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            data_produk_penjualan_pojo data = getItem(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_penjualanproduct, parent, false);

                viewHolder.namaproduk = convertView.findViewById(R.id.namaproduk);
                viewHolder.qty_order = convertView.findViewById(R.id.qty_order);

                viewHolder.refresh = convertView.findViewById(R.id.refresh);
                viewHolder.tambahproduk = convertView.findViewById(R.id.tambahproduk);
//                viewHolder.icons = (ImageView) convertView.findViewById(R.id.icons);


                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            if(!pilihkategori.getText().toString().equals("BOTOL")){
//                viewHolder.icons.setImageDrawable(getResources().getDrawable(R.drawable.crate));
//            }

            viewHolder.namaproduk.setText(data.getSzName());
            if(data.getStock_qty() == null){
                viewHolder.qty_order.setText("Order : 0");
            } else {
                viewHolder.qty_order.setText("Order : " + data.getStock_qty());
            }

            viewHolder.tambahproduk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (countDownTimer == null) {
                        float Second= (float) 0.25; //Detecting the type of event within a quarter of a second
                        countDownTimer= new CountDownTimer((long) (Second * 1000), 50) {
                            @Override public void onTick(long l){

                            }
                            @Override
                            public void onFinish() {
                                if (click_duble >= 2) {

                                } else {
                                    if(data.getQty().equals("0") || data.getQty().equals("null")){
                                        new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Stock Kosong")
                                                .setConfirmText("OK")
                                                .show();
                                    } else {
                                        pDialog = new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.PROGRESS_TYPE);
                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                        pDialog.setTitleText("Harap Menunggu");
                                        pDialog.setCancelable(false);
                                        pDialog.show();
                                        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Compare_Product?szDocId="+noSO+"&szProductId=" + data.getSzId(),
                                                new Response.Listener<String>() {

                                                    @Override
                                                    public void onResponse(String response) {
                                                        pDialog.dismissWithAnimation();
                                                        new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.WARNING_TYPE)
                                                                .setTitleText("Product ini sudah ada, silahkan check history penjualan")
                                                                .setConfirmText("OK")
                                                                .show();

                                                    }
                                                }, new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                pDialog.dismissWithAnimation();
                                                showIsiHarga(String.valueOf(position), data.getSzName(), data.getDecPrice(), data.getQty());
//                                                intent.putExtra("list", String.valueOf(position));
//                                                intent.putExtra("nama_barang", data.getSzName());
//                                                intent.putExtra("uang", data.getDecPrice());
//                                                startActivity(intent);
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
                                                SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                String nik_baru = sharedPreferences.getString("szDocCall", null);

                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                                String currentDateandTime2 = sdf2.format(new Date());


                                                return params;
                                            }

                                        };
                                        stringRequest2.setRetryPolicy(
                                                new DefaultRetryPolicy(
                                                        500000,
                                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                )
                                        );
                                        if (requestQueue == null) {
                                            requestQueue = Volley.newRequestQueue(product_penjualan.this);
                                            requestQueue.add(stringRequest2);
                                        } else {
                                            requestQueue.add(stringRequest2);
                                        }
                                    }


                                }
                                click_duble = 1;
                                countDownTimer = null;
                            }};countDownTimer.start();
                    }else {
                        click_duble += 1;
                    }

                }
            });

            viewHolder.refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product_penjualan.adapter.getItem(position).setStock(null);
                    product_penjualan.adapter.getItem(position).setDisplay(null);
                    product_penjualan.adapter.getItem(position).setExpired(null);
                    product_penjualan.adapter.getItem(position).setExpired_date(null);

                    product_penjualan.adapter.getItem(position).setStock_qty(null);
                    product_penjualan.adapter.getItem(position).setDisc_principle(null);
                    product_penjualan.adapter.getItem(position).setDisc_distributor(null);
                    product_penjualan.adapter.getItem(position).setDisc_internal(null);
                    product_penjualan.adapter.getItem(position).setDisc_total(null);

                    viewHolder.qty_order.setText("Order : 0");

                    product_penjualan.adapter.notifyDataSetChanged();

                }
            });




            return convertView;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            data_produk_penjualan_pojos.clear();
            if (charText.length() == 0) {
                data_produk_penjualan_pojos.addAll(data_produk_penjualanList);
            } else {
                for (data_produk_penjualan_pojo wp : data_produk_penjualanList) {
                    if (wp.getSzName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        data_produk_penjualan_pojos.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private void showIsiHarga(String position, String szName, String decPrice, String qty) {
        final Dialog dialog = new Dialog(product_penjualan.this);
        dialog.setContentView(R.layout.isi_harga);
        dialog.setCancelable(false);
        dialog.show();

        ImageButton qtystockminus = dialog.findViewById(R.id.qtystockminus);
        ImageButton qtystockadd = dialog.findViewById(R.id.qtystockadd);

        ImageButton qtydisplayminus = dialog.findViewById(R.id.qtydisplayminus);
        ImageButton qtydisplayadd = dialog.findViewById(R.id.qtydisplayadd);

        ImageButton qtyexpiredminus = dialog.findViewById(R.id.qtyexpiredminus);
        ImageButton qtyexpiredadd = dialog.findViewById(R.id.qtyexpiredadd);

        ImageButton qtyorderminus = dialog.findViewById(R.id.qtyorderminus);
        ImageButton qtyorderadd = dialog.findViewById(R.id.qtyorderadd);
        
        EditText qtystock = dialog.findViewById(R.id.qtystock);
        EditText qtydisplay = dialog.findViewById(R.id.qtydisplay);
        EditText qtyexpired = dialog.findViewById(R.id.qtyexpired);
        EditText qtyorder = dialog.findViewById(R.id.qtyorder);
        EditText tglorder = dialog.findViewById(R.id.tglorder);

        TextView namaproduk = dialog.findViewById(R.id.namaproduk);
        TextView harga = dialog.findViewById(R.id.harga);

        EditText disc_principal = dialog.findViewById(R.id.disc_principal);
        EditText disc_distributor = dialog.findViewById(R.id.disc_distributor);
        EditText disc_internal = dialog.findViewById(R.id.disc_internal);

        Button batal = dialog.findViewById(R.id.batal);
        Button order = dialog.findViewById(R.id.order);

        namaproduk.setText(szName);

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        String[] parts = decPrice.split("\\.");
        String szIdSlice = parts[0];

        harga.setText("Harga Per Produk = " + kursIndonesia.format(Integer.parseInt(szIdSlice)));

        count = 0;
        count1 = 0;
        count2 = 0;
        count3 = 0;

        qtyorderadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count3++;
                qtyorder.setText(String.valueOf(count3));
                qtyorder.setError(null);
            }

        });
        qtyorderminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtyorder.setText(String.valueOf(count3));
                qtyorder.setError(null);
                if (count3 == 0) {
                    return;
                }
                count3--;
            }
        });
        
        qtyexpiredadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count2++;
                qtyexpired.setText(String.valueOf(count2));
            }

        });
        qtyexpiredminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtyexpired.setText(String.valueOf(count2));
                if (count2 == 0) {
                    return;
                }
                count2 --;
            }
        });

        qtydisplayadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count1++;
                qtydisplay.setText(String.valueOf(count1));
            }

        });
        qtydisplayminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtydisplay.setText(String.valueOf(count1));
                if (count1 == 0) {
                    return;
                }
                count1--;
            }
        });

        qtystockadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                qtystock.setText(String.valueOf(count));
            }

        });
        qtystockminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtystock.setText(String.valueOf(count));
                if (count == 0) {
                    return;
                }
                count--;
            }
        });



        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM yyyy");
        String currentDateandTime2 = sdf2.format(new Date());
        tglorder.setText(currentDateandTime2);
        tglorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                Calendar twoDaysAgo = (Calendar) currentDate.clone();
                twoDaysAgo.add(Calendar.DATE, 0);

                date = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date.set(year, monthOfYear, dayOfMonth);

                        tglorder.setText(dateFormatter.format(date.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(product_penjualan.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(twoDaysAgo.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        order.setOnClickListener(v -> {
            qtystock.setError(null);
            qtydisplay.setError(null);
            qtyexpired.setError(null);
            String sourceString = "<b>" + qty + "</b> ";

            if(qtystock.getText().toString().length() == 0){
                qtystock.setError("Qty Stok Wajib Di isi");
            } else if(qtydisplay.getText().toString().length() == 0){
                qtydisplay.setError("Qty Display Wajib Di isi");
            } else if(qtyexpired.getText().toString().length() == 0){
                qtyexpired.setError("Qty Expired Di isi");
            } else if (qtyorder.getText().toString().length() == 0 || qtyorder.getText().toString().equals("0")) {
                qtyorder.setError("Qty Order Di isi");
            } else if (Integer.parseInt(qty) < Integer.parseInt(qtyorder.getText().toString())){
                new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Qty Order tidak boleh melebihi kapasitas stok. Sisa stok produk ini adalah " + Html.fromHtml(sourceString))
                        .setConfirmText("OK")
                        .show();
            } else if (MaxQty < Integer.parseInt(qtyorder.getText().toString())){
                new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Produk harus maksimal " + String.valueOf(MaxQty))
                        .setConfirmText("OK")
                        .show();
            } else {
                if(disc_distributor.getText().toString().isEmpty()){
                    disc_distributor.setText("0");
                }

                if(disc_internal.getText().toString().isEmpty()){
                    disc_internal.setText("0");
                }

                if(disc_principal.getText().toString().isEmpty()){
                    disc_principal.setText("0");
                }

                if(qtyorder.getText().toString().length() == 0){
                    hargaproduk = Integer.parseInt(szIdSlice) * 0;
                } else {
                    hargaproduk = Integer.parseInt(szIdSlice) * Integer.parseInt(qtyorder.getText().toString());
                }

                discount = Integer.parseInt(disc_principal.getText().toString()) + Integer.parseInt(disc_distributor.getText().toString()) + Integer.parseInt(disc_internal.getText().toString());


                if(qtyorder.getText().toString().length() == 0 || qtyorder.getText().toString().equals("0")){
                    qtyorder.setError("Qty tidak boleh kosong");
                } else if(decPrice.equals("0.0000")){
                    int total = Integer.parseInt(qtyorder.getText().toString()) + Integer.parseInt(disc_principal.getText().toString()) + Integer.parseInt(disc_distributor.getText().toString()) + Integer.parseInt(disc_internal.getText().toString());

                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setStock(qtystock.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisplay(qtydisplay.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setExpired(qtyexpired.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setExpired_date(tglorder.getText().toString());

                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setStock_qty(qtyorder.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_principle(disc_principal.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_distributor(disc_distributor.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_internal(disc_internal.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_total(String.valueOf(total));

                    Intent intent = new Intent(getApplicationContext(), product_penjualan.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    product_penjualan.adapter.notifyDataSetChanged();
                    cariproduct.setQuery("", false);
                } else if (hargaproduk <= discount){
                    disc_principal.setText("0");
                    disc_distributor.setText("0");
                    disc_internal.setText("0");
                    new SweetAlertDialog(product_penjualan.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Diskon Tidak Boleh Lebih Besar dari Total Harga")
                            .setConfirmText("OK")
                            .show();

                } else {
                    int total = Integer.parseInt(qtyorder.getText().toString()) + Integer.parseInt(disc_principal.getText().toString()) + Integer.parseInt(disc_distributor.getText().toString()) + Integer.parseInt(disc_internal.getText().toString());

                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setStock(qtystock.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisplay(qtydisplay.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setExpired(qtyexpired.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setExpired_date(tglorder.getText().toString());

                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setStock_qty(qtyorder.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_principle(disc_principal.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_distributor(disc_distributor.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_internal(disc_internal.getText().toString());
                    product_penjualan.adapter.getItem(Integer.parseInt(position)).setDisc_total(String.valueOf(total));

                    product_penjualan.adapter.notifyDataSetChanged();
                    cariproduct.setQuery("", false);

                }
                dialog.dismiss();
            }


        });
        
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        lanjutkan.setEnabled(true);
    }


//    @Override
//    public void onBackPressed() {
//        adapter.clear();
//        finish();
//        super.onBackPressed();
//    }
}