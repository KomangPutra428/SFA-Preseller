package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.tvip.sfa.menu_selesai_perjalanan.selesai_perjalanan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

public class feedback extends AppCompatActivity {
    TextView toko, kodetoko, alamattoko;
    AutoCompleteTextView jenisfeedback;
    EditText catatan;
    Button batal, simpan;
    ArrayList<String> GagalCheckin = new ArrayList<>();
    SweetAlertDialog pDialog;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_feedback);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        toko = findViewById(R.id.toko);
        kodetoko = findViewById(R.id.kodetoko);
        alamattoko = findViewById(R.id.alamattoko);
        jenisfeedback = findViewById(R.id.jenisfeedback);
        catatan = findViewById(R.id.catatan);

        batal = findViewById(R.id.batal);
        simpan = findViewById(R.id.simpan);

        jenisfeedback.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    jenisfeedback.showDropDown();
            }
        });

        jenisfeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                jenisfeedback.showDropDown();
                return false;
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisfeedback.setError(null);
                catatan.setError(null);
                if(jenisfeedback.getText().toString().length() == 0){
                    jenisfeedback.setError("Pilih Jenis Feedback");
                } else if (catatan.getText().toString().length() == 0){
                    catatan.setError("Isi Catatan");
                } else {
                    pDialog = new SweetAlertDialog(feedback.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_feedback",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    pDialog.dismissWithAnimation();
                                    new SweetAlertDialog(feedback.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setContentText("Data Sudah Disimpan")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
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

                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                            String currentDateandTime2 = sdf2.format(new Date());

                            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateandTime3 = sdf3.format(new Date());

                            String[] parts = jenisfeedback.getText().toString().split("-");
                            String restnomor = parts[0];
                            String restnomorbaru = restnomor.replace(" ", "");

                            String[] parts2 = nik_baru.split("-");
                            String restnomor2 = parts2[0];
                            String restnomorbaru2 = restnomor2.replace(" ", "");

                            params.put("szDocId", currentDateandTime2);
                            params.put("dtmDoc", currentDateandTime3);

                            params.put("szCustomerId", no_surat);
                            params.put("szEmployeeId", nik_baru);

                            params.put("szFeedbackType", restnomorbaru);
                            params.put("szFeedback", catatan.getText().toString());

                            params.put("szDocCallId", mulai_perjalanan.id_pelanggan);

                            params.put("intPrintedCount", "0");
                            params.put("szFeedback", catatan.getText().toString());

                            params.put("szBranchId", restnomorbaru2);

                            if(restnomorbaru2.equals("321") || restnomorbaru2.equals("336") || restnomorbaru2.equals("324") || restnomorbaru2.equals("317") || restnomorbaru2.equals("036")){
                                params.put("szCompanyId", "ASA");
                            } else {
                                params.put("szCompanyId", "TVIP");
                            }

                            params.put("szDocStatus", "Draft");

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
                    RequestQueue requestQueue2 = Volley.newRequestQueue(feedback.this);
                    requestQueue2.getCache().clear();
                    requestQueue2.add(stringRequest2);
                }
            }
        });

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Reason_feedback",
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
                            jenisfeedback.setAdapter(new ArrayAdapter<String>(feedback.this, android.R.layout.simple_expandable_list_item_1, GagalCheckin));
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
        RequestQueue requestkota = Volley.newRequestQueue(feedback.this);
        requestkota.add(rest);

        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Detail_Pelanggan?szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                kodetoko.setText(biodatas.getString("szCustomerId"));
                                toko.setText(biodatas.getString("szName"));
                                alamattoko.setText(biodatas.getString("szAddress"));



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
        RequestQueue channel_statusQueue = Volley.newRequestQueue(this);
        channel_statusQueue.add(channel_status);

    }
}