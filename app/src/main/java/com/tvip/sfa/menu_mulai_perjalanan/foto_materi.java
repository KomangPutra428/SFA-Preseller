package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.tvip.sfa.R;
import com.tvip.sfa.pojo.data_posm_foto_pojo;
import com.tvip.sfa.pojo.data_posm_pojo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class foto_materi extends AppCompatActivity {
    ListView list_fotomateriposm;
    ListViewAdapterFoto adapterFoto;
    ListViewAdapterFotoCek adapterFoto2;
    Button batal, lanjutkan;
    int request;
    SharedPreferences sharedPreferences;
    Bitmap bitmap;
    String currentDateandTime2;
    SweetAlertDialog pDialog, pSuccess;

    ContentValues cv;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_foto_materi);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        currentDateandTime2 = sdf2.format(new Date());
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if(getIntent().getStringExtra("POSM").equals("materi")){
                    adapterFoto.clear();
                } else {
                    adapterFoto2.clear();
                }
            }
        });

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent().getStringExtra("POSM").equals("materi")){
                    for(int i = 0; i < materi_posm.dataPosmFotoPojoList.size(); i++){
                        if(adapterFoto.getItem(i).getFoto().equals("0")){
                            new SweetAlertDialog(foto_materi.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Foto Tidak Boleh Kosong")
                                    .setConfirmText("OK")
                                    .show();
                            break;
                        } else if (i == materi_posm.dataPosmFotoPojoList.size() - 1){
                            materiPosm();
                        }
                    }
                } else {
                    for(int i = 0; i < cek_posm.dataPosmFotoPojoList.size(); i++){
                        if(adapterFoto2.getItem(i).getFoto().equals("0")){
                            new SweetAlertDialog(foto_materi.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Foto Tidak Boleh Kosong")
                                    .setConfirmText("OK")
                                    .show();
                            break;
                        } else if (i == cek_posm.dataPosmFotoPojoList.size() - 1){
                            cekPosm();
                        }
                    }

                }

            }
        });

        list_fotomateriposm = findViewById(R.id.list_fotomateriposm);

        list_fotomateriposm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, position);
                request = position;
            }
        });

        if(getIntent().getStringExtra("POSM").equals("materi")){
            adapterFoto = new ListViewAdapterFoto(materi_posm.dataPosmFotoPojoList, foto_materi.this);
            list_fotomateriposm.setAdapter(adapterFoto);
        } else {
            adapterFoto2 = new ListViewAdapterFotoCek(cek_posm.dataPosmFotoPojoList, foto_materi.this);
            list_fotomateriposm.setAdapter(adapterFoto2);
        }
    }

    private void cekPosm() {
        pDialog = new SweetAlertDialog(foto_materi.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_CekPosm",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        CheckedPosm();

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

                params.put("szDocId", currentDateandTime2 + "_" + no_surat);
                params.put("szCustomerId", no_surat);
                params.put("szEmployeeId", nik_baru);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(foto_materi.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void CheckedPosm() {
        uploadFotoCek();
        uploadKeServer2();

        for(int i = 0; i <= cek_posm.dataPosmFotoPojoList.size(); i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SalesCekItem",
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


                    params.put("szDocId", currentDateandTime2 + "_" + no_surat);
                    params.put("intItemNumber", String.valueOf(finalI));

                    params.put("szProductId", adapterFoto2.getItem(finalI).getSzId());

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
            RequestQueue requestQueue2 = Volley.newRequestQueue(foto_materi.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void uploadKeServer2() {
        for(int i = 0; i <= cek_posm.dataPosmFotoPojoList.size() - 1; i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/mobile_eis_2/upload_sfa.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(finalI == cek_posm.dataPosmFotoPojoList.size() - 1){
                                pDialog.dismissWithAnimation();
                                pSuccess = new SweetAlertDialog(foto_materi.this, SweetAlertDialog.SUCCESS_TYPE);
                                pSuccess.setContentText("Data Sudah Disimpan");
                                pSuccess.setCancelable(false);
                                pSuccess.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("kode", no_surat);
                                        startActivity(intent);

                                        if(getIntent().getStringExtra("POSM").equals("materi")){
                                            adapterFoto.clear();
                                        } else {
                                            adapterFoto2.clear();
                                        }
                                    }
                                });
                                pSuccess.show();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(finalI == cek_posm.dataPosmFotoPojoList.size() - 1){
                                pDialog.dismissWithAnimation();
                                pSuccess = new SweetAlertDialog(foto_materi.this, SweetAlertDialog.SUCCESS_TYPE);
                                pSuccess.setContentText("Data Sudah Disimpan");
                                pSuccess.setCancelable(false);
                                pSuccess.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("kode", no_surat);
                                        startActivity(intent);

                                        if(getIntent().getStringExtra("POSM").equals("materi")){
                                            adapterFoto.clear();
                                        } else {
                                            adapterFoto2.clear();
                                        }
                                    }
                                });
                                pSuccess.show();
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

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                    String currentDateandTime2 = sdf2.format(new Date());

                    String gambar = imagetoString(bitmap);

                    params.put("nik", "CHECK_POSM" + "_" + adapterFoto2.getItem(finalI).getSzName() + "_" + nik_baru + "_" + no_surat + "_" + currentDateandTime2);
                    params.put("foto", adapterFoto2.getItem(finalI).getFoto());
                    params.put("nama_folder", "foto_posm");


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

            RequestQueue requestQueue2 = Volley.newRequestQueue(this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void uploadFotoCek() {


        for(int i = 0; i < cek_posm.dataPosmFotoPojoList.size(); i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_gambar_posm",
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

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                    String currentDateandTime2 = sdf2.format(new Date());

                    String[] parts = nik_baru.split("-");
                    String restnomor = parts[0];
                    String restnomorbaru = restnomor.replace(" ", "");

                    params.put("szId", mulai_perjalanan.id_pelanggan);

                    params.put("szImageType", "CHECK POSM");
                    params.put("szImage", adapterFoto2.getItem(finalI).getFoto());
                    params.put("szCustomerId", no_surat);
                    params.put("intItemNumber", String.valueOf(finalI));


                    params.put("szBranchId", restnomorbaru);
                    params.put("szUserCreatedId", nik_baru);
                    params.put("szUserUpdatedId", nik_baru);

                    params.put("dtmCreated", currentDateandTime2);
                    params.put("dtmLastUpdated", currentDateandTime2);
                    params.put("szSurveyId", adapterFoto2.getItem(finalI).getSzName());


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
            RequestQueue requestQueue2 = Volley.newRequestQueue(foto_materi.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void materiPosm() {
        pDialog = new SweetAlertDialog(foto_materi.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SalesPosm",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        qtyPosm();

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

                params.put("szDocId", currentDateandTime2 + "_" + no_surat);
                params.put("szCustomerId", no_surat);
                params.put("szEmployeeId", nik_baru);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(foto_materi.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void qtyPosm() {
        uploadFoto();
        uploadKeserver();

        for(int i = 0; i <= materi_posm.dataPosmFotoPojoList.size() - 1; i++){
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SalesPosmItem",
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



                    params.put("szDocId", currentDateandTime2 + "_" + no_surat);
                    params.put("intItemNumber", String.valueOf(finalI));
                    params.put("szProductId", adapterFoto.getItem(finalI).getSzId());

                    params.put("decQty", adapterFoto.getItem(finalI).getQty());
                    params.put("szUomId", nik_baru);

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
            RequestQueue requestQueue2 = Volley.newRequestQueue(foto_materi.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void uploadKeserver() {
        for(int i = 0; i <= materi_posm.dataPosmFotoPojoList.size() - 1; i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/mobile_eis_2/upload_sfa.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(finalI == materi_posm.dataPosmFotoPojoList.size() - 1){
                                pDialog.dismissWithAnimation();
                                pSuccess = new SweetAlertDialog(foto_materi.this, SweetAlertDialog.SUCCESS_TYPE);
                                pSuccess.setContentText("Data Sudah Disimpan");
                                pSuccess.setCancelable(false);
                                pSuccess.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("kode", no_surat);
                                        startActivity(intent);

                                        if(getIntent().getStringExtra("POSM").equals("materi")){
                                            adapterFoto.clear();
                                        } else {
                                            adapterFoto2.clear();
                                        }
                                    }
                                });
                                pSuccess.show();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(finalI == materi_posm.dataPosmFotoPojoList.size() - 1){
                                pDialog.dismissWithAnimation();
                                pSuccess = new SweetAlertDialog(foto_materi.this, SweetAlertDialog.SUCCESS_TYPE);
                                pSuccess.setContentText("Data Sudah Disimpan");
                                pSuccess.setCancelable(false);
                                pSuccess.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(getApplicationContext(), menu_pelanggan.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("kode", no_surat);
                                        startActivity(intent);

                                        if(getIntent().getStringExtra("POSM").equals("materi")){
                                            adapterFoto.clear();
                                        } else {
                                            adapterFoto2.clear();
                                        }
                                    }
                                });
                                pSuccess.show();
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

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                    String currentDateandTime2 = sdf2.format(new Date());

                    String gambar = imagetoString(bitmap);

                    params.put("nik", "POSM" + "_" + adapterFoto.getItem(finalI).getSzName() + "_" + nik_baru + "_" + no_surat + "_" + currentDateandTime2);
                    params.put("foto", adapterFoto.getItem(finalI).getFoto());
                    params.put("nama_folder", "foto_posm");

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

            RequestQueue requestQueue2 = Volley.newRequestQueue(this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void uploadFoto() {

        for(int i = 0; i < materi_posm.dataPosmFotoPojoList.size(); i++){
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_gambar_posm",
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

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                    String currentDateandTime2 = sdf2.format(new Date());

                    String[] parts = nik_baru.split("-");
                    String restnomor = parts[0];
                    String restnomorbaru = restnomor.replace(" ", "");

                    params.put("szId", mulai_perjalanan.id_pelanggan);

                    params.put("szImageType", "POSM");
                    params.put("szImage", adapterFoto.getItem(finalI).getFoto());
                    params.put("szCustomerId", no_surat);
                    params.put("intItemNumber", String.valueOf(finalI));


                    params.put("szBranchId", restnomorbaru);
                    params.put("szUserCreatedId", nik_baru);
                    params.put("szUserUpdatedId", nik_baru);

                    params.put("dtmCreated", currentDateandTime2);
                    params.put("dtmLastUpdated", currentDateandTime2);
                    params.put("szSurveyId", adapterFoto.getItem(finalI).getSzName());


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
            RequestQueue requestQueue2 = Volley.newRequestQueue(foto_materi.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        if(getIntent().getStringExtra("POSM").equals("materi")){
            adapterFoto.clear();
        } else {
            adapterFoto2.clear();
        }
    }

    public class ListViewAdapterFotoCek extends ArrayAdapter<data_posm_foto_pojo> {

        private class ViewHolder {
            TextView produk;
            ImageView uploadgambar;
        }

        List<data_posm_foto_pojo> dataPosmPojoList;
        private final Context context;

        public ListViewAdapterFotoCek(List<data_posm_foto_pojo> dataPosmPojoList, Context context) {
            super(context, R.layout.list_uploadfoto, dataPosmPojoList);
            this.dataPosmPojoList = dataPosmPojoList;
            this.context = context;

        }

        public int getCount() {
            return cek_posm.dataPosmFotoPojoList.size();
        }

        public data_posm_foto_pojo getItem(int position) {
            return cek_posm.dataPosmFotoPojoList.get(position);
        }

        @Override
        public int getViewTypeCount() {
            int count;
            if (cek_posm.dataPosmFotoPojoList.size() > 0) {
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
            data_posm_foto_pojo movieItem = cek_posm.dataPosmFotoPojoList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_uploadfoto, parent, false);
                viewHolder.produk = convertView.findViewById(R.id.produk);
                viewHolder.uploadgambar = convertView.findViewById(R.id.uploadgambar);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.produk.setText(movieItem.getSzName());
            if(!movieItem.getFoto().equals("0")){

                int width=720;
                int height=720;
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                viewHolder.uploadgambar.setImageBitmap(StringToBitMap(movieItem.getFoto()));
            }


            return convertView;
        }
    }

    public class ListViewAdapterFoto extends ArrayAdapter<data_posm_foto_pojo> {

        private class ViewHolder {
            TextView produk;
            ImageView uploadgambar;
        }

        List<data_posm_foto_pojo> dataPosmPojoList;
        private final Context context;

        public ListViewAdapterFoto(List<data_posm_foto_pojo> dataPosmPojoList, Context context) {
            super(context, R.layout.list_uploadfoto, dataPosmPojoList);
            this.dataPosmPojoList = dataPosmPojoList;
            this.context = context;

        }

        public int getCount() {
            return materi_posm.dataPosmFotoPojoList.size();
        }

        public data_posm_foto_pojo getItem(int position) {
            return materi_posm.dataPosmFotoPojoList.get(position);
        }

        @Override
        public int getViewTypeCount() {
            int count;
            if (materi_posm.dataPosmFotoPojoList.size() > 0) {
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
            data_posm_foto_pojo movieItem = materi_posm.dataPosmFotoPojoList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_uploadfoto, parent, false);
                viewHolder.produk = convertView.findViewById(R.id.produk);
                viewHolder.uploadgambar = convertView.findViewById(R.id.uploadgambar);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.produk.setText(movieItem.getSzName());
            if(!movieItem.getFoto().equals("0")){
                viewHolder.uploadgambar.setImageBitmap(StringToBitMap(movieItem.getFoto()));
            }


            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(getIntent().getStringExtra("POSM").equals("materi")){
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                int width=720;
                int height=720;
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                adapterFoto.getItem(request).setFoto(imagetoString(bitmap));
                adapterFoto.notifyDataSetChanged();
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                int width=720;
                int height=720;
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                adapterFoto2.getItem(request).setFoto(imagetoString(bitmap));
                adapterFoto2.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imagetoString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}