package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class pelanggan_baru_pemilik extends AppCompatActivity {
    static EditText namaowner;
    EditText alamatowner;
    AutoCompleteTextView editkotaowner, editkecamatanowner, editkelurahanowner;
    EditText editRTowner, editRWowner, editKodePosowner;
    static EditText editteleponowner;

    ArrayList<String> KotaOwner = new ArrayList<>();
    ArrayList<String> KecamatanOwner = new ArrayList<>();
    ArrayList<String> KelurahanOwner = new ArrayList<>();

    SharedPreferences sharedPreferences;

    Button batal, lanjutkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_pelanggan_baru_pemilik);
        namaowner = findViewById(R.id.namaowner);
        alamatowner = findViewById(R.id.alamatowner);

        editkotaowner = findViewById(R.id.editkotaowner);
        editkecamatanowner = findViewById(R.id.editkecamatanowner);
        editkelurahanowner = findViewById(R.id.editkelurahanowner);

        editRTowner = findViewById(R.id.editRTowner);
        editRWowner = findViewById(R.id.editRWowner);
        editKodePosowner = findViewById(R.id.editKodePosowner);

        editteleponowner = findViewById(R.id.editteleponowner);

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
                if(alamatowner.getText().toString().length() == 0){
                    alamatowner.setError("Isi Alamat Owner");
                } else if (editkotaowner.getText().toString().length() == 0){
                    editkotaowner.setError("Pilih Kota Owner");
                } else if (editkecamatanowner.getText().toString().length() == 0){
                    editkecamatanowner.setError("Pilih Kecamatan Owner");
                } else if (editkelurahanowner.getText().toString().length() ==0){
                    editkelurahanowner.setError("Isi Kelurahan Owner");
                } else if (editRTowner.getText().toString().length() == 0){
                    editRTowner.setError("Isi RT Owner");
                } else if (editRWowner.getText().toString().length() == 0){
                    editRWowner.setError("Isi RW Owner");
                } else if (editKodePosowner.getText().toString().length() == 0){
                    editKodePosowner.setError("Kode Pos Kosong");
                } else if (editteleponowner.getText().toString().length() == 0){
                    editteleponowner.setError("Isi Nomor Telepon Owner");
                } else {
                    Intent intent = new Intent(getApplicationContext(), pelanggan_baru_jadwal_kunjungan.class);
                    startActivity(intent);
                }
            }
        });

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String[] parts = nik_baru.split("-");
        String restnomor = parts[0];

        StringRequest kota = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_kota?kode_dms=" + restnomor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String city = jsonObject1.getString("szCity");
                                    KotaOwner.add(city);

                                }
                            }
                            editkotaowner.setAdapter(new ArrayAdapter<String>(pelanggan_baru_pemilik.this, android.R.layout.simple_expandable_list_item_1, KotaOwner));
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
        kota.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestkota = Volley.newRequestQueue(this);
        requestkota.getCache().clear();
        requestkota.add(kota);

        editkotaowner.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                editkecamatanowner.setText("");
                editkelurahanowner.setText("");
                editKodePosowner.setText("");
                KecamatanOwner.clear();
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("nik_baru", null);
                String rest = nik_baru;
                String[] parts = rest.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");
                StringRequest kecamatan = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_kecamatan?kota=" + editkotaowner.getText().toString() +"&kode_dms=" + restnomorbaru,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("true")) {
                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            String city = jsonObject1.getString("szDistrict");
                                            KecamatanOwner.add(city);

                                        }
                                    }
                                    editkecamatanowner.setAdapter(new ArrayAdapter<String>(pelanggan_baru_pemilik.this, android.R.layout.simple_expandable_list_item_1, KecamatanOwner));
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
                kecamatan.setRetryPolicy(new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestkota = Volley.newRequestQueue(pelanggan_baru_pemilik.this);
                requestkota.getCache().clear();
                requestkota.add(kecamatan);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        editkecamatanowner.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                editkelurahanowner.setText("");
                editKodePosowner.setText("");
                KelurahanOwner.clear();
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("nik_baru", null);
                String rest = nik_baru;
                String[] parts = rest.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");
                StringRequest kecamatan = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_kelurahan?kelurahan=" + editkecamatanowner.getText().toString() +"&kode_dms=" + restnomorbaru,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("true")) {
                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            String city = jsonObject1.getString("szSubDistrict");
                                            KelurahanOwner.add(city);

                                        }
                                    }
                                    editkelurahanowner.setAdapter(new ArrayAdapter<String>(pelanggan_baru_pemilik.this, android.R.layout.simple_expandable_list_item_1, KelurahanOwner));
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
                kecamatan.setRetryPolicy(new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestkota = Volley.newRequestQueue(pelanggan_baru_pemilik.this);
                requestkota.getCache().clear();
                requestkota.add(kecamatan);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        editkelurahanowner.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                editKodePosowner.setText("");
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("nik_baru", null);
                String rest = nik_baru;
                String[] parts = rest.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");
                System.out.println("Hasil Nomor = " + restnomorbaru);
                StringRequest kecamatan = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_kodepos?kelurahan="+ editkecamatanowner.getText().toString()+"&kecamatan=" +editkelurahanowner.getText().toString() +"&kode_dms=" + restnomorbaru,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("true")) {
                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            String nomor = jsonObject1.getString("szZipCode");
                                            editKodePosowner.setText(nomor);
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
                kecamatan.setRetryPolicy(new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestkota = Volley.newRequestQueue(pelanggan_baru_pemilik.this);
                requestkota.getCache().clear();
                requestkota.add(kecamatan);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


    }
}