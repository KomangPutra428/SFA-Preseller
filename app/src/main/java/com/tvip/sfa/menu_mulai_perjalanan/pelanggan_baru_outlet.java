package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.tvip.sfa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class pelanggan_baru_outlet extends AppCompatActivity implements LocationListener {
    static EditText namaoutlet;
    static AutoCompleteTextView segment;
    ArrayList<String> idjenissegment = new ArrayList<>();
    ArrayList<String> jenissegment = new ArrayList<>();
    Button batal, lanjutkan;

    static String langitude;
    static String longitude;
    static String propinsi;

    LocationManager locationManager;

    static String fullsegment;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_pelanggan_baru_outlet);
        namaoutlet = findViewById(R.id.namaoutlet);
        segment = findViewById(R.id.segment);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

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
                if(namaoutlet.getText().toString().length() == 0){
                    namaoutlet.setError("Isi Nama Outlet");
                } else if (segment.getText().toString().length() == 0){
                    segment.setError("Pilih Segment");
                } else if (longitude == null){
                    new SweetAlertDialog(pelanggan_baru_outlet.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Longlat tidak ditemukan")
                            .setConfirmText("OK")
                            .show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), pelanggan_baru_alamat_outlet.class);
                    startActivity(intent);
                }
            }
        });

        getLocation();

        segment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fullsegment = jenissegment.get(position);
            }
        });



        StringRequest product_competitor = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Segment",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String id = jsonObject1.getString("id_sub_segment");
                                    String jenis_istirahat = jsonObject1.getString("sub_segment");

                                    String id_channel = jsonObject1.getString("id_channel");
                                    String channel = jsonObject1.getString("channel");

                                    String id_segment = jsonObject1.getString("id_segment");
                                    String segment = jsonObject1.getString("segment");

                                    String id_sub_segment = jsonObject1.getString("id_sub_segment");
                                    String sub_segment = jsonObject1.getString("sub_segment");

                                    idjenissegment.add(id + " - " + jenis_istirahat);

                                    jenissegment.add(id_channel + " - " + channel + " " + id_segment + " - " + segment + " " + id_sub_segment + " - " + sub_segment);

                                }
                            }
                            segment.setAdapter(new ArrayAdapter<String>(pelanggan_baru_outlet.this, android.R.layout.simple_expandable_list_item_1, idjenissegment));
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
        RequestQueue requestproduct_competitor = Volley.newRequestQueue(pelanggan_baru_outlet.this);
        requestproduct_competitor.add(product_competitor);

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,5, pelanggan_baru_outlet.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(pelanggan_baru_outlet.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAdminArea();
            propinsi = address;
            longitude = String.valueOf(location.getLongitude());
            langitude = String.valueOf(location.getLatitude());


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}