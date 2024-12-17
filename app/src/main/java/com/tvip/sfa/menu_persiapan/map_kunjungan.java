package com.tvip.sfa.menu_persiapan;

import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tvip.sfa.Perangkat.HttpsTrustManager;
import com.tvip.sfa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class map_kunjungan extends FragmentActivity implements OnMapReadyCallback, OnMapsSdkInitializedCallback {

    private GoogleMap mMap;
    BottomSheetBehavior sheetBehavior;
    View bottom_sheet;
    BottomSheetDialog sheetDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_map_kunjungan);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Double langitude = Double.valueOf(getIntent().getStringExtra("langitude"));
        Double longitude = Double.valueOf(getIntent().getStringExtra("longitude"));

        // Add a marker in Sydney and move the camera
        LatLng zoom = new LatLng(langitude, longitude);

        mMap.addMarker(new MarkerOptions().position(zoom).title(getIntent().getStringExtra("namatoko")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(zoom));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                showBottomSheetDialog();
            }
        });

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoom, 20));
    }

    private void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.keterangan_pelanggan, null);

        TextView namatoko = view.findViewById(R.id.namatoko);
        TextView kode = view.findViewById(R.id.kode);
        TextView alamat = view.findViewById(R.id.alamat);
        TextView term_payment = view.findViewById(R.id.term_payment);
        TextView limit_kredit = view.findViewById(R.id.limit_kredit);
        TextView piutang = view.findViewById(R.id.piutang);
        TextView sisa_limit_kredit = view.findViewById(R.id.sisa_limit_kredit);
        TextView channel = view.findViewById(R.id.channel);
        TextView status = view.findViewById(R.id.status);
        Button direction = view.findViewById(R.id.direction);

        namatoko.setText(getIntent().getStringExtra("namatoko"));
        kode.setText(getIntent().getStringExtra("szId"));
        alamat.setText(getIntent().getStringExtra("address"));

        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Detail_Pelanggan?szId=" + getIntent().getStringExtra("szId"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                term_payment.setText(biodatas.getString("szPaymetTermId"));
                                limit_kredit.setText(biodatas.getString("decCreditLimit"));

                                channel.setText(biodatas.getString("Nama_Channel"));
                                status.setText(biodatas.getString("szStatus"));



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
        channel_statusQueue.getCache().clear();
        channel_statusQueue.add(channel_status);


        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double langitude = Double.valueOf(getIntent().getStringExtra("langitude"));
                Double longitude = Double.valueOf(getIntent().getStringExtra("longitude"));

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://maps.google.com?q="+langitude+","+longitude));
                startActivity(i);
            }
        });

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        sheetDialog.show();
        sheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sheetDialog = null;
            }
        });
    }
    private String formatRupiah(Double number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format(number);
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.d("MapsDemo", "The latest version of the renderer is used.");
                break;
            case LEGACY:
                Log.d("MapsDemo", "The legacy version of the renderer is used.");
                break;
        }
    }
}