package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class pelanggan_baru_alamat_outlet extends AppCompatActivity {
    static EditText alamatoutlet;
    static AutoCompleteTextView editkotaoutlet;
    static AutoCompleteTextView editkecamatanoutlet;
    static AutoCompleteTextView editkelurahanoutlet;
    EditText editRToutlet;
    EditText editRWoutlet;
    static EditText editKodePosoutlet;
    static EditText editteleponoutlet;

    ArrayList<String> KotaOutlet = new ArrayList<>();
    ArrayList<String> KecamatanOutlet = new ArrayList<>();
    ArrayList<String> KelurahanOutlet = new ArrayList<>();

    SharedPreferences sharedPreferences;

    Button batal, lanjutkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_pelanggan_baru_alamat_outlet);
        alamatoutlet = findViewById(R.id.alamatoutlet);

        editkotaoutlet = findViewById(R.id.editkotaoutlet);
        editkecamatanoutlet = findViewById(R.id.editkecamatanoutlet);
        editkelurahanoutlet = findViewById(R.id.editkelurahanoutlet);

        editRToutlet = findViewById(R.id.editRToutlet);
        editRWoutlet = findViewById(R.id.editRWoutlet);
        editKodePosoutlet = findViewById(R.id.editKodePosoutlet);

        editteleponoutlet = findViewById(R.id.editteleponoutlet);

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
                if(alamatoutlet.getText().toString().length() == 0){
                    alamatoutlet.setError("Isi Alamat Outlet");
                } else if (editkotaoutlet.getText().toString().length() == 0){
                    editkotaoutlet.setError("Pilih Kota Outlet");
                } else if (editkecamatanoutlet.getText().toString().length() == 0){
                    editkecamatanoutlet.setError("Pilih Kecamatan Outlet");
                } else if (editkelurahanoutlet.getText().toString().length() ==0){
                    editkelurahanoutlet.setError("Isi Kelurahan Outlet");
                } else if (editRToutlet.getText().toString().length() == 0){
                    editRToutlet.setError("Isi RT Outlet");
                } else if (editRWoutlet.getText().toString().length() == 0){
                    editRWoutlet.setError("Isi RW Outlet");
                } else if (editKodePosoutlet.getText().toString().length() == 0){
                    editKodePosoutlet.setError("Kode Pos Kosong");
                } else if (editteleponoutlet.getText().toString().length() == 0){
                    editteleponoutlet.setError("Isi Nomor Telepon Outlet");
                } else {
                    Intent intent = new Intent(getApplicationContext(), pelanggan_baru_pemilik.class);
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
                                    KotaOutlet.add(city);

                                }
                            }
                            editkotaoutlet.setAdapter(new ArrayAdapter<String>(pelanggan_baru_alamat_outlet.this, android.R.layout.simple_expandable_list_item_1, KotaOutlet));
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

        editkotaoutlet.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                editkecamatanoutlet.setText("");
                editkelurahanoutlet.setText("");
                editKodePosoutlet.setText("");
                KecamatanOutlet.clear();
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("nik_baru", null);
                String rest = nik_baru;
                String[] parts = rest.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");
                StringRequest kecamatan = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_kecamatan?kota=" + editkotaoutlet.getText().toString() +"&kode_dms=" + restnomorbaru,
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
                                            KecamatanOutlet.add(city);

                                        }
                                    }
                                    editkecamatanoutlet.setAdapter(new ArrayAdapter<String>(pelanggan_baru_alamat_outlet.this, android.R.layout.simple_expandable_list_item_1, KecamatanOutlet));
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
                RequestQueue requestkota = Volley.newRequestQueue(pelanggan_baru_alamat_outlet.this);
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

        editkecamatanoutlet.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                editkelurahanoutlet.setText("");
                editKodePosoutlet.setText("");
                KelurahanOutlet.clear();
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("nik_baru", null);
                String rest = nik_baru;
                String[] parts = rest.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");
                StringRequest kecamatan = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_kelurahan?kelurahan=" + editkecamatanoutlet.getText().toString() +"&kode_dms=" + restnomorbaru,
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
                                            KelurahanOutlet.add(city);

                                        }
                                    }
                                    editkelurahanoutlet.setAdapter(new ArrayAdapter<String>(pelanggan_baru_alamat_outlet.this, android.R.layout.simple_expandable_list_item_1, KelurahanOutlet));
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
                RequestQueue requestkota = Volley.newRequestQueue(pelanggan_baru_alamat_outlet.this);
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

        editkelurahanoutlet.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                editKodePosoutlet.setText("");
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("nik_baru", null);
                String rest = nik_baru;
                String[] parts = rest.split("-");
                String restnomor = parts[0];
                String restnomorbaru = restnomor.replace(" ", "");
                System.out.println("Hasil Nomor = " + restnomorbaru);
                StringRequest kecamatan = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_kodepos?kelurahan="+ editkecamatanoutlet.getText().toString()+"&kecamatan=" +editkelurahanoutlet.getText().toString() +"&kode_dms=" + restnomorbaru,
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
                                            editKodePosoutlet.setText(nomor);
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
                RequestQueue requestkota = Volley.newRequestQueue(pelanggan_baru_alamat_outlet.this);
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