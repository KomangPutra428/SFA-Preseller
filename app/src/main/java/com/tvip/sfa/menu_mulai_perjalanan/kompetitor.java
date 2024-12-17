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

public class kompetitor extends AppCompatActivity {
    TextView toko, kodetoko, alamattoko;
    AutoCompleteTextView kompetitor, jenispenawaran;
    ArrayList<String> Kompetitor = new ArrayList<>();
    ArrayList<String> JenisPenawaran = new ArrayList<>();
    Button batal, simpan;
    EditText catatan;
    SweetAlertDialog pDialog;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_kompetitor);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        toko = findViewById(R.id.toko);
        kodetoko = findViewById(R.id.kodetoko);
        alamattoko = findViewById(R.id.alamattoko);

        batal = findViewById(R.id.batal);
        simpan = findViewById(R.id.simpan);

        kompetitor = findViewById(R.id.kompetitor);
        jenispenawaran = findViewById(R.id.jenispenawaran);

        catatan = findViewById(R.id.catatan);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kompetitor.getText().toString().length() == 0){
                    kompetitor.setError("Pilih Kompetitor");
                } else if(jenispenawaran.getText().toString().length() == 0){
                    jenispenawaran.setError("Pilih Jenis Penawaran");
                } else if(catatan.getText().toString().length() == 0){
                    catatan.setError("Isi Catatan");
                } else {
                    pDialog = new SweetAlertDialog(kompetitor.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_competitor",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    pDialog.dismissWithAnimation();
                                    new SweetAlertDialog(kompetitor.this, SweetAlertDialog.SUCCESS_TYPE)
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

                            String[] parts = kompetitor.getText().toString().split("-");
                            String restnomor = parts[0];
                            String restnomorbaru = restnomor.replace(" ", "");

                            String[] parts2 = nik_baru.split("-");
                            String restnomor2 = parts2[0];
                            String restnomorbaru2 = restnomor2.replace(" ", "");

                            String[] parts3 = jenispenawaran.getText().toString().split("-");
                            String restnomor3 = parts3[0];
                            String restnomorbaru3 = restnomor3.replace(" ", "");

                            params.put("szDocId", currentDateandTime2);
                            params.put("dtmDoc", currentDateandTime3);

                            params.put("szCustomerId", no_surat);
                            params.put("szEmployeeId", nik_baru);

                            params.put("szCompetitorId", restnomorbaru);
                            params.put("szType", restnomorbaru3);

                            params.put("szValue", catatan.getText().toString());
                            params.put("szDocCallId", mulai_perjalanan.id_pelanggan);

                            params.put("intPrintedCount", "0");
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
                    RequestQueue requestQueue2 = Volley.newRequestQueue(kompetitor.this);
                    requestQueue2.getCache().clear();
                    requestQueue2.add(stringRequest2);
                }
            }
        });

        jenispenawaran.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    jenispenawaran.showDropDown();
            }
        });

        jenispenawaran.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                jenispenawaran.showDropDown();
                return false;
            }
        });

        kompetitor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    kompetitor.showDropDown();
            }
        });

        kompetitor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                kompetitor.showDropDown();
                return false;
            }
        });

        StringRequest product_competitor = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Product_competitor",
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
                                    Kompetitor.add(id + "-" + jenis_istirahat);

                                }
                            }
                            kompetitor.setAdapter(new ArrayAdapter<String>(kompetitor.this, android.R.layout.simple_expandable_list_item_1, Kompetitor));
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
        product_competitor.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestproduct_competitor = Volley.newRequestQueue(kompetitor.this);
        requestproduct_competitor.getCache().clear();
        requestproduct_competitor.add(product_competitor);

        StringRequest reason_competitor = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Reason_competitor",
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
                                    JenisPenawaran.add(id + "-" + jenis_istirahat);

                                }
                            }
                            jenispenawaran.setAdapter(new ArrayAdapter<String>(kompetitor.this, android.R.layout.simple_expandable_list_item_1, JenisPenawaran));
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
        reason_competitor.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestreason_competitor = Volley.newRequestQueue(kompetitor.this);
        requestreason_competitor.getCache().clear();
        requestreason_competitor.add(reason_competitor);

        StringRequest detail_pelanggan = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Detail_Pelanggan?szCustomerId=" + no_surat,
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

        detail_pelanggan.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue detail_pelangganQueue = Volley.newRequestQueue(this);
        detail_pelangganQueue.getCache().clear();
        detail_pelangganQueue.add(detail_pelanggan);
    }
}