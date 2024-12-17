package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.tvip.sfa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class pengajuan_kredit extends AppCompatActivity {
    SignaturePad signature_pad;
    ImageButton refresh;
    EditText limitsaatini, jumlahpengajuan;
    Button batal, simpan;
    SharedPreferences sharedPreferences;
    SweetAlertDialog pDialog, Success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(SavedInstanceFragment.getInstance(getFragmentManager()).popData());
        setContentView(R.layout.activity_pengajuan_kredit);
        refresh = findViewById(R.id.refresh);
        signature_pad = findViewById(R.id.signature_pad);
        limitsaatini = findViewById(R.id.limitsaatini);
        jumlahpengajuan = findViewById(R.id.jumlahpengajuan);
        batal = findViewById(R.id.batal);
        simpan = findViewById(R.id.simpan);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        jumlahpengajuan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] parts = limitsaatini.getText().toString().split("\\.");
                String szIdSlice = parts[0];

                if (jumlahpengajuan.getText().toString().length() == 0){

                } else if (Long.parseLong(szIdSlice) < Long.parseLong(jumlahpengajuan.getText().toString())){
                    jumlahpengajuan.setText("0");
                    new SweetAlertDialog(pengajuan_kredit.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Pengajuan limit tidak boleh lebih besar dari saat ini")
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
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] parts = limitsaatini.getText().toString().split("\\.");
                String szIdSlice = parts[0];

                if(jumlahpengajuan.getText().toString().length() == 0){
                    jumlahpengajuan.setError("Isi jumlah pengajuan");
                } else if (Long.parseLong(szIdSlice) < Long.parseLong(jumlahpengajuan.getText().toString())){
                    new SweetAlertDialog(pengajuan_kredit.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Pengajuan limit tidak boleh lebih besar dari saat ini")
                            .setConfirmText("OK")
                            .show();

                } else {
                    uploadImage();
                }
            }
        });

        refresh.bringToFront();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signature_pad.clear();
            }
        });

        pDialog = new SweetAlertDialog(pengajuan_kredit.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Detail_Pelanggan?szId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                limitsaatini.setText(biodatas.getString("decCreditLimit"));

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
                        limitsaatini.setText("0.0000");
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
        channel_statusQueue.getCache().clear();
        channel_statusQueue.add(channel_status);

    }

    private void uploadImage() {
        pDialog = new SweetAlertDialog(pengajuan_kredit.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_gambar",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        postDecLimit();
                        uploadKeServer();
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

                params.put("szImageType", "CREDIT_LIMIT");
                params.put("szImage", ImageToString(signature_pad.getSignatureBitmap()));
                params.put("szCustomerId", no_surat);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(pengajuan_kredit.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void uploadKeServer() {
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

                String gambar = ImageToString(signature_pad.getSignatureBitmap());

                params.put("nik", "CREDIT_LIMIT"  + "_" + nik_baru + "_" + no_surat + "_" + currentDateandTime2);
                params.put("foto", gambar);
                params.put("nama_folder", "foto_tanda_tangan_kredit");

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

    private void postDecLimit() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_limitrequest",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        Success = new SweetAlertDialog(pengajuan_kredit.this, SweetAlertDialog.SUCCESS_TYPE);
                        Success.setCancelable(false);
                        Success.setContentText("Data Sudah Disimpan");
                        Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        finish();

                                    }
                                });
                        Success.show();

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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentDateandTime2 = sdf2.format(new Date());

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime3 = sdf3.format(new Date());

                params.put("szDocId", currentDateandTime2);
                params.put("dtmDoc", currentDateandTime3);

                params.put("szEmployeeId", nik_baru);
                params.put("szCustomerId", no_surat);

                params.put("decCurrentLimit", limitsaatini.getText().toString());
                params.put("decLimitRequest", jumlahpengajuan.getText().toString());


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
        RequestQueue requestQueue2 = Volley.newRequestQueue(pengajuan_kredit.this);
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
}