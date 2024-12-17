package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.tvip.sfa.Perangkat.ExceptionHandler;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tvip.sfa.R;
import com.tvip.sfa.pojo.data_produk_penjualan_pojo;
import com.tvip.sfa.pojo.total_penjualan_pojo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.noSO;
import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.pengaturanBar;
import static com.tvip.sfa.menu_mulai_perjalanan.mulai_perjalanan.noSOInduk;
import static com.tvip.sfa.menu_mulai_perjalanan.product_penjualan.data_produk_penjualan_pojos;
import static com.tvip.sfa.menu_mulai_perjalanan.product_penjualan.lastnumber;
import static com.tvip.sfa.menu_mulai_perjalanan.product_penjualan.lastremain;
import static com.tvip.sfa.menu_mulai_perjalanan.product_penjualan.soldtobranch;
import static com.tvip.sfa.menu_mulai_perjalanan.product_penjualan.totalPenjualanPojos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class summary_order extends AppCompatActivity {

    private RequestQueue requestQueue;
    private RequestQueue requestQueue2;
    private RequestQueue requestQueue3;
    private RequestQueue requestQueue4;

    private RequestQueue requestQueue5;
    private RequestQueue requestQueue6;
    private RequestQueue requestQueue7;
    private RequestQueue requestQueue8;

    private RequestQueue requestQueue9;
    private RequestQueue requestQueue10;
    private RequestQueue requestQueue11;
    private RequestQueue requestQueue12;

    private RequestQueue requestQueue13;
    private RequestQueue requestQueue14;
    private RequestQueue requestQueue15;
    private RequestQueue requestQueue16;

    private RequestQueue requestQueue17;
    private RequestQueue requestQueue18;
    private RequestQueue requestQueue19;
    private RequestQueue requestQueue20;

    private RequestQueue requestQueue21;
    private RequestQueue requestQueue22;
    private RequestQueue requestQueue23;
    private RequestQueue requestQueue24;

    private RequestQueue requestQueue25;
    private RequestQueue requestQueue26;
    private RequestQueue requestQueue27;
    private RequestQueue requestQueue28;

    private RequestQueue requestQueue29;
    private RequestQueue requestQueue30;
    private RequestQueue requestQueue31;
    private RequestQueue requestQueue32;

    private RequestQueue requestQueue33;
    private RequestQueue requestQueue34;
    private RequestQueue requestQueue35;
    private RequestQueue requestQueue36;
    private RequestQueue requestQueue52;

    private RequestQueue requestQueue53;
    private RequestQueue requestQueue54;

    private RequestQueue requestQueue55;

    private RequestQueue requestQueue56, requestQueue57;

    StringRequest stringRequest100;
    ListView listtotalpenjualan;
    ListViewAdapterTotalPenjualan adapter;
    TextView jumlah, diskon, total;
    int jumlah_harga = 0;
    int jumlah_diskon = 0;
    int jumlah_stock = 0;
    int total_harga = 0;
    String condition;


    Handler handler = new Handler();
    int apiDelayed = 3*1000; //1 second=1000 milisecond, 5*1000=5seconds
    Runnable runnable;

    String bStarted, bFinisihed, bVisited, bSuccess, szFailReason,
            bPostPone, szLangitude, szLongitude, bOutOfRoute, bNewCustomer, bScanBarcode,
            decDuration, szReasonIdCheckin, szReasonFailedScan, dtmStart;


    int discount = 0;

    String statusLoop;

    int angka = 0;

    String SOInduk, newSO;

    Button batal, lanjutkan;
    AutoCompleteTextView pilihpembayaran;
    EditText tanggalkirim;
    EditText catatan;
    // SignaturePad signature_pad;
    // ImageButton refresh;
    BottomSheetBehavior sheetBehavior;
    BottomSheetDialog sheetDialog;
    View bottom_sheet;

    int waktu = 0;

    EditText noSiInduk;
    int max;
    private static byte[] reserve = new byte[1024 * 1024]; // Reserves 1MB.

    SweetAlertDialog pDialog, success;

    private SimpleDateFormat dateFormatter;
    private Calendar date;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(SavedInstanceFragment.getInstance(getFragmentManager()).popData());
        HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_summary_order);

        getCacheDir().delete();

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        listtotalpenjualan = findViewById(R.id.listtotalpenjualan);
        noSiInduk = findViewById(R.id.noSiInduk);

        dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        pilihpembayaran = findViewById(R.id.pilihpembayaran);
        tanggalkirim = findViewById(R.id.tanggalkirim);
        catatan = findViewById(R.id.catatan);
        // signature_pad = findViewById(R.id.signature_pad);
       // refresh = findViewById(R.id.refresh);

        jumlah = findViewById(R.id.jumlah);
        diskon = findViewById(R.id.diskon);
        total = findViewById(R.id.total);

        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);
        // refresh.bringToFront();
        getNoSO();
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signature_pad.clear();
//            }
//        });

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        boolean isOutOfMemory = false;
        try {

        }
        catch (OutOfMemoryError ex) {
            System.gc();
            isOutOfMemory = true;
        }

        pilihpembayaran.setText("Tunai");
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        max = product_penjualan.MaxQty;

        statusLoop = "gagal";


//        if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
//            product_penjualan.MaxQty = 250;
//        } else {
//        }



        getData();
        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    if(totalPenjualanPojos.isEmpty()){
                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Produk masih kosong")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    } else if (noSiInduk.getText().toString().length() == 0){
                        noSiInduk.setError("No SO Induk Kosong");
                    } else if (pilihpembayaran.getText().toString().length() == 0){
                        pilihpembayaran.setError("Pilih Pembayaran");
                    } else if (tanggalkirim.getText().toString().length() == 0){
                        tanggalkirim.setError("Isi Tanggal Kirim");
                    } else if (catatan.getText().toString().length() == 0){
                        catatan.setError("Isi Catatan");
                    } else {
                        pDialog = new SweetAlertDialog(summary_order.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("Harap Menunggu");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        postReady();

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            final String[] status = new String[1];
//                            status[0] = "Valid";
//                            if(status[0].equals("Valid")){
//
//                            }
////                            for(int i = 0;i <=  totalPenjualanPojos.size() - 1 ;i++){
////                                int finalI = i;
////                                stringRequest100 = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_cek_warehouse?szProductId=" + adapter.getItem(i).getSzId() + "&warehouse=" + soldtobranch + "-W01",
////                                        new Response.Listener<String>() {
////                                            @Override
////                                            public void onResponse(String response) {
////                                                try {
////                                                    int number = 0;
////                                                    JSONObject obj = new JSONObject(response);
////                                                    final JSONArray movieArray = obj.getJSONArray("data");
////                                                    final JSONObject movieObject = movieArray.getJSONObject(0);
////
////                                                    if(movieObject.getInt("decQtyOnHand") == 0){
////                                                        pDialog.dismissWithAnimation();
////                                                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.WARNING_TYPE)
////                                                                .setTitleText("Stock "+ adapter.getItem(finalI).getSzName() +" Kosong")
////                                                                .setConfirmText("OK")
////                                                                .show();
////                                                        status[0] = "Invalid";
////                                                    } else if(movieObject.getInt("decQtyOnHand") < Integer.parseInt(adapter.getItem(finalI).getStock())){
////                                                        pDialog.dismissWithAnimation();
////                                                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.WARNING_TYPE)
////                                                                .setTitleText("Stock Produk " + adapter.getItem(finalI).getSzName() + " Melebihi Stock Gudang")
////                                                                .setConfirmText("OK")
////                                                                .show();
////                                                        status[0] = "Invalid";
////                                                    } else {
////                                                        if (finalI == totalPenjualanPojos.size() - 1) {
////
////
////                                                        }
////                                                    }
////
////
////                                                } catch(JSONException e){
////                                                    e.printStackTrace();
////
////                                                }
////                                            }
////                                        },
////                                        new Response.ErrorListener() {
////                                            @Override
////                                            public void onErrorResponse(VolleyError error) {
////
////                                                if(statusLoop.equals("gagal")){
////                                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
////                                                        statusLoop = "Sukses";
////                                                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
////                                                                .setTitleText("Jaringan Bermasalah")
////                                                                .setConfirmText("OK")
////                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                                                                    @Override
////                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                                                        pDialog.dismissWithAnimation();
////                                                                        sweetAlertDialog.dismissWithAnimation();
////                                                                        statusLoop = "gagal";
////                                                                    }
////                                                                })
////                                                                .show();
////                                                    } else if (error instanceof AuthFailureError) {
////                                                        Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
////                                                    } else if (error instanceof ServerError) {
////
////                                                    } else if (error instanceof NetworkError) {
////                                                        Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
////                                                    } else if (error instanceof ParseError) {
////                                                        Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
////                                                    }
////                                                }
////                                            }
////                                        })
////
////                                {
////                                    @Override
////                                    public Map<String, String> getHeaders() throws AuthFailureError {
////                                        HashMap<String, String> params = new HashMap<String, String>();
////                                        String creds = String.format("%s:%s", "admin", "Databa53");
////                                        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
////                                        params.put("Authorization", auth);
////                                        return params;
////                                    }
////                                };
////
////                                stringRequest100.setRetryPolicy(
////                                        new DefaultRetryPolicy(
////                                                5000,
////                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
////                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
////                                        ));
////                                if (requestQueue56 == null) {
////                                    requestQueue56 = Volley.newRequestQueue(summary_order.this);
////                                    requestQueue56.add(stringRequest100);
////                                } else {
////                                    requestQueue56.add(stringRequest100);
////                                }
////
////                            }
//
//                        }
//                    }, 2000);


                    }
                } else {
                    new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Jaringan tidak stabil, silahkan coba lagi")
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

            private void postReady() {

                    if(pengaturanBar.getTitle().toString().equals("Pelanggan Dalam Rute")){
                        if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoData",
                                    new Response.Listener<String>() {

                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObj = new JSONObject(response);
                                                String message = jsonObj.getString("message");
                                                menu_pelanggan.noSO = message;
                                                System.out.println("Message = " + response.toString());


                                                if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                                                    PostAQ(menu_pelanggan.noSO);
                                                    postRemain();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();

                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    if(statusLoop.equals("gagal")){
                                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                            statusLoop = "Sukses";
                                            System.out.println("Error system API :1");

                                            new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Jaringan Bermasalah")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            pDialog.dismissWithAnimation();
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            statusLoop = "gagal";
                                                        }
                                                    })
                                                    .show();
                                        } else if (error instanceof AuthFailureError) {
                                            Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                        } else if (error instanceof ServerError) {

                                        } else if (error instanceof NetworkError) {
                                            Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                        } else if (error instanceof ParseError) {
                                            Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                    String nik_baru = sharedPreferences.getString("szDocCall", null);

                                    String[] parts = nik_baru.split("-");
                                    String branch = parts[0];

                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String currentDateandTime2 = sdf2.format(new Date());

                                    params.put("szMobileId", "");

                                    params.put("dtmDoc", currentDateandTime2);
                                    params.put("szCustomerId", no_surat);
                                    params.put("szEmployeeId", nik_baru);

                                    params.put("bCash", "1");

                                    params.put("szPaymentTermId", "0HARI");

                                    params.put("dtmPO", currentDateandTime2);
                                    params.put("dtmPOExpired", currentDateandTime2);
                                    params.put("szBranchId", soldtobranch);
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
                            RequestQueue requestQueue2 = Volley.newRequestQueue(summary_order.this);
                            requestQueue2.getCache().clear();
                            requestQueue2.add(stringRequest2);
                        } else {
                            postRemain();
                        }
                    } else {
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CheckDraft?szDocId=" + noSO,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            int number = 0;
                                            JSONObject obj = new JSONObject(response);
                                            final JSONArray movieArray = obj.getJSONArray("data");
                                            for (int i = 0; i < movieArray.length(); i++) {
                                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                                System.out.println("Status Sales Order = " + movieObject.getString("szDocStatus"));
                                                if(movieObject.getString("szDocStatus").equals("Draft")){
                                                    cekComparing("Draft");
                                                } else {
                                                    cekComparing("Applied");
                                                }

                                            }
                                        } catch(JSONException e){
                                            e.printStackTrace();

                                        }
                                    }

                                    private void cekComparing(String applied) {
                                        if(pengaturanBar.getTitle().toString().equals("Pelanggan Luar Rute")) {
                                            if(applied.equals("Applied")){
                                                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoData",
                                                        new Response.Listener<String>() {

                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonObj = new JSONObject(response);
                                                                    String message = jsonObj.getString("message");

                                                                    menu_pelanggan.noSO = message;
                                                                    PostAQ(message);
                                                                    postRemain();
                                                                    lastnumber = 0;

                                                                    System.out.println("Status Sales Message = " + response.toString());




                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();

                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                        if(statusLoop.equals("gagal")){
                                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                                statusLoop = "Sukses";
                                                                System.out.println("Error system API :2");

                                                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                                        .setTitleText("Jaringan Bermasalah")
                                                                        .setConfirmText("OK")
                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                                pDialog.dismissWithAnimation();
                                                                                sweetAlertDialog.dismissWithAnimation();
                                                                                statusLoop = "gagal";
                                                                            }
                                                                        })
                                                                        .show();
                                                            } else if (error instanceof AuthFailureError) {
                                                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                                            } else if (error instanceof ServerError) {

                                                            } else if (error instanceof NetworkError) {
                                                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                                            } else if (error instanceof ParseError) {
                                                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                                                        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                        String nik_baru = sharedPreferences.getString("szDocCall", null);

                                                        String[] parts = nik_baru.split("-");
                                                        String branch = parts[0];

                                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        String currentDateandTime2 = sdf2.format(new Date());

                                                        params.put("szMobileId", "");

                                                        params.put("dtmDoc", currentDateandTime2);
                                                        params.put("szCustomerId", no_surat);
                                                        params.put("szEmployeeId", nik_baru);

                                                        params.put("bCash", "1");

                                                        params.put("szPaymentTermId", "0HARI");

                                                        params.put("dtmPO", currentDateandTime2);
                                                        params.put("dtmPOExpired", currentDateandTime2);
                                                        params.put("szBranchId", soldtobranch);
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
                                                RequestQueue requestQueue2 = Volley.newRequestQueue(summary_order.this);
                                                requestQueue2.getCache().clear();
                                                requestQueue2.add(stringRequest2);
                                            } else {
                                                postRemain();
                                            }


                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        if(statusLoop.equals("gagal")){
                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                statusLoop = "Sukses";
                                                System.out.println("Error system API :3");

                                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Jaringan Bermasalah")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                pDialog.dismissWithAnimation();
                                                                sweetAlertDialog.dismissWithAnimation();
                                                                statusLoop = "gagal";
                                                            }
                                                        })
                                                        .show();
                                            } else if (error instanceof AuthFailureError) {
                                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ServerError) {

                                            } else if (error instanceof NetworkError) {
                                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ParseError) {
                                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                        RequestQueue requestQueue = Volley.newRequestQueue(summary_order.this);
                        requestQueue.add(stringRequest);
                    }
                }


            private void postRemain() {
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CekData?szCustomerId="+no_surat+"&szEmployeeId=" + nik_baru,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    final JSONArray movieArray = obj.getJSONArray("data");
                                    for (int i = 0; i < movieArray.length(); i++) {
                                        final JSONObject movieObject = movieArray.getJSONObject(i);

                                        header(movieObject.getString("szDocId"));
                                        sub(movieObject.getString("szDocId"), movieObject.getInt("COUNT"));

                                    }


                                } catch(JSONException e){
                                    e.printStackTrace();

                                }
                            }

                            private void sub(String iDremain, int count) {
                                for(int i = 0;i <=  totalPenjualanPojos.size() - 1 ;i++) {
                                    int finalI1 = i;
                                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocRemainStockItem",
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    if(finalI1 == totalPenjualanPojos.size() -1){
                                                        postSoDocItem(menu_pelanggan.noSO);
                                                    }


                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                            if(statusLoop.equals("gagal")){
                                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                    statusLoop = "Sukses";
                                                    System.out.println("Error system API :4");

                                                    new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText("Jaringan Bermasalah")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    pDialog.dismissWithAnimation();
                                                                    sweetAlertDialog.dismissWithAnimation();
                                                                    statusLoop = "gagal";
                                                                }
                                                            })
                                                            .show();
                                                } else if (error instanceof AuthFailureError) {
                                                    Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                                } else if (error instanceof ServerError) {
                                                        if(finalI1 == totalPenjualanPojos.size() -1){
                                                            postSoDocItem(menu_pelanggan.noSO);
                                                        }

                                                } else if (error instanceof NetworkError) {
                                                    Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                                } else if (error instanceof ParseError) {
                                                    Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                                            sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

                                            params.put("szDocId", iDremain);

                                            params.put("szProductId", adapter.getItem(finalI1).getSzId());
                                            if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                                                params.put("intItemNumber", String.valueOf(finalI1));
                                            } else {
                                                params.put("intItemNumber", String.valueOf(finalI1 + lastremain));
                                            }

                                            params.put("decQtyStock", adapter.getItem(finalI1).getStock());
                                            params.put("decQtyDisplay", adapter.getItem(finalI1).getDisplay());
                                            params.put("decQtyExpired", adapter.getItem(finalI1).getExpired());
                                            params.put("dtmExpired", convertFormat(adapter.getItem(finalI1).getExpired_date()) + " 00:00:00");

                                            System.out.println("Params Sub = " + params);

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
                                        requestQueue = Volley.newRequestQueue(summary_order.this);
                                        requestQueue.add(stringRequest2);
                                    } else {
                                        requestQueue.add(stringRequest2);
                                    }
                                }
                            }

                            private void header(String iDremain) {
                                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocRemainStock",
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        if(statusLoop.equals("gagal")){
                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                statusLoop = "Sukses";
                                                System.out.println("Error system API :5");

                                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Jaringan Bermasalah")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                pDialog.dismissWithAnimation();
                                                                sweetAlertDialog.dismissWithAnimation();
                                                                statusLoop = "gagal";
                                                            }
                                                        })
                                                        .show();
                                            } else if (error instanceof AuthFailureError) {
                                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ServerError) {

                                            } else if (error instanceof NetworkError) {
                                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ParseError) {
                                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                                        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                        String nik_baru = sharedPreferences.getString("szDocCall", null);


                                        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String currentDateandTime3 = sdf3.format(new Date());

                                        String[] parts = nik_baru.split("-");
                                        String restnomor = parts[0];

                                        params.put("szDocId", iDremain);
                                        params.put("dtmDoc", currentDateandTime3);
                                        params.put("szCustomerId", no_surat);

                                        params.put("szEmployeeId", nik_baru);
                                        params.put("szBranchId", restnomor);

                                        if(restnomor.equals("321") || restnomor.equals("336") || restnomor.equals("317") || restnomor.equals("036")){
                                            params.put("szCompanyId", "ASA");
                                        } else {
                                            params.put("szCompanyId", "TVIP");
                                        }
                                        params.put("szUserCreatedId", nik_baru);
                                        params.put("dtmCreated", currentDateandTime3);
                                        params.put("szDocCallId", mulai_perjalanan.id_pelanggan);
                                        System.out.println("Params Head = " + params);
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
                                    requestQueue2 = Volley.newRequestQueue(summary_order.this);
                                    requestQueue2.add(stringRequest2);
                                } else {
                                    requestQueue2.add(stringRequest2);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_IdRemain?szDocId="+ nik_baru,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    final JSONArray movieArray = obj.getJSONArray("data");
                                    for (int i = 0; i < movieArray.length(); i++) {
                                        final JSONObject movieObject = movieArray.getJSONObject(i);


                                        header(movieObject.getString("IDremain"));
                                        sub(movieObject.getString("IDremain"));

                                    }


                                } catch(JSONException e){
                                    e.printStackTrace();

                                }
                            }

                            private void sub(String iDremain) {
                                for(int i = 0;i <=  totalPenjualanPojos.size() - 1 ;i++) {
                                    int finalI1 = i;
                                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocRemainStockItem",
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    if(finalI1 == totalPenjualanPojos.size() -1){
                                                        postSoDocItem(menu_pelanggan.noSO);
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {


                                            if(statusLoop.equals("gagal")){
                                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                    statusLoop = "Sukses";
                                                    System.out.println("Error system API :6");

                                                    new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText("Jaringan Bermasalah")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    pDialog.dismissWithAnimation();
                                                                    sweetAlertDialog.dismissWithAnimation();
                                                                    statusLoop = "gagal";
                                                                }
                                                            })
                                                            .show();
                                                } else if (error instanceof AuthFailureError) {
                                                    Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                                } else if (error instanceof ServerError) {
                                                    if(finalI1 == totalPenjualanPojos.size() -1){
                                                        postSoDocItem(menu_pelanggan.noSO);
                                                    }
                                                } else if (error instanceof NetworkError) {
                                                    Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                                } else if (error instanceof ParseError) {
                                                    Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                                            sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

                                            params.put("szDocId", iDremain);

                                            params.put("szProductId", adapter.getItem(finalI1).getSzId());
                                            params.put("intItemNumber", String.valueOf(finalI1));

                                            params.put("decQtyStock", adapter.getItem(finalI1).getStock());
                                            params.put("decQtyDisplay", adapter.getItem(finalI1).getDisplay());
                                            params.put("decQtyExpired", adapter.getItem(finalI1).getExpired());
                                            params.put("dtmExpired", convertFormat(adapter.getItem(finalI1).getExpired_date()) + " 00:00:00");
                                            System.out.println("Params Sub = " + params);


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
                                    if (requestQueue3 == null) {
                                        requestQueue3 = Volley.newRequestQueue(summary_order.this);
                                        requestQueue3.add(stringRequest2);
                                    } else {
                                        requestQueue3.add(stringRequest2);
                                    }
                                }
                            }

                            private void header(String iDremain) {
                                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocRemainStock",
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        if(statusLoop.equals("gagal")){
                                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                statusLoop = "Sukses";
                                                System.out.println("Error system API :7");

                                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Jaringan Bermasalah")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                pDialog.dismissWithAnimation();
                                                                sweetAlertDialog.dismissWithAnimation();
                                                                statusLoop = "gagal";
                                                            }
                                                        })
                                                        .show();
                                            } else if (error instanceof AuthFailureError) {
                                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ServerError) {

                                            } else if (error instanceof NetworkError) {
                                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ParseError) {
                                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                                        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                        String nik_baru = sharedPreferences.getString("szDocCall", null);


                                        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String currentDateandTime3 = sdf3.format(new Date());

                                        String[] parts = nik_baru.split("-");
                                        String restnomor = parts[0];

                                        params.put("szDocId", iDremain);
                                        params.put("dtmDoc", currentDateandTime3);
                                        params.put("szCustomerId", no_surat);

                                        params.put("szEmployeeId", nik_baru);
                                        params.put("szBranchId", restnomor);

                                        if(restnomor.equals("321") || restnomor.equals("336") || restnomor.equals("317") || restnomor.equals("036")){
                                            params.put("szCompanyId", "ASA");
                                        } else {
                                            params.put("szCompanyId", "TVIP");
                                        }
                                        params.put("szUserCreatedId", nik_baru);
                                        params.put("dtmCreated", currentDateandTime3);
                                        params.put("szDocCallId", mulai_perjalanan.id_pelanggan);
                                        System.out.println("Params Head = " + params);
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
                                if (requestQueue4 == null) {
                                    requestQueue4 = Volley.newRequestQueue(summary_order.this);
                                    requestQueue4.add(stringRequest2);
                                } else {
                                    requestQueue4.add(stringRequest2);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                if(statusLoop.equals("gagal")){
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                        statusLoop = "Sukses";
                                        System.out.println("Error system API :8");

                                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Jaringan Bermasalah")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        pDialog.dismissWithAnimation();
                                                        sweetAlertDialog.dismissWithAnimation();
                                                        statusLoop = "gagal";
                                                    }
                                                })
                                                .show();
                                    } else if (error instanceof AuthFailureError) {
                                        Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof ServerError) {

                                    } else if (error instanceof NetworkError) {
                                        Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof ParseError) {
                                        Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                                if (requestQueue5 == null) {
                                    requestQueue5 = Volley.newRequestQueue(summary_order.this);
                                    requestQueue5.add(stringRequest);
                                } else {
                                    requestQueue5.add(stringRequest);
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
                if (requestQueue6 == null) {
                    requestQueue6 = Volley.newRequestQueue(summary_order.this);
                    requestQueue6.add(stringRequest);
                } else {
                    requestQueue6.add(stringRequest);
                }
//
            }




        });

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String[] parts = nik_baru.split("-");
        String branch = parts[0];

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime2 = sdf2.format(new Date());
        StringRequest biodata = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_NoSoInduk?depo="+branch+"&tgl=" + currentDateandTime2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);
                                SOInduk = (biodatas.getString("iniso_induk"));





                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :9");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

        biodata.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue7 == null) {
            requestQueue7 = Volley.newRequestQueue(summary_order.this);
            requestQueue7.add(biodata);
        } else {
            requestQueue7.add(biodata);
        }

        tanggalkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                Calendar twoDaysAgo = (Calendar) currentDate.clone();
                twoDaysAgo.add(Calendar.DATE, 1);

                date = currentDate.getInstance();

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date.set(year, monthOfYear, dayOfMonth);

                        tanggalkirim.setText(dateFormatter.format(date.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(summary_order.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(twoDaysAgo.getTimeInMillis());
                datePickerDialog.show();

            }
        });




        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPenjualanPojos.clear();
                adapter.notifyDataSetChanged();
                finish();
            }
        });

        adapter = new ListViewAdapterTotalPenjualan(totalPenjualanPojos, getApplicationContext());
        listtotalpenjualan.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(listtotalpenjualan);

        adapter.notifyDataSetChanged();

        for(int i = 0; i < totalPenjualanPojos.size();i++){
            jumlah_stock += Integer.parseInt(adapter.getItem(i).getStock());
        }

        for(int i = 0; i < totalPenjualanPojos.size();i++){
            jumlah_harga += Integer.parseInt(adapter.getItem(i).getJumlah_harga());
            jumlah_diskon += Integer.parseInt(adapter.getItem(i).getDisc_distributor()) + Integer.parseInt(adapter.getItem(i).getDisc_internal()) + Integer.parseInt(adapter.getItem(i).getDisc_principle());
        }
        total_harga = jumlah_harga - jumlah_diskon;

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        jumlah.setText(kursIndonesia.format(jumlah_harga));
        diskon.setText(kursIndonesia.format(jumlah_diskon));
        total.setText(kursIndonesia.format(total_harga));



    }

    private void updateRef(String message) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_RefDocId",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(statusLoop.equals("gagal")){
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        statusLoop = "Sukses";
                        System.out.println("Error system API :10");

                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Jaringan Bermasalah")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        pDialog.dismissWithAnimation();
                                        sweetAlertDialog.dismissWithAnimation();
                                        statusLoop = "gagal";
                                    }
                                })
                                .show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {

                    } else if (error instanceof NetworkError) {
                        Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("szCustomerId", no_surat);

                params.put("szDocSO", message);
                params.put("szRefDocId", message);

                System.out.println("Update params = " + params);




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
            requestQueue8 = Volley.newRequestQueue(summary_order.this);
            requestQueue8.add(stringRequest2);
        } else {
            requestQueue8.add(stringRequest2);
        }
    }


    private void putMobile() {
            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_szMobile",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if(statusLoop.equals("gagal")){
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    statusLoop = "Sukses";
                                    System.out.println("Error system API :11");

                                    new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Jaringan Bermasalah")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    pDialog.dismissWithAnimation();
                                                    sweetAlertDialog.dismissWithAnimation();
                                                    statusLoop = "gagal";
                                                }
                                            })
                                            .show();
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ServerError) {

                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ParseError) {
                                    Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);

                    String[] parts = nik_baru.split("-");
                    String branch = parts[0];

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateandTime2 = sdf2.format(new Date());

                    params.put("szDocId", menu_pelanggan.noSO);
                    params.put("szCustomerId", no_surat);
                    params.put("bCash", "1");

                    params.put("dtmPO", currentDateandTime2);
                    params.put("dtmPOExpired", currentDateandTime2);
                    params.put("szShipBranchId", branch);
                    params.put("szUserUpdatedId", nik_baru);

                    params.put("dtmLastUpdated", currentDateandTime2);
                    params.put("dtmMobileTransaction", currentDateandTime2);


                    params.put("szMobileId", menu_pelanggan.noSO);

                    params.put("dtmReqDlvDate", convertFormat(tanggalkirim.getText().toString()));



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
        if (requestQueue9 == null) {
            requestQueue9 = Volley.newRequestQueue(summary_order.this);
            requestQueue9.add(stringRequest2);
        } else {
            requestQueue9.add(stringRequest2);
        }
    }

    private void putMobileMDBA() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_szMobileMDBA",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :12");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                String[] parts = nik_baru.split("-");
                String branch = parts[0];

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("szDocId", menu_pelanggan.noSO);
                params.put("szCustomerId", no_surat);
                params.put("bCash", "1");

                params.put("dtmPO", currentDateandTime2);
                params.put("dtmPOExpired", currentDateandTime2);
                params.put("szShipBranchId", branch);
                params.put("szUserUpdatedId", nik_baru);

                params.put("dtmLastUpdated", currentDateandTime2);
                params.put("dtmMobileTransaction", currentDateandTime2);


                params.put("szMobileId", menu_pelanggan.noSO);

                params.put("dtmReqDlvDate", convertFormat(tanggalkirim.getText().toString()));



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
        if (requestQueue10 == null) {
            requestQueue10 = Volley.newRequestQueue(summary_order.this);
            requestQueue10.add(stringRequest2);
        } else {
            requestQueue10.add(stringRequest2);
        }
    }


    private void mdbasoinduk() {
        getTotalSekarang();
                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SoInduk",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                totalHarga(menu_pelanggan.noSO);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :13");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                totalHarga(menu_pelanggan.noSO);
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                        String nik_baru = sharedPreferences.getString("szDocCall", null);

                        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateandTime3 = sdf3.format(new Date());

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDateandTime = sdf.format(new Date());

                        String[] parts = nik_baru.split("-");
                        String branch = parts[0];

                        params.put("noso_induk", noSiInduk.getText().toString());

                        params.put("noso_turunan", menu_pelanggan.noSO);


                        params.put("id_customer", no_surat);
                        params.put("id_employ", nik_baru);
                        params.put("Id_User_Created", nik_baru);
                        if(branch.equals("321") || branch.equals("336") || branch.equals("317") || branch.equals("'036'")){
                            params.put("kode_company", "ASA");
                        } else {
                            params.put("kode_company", "TVIP");
                        }

                        params.put("kode_depo", branch);

                        params.put("tgl_cari", currentDateandTime);
                        params.put("tgl_insert", currentDateandTime3);


                        params.put("status", "OPEN");

                        if(pilihpembayaran.getText().toString().equals("Tunai")){
                            params.put("type_bayar", "2");
                        } else {
                            params.put("type_bayar", "1");
                        }


                        params.put("req_date", convertFormat(tanggalkirim.getText().toString()));



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
                if (requestQueue11 == null) {
                    requestQueue11 = Volley.newRequestQueue(summary_order.this);
                    requestQueue11.add(stringRequest2);
                } else {
                    requestQueue11.add(stringRequest2);
                }


    }

    private void getData() {
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                bStarted = biodatas.getString("bStarted");
                                bFinisihed = biodatas.getString("bFinisihed");
                                bVisited = biodatas.getString("bVisited");
                                bSuccess = biodatas.getString("bSuccess");

                                szFailReason = biodatas.getString("szFailReason");
                                bPostPone = biodatas.getString("bPostPone");
                                szLangitude = biodatas.getString("szLangitude");
                                szLongitude = biodatas.getString("szLongitude");

                                bOutOfRoute = biodatas.getString("bOutOfRoute");
                                bNewCustomer = biodatas.getString("bNewCustomer");
                                bScanBarcode = biodatas.getString("bScanBarcode");
                                decDuration = biodatas.getString("decDuration");

                                szReasonIdCheckin = biodatas.getString("szReasonIdCheckin");
                                szReasonFailedScan = biodatas.getString("szReasonFailedScan");

                                dtmStart = biodatas.getString("dtmStart");



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :14");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

        channel_status.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue12 == null) {
            requestQueue12 = Volley.newRequestQueue(summary_order.this);
            requestQueue12.add(channel_status);
        } else {
            requestQueue12.add(channel_status);
        }

    }

    private void getNoSO() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime2 = sdf2.format(new Date());

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String[] parts = nik_baru.split("-");
        String restnomor = parts[0];

        pDialog = new SweetAlertDialog(summary_order.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SOIndukCurrent?noso_turunan=" + menu_pelanggan.noSO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                condition = "OK";
                                noSOInduk = movieObject.getString("noso_induk");
                                noSiInduk.setText(movieObject.getString("noso_induk"));

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :15");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                getNoBaru();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue13 == null) {
            requestQueue13 = Volley.newRequestQueue(summary_order.this);
            requestQueue13.add(stringRequest);
        } else {
            requestQueue13.add(stringRequest);
        }
    }

    private void getNoBaru() {
        pDialog.dismissWithAnimation();
        condition = "NOT OK";
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
                                noSiInduk.setText(noSOInduk);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :16");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue14 == null) {
            requestQueue14 = Volley.newRequestQueue(summary_order.this);
            requestQueue14.add(stringRequest);
        } else {
            requestQueue14.add(stringRequest);
        } // so basically after your getHeroes(), from next time it will be 5 sec repeated
    }

    private void getMdbaTotalHarga() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_MDBATotalHarga?szDocSO="+ menu_pelanggan.noSO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                int dpp = movieObject.getInt("dpp");
                                int ppn = movieObject.getInt("ppn");
                                int totalDiscount = movieObject.getInt("totalDiscount");
                                int totalHarga = movieObject.getInt("totalHarga");


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :17");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue16 == null) {
            requestQueue16 = Volley.newRequestQueue(summary_order.this);
            requestQueue16.add(stringRequest);
        } else {
            requestQueue16.add(stringRequest);
        }
    }

    private void getUpdateTotalHarga() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSo?szDocId="+ menu_pelanggan.noSO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :18");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue19 == null) {
            requestQueue19 = Volley.newRequestQueue(summary_order.this);
            requestQueue19.add(stringRequest);
        } else {
            requestQueue19.add(stringRequest);
        }

    }

    private void getSales() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Sales?szId="+ no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                if(movieObject.getString("bAllowToCredit").equals("0")){
                                    pilihpembayaran.setText("Tunai");
                                } else {
                                    pilihpembayaran.setText("Kredit");
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

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :19");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue23 == null) {
            requestQueue23 = Volley.newRequestQueue(summary_order.this);
            requestQueue23.add(stringRequest);
        } else {
            requestQueue23.add(stringRequest);
        }
    }


    private void postItem() {

        for(int i = 0;i <=  totalPenjualanPojos.size() - 1 ;i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoPrice",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            if(finalI == totalPenjualanPojos.size() -1){
                                updateDraftToApplied(noSO);
                                putMobile();
                                putMobileMDBA();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(statusLoop.equals("gagal")){
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            statusLoop = "Sukses";
                            System.out.println("Error system API :20");

                            new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Jaringan Bermasalah")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            pDialog.dismissWithAnimation();
                                            sweetAlertDialog.dismissWithAnimation();
                                            statusLoop = "gagal";
                                        }
                                    })
                                    .show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            if(finalI == totalPenjualanPojos.size() -1){
                                updateDraftToApplied(noSO);
                                putMobile();
                                putMobileMDBA();
                            }
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);

//                double base = 100d/111;
//                double dpp = (counts*base);
//
//                double DoubleValue = dpp;
//                int IntValue = (int) Math.round(DoubleValue);

                    params.put("szDocId", menu_pelanggan.noSO);
                    if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                        params.put("intItemNumber", String.valueOf(finalI));
                    } else {
                        params.put("intItemNumber", String.valueOf(finalI + lastnumber));
                    }

                    params.put("szPriceId", adapter.getItem(finalI).getSzPriceSegmentId());
                    params.put("decPrice", adapter.getItem(finalI).getDecPrice());

                    int discounttotal = Integer.parseInt(adapter.getItem(finalI).getDisc_principle()) + Integer.parseInt(adapter.getItem(finalI).getDisc_distributor()) + Integer.parseInt(adapter.getItem(finalI).getDisc_internal());
                    params.put("decDiscount", "0.0000");

                    String[] parts = adapter.getItem(finalI).getDecPrice().split("\\.");
                    String restnomor = parts[0];
                    params.put("decAmount", String.valueOf((Integer.parseInt(restnomor) * Integer.parseInt(adapter.getItem(finalI).getStock())) - discounttotal));

                    params.put("szTaxId", "PPN");

                    params.put("decTaxRate", menu_pelanggan.tax);

                    params.put("decDiscPrinciple", adapter.getItem(finalI).getDisc_principle() + ".0000");
                    params.put("decDiscDistributor", adapter.getItem(finalI).getDisc_distributor() + ".0000");
                    params.put("decDiscInternal", adapter.getItem(finalI).getDisc_internal() + ".0000");





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
            if (requestQueue24 == null) {
                requestQueue24 = Volley.newRequestQueue(summary_order.this);
                requestQueue24.add(stringRequest2);
            } else {
                requestQueue24.add(stringRequest2);
            }
        }


    }

    private void getTotalSekarang() {

        totalsekarang();
    }

    private void totalsekarang() {
        StringRequest biodata = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_TotalSO?szDocId="+noSO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);
                                String[] uangparts = biodatas.getString("totalsekarang").split("\\.");
                                String uangSlice = uangparts[0];

                                String[] diskonparts = biodatas.getString("totaldiskon").split("\\.");
                                String diskonSlice = diskonparts[0];
                                
                                updateMdbaTotalharga(noSO, uangSlice, diskonSlice);
                                updateSFADocso(noSO, uangSlice, diskonSlice);






                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :21");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

        biodata.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue52 == null) {
            requestQueue52 = Volley.newRequestQueue(summary_order.this);
            requestQueue52.add(biodata);
        } else {
            requestQueue52.add(biodata);
        }
    }

    private void updateDraftToApplied(String noSO) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToApplied",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :22");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue2 = Volley.newRequestQueue(summary_order.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void updateSFADocso(String noSO, String uangSlice, String diskonSlice) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_sfadocso",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(statusLoop.equals("gagal")){
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        statusLoop = "Sukses";
                        System.out.println("Error system API :23");

                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Jaringan Bermasalah")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        pDialog.dismissWithAnimation();
                                        sweetAlertDialog.dismissWithAnimation();
                                        statusLoop = "gagal";
                                    }
                                })
                                .show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {

                    } else if (error instanceof NetworkError) {
                        Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                params.put("szDocId", menu_pelanggan.noSO);
                params.put("decAmount", uangSlice);

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
        if (requestQueue54 == null) {
            requestQueue54 = Volley.newRequestQueue(summary_order.this);
            requestQueue54.add(stringRequest2);
        } else {
            requestQueue54.add(stringRequest2);
        }
    }

    private void updateMdbaTotalharga(String noSO, String uangSlice, String diskonSlice) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_TotalHarga",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(statusLoop.equals("gagal")){
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        statusLoop = "Sukses";
                        System.out.println("Error system API :24");

                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Jaringan Bermasalah")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        pDialog.dismissWithAnimation();
                                        sweetAlertDialog.dismissWithAnimation();
                                        statusLoop = "gagal";
                                    }
                                })
                                .show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {

                    } else if (error instanceof NetworkError) {
                        Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                params.put("szDocSo", menu_pelanggan.noSO);

                params.put("totalDiscount", diskonSlice);
                params.put("totalHarga", uangSlice);
                params.put("depo", soldtobranch);

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
        if (requestQueue53 == null) {
            requestQueue53 = Volley.newRequestQueue(summary_order.this);
            requestQueue53.add(stringRequest2);
        } else {
            requestQueue53.add(stringRequest2);
        }

    }

    private void getDoccallItemVT() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ComparatorSO?szDocSo="+ menu_pelanggan.noSO + "VT",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :25");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                getVT();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue25 == null) {
            requestQueue25 = Volley.newRequestQueue(summary_order.this);
            requestQueue25.add(stringRequest);
        } else {
            requestQueue25.add(stringRequest);
        }


    }

    private void getDoccallItemAQ() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_ComparatorSO?szDocSo="+ menu_pelanggan.noSO + "AQ",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getDoccallItemVT();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :26");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                getAQ();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue26 == null) {
            requestQueue26 = Volley.newRequestQueue(summary_order.this);
            requestQueue26.add(stringRequest);
        } else {
            requestQueue26.add(stringRequest);
        }



    }

    private void totalHarga(String szRefDocId) {
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_TotalHarga",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            success = new SweetAlertDialog(summary_order.this, SweetAlertDialog.SUCCESS_TYPE);
                            success.setCancelable(false);
                            success.setContentText("Data Sudah Disimpan");
                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                    deleteCache(getApplicationContext());
                                    totalPenjualanPojos.clear();
                                    data_produk_penjualan_pojos.clear();
                                    adapter.notifyDataSetChanged();

                                    Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                    intent.putExtra("kode", no_surat);
                                    startActivity(intent);
                                    finish();
                                }
                            });
//                            success.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(statusLoop.equals("gagal")){
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            statusLoop = "Sukses";
                            System.out.println("Error system API :27");

                            new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Jaringan Bermasalah")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            pDialog.dismissWithAnimation();
                                            sweetAlertDialog.dismissWithAnimation();
                                            statusLoop = "gagal";
                                        }
                                    })
                                    .show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            success = new SweetAlertDialog(summary_order.this, SweetAlertDialog.SUCCESS_TYPE);
                            success.setCancelable(false);
                            success.setContentText("Data Sudah Disimpan");
                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                    deleteCache(getApplicationContext());
                                    totalPenjualanPojos.clear();
                                    data_produk_penjualan_pojos.clear();
                                    adapter.notifyDataSetChanged();

                                    Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                    intent.putExtra("kode", no_surat);
                                    startActivity(intent);
                                    finish();
                                }
                            });
//                            success.show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);

                    String[] parts = nik_baru.split("-");
                    String branch = parts[0];

                    double base = 100d/111;
                    double dpp = (total_harga*base);
                    double DoubleValue = dpp;
                    int IntValue = (int) Math.round(DoubleValue);

                    int ppn = Math.toIntExact(total_harga - IntValue);


                    params.put("szDocSo", menu_pelanggan.noSO);


                    params.put("totalDiscount", String.valueOf(jumlah_diskon));
                    params.put("totalHarga", String.valueOf(total_harga));
                    params.put("depo", soldtobranch);

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
            if (requestQueue28 == null) {
                requestQueue28 = Volley.newRequestQueue(summary_order.this);
                requestQueue28.add(stringRequest2);
            } else {
                requestQueue28.add(stringRequest2);
            }

    }



    private void postSoDoc(String szRefDocId) {

            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSo",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            postDocSoItem();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(statusLoop.equals("gagal")){
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            statusLoop = "Sukses";
                            System.out.println("Error system API :28");

                            new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Jaringan Bermasalah")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            pDialog.dismissWithAnimation();
                                            sweetAlertDialog.dismissWithAnimation();
                                            statusLoop = "gagal";
                                        }
                                    })
                                    .show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            postDocSoItem();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);

                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateandTime3 = sdf3.format(new Date());


                    params.put("szDocId", szRefDocId);

                    params.put("dtmDoc", currentDateandTime3);
                    params.put("szEmployeeId", nik_baru);
                    params.put("szCustomerId", no_surat);
                    params.put("decAmount", String.valueOf(total_harga) + ".0000");
                    params.put("intPrintedCount", "0");
                    params.put("szDocCallId", mulai_perjalanan.id_pelanggan);

                    String[] parts = nik_baru.split("-");
                    String branch = parts[0];

                    params.put("szBranchId", branch);

                    if(branch.equals("321") || branch.equals("336") || branch.equals("317") || branch.equals("'036'")){
                        params.put("szCompanyId", "ASA");
                    } else {
                        params.put("szCompanyId", "TVIP");
                    }
                    params.put("szDocStatus", "Draft");


                    params.put("szUserCreatedId", nik_baru);
                    params.put("szUserUpdatedId", nik_baru);
                    params.put("dtmCreated", currentDateandTime3);

                    params.put("dtmLastUpdated", currentDateandTime3);

                    params.put("dtmDelivery", convertFormat(tanggalkirim.getText().toString()));
                    params.put("szNote", catatan.getText().toString());

                    params.put("bCash", "1");

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
            if (requestQueue29 == null) {
                requestQueue29 = Volley.newRequestQueue(summary_order.this);
                requestQueue29.add(stringRequest2);
            } else {
                requestQueue29.add(stringRequest2);
            }
    }

    private void postSoDocItem(String szRefDocId) {
        updateDraftToApplied(noSO);
        putMobile();
        putMobileMDBA();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        for(int i = 0;i <=  totalPenjualanPojos.size() - 1 ;i++){
            int finalI = i;
            int finalI1 = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_docso_all",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if(finalI == totalPenjualanPojos.size() -1){
                                success = new SweetAlertDialog(summary_order.this, SweetAlertDialog.SUCCESS_TYPE);
                                success.setCancelable(false);
                                success.setContentText("Data Sudah Disimpan");
                                success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        deleteCache(getApplicationContext());
                                        totalPenjualanPojos.clear();
                                        data_produk_penjualan_pojos.clear();
                                        adapter.notifyDataSetChanged();

                                        Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                        intent.putExtra("kode", no_surat);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                success.show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(statusLoop.equals("gagal")){
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            statusLoop = "Sukses";
                            System.out.println("Error system API :29");
                            new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Jaringan Bermasalah")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            pDialog.dismissWithAnimation();
                                            sweetAlertDialog.dismissWithAnimation();
                                            statusLoop = "gagal";
                                        }
                                    })
                                    .show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            if(finalI == totalPenjualanPojos.size() -1){
                                success = new SweetAlertDialog(summary_order.this, SweetAlertDialog.SUCCESS_TYPE);
                                success.setCancelable(false);
                                success.setContentText("Data Sudah Disimpan");
                                success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        deleteCache(getApplicationContext());
                                        totalPenjualanPojos.clear();
                                        data_produk_penjualan_pojos.clear();
                                        adapter.notifyDataSetChanged();

                                        Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                        intent.putExtra("kode", no_surat);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                success.show();
                            }
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);

                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateandTime3 = sdf3.format(new Date());

                    SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
                    String hariini = today.format(new Date());

                    String[] parts = nik_baru.split("-");
                    String branch = parts[0];
                    //postSFA_docso//
                    params.put("szDocId", szRefDocId);
                    params.put("dtmDoc", currentDateandTime3);
                    params.put("szEmployeeId", nik_baru);
                    params.put("szCustomerId", no_surat);
                    params.put("totalDocSo", String.valueOf(total_harga) + ".0000");
                    params.put("intPrintedCount", "0");
                    params.put("szDocCallId", mulai_perjalanan.id_pelanggan);
                    params.put("szBranchId", branch);
                    if(branch.equals("321") || branch.equals("336") || branch.equals("317") || branch.equals("'036'")){
                        params.put("szCompanyId", "ASA");
                    } else {
                        params.put("szCompanyId", "TVIP");
                    }
                    params.put("szDocStatus", "Draft");
                    params.put("szUserCreatedId", nik_baru);
                    params.put("szUserUpdatedId", nik_baru);
                    params.put("dtmCreated", currentDateandTime3);
                    params.put("dtmLastUpdated", currentDateandTime3);
                    params.put("dtmDelivery", convertFormat(tanggalkirim.getText().toString()));
                    params.put("szNote", catatan.getText().toString());
                    params.put("bCash", "1");

                    //postSOInduk
                    params.put("noso_induk", noSiInduk.getText().toString());
                    params.put("noso_turunan", szRefDocId);
                    params.put("id_customer", no_surat);
                    params.put("id_employ", nik_baru);
                    params.put("Id_User_Created", nik_baru);
                    if(branch.equals("321") || branch.equals("336") || branch.equals("317") || branch.equals("'036'")){
                        params.put("kode_company", "ASA");
                    } else {
                        params.put("kode_company", "TVIP");
                    }

                    params.put("kode_depo", branch);
                    params.put("tgl_cari", hariini);
                    params.put("tgl_insert", currentDateandTime3);
                    params.put("status", "OPEN");
                    if(pilihpembayaran.getText().toString().equals("Tunai")){
                        params.put("type_bayar", "2");
                    } else {
                        params.put("type_bayar", "1");
                    }
                    params.put("req_date", convertFormat(tanggalkirim.getText().toString()));


                    //postTotalHarga
                    params.put("szDocSo", szRefDocId);
                    params.put("totalDiscount", String.valueOf(jumlah_diskon));
                    params.put("totalHarga", String.valueOf(total_harga));
                    params.put("depo", soldtobranch);

                    //postDocSoItem
                    params.put("szDocId", szRefDocId);
                    if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                        params.put("intItemNumber", String.valueOf(finalI));
                    } else {
                        params.put("intItemNumber", String.valueOf(finalI + lastnumber));
                    }
                    params.put("szProductId", adapter.getItem(finalI1).getSzId());

                    params.put("decQty", adapter.getItem(finalI1).getStock());
                    params.put("szUomId", adapter.getItem(finalI1).getSzUomId());

                    //postDocSOItem
                    params.put("szDocId", szRefDocId);
                    if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                        params.put("intItemNumber", String.valueOf(finalI));
                    } else {
                        params.put("intItemNumber", String.valueOf(finalI + lastnumber));
                    }

                    params.put("szPriceId", adapter.getItem(finalI).getSzPriceSegmentId());
                    params.put("decPrice", adapter.getItem(finalI).getDecPrice());

                    int discounttotal = Integer.parseInt(adapter.getItem(finalI).getDisc_principle()) + Integer.parseInt(adapter.getItem(finalI).getDisc_distributor()) + Integer.parseInt(adapter.getItem(finalI).getDisc_internal());
                    params.put("decDiscount", "0.0000");

                    String[] parts2 = adapter.getItem(finalI).getDecPrice().split("\\.");
                    String restnomor = parts2[0];
                    params.put("decAmount", String.valueOf((Integer.parseInt(restnomor) * Integer.parseInt(adapter.getItem(finalI).getStock())) - discounttotal));
                    params.put("szTaxId", "PPN");
                    params.put("decTaxRate", menu_pelanggan.tax);
                    params.put("decDiscPrinciple", adapter.getItem(finalI).getDisc_principle() + ".0000");
                    params.put("decDiscDistributor", adapter.getItem(finalI).getDisc_distributor() + ".0000");
                    params.put("decDiscInternal", adapter.getItem(finalI).getDisc_internal() + ".0000");

                    // post_sfaDocsoItem
                    int principla = Integer.parseInt(adapter.getItem(finalI1).getDisc_principle());
                    int distributor = Integer.parseInt(adapter.getItem(finalI1).getDisc_distributor());
                    int Internal = Integer.parseInt(adapter.getItem(finalI1).getDisc_internal());
                    int ammount = Integer.parseInt(adapter.getItem(finalI1).getJumlah_harga()) - (principla + distributor + Internal);

                    params.put("szDocId", menu_pelanggan.noSO);
                    if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                        params.put("intItemNumber", String.valueOf(finalI));
                    } else {
                        params.put("intItemNumber", String.valueOf(finalI + lastnumber));
                    }
                    params.put("szProductId", adapter.getItem(finalI1).getSzId());
                    params.put("decQty", adapter.getItem(finalI1).getStock());
                    params.put("decPrice", adapter.getItem(finalI1).getDecPrice());
                    params.put("decAmount", String.valueOf(ammount) + ".0000");
                    params.put("decDiscount", "0.0000");
                    params.put("szUomId", adapter.getItem(finalI1).getSzUomId());
                    params.put("decDiscPrinciple", adapter.getItem(finalI1).getDisc_principle() + ".0000");
                    params.put("decDiscountDistributor", adapter.getItem(finalI1).getDisc_distributor() + ".0000");
                    params.put("decDiscountInternal", adapter.getItem(finalI1).getDisc_internal() + ".0000");



                    System.out.println("Params = " + params);
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
            if (requestQueue30 == null) {
                requestQueue30 = Volley.newRequestQueue(summary_order.this);
                requestQueue30.add(stringRequest2);
            } else {
                requestQueue30.add(stringRequest2);
            }
        }

    }

    private void postStockOnHand(String szId, String nik_baru, String warehouse, String stock, String szUomId, String currentDateandTime, String branch, String szRefDocId) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_cek_Stock",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(statusLoop.equals("gagal")){
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        statusLoop = "Sukses";
                        System.out.println("Error system API :30");

                        new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Jaringan Bermasalah")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        pDialog.dismissWithAnimation();
                                        sweetAlertDialog.dismissWithAnimation();
                                        statusLoop = "gagal";
                                    }
                                })
                                .show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {

                    } else if (error instanceof NetworkError) {
                        Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

                params.put("szProductId", szId);
                params.put("szLocationId", nik_baru);
                params.put("warehouse", warehouse);
                params.put("decQtyOnHandNow", stock);
                params.put("szUomId", szUomId);
                params.put("tanggal", currentDateandTime);
                params.put("szReportedAsId", branch);
                params.put("szDocId", szRefDocId);






                System.out.println("Params Post = " + params);




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
        if (requestQueue55 == null) {
            requestQueue55 = Volley.newRequestQueue(summary_order.this);
            requestQueue55.add(stringRequest2);
        } else {
            requestQueue55.add(stringRequest2);
        }
    }

    private void postDocSoItem() {
        for(int i = 0;i <=  totalPenjualanPojos.size() - 1 ;i++){
            int finalI = i;
            int finalI1 = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoItem",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if(finalI == totalPenjualanPojos.size() - 1){
                                mdbasoinduk();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(statusLoop.equals("gagal")){
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            statusLoop = "Sukses";
                            System.out.println("Error system API :31");
                            new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Jaringan Bermasalah")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            pDialog.dismissWithAnimation();
                                            sweetAlertDialog.dismissWithAnimation();
                                            statusLoop = "gagal";
                                        }
                                    })
                                    .show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            if(finalI == totalPenjualanPojos.size() -1){
                                mdbasoinduk();
                            }
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

                    int principla = Integer.parseInt(adapter.getItem(finalI1).getDisc_principle());
                    int distributor = Integer.parseInt(adapter.getItem(finalI1).getDisc_distributor());
                    int Internal = Integer.parseInt(adapter.getItem(finalI1).getDisc_internal());

                    int ammount = Integer.parseInt(adapter.getItem(finalI1).getJumlah_harga()) - (principla + distributor + Internal);



                    params.put("szDocId", menu_pelanggan.noSO);

                    if(product_penjualan.pilihkategori.getText().toString().equals("BOTOL")){
                        params.put("intItemNumber", String.valueOf(finalI));
                    } else {
                        params.put("intItemNumber", String.valueOf(finalI + lastnumber));
                    }
                    params.put("szProductId", adapter.getItem(finalI1).getSzId());

                    params.put("decQty", adapter.getItem(finalI1).getStock());
                    params.put("decPrice", adapter.getItem(finalI1).getDecPrice() + ".0000");
                    params.put("decAmount", String.valueOf(ammount) + ".0000");
                    params.put("decDiscount", "0.0000");

                    params.put("szUomId", adapter.getItem(finalI1).getSzUomId());

                    params.put("decDiscPrinciple", adapter.getItem(finalI1).getDisc_principle() + ".0000");
                    params.put("decDiscountDistributor", adapter.getItem(finalI1).getDisc_distributor() + ".0000");
                    params.put("decDiscountInternal", adapter.getItem(finalI1).getDisc_internal() + ".0000");





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
            if (requestQueue32 == null) {
                requestQueue32 = Volley.newRequestQueue(summary_order.this);
                requestQueue32.add(stringRequest2);
            } else {
                requestQueue32.add(stringRequest2);
            }
        }
    }

    private void putSO() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToApplied",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :32");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

                params.put("szDocId", menu_pelanggan.noSO);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(summary_order.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void getAQ() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoItem?szDocId=" + menu_pelanggan.noSO + "AQ",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                movieObject.getString("szDocId");
                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :33");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                getDoccallItemVT();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue34 == null) {
            requestQueue34 = Volley.newRequestQueue(summary_order.this);
            requestQueue34.add(stringRequest);
        } else {
            requestQueue34.add(stringRequest);
        }
    }

    private void getVT() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoItem?szDocId=" + menu_pelanggan.noSO + "VT",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                movieObject.getString("szDocId");
                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :34");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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
        if (requestQueue36 == null) {
            requestQueue36 = Volley.newRequestQueue(summary_order.this);
            requestQueue36.add(stringRequest);
        } else {
            requestQueue36.add(stringRequest);
        }
    }


    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public class ListViewAdapterTotalPenjualan extends ArrayAdapter<total_penjualan_pojo> {
        private List<total_penjualan_pojo> totalPenjualanPojos;

        private Context context;

        public ListViewAdapterTotalPenjualan(List<total_penjualan_pojo> totalPenjualanPojos, Context context) {
            super(context, R.layout.list_total_penjualan, totalPenjualanPojos);
            this.totalPenjualanPojos = totalPenjualanPojos;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_total_penjualan, null, true);

            TextView namaproduk = listViewItem.findViewById(R.id.namaproduk);
            TextView qty_order = listViewItem.findViewById(R.id.qty_order);
            TextView total = listViewItem.findViewById(R.id.total);
            ImageButton more = listViewItem.findViewById(R.id.more);

            total_penjualan_pojo data = totalPenjualanPojos.get(position);

            String[] parts = data.getDecPrice().split("\\.");
            String restnomor = parts[0];
            String restnomorbaru = restnomor.replace(" ", "");

            namaproduk.setText(data.getSzName());

            int harga = Integer.parseInt(data.getJumlah_harga());
            int totals = Integer.parseInt(restnomorbaru);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            qty_order.setText("Order : " + data.getStock() + " x " + kursIndonesia.format(totals));


            total.setText(kursIndonesia.format(harga));

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialog(data.getSzName(), data.getStockawal(), data.getDisplay(), data.getExpired());
                }
            });




            return listViewItem;
        }
    }

    @Override
    public void onBackPressed() {
        totalPenjualanPojos.clear();
        adapter.notifyDataSetChanged();
        finish();
        super.onBackPressed();
    }

    private void showBottomSheetDialog(String szName, String stockawal, String display, String expired) {
        View view = getLayoutInflater().inflate(R.layout.detail_qty, null);

        TextView qty_stok = view.findViewById(R.id.stok);
        TextView qty_display = view.findViewById(R.id.display);
        TextView qty_expired = view.findViewById(R.id.expired);
        TextView barang = view.findViewById(R.id.barang);

        barang.setText(szName);
        qty_stok.setText(stockawal);
        qty_display.setText(display);
        qty_expired.setText(expired);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        sheetDialog.show();
        sheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sheetDialog = null;
            }
        });
    }

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return convetDateFormat.format(date);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            super.onSaveInstanceState(outState);

            SavedInstanceFragment.getInstance(getFragmentManager()).pushData((Bundle) outState.clone());
            outState.clear();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(SavedInstanceFragment.getInstance(getFragmentManager()).popData());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        StringRequest rest = new StringRequest(Request.Method.GET,  "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Rollback/rollback?szDocSO=" + menu_pelanggan.noSO,
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
        };
        rest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (requestQueue56 == null) {
            requestQueue56 = Volley.newRequestQueue(summary_order.this);
            requestQueue56.add(rest);
        } else {
            requestQueue56.add(rest);
        }
    }



    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {

        }
    }

//    public boolean hostAvailable(String host, int port) {
//        try (Socket socket = new Socket()) {
//            socket.connect(new InetSocketAddress(host, port), 2000);
//            return true;
//        } catch (IOException e) {
//            // Either we have a timeout or unreachable host or failed DNS lookup
//            System.out.println(e);
//            return false;
//        }
//    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void postVT() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DoccallItems",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :35");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {

                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                String currentDateandTime2 = sdf2.format(new Date());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("szCustomerId", no_surat);

                params.put("dtmStart", currentDateandTime2);
                params.put("dtmFinish", currentDateandTime2);

                params.put("bStarted", bStarted);
                params.put("bFinisihed", bFinisihed);
                params.put("bVisited", bVisited);
                params.put("bSuccess", bSuccess);

                params.put("szFailReason", szFailReason);
                params.put("bPostPone", bPostPone);
                params.put("szLangitude", szLangitude);
                params.put("szLongitude", szLongitude);

                params.put("bOutOfRoute", bOutOfRoute);
                params.put("bNewCustomer", bNewCustomer);
                params.put("bScanBarcode", bScanBarcode);
                params.put("decDuration", decDuration);

                params.put("szReasonIdCheckin", szReasonIdCheckin);
                params.put("szReasonFailedScan", szReasonFailedScan);

                params.put("dtmLastCheckin", currentDateandTime);
                params.put("szDocSO", menu_pelanggan.noSO);


                System.out.println("Parameters VT= " + params);

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
        if (requestQueue35 == null) {
            requestQueue35 = Volley.newRequestQueue(summary_order.this);
            requestQueue35.add(stringRequest2);
        } else {
            requestQueue35.add(stringRequest2);
        }
    }

    private void PostAQ(String newSO) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DoccallItems",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        updateRef(newSO);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(statusLoop.equals("gagal")){
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                statusLoop = "Sukses";
                                System.out.println("Error system API :36");

                                new SweetAlertDialog(summary_order.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Jaringan Bermasalah")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                pDialog.dismissWithAnimation();
                                                sweetAlertDialog.dismissWithAnimation();
                                                statusLoop = "gagal";
                                            }
                                        })
                                        .show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(summary_order.this, "Auth Failure Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                updateRef(newSO);
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(summary_order.this, "Network Error", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(summary_order.this, "Parse Error", Toast.LENGTH_SHORT).show();
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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                String currentDateandTime2 = sdf2.format(new Date());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("szCustomerId", no_surat);

                params.put("dtmStart", dtmStart);
                params.put("dtmFinish", currentDateandTime2);

                params.put("bStarted", bStarted);
                params.put("bFinisihed", bFinisihed);
                params.put("bVisited", bVisited);
                params.put("bSuccess", bSuccess);

                params.put("szFailReason", szFailReason);
                params.put("bPostPone", bPostPone);
                params.put("szLangitude", szLangitude);
                params.put("szLongitude", szLongitude);

                params.put("bOutOfRoute", bOutOfRoute);
                params.put("bNewCustomer", bNewCustomer);
                params.put("bScanBarcode", bScanBarcode);
                params.put("decDuration", decDuration);

                params.put("szReasonIdCheckin", szReasonIdCheckin);
                params.put("szReasonFailedScan", szReasonFailedScan);

                params.put("dtmLastCheckin", currentDateandTime);
                params.put("szDocSO", newSO);

                System.out.println("Parameter = " + params);
                System.out.println("NO SO = " + menu_pelanggan.noSO);
                System.out.println("NO SO = " + newSO);



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
        if (requestQueue33 == null) {
            requestQueue33 = Volley.newRequestQueue(summary_order.this);
            requestQueue33.add(stringRequest2);
        } else {
            requestQueue33.add(stringRequest2);
        }
    }

    public boolean isOnline() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            int timeoutMs = 150;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}