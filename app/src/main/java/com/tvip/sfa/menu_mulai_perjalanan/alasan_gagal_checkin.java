package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.detailscangagal_dalamrute.intItemNumbers;
import static com.tvip.sfa.menu_mulai_perjalanan.mulai_perjalanan.lastitem;
import static com.tvip.sfa.menu_mulai_perjalanan.mulai_perjalanan.outofroute;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_utama.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class alasan_gagal_checkin extends AppCompatActivity {
    TextView toko, kodetoko, alamattoko;
    AutoCompleteTextView alasangagalcheckin;
    Button batal, lanjutkan;
    LinearLayout fotooutlet, linear_gambar;
    ImageView uploadgambar;
    Bitmap bitmap;
    TextView textupload;
    ArrayList<String> GagalCheckin = new ArrayList<>();
    SharedPreferences sharedPreferences;

    private RequestQueue requestQueue, requestQueue2;

    ContentValues cv;
    Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_alasan_gagal_checkin);

        toko = findViewById(R.id.toko);
        kodetoko = findViewById(R.id.kodetoko);
        alamattoko = findViewById(R.id.alamattoko);
        alasangagalcheckin = findViewById(R.id.alasangagalcheckin);

        fotooutlet = findViewById(R.id.fotooutlet);
        uploadgambar = findViewById(R.id.uploadgambar);
        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);
        linear_gambar = findViewById(R.id.linear_gambar);
        textupload = findViewById(R.id.textupload);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        toko.setText(detailscangagal_dalamrute.namatoko.getText().toString());
        kodetoko.setText(detailscangagal_dalamrute.code.getText().toString());
        alamattoko.setText(detailscangagal_dalamrute.address.getText().toString());




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
                            alasangagalcheckin.setAdapter(new ArrayAdapter<String>(alasan_gagal_checkin.this, android.R.layout.simple_expandable_list_item_1, GagalCheckin));
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
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(alasan_gagal_checkin.this);
            requestQueue.getCache().clear();
            requestQueue.add(rest);
        } else {
            requestQueue.add(rest);
        }


        alasangagalcheckin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    alasangagalcheckin.showDropDown();
            }
        });

        alasangagalcheckin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                alasangagalcheckin.showDropDown();
                return false;
            }
        });

        fotooutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alasangagalcheckin.setError(null);
                if(alasangagalcheckin.getText().toString().length() == 0){
                    alasangagalcheckin.setError("Silahkan Pilih Alasan");
                } else if (textupload.getVisibility() == View.VISIBLE) {
                    new SweetAlertDialog(alasan_gagal_checkin.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Upload Gambar")
                            .setConfirmText("OK")
                            .show();
                } else {
                    Intent dalam_rute = new Intent(getBaseContext(), menu_pelanggan.class);
                    dalam_rute.putExtra("kode", kodetoko.getText().toString());
                    startActivity(dalam_rute);

                    sfa_doccall();


                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_gagal_CheckIn",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    uploadgambarsfa();
                                    uploadkeServer();

                                }

                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    uploadgambarsfa();
                                    uploadkeServer();
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

                            String rest = alasangagalcheckin.getText().toString();
                            String[] parts = rest.split("-");
                            String restnomor = parts[0];
                            String restnomorbaru = restnomor.replace(" ", "");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateandTime2 = sdf2.format(new Date());

                            params.put("szDocId", mulai_perjalanan.id_pelanggan);
                            params.put("szCustomerId", detailscangagal_dalamrute.code.getText().toString());
                            params.put("szReasonIdCheckin", restnomorbaru);
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
                    RequestQueue requestQueue2 = Volley.newRequestQueue(alasan_gagal_checkin.this);
                    requestQueue2.getCache().clear();
                    requestQueue2.add(stringRequest2);

                }

            }

            private void sfa_doccall() {
                String rest = alasangagalcheckin.getText().toString();
                String[] parts = rest.split("-");
                String restnomor = parts[0];

                String rest2 = detailscangagal_dalamrute.alasangagalscan.getText().toString();
                String[] parts2 = rest2.split("-");
                String restnomor2 = parts2[0];

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());




                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getStackTrace());
                        VolleyLog.d("ErrorVolley", "Error: " + error.getStackTrace());
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

                        if(outofroute.equals("0")){
                            params.put("intItemNumber", intItemNumbers);
                        } else {
                            params.put("intItemNumber", lastitem);
                        }


                        params.put("szCustomerId", kodetoko.getText().toString());

                        params.put("dtmStart", currentDateandTime2);
                        params.put("dtmFinish", currentDateandTime2);

                        params.put("bVisited", "1");
                        params.put("bSuccess", "0");


                        params.put("szFailReason", "");
                        params.put("szLangitude", MainActivity.latitude);
                        params.put("szLongitude", MainActivity.longitude);
                        params.put("bOutOfRoute", outofroute);
                        params.put("szRefDocId", "");

                        params.put("bScanBarcode", "0");

                        params.put("szReasonIdCheckin", restnomor);
                        params.put("szReasonFailedScan", restnomor2);
                        params.put("decRadiusDiff", "0");

                        System.out.println(params);

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
                RequestQueue requestQueue2 = Volley.newRequestQueue(alasan_gagal_checkin.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);
            }
        });
    }

    private void uploadkeServer() {
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

                String gambar = imagetoString(bitmap);

                params.put("nik", "FailedCheckIn" + "_" + nik_baru + "_" + kodetoko.getText().toString() + "_" + currentDateandTime2);
                params.put("nama_folder", "foto_gagal_checkin");
                params.put("foto", gambar);


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
            requestQueue2 = Volley.newRequestQueue(alasan_gagal_checkin.this);
            requestQueue2.add(stringRequest2);
        } else {
            requestQueue2.add(stringRequest2);
        }

    }

    private void uploadgambarsfa() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_gambar",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
//                        SF();

                    }

                    private void SF() {
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


                                String rest2 = detailscangagal_dalamrute.alasangagalscan.getText().toString();
                                String[] parts2 = rest2.split("-");
                                String restnomor2 = parts2[0];

                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateandTime2 = sdf2.format(new Date());

                                String rest = alasangagalcheckin.getText().toString();
                                String[] parts = rest.split("-");
                                String restnomor = parts[0];

                                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                params.put("intItemNumber", lastitem);

                                params.put("szCustomerId", detailscangagal_dalamrute.code.getText().toString());

                                params.put("dtmStart", currentDateandTime2);
                                params.put("dtmFinish", currentDateandTime2);

                                params.put("bVisited", "1");
                                params.put("bSuccess", "0");

                                params.put("szFailReason", "");
                                params.put("szLangitude", MainActivity.latitude);
                                params.put("szLongitude", MainActivity.longitude);
                                params.put("bOutOfRoute", outofroute);
                                params.put("szRefDocId", "");

                                params.put("bScanBarcode", "0");

                                params.put("szReasonIdCheckin", restnomor);
                                params.put("szReasonFailedScan", restnomor2);
                                params.put("decRadiusDiff", "0");








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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(alasan_gagal_checkin.this);
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
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                String currentDateandTime2 = sdf2.format(new Date());

                String[] parts = nik_baru.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");

                params.put("iId", currentDateandTime2);
                params.put("szId", mulai_perjalanan.id_pelanggan);

                params.put("szImageType", "FAILED_CHECKIN");
                params.put("szImage", ImageToString(bitmap));
                params.put("szCustomerId", detailscangagal_dalamrute.code.getText().toString());

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(alasan_gagal_checkin.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imagetoString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}