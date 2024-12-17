package com.tvip.sfa.menu_persiapan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;
import com.tvip.sfa.R;

import java.util.ArrayList;

public class persiapan extends AppCompatActivity {
    ViewPager pager;
    ArrayList<String> Istirahat = new ArrayList<>();

    MaterialCardView daftarkunjungan, outletkritis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_persiapan);
        pager = findViewById(R.id.pager);
        daftarkunjungan = findViewById(R.id.daftarkunjungan);
        outletkritis = findViewById(R.id.outletkritis);

        daftarkunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent daftar_kunjungan = new Intent(getBaseContext(), callplan.class);
                startActivity(daftar_kunjungan);
            }
        });

        outletkritis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent outletkritis_intent = new Intent(getBaseContext(), outlet_kritis.class);
                startActivity(outletkritis_intent);
            }
        });

//        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Istirahat/index_JenisIstirahat",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if (jsonObject.getString("status").equals("true")) {
//                                JSONArray jsonArray = jsonObject.getJSONArray("data");
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                    String id = jsonObject1.getString("szId");
//                                    String jenis_istirahat = jsonObject1.getString("szName");
//                                    Istirahat.add(id + "-" + jenis_istirahat);
//
//                                }
//                            }
//                            pager.setAdapter(new ArrayAdapter<String>(persiapan.this, android.R.layout.simple_expandable_list_item_1, Istirahat));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s", "admin", "Databa53");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//        rest.setRetryPolicy(new DefaultRetryPolicy(
//                5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestkota = Volley.newRequestQueue(MainActivity.this);
//        requestkota.add(rest);


    }
}