package com.tvip.sfa.menu_biodata;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.sfa.Perangkat.HttpsTrustManager;
import com.tvip.sfa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class biodata extends AppCompatActivity {
    EditText nama, nik, jabatan, department, lokasi, tanggalmasuk, status, totalpunishment;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_biodata);

        nama = findViewById(R.id.editnama);
        nik = findViewById(R.id.editnik);
        jabatan = findViewById(R.id.editjabatan);
        department = findViewById(R.id.editdepartment);
        lokasi = findViewById(R.id.editlokasi);
        tanggalmasuk = findViewById(R.id.edittanggalmasuk);
        status = findViewById(R.id.editstatus);
        totalpunishment = findViewById(R.id.edittotalpunishment);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("nik_baru", null);

        StringRequest biodata = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server/master/karyawan/index?nik_baru=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);
                                nama.setText(biodatas.getString("nama_karyawan_struktur"));
                                nik.setText(biodatas.getString("nik_baru"));
                                jabatan.setText(biodatas.getString("jabatan_karyawan"));
                                department.setText(biodatas.getString("dept_struktur"));
                                lokasi.setText(biodatas.getString("lokasi_struktur"));
                                tanggalmasuk.setText(convertFormat(biodatas.getString("join_date_struktur")));
                                status.setText(biodatas.getString("status_karyawan_struktur"));




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

        biodata.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue biodataQueue = Volley.newRequestQueue(this);
        biodataQueue.getCache().clear();
        biodataQueue.add(biodata);

        StringRequest total = new StringRequest(Request.Method.GET, "http://hrd.tvip.co.id/rest_server/pengajuan/punishment/index?nik_baru="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray movieArray = obj.getJSONArray("data");
                            int number = 0;

                            for (int i = 0; i < movieArray.length(); i++) {

                                JSONObject totalhukuman = movieArray.getJSONObject(i);

                                totalhukuman.getString("rekomondasi_historical_violance");

                                if (totalhukuman.getString("rekomondasi_historical_violance").contains("Indisipliner"))
                                    number++;  {
                                    totalpunishment.setText(String.valueOf(number));
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
                        totalpunishment.setText("0");
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
        total.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue totalQueue = Volley.newRequestQueue(this);
        totalQueue.getCache().clear();
        totalQueue.add(total);
    }

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return convetDateFormat.format(date);
    }
}