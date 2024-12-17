package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

public class update_outlet extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    AutoCompleteTextView jenisperubahan;
    EditText editnilai;
    LinearLayout fotooutlet, linear_gambar;
    ImageView uploadgambar;
    Bitmap bitmap;
    Button simpan, batal;
    TextView textupload;
    ArrayList<String> ReasonUpdate = new ArrayList<>();
    SweetAlertDialog pDialog;

    ContentValues cv;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_update_outlet);
        jenisperubahan = findViewById(R.id.jenisperubahan);
        fotooutlet = findViewById(R.id.fotooutlet);
        linear_gambar = findViewById(R.id.linear_gambar);
        editnilai = findViewById(R.id.editnilai);
        simpan = findViewById(R.id.simpan);
        batal = findViewById(R.id.batal);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        uploadgambar = findViewById(R.id.uploadgambar);
        textupload = findViewById(R.id.textupload);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });

        jenisperubahan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    jenisperubahan.showDropDown();
            }
        });

        jenisperubahan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                jenisperubahan.showDropDown();
                return false;
            }
        });

        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Reason_Update",
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
                                    ReasonUpdate.add(id + "-" + jenis_istirahat);

                                }
                            }
                            jenisperubahan.setAdapter(new ArrayAdapter<String>(update_outlet.this, android.R.layout.simple_expandable_list_item_1, ReasonUpdate));
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
        RequestQueue requestkota = Volley.newRequestQueue(update_outlet.this);
        requestkota.getCache().clear();

        requestkota.add(rest);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisperubahan.setError(null);
                editnilai.setError(null);
                if(jenisperubahan.getText().toString().length() == 0){
                    jenisperubahan.setError("Silahkan Pilih Alasan");
                } else if (editnilai.getText().toString().length() == 0) {
                    editnilai.setError("Silahkan Isi Nilai");
                } else if (textupload.getVisibility() == View.VISIBLE) {
                    new SweetAlertDialog(update_outlet.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Upload Gambar")
                            .setConfirmText("OK")
                            .show();
                } else {
                    pDialog = new SweetAlertDialog(update_outlet.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_update_outlet",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    uploadgambaroutlet();

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

                            String[] parts = jenisperubahan.getText().toString().split("-");
                            String restnomor = parts[0];
                            String restnomorbaru = restnomor.replace(" ", "");

                            params.put("szDocId", currentDateandTime2);
                            params.put("dtmDoc", currentDateandTime3);

                            params.put("szEmployeeId", nik_baru);
                            params.put("szCustomerId", no_surat);
                            params.put("CustomerFieldUpdate", restnomorbaru);

                            params.put("szValue", editnilai.getText().toString());
                            params.put("szDocCallId", mulai_perjalanan.id_pelanggan);

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
                    RequestQueue requestQueue2 = Volley.newRequestQueue(update_outlet.this);
                    requestQueue2.getCache().clear();
                    requestQueue2.add(stringRequest2);
                }
            }
        });


    }

    private void uploadgambaroutlet() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_gambar",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        SweetAlertDialog success = new SweetAlertDialog(update_outlet.this, SweetAlertDialog.SUCCESS_TYPE);
                        success.setContentText("Data Telah Disimpan");
                        success.setCancelable(false);
                        success.setConfirmText("OK");
                        success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                success.dismissWithAnimation();
                                finish();
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
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                String[] parts = nik_baru.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");

                params.put("iId", currentDateandTime2);
                params.put("szId", mulai_perjalanan.id_pelanggan);

                params.put("szImageType", "SURVEY");
                params.put("szImage", ImageToString(bitmap));
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
        RequestQueue requestQueue2 = Volley.newRequestQueue(update_outlet.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
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

    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}