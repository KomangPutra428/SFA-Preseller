package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.tvip.sfa.menu_persiapan.callplan;
import com.tvip.sfa.menu_persiapan.daftar_kunjungan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class detailscangagal_dalamrute extends AppCompatActivity {
    static TextView namatoko;
    static EditText code;
    static EditText address;
    static AutoCompleteTextView alasangagalscan;
    static String intItemNumbers;
    Button batal, lanjutkan;
    ArrayList<String> GagalScan = new ArrayList<>();
    MaterialToolbar persiapanbar;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_detailscangagal_dalamrute);
        namatoko = findViewById(R.id.namatoko);
        code = findViewById(R.id.code);
        address = findViewById(R.id.address);
        alasangagalscan = findViewById(R.id.alasangagalscan);
        persiapanbar = findViewById(R.id.persiapanbar);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);

        namatoko.setText(getIntent().getStringExtra("nama"));
        code.setText(getIntent().getStringExtra("kode"));
        address.setText(getIntent().getStringExtra("alamat"));

        persiapanbar.setTitle(getIntent().getStringExtra("jenispelanggan"));


        intItemNumbers = getIntent().getStringExtra("intItemNumber");

        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Scan_Barcode_Failed",
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
                                    GagalScan.add(id + "-" + jenis_istirahat);

                                }
                            }
                            alasangagalscan.setAdapter(new ArrayAdapter<String>(detailscangagal_dalamrute.this, android.R.layout.simple_expandable_list_item_1, GagalScan));
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
        RequestQueue requestkota = Volley.newRequestQueue(detailscangagal_dalamrute.this);
        requestkota.getCache().clear();
        requestkota.add(rest);

        alasangagalscan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    alasangagalscan.showDropDown();
            }
        });

        alasangagalscan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                alasangagalscan.showDropDown();
                return false;
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
                alasangagalscan.setError(null);
                if(alasangagalscan.getText().toString().length() ==0){
                    alasangagalscan.setError("Silahkan Pilih Alasan");
                } else {
                    if(getIntent().getStringExtra("langitude").equals("")){
                        Toast.makeText(getBaseContext(), "Toko Tidak Ada Longlat", Toast.LENGTH_LONG).show();
                    } else {
                        Intent direction = new Intent(getBaseContext(), scangagal_map.class);

                        direction.putExtra("langitude", getIntent().getStringExtra("langitude"));
                        direction.putExtra("longitude", getIntent().getStringExtra("longitude"));
                        direction.putExtra("toko", namatoko.getText().toString());
                        direction.putExtra("alamat", address.getText().toString());
                        direction.putExtra("kode", code.getText().toString());

                        startActivity(direction);
                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_gagal_scan",
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

                                String rest = alasangagalscan.getText().toString();
                                String[] parts = rest.split("-");
                                String restnomor = parts[0];
                                String restnomorbaru = restnomor.replace(" ", "");

                                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                params.put("szCustomerId", getIntent().getStringExtra("kode"));
                                params.put("szReasonFailedScan", restnomorbaru);



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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(detailscangagal_dalamrute.this);
                        requestQueue2.getCache().clear();
                        requestQueue2.add(stringRequest2);

                    }
                }


            }
        });

    }
}