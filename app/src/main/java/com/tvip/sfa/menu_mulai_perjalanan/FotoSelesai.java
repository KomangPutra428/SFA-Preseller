package com.tvip.sfa.menu_mulai_perjalanan;

import static android.content.ContentValues.TAG;
import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.failreason;
import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import org.apache.commons.io.FileUtils;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_utama.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FotoSelesai extends AppCompatActivity {
    MaterialCardView fototampakdepan, fotochiller, fototampaksamping;
    ImageView uploadgambar, uploadgambar2, uploadgambar3;
    TextView textupload, textupload2, textupload3;

    ContentValues cv;
    Uri imageUri;
    Bitmap bitmap, bitmap2, bitmap3;

    Button lanjutkan;

    SharedPreferences sharedPreferences;

    SweetAlertDialog success;
    static SweetAlertDialog pDialog;

    private RequestQueue requestQueue;
    private RequestQueue requestQueue2;
    private RequestQueue requestQueue3;
    private RequestQueue requestQueue4;

    private RequestQueue requestQueue5;
    private RequestQueue requestQueue6;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_selesai);
        fototampakdepan = findViewById(R.id.fototampakdepan);
        fotochiller = findViewById(R.id.fotochiller);
        fototampaksamping = findViewById(R.id.fototampaksamping);

        uploadgambar = findViewById(R.id.uploadgambar);
        uploadgambar2 = findViewById(R.id.uploadgambar2);
        uploadgambar3 = findViewById(R.id.uploadgambar3);

        textupload = findViewById(R.id.textupload);
        textupload2 = findViewById(R.id.textupload2);
        textupload3 = findViewById(R.id.textupload3);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        lanjutkan = findViewById(R.id.lanjutkan);

        failreason = "";


        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_NonSO?szDocId=" + mulai_perjalanan.id_pelanggan + "&szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String szDocSO = jsonObject1.getString("szDocSO");
                                    String szFailReason = jsonObject1.getString("szFailReason");

                                    //updateDoccall(szDocSO, szFailReason);

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
        rest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestkota = Volley.newRequestQueue(FotoSelesai.this);
        requestkota.getCache().clear();
        requestkota.add(rest);

        lanjutkan.setOnClickListener(v -> {
            if (textupload.getVisibility() == View.VISIBLE) {
                new SweetAlertDialog(FotoSelesai.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Upload Gambar")
                        .setConfirmText("OK")
                        .show();
            } else if (textupload2.getVisibility() == View.VISIBLE) {
                new SweetAlertDialog(FotoSelesai.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Upload Gambar")
                        .setConfirmText("OK")
                        .show();
            } else if (textupload3.getVisibility() == View.VISIBLE) {
                new SweetAlertDialog(FotoSelesai.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Upload Gambar")
                        .setConfirmText("OK")
                        .show();
            } else {
                pDialog = new SweetAlertDialog(FotoSelesai.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Harap Menunggu");
                pDialog.setCancelable(false);
                pDialog.show();
                postImage();
            }
        });

        fototampakdepan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });

        fotochiller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2);
            }
        });

        fototampaksamping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 3);
            }
        });
    }

    private void updateDoccall(String szDocSO, String szFailReason) {
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
                params.put("szDocSO", szDocSO);
                params.put("bPostPone", "0");
                params.put("bFinisihed", "1");
                params.put("bSuccess", "0");
                params.put("bVisited" , "1");
                params.put("dtmFinish", currentDateandTime2);


                params.put("szFailReason", szFailReason);


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
        RequestQueue requestQueue2 = Volley.newRequestQueue(FotoSelesai.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);

    }

    private void postImage3() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_selesai",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();

                        success = new SweetAlertDialog(FotoSelesai.this, SweetAlertDialog.SUCCESS_TYPE);
                        success.setContentText("Data Sudah Disimpan");
                        success.setCancelable(false);
                        success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

//                                deletePictures();
                                Toast.makeText(getApplicationContext(), "Sedang Memuat...", Toast.LENGTH_SHORT).show();


                                Intent intent = new Intent(FotoSelesai.this, MainActivity.class);
                                startActivity(intent);
                                System.exit(0);

//                                ExampleAsync task = new ExampleAsync();
//                                task.execute(10);





                                // deleteCache(getApplicationContext());
                              }
                        });
                        success.show();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();

                success = new SweetAlertDialog(FotoSelesai.this, SweetAlertDialog.SUCCESS_TYPE);
                success.setContentText("Data Sudah Disimpan");
                success.setCancelable(false);
                success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

//                        deletePictures();
                        Toast.makeText(getApplicationContext(), "Sedang Memuat...", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(FotoSelesai.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        System.exit(0);
                    }
                });
                success.show();
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

                params.put("iId", currentDateandTime2);
                params.put("szId", mulai_perjalanan.id_pelanggan);

                params.put("szImageType", "VISIT");
                params.put("intItemNumber", "2");

                params.put("szImage", ImageToString(bitmap3));
                params.put("szCustomerId", menu_pelanggan.no_surat);

                params.put("szBranchId", restnomorbaru);
                params.put("szUserCreatedId", nik_baru);
                params.put("szUserUpdatedId", nik_baru);

                params.put("dtmCreated", currentDateandTime2);
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
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(FotoSelesai.this);
            requestQueue.add(stringRequest2);
        } else {
            requestQueue.add(stringRequest2);
        }
    }

    private void postImage2() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_selesai",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        postImage3();
                        uploadKeServer2();
                        uploadKeServer3();
                    }

                    private void uploadKeServer3() {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/mobile_eis_2/upload_sfa.php",
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
                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                String nik_baru = sharedPreferences.getString("szDocCall", null);

                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                                String currentDateandTime2 = sdf2.format(new Date());

                                String gambar = ImageToString(bitmap3);

                                params.put("nik", "VISIT_2" + "_" + nik_baru + "_" + no_surat + "_" + currentDateandTime2);
                                params.put("foto", gambar);
                                params.put("nama_folder", "foto_selesai_kunjungan");


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
                            requestQueue2 = Volley.newRequestQueue(FotoSelesai.this);
                            requestQueue2.add(stringRequest2);
                        } else {
                            requestQueue2.add(stringRequest2);
                        }
                    }

                    private void uploadKeServer2() {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/mobile_eis_2/upload_sfa.php",
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
                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                String nik_baru = sharedPreferences.getString("szDocCall", null);

                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                                String currentDateandTime2 = sdf2.format(new Date());

                                String gambar = ImageToString(bitmap2);

                                params.put("nik", "VISIT_1" + "_" + nik_baru + "_" + no_surat + "_" + currentDateandTime2);
                                params.put("foto", gambar);
                                params.put("nama_folder", "foto_selesai_kunjungan");

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
                            requestQueue3 = Volley.newRequestQueue(FotoSelesai.this);
                            requestQueue3.add(stringRequest2);
                        } else {
                            requestQueue3.add(stringRequest2);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                postImage3();
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

                params.put("iId", currentDateandTime2);
                params.put("szId", mulai_perjalanan.id_pelanggan);

                params.put("szImageType", "VISIT");
                params.put("intItemNumber", "1");

                params.put("szImage", ImageToString(bitmap2));
                params.put("szCustomerId", menu_pelanggan.no_surat);

                params.put("szBranchId", restnomorbaru);
                params.put("szUserCreatedId", nik_baru);
                params.put("szUserUpdatedId", nik_baru);

                params.put("dtmCreated", currentDateandTime2);
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
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(FotoSelesai.this);
            requestQueue4.add(stringRequest2);
        } else {
            requestQueue4.add(stringRequest2);
        }
    }

    private void postImage() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_selesai",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        postImage2();
                        UploadKeServer();
                    }

                    private void UploadKeServer() {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/mobile_eis_2/upload_sfa.php",
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
                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                String nik_baru = sharedPreferences.getString("szDocCall", null);

                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                                String currentDateandTime2 = sdf2.format(new Date());

                                String gambar = ImageToString(bitmap);

                                params.put("nik", "VISIT_0" + "_" + nik_baru + "_" + no_surat + "_" + currentDateandTime2);
                                params.put("foto", gambar);
                                params.put("nama_folder", "foto_selesai_kunjungan");


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
                            requestQueue5 = Volley.newRequestQueue(FotoSelesai.this);
                            requestQueue5.add(stringRequest2);
                        } else {
                            requestQueue5.add(stringRequest2);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                postImage2();
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

                params.put("iId", currentDateandTime2);
                params.put("szId", mulai_perjalanan.id_pelanggan);

                params.put("szImageType", "VISIT");
                params.put("intItemNumber", "0");

                params.put("szImage", ImageToString(bitmap));
                params.put("szCustomerId", menu_pelanggan.no_surat);

                params.put("szBranchId", restnomorbaru);
                params.put("szUserCreatedId", nik_baru);
                params.put("szUserUpdatedId", nik_baru);

                params.put("dtmCreated", currentDateandTime2);
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
        if (requestQueue6 == null) {
            requestQueue6 = Volley.newRequestQueue(FotoSelesai.this);
            requestQueue6.add(stringRequest2);
        } else {
            requestQueue6.add(stringRequest2);
        }
    }

    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    int width=720;
                    int height=720;
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                    uploadgambar.setImageBitmap(bitmap);
                    textupload.setVisibility(View.GONE);

                    ViewGroup.LayoutParams paramktp = uploadgambar.getLayoutParams();

                    double sizeInDP = 226;
                    int marginInDp = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP, getResources()
                                    .getDisplayMetrics());

                    double sizeInDP2 = 226;
                    int marginInDp2 = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP2, getResources()
                                    .getDisplayMetrics());

                    paramktp.width = marginInDp;
                    paramktp.height = marginInDp2;
                    uploadgambar.setLayoutParams(paramktp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bitmap2 = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    int width=720;
                    int height=720;
                    bitmap2 = Bitmap.createScaledBitmap(bitmap2, width, height, true);
                    uploadgambar2.setImageBitmap(bitmap2);
                    textupload2.setVisibility(View.GONE);

                    ViewGroup.LayoutParams paramktp = uploadgambar2.getLayoutParams();

                    double sizeInDP = 226;
                    int marginInDp = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP, getResources()
                                    .getDisplayMetrics());

                    double sizeInDP2 = 226;
                    int marginInDp2 = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP2, getResources()
                                    .getDisplayMetrics());

                    paramktp.width = marginInDp;
                    paramktp.height = marginInDp2;
                    uploadgambar2.setLayoutParams(paramktp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 3){
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bitmap3 = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    int width=720;
                    int height=720;
                    bitmap3 = Bitmap.createScaledBitmap(bitmap3, width, height, true);
                    uploadgambar3.setImageBitmap(bitmap3);
                    textupload3.setVisibility(View.GONE);

                    ViewGroup.LayoutParams paramktp = uploadgambar3.getLayoutParams();

                    double sizeInDP = 226;
                    int marginInDp = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP, getResources()
                                    .getDisplayMetrics());

                    double sizeInDP2 = 226;
                    int marginInDp2 = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP2, getResources()
                                    .getDisplayMetrics());

                    paramktp.width = marginInDp;
                    paramktp.height = marginInDp2;
                    uploadgambar3.setLayoutParams(paramktp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

    }

    private void deletePictures(){
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            getApplicationContext().getContentResolver().delete(deleteUri, null, null);
        }
        cursor.close();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }


    private class ExampleAsync extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // ...
        }

        @Override
        protected String doInBackground(Integer... integers) {
            // ...
            pDialog = new SweetAlertDialog(FotoSelesai.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Harap Menunggu");
            pDialog.setCancelable(false);
            pDialog.show();
            return "Finished!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // ...
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            // ...
        }
    }



}