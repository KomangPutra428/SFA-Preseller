package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.detailscangagal_dalamrute.intItemNumbers;
import static com.tvip.sfa.menu_mulai_perjalanan.mulai_perjalanan.lastitem;
import static com.tvip.sfa.menu_mulai_perjalanan.mulai_perjalanan.outofroute;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import com.android.volley.VolleyLog;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tvip.sfa.Perangkat.GPSTracker;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tvip.sfa.R;
import com.tvip.sfa.survei.detail_survey;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class scangagal_map extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Handler handler = new Handler();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    TextView namatoko, alamat;
    Button arahkan, lanjutkan;
    String langitude, longitude;
    LocationManager locationManager;
    SweetAlertDialog pDialog;
    private RequestQueue requestQueue;


    GPSTracker gpsTracker;

    SharedPreferences sharedPreferences;

    ContentValues cv;
    Uri imageUri;

    ImageView uploadgambar;
    TextView textupload, longlat, jarak;

    ArrayList<String> GagalCheckin = new ArrayList<>();

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_scangagal_map);
        namatoko = findViewById(R.id.namatoko);
        alamat = findViewById(R.id.alamat);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        arahkan = findViewById(R.id.arahkan);
        lanjutkan = findViewById(R.id.lanjutkan);
        longlat = findViewById(R.id.longlat);
        jarak = findViewById(R.id.jarak);

        getIntent().getStringExtra("langitude");
        namatoko.setText(getIntent().getStringExtra("toko"));
        alamat.setText(getIntent().getStringExtra("alamat"));
        longlat.setText("Longlat Toko : " + getIntent().getStringExtra("langitude") + ", " + getIntent().getStringExtra("longitude"));

        arahkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double langitude = Double.valueOf(getIntent().getStringExtra("langitude"));
                Double longitude = Double.valueOf(getIntent().getStringExtra("longitude"));

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://maps.google.com?q=" + langitude + "," + longitude));
                startActivity(i);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(scangagal_map.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Harap Menunggu");
                pDialog.setCancelable(false);
                pDialog.show();

                getLocation();


//                if(longitude != null || !longitude.equals("0.0")){
//
//
//                } else {
//                    Toast.makeText(getBaseContext(),
//                            "Lokasi Tidak Terdeteksi" ,
//                            Toast.LENGTH_LONG).show();
//                 //   getLocation();
//                }

            }
        });


    }

    private void showDialogCheckin() {
        GagalCheckin.clear();
        final Dialog dialog = new Dialog(scangagal_map.this);
        dialog.setContentView(R.layout.gagal_checkin);
        dialog.setCancelable(false);
        dialog.show();

        TextView toko = dialog.findViewById(R.id.toko);
        TextView kodetoko = dialog.findViewById(R.id.kodetoko);
        TextView alamattoko = dialog.findViewById(R.id.alamattoko);

        LinearLayout fotooutlet = dialog.findViewById(R.id.fotooutlet);

        Button batal = dialog.findViewById(R.id.batal);
        Button lanjutkan = dialog.findViewById(R.id.lanjutkan);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        uploadgambar = dialog.findViewById(R.id.uploadgambar);
        textupload = dialog.findViewById(R.id.textupload);

        AutoCompleteTextView alasangagalcheckin = dialog.findViewById(R.id.alasangagalcheckin);

        alasangagalcheckin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    alasangagalcheckin.showDropDown();
            }
        });

        alasangagalcheckin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                alasangagalcheckin.showDropDown();
                return false;
            }
        });

        toko.setText(detailscangagal_dalamrute.namatoko.getText().toString());
        kodetoko.setText(detailscangagal_dalamrute.code.getText().toString());
        alamattoko.setText(detailscangagal_dalamrute.address.getText().toString());

        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Check_In_Failed",
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
                            alasangagalcheckin.setAdapter(new ArrayAdapter<String>(scangagal_map.this, android.R.layout.simple_expandable_list_item_1, GagalCheckin));
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
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(scangagal_map.this);
            requestQueue.getCache().clear();
            requestQueue.add(rest);
        } else {
            requestQueue.add(rest);
        }

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alasangagalcheckin.setError(null);
                if (alasangagalcheckin.getText().toString().length() == 0) {
                    alasangagalcheckin.setError("Silahkan Pilih Alasan");
                } else if (textupload.getVisibility() == View.VISIBLE) {
                    new SweetAlertDialog(scangagal_map.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Upload Gambar")
                            .setConfirmText("OK")
                            .show();
                } else {
                    Intent dalam_rute = new Intent(getBaseContext(), menu_pelanggan.class);
                    dalam_rute.putExtra("kode", detailscangagal_dalamrute.code.getText().toString());
                    startActivity(dalam_rute);

                    sfa_doccall();

                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_gagal_CheckIn",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    uploadgambarsfa();
                                    uploadkeServer();

                                }

                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    uploadgambarsfa();
                                    uploadkeServer();
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

                            String rest = alasangagalcheckin.getText().toString();
                            String[] parts = rest.split("-");
                            String restnomor = parts[0];
                            String restnomorbaru = restnomor.replace(" ", "");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateandTime2 = sdf2.format(new Date());

                            params.put("szDocId", mulai_perjalanan.id_pelanggan);
                            params.put("szCustomerId", detailscangagal_dalamrute.code.getText().toString());
                            params.put("szReasonIdCheckin", restnomorbaru);
                            params.put("dtmStart", currentDateandTime2);


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
                    RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_map.this);
                    requestQueue2.getCache().clear();
                    requestQueue2.add(stringRequest2);

                }

            }

            private void uploadkeServer() {
                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/mobile_eis_2/upload_sfa.php",
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
                        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                        String nik_baru = sharedPreferences.getString("szDocCall", null);

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                        String currentDateandTime2 = sdf2.format(new Date());

                        String gambar = ImageToString(bitmap);

                        params.put("nik", "FailedCheckIn" + "_" + nik_baru + "_" + kodetoko.getText().toString() + "_" + currentDateandTime2);
                        params.put("nama_folder", "foto_gagal_checkin");
                        params.put("foto", gambar);


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

                RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_map.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);
            }

            private void uploadgambarsfa() {
                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_gambar",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
//                        SF();

                            }

                            private void SF() {
                                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {
//                        DocSOMDBA(s);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse response = error.networkResponse;
                                        if (response != null && response.data != null) {

                                        } else {
                                            String errorMessage = error.getClass().getSimpleName();
                                            if (!TextUtils.isEmpty(errorMessage)) {

                                            }
                                        }
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


                                        String rest2 = detailscangagal_dalamrute.alasangagalscan.getText().toString();
                                        String[] parts2 = rest2.split("-");
                                        String restnomor2 = parts2[0];

                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String currentDateandTime2 = sdf2.format(new Date());

                                        String rest = alasangagalcheckin.getText().toString();
                                        String[] parts = rest.split("-");
                                        String restnomor = parts[0];

                                        params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                        params.put("intItemNumber", lastitem);

                                        params.put("szCustomerId", detailscangagal_dalamrute.code.getText().toString());

                                        params.put("dtmStart", currentDateandTime2);
                                        params.put("dtmFinish", currentDateandTime2);

                                        params.put("bVisited", "1");
                                        params.put("bSuccess", "0");

                                        params.put("szFailReason", "");
                                        params.put("szLangitude", langitude);
                                        params.put("szLongitude", longitude);
                                        params.put("bOutOfRoute", outofroute);
                                        params.put("szRefDocId", "");

                                        params.put("bScanBarcode", "0");

                                        params.put("szReasonIdCheckin", restnomor);
                                        params.put("szReasonFailedScan", restnomor2);
                                        params.put("decRadiusDiff", "0");


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
                                RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_map.this);
                                requestQueue2.getCache().clear();
                                requestQueue2.add(stringRequest2);
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

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                        String currentDateandTime2 = sdf2.format(new Date());

                        String[] parts = nik_baru.split("-");
                        String restnomor = parts[0];
                        String restnomorbaru = restnomor.replace(" ", "");

                        params.put("iId", currentDateandTime2);
                        params.put("szId", mulai_perjalanan.id_pelanggan);

                        params.put("szImageType", "FAILED_CHECKIN");
                        params.put("szImage", ImageToString(bitmap));
                        params.put("szCustomerId", detailscangagal_dalamrute.code.getText().toString());

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
                RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_map.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);
            }

            private void sfa_doccall() {
                String rest = alasangagalcheckin.getText().toString();
                String[] parts = rest.split("-");
                String restnomor = parts[0];

                String rest2 = detailscangagal_dalamrute.alasangagalscan.getText().toString();
                String[] parts2 = rest2.split("-");
                String restnomor2 = parts2[0];

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());


                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getStackTrace());
                        VolleyLog.d("ErrorVolley", "Error: " + error.getStackTrace());
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


                        params.put("szDocId", mulai_perjalanan.id_pelanggan);

                        if (outofroute.equals("0")) {
                            params.put("intItemNumber", intItemNumbers);
                        } else {
                            params.put("intItemNumber", lastitem);
                        }


                        params.put("szCustomerId", kodetoko.getText().toString());

                        params.put("dtmStart", currentDateandTime2);
                        params.put("dtmFinish", currentDateandTime2);

                        params.put("bVisited", "1");
                        params.put("bSuccess", "0");


                        params.put("szFailReason", "");
                        params.put("szLangitude", langitude);
                        params.put("szLongitude", longitude);
                        params.put("bOutOfRoute", outofroute);
                        params.put("szRefDocId", "");

                        params.put("bScanBarcode", "0");

                        params.put("szReasonIdCheckin", restnomor);
                        params.put("szReasonFailedScan", restnomor2);
                        params.put("decRadiusDiff", "0");

                        System.out.println(params);

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
                RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_map.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);
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
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);

                    int width = 720;
                    int height = 720;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        pDialog = new SweetAlertDialog(scangagal_map.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        mMap = googleMap;

        // Get the target location from Intent
        Double langitude = Double.valueOf(getIntent().getStringExtra("langitude"));
        Double longitude = Double.valueOf(getIntent().getStringExtra("longitude"));

        // Target location marker
        LatLng targetLocation = new LatLng(langitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(targetLocation)
                .title(getIntent().getStringExtra("toko"))
        );

        // Adjust the camera to the target location initially
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15));

        // Add user's location marker
        addUserMarker(targetLocation);
    }

    private void getLocation() {
        gpsTracker = new GPSTracker(scangagal_map.this);
        if (gpsTracker.canGetLocation()) {
            pDialog.dismissWithAnimation();

            Location startPoint = new Location("locationA");
            startPoint.setLatitude(Double.valueOf(langitude));
            startPoint.setLongitude(Double.valueOf(longitude));

            Location endPoint = new Location("locationA");
            endPoint.setLatitude(Double.valueOf(getIntent().getStringExtra("langitude")));
            endPoint.setLongitude(Double.valueOf(getIntent().getStringExtra("longitude")));
            double distance2 = startPoint.distanceTo(endPoint);
            int value = (int) distance2;

            if (value > 30) {
                final Dialog dialog = new Dialog(scangagal_map.this);
                dialog.setContentView(R.layout.diluarradius);
                dialog.setCancelable(false);

                Button tidak = dialog.findViewById(R.id.tidak);
                Button ya = dialog.findViewById(R.id.ya);
                tidak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                ya.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_latlong",
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

                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateandTime2 = sdf2.format(new Date());

                                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                params.put("szCustomerId", getIntent().getStringExtra("kode"));
                                params.put("bVisited", "0");
                                params.put("szLangitude", langitude);
                                params.put("szLongitude", longitude);

                                params.put("dtmStart", currentDateandTime2);


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
                        if (requestQueue == null) {
                            requestQueue = Volley.newRequestQueue(scangagal_map.this);
                            requestQueue.getCache().clear();
                            requestQueue.add(stringRequest2);
                        } else {
                            requestQueue.add(stringRequest2);
                        }
                        showDialogCheckin();

                    }
                });
                dialog.show();
            } else {
                StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_latlong",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                sf();

                            }

                            private void sf() {
                                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocCallItem",
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {
//                        DocSOMDBA(s);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse response = error.networkResponse;
                                        if (response != null && response.data != null) {

                                        } else {
                                            String errorMessage = error.getClass().getSimpleName();
                                            if (!TextUtils.isEmpty(errorMessage)) {

                                            }
                                        }
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


                                        String rest2 = detailscangagal_dalamrute.alasangagalscan.getText().toString();
                                        String[] parts2 = rest2.split("-");
                                        String restnomor2 = parts2[0];

                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String currentDateandTime2 = sdf2.format(new Date());

                                        params.put("szDocId", mulai_perjalanan.id_pelanggan);
                                        params.put("intItemNumber", lastitem);

                                        params.put("szCustomerId", detailscangagal_dalamrute.code.getText().toString());

                                        params.put("dtmStart", currentDateandTime2);
                                        params.put("dtmFinish", currentDateandTime2);

                                        params.put("bVisited", "1");
                                        params.put("bSuccess", "0");


                                        params.put("szFailReason", "");
                                        params.put("szLangitude", langitude);
                                        params.put("szLongitude", longitude);
                                        params.put("bOutOfRoute", outofroute);
                                        params.put("szRefDocId", "");

                                        params.put("bScanBarcode", "0");

                                        params.put("szReasonIdCheckin", "");
                                        params.put("szReasonFailedScan", restnomor2);
                                        params.put("decRadiusDiff", "0");


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
                                RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_map.this);
                                requestQueue2.getCache().clear();
                                requestQueue2.add(stringRequest2);
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

                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateandTime2 = sdf2.format(new Date());

                        params.put("szDocId", mulai_perjalanan.id_pelanggan);
                        params.put("szCustomerId", getIntent().getStringExtra("kode"));
                        params.put("bVisited", "0");
                        params.put("szLangitude", langitude);
                        params.put("szLongitude", longitude);

                        params.put("dtmStart", currentDateandTime2);


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
                RequestQueue requestQueue2 = Volley.newRequestQueue(scangagal_map.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);
                Intent dalam_rute = new Intent(getBaseContext(), menu_pelanggan.class);
                dalam_rute.putExtra("kode", getIntent().getStringExtra("kode"));
                startActivity(dalam_rute);
            }
        } else {
            gpsTracker.showSettingsAlert();

        }
    }

    private void addUserMarker(LatLng targetLocation) {
        // Initialize location services
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Enable MyLocation layer on the map
        mMap.setMyLocationEnabled(true);

        // Define the Runnable to fetch location and update UI
        Runnable updateLocation = new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(scangagal_map.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(scangagal_map.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                // Get the user's current location
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null && targetLocation != null) {
                        pDialog.dismissWithAnimation();
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // Clear the map only if necessary
                        mMap.clear();

                        // Add marker for user's location
                        mMap.addMarker(new MarkerOptions()
                                .position(userLocation)
                                .title("You are here")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        // Add a polyline between the user and the target location
                        mMap.addPolyline(new PolylineOptions()
                                .add(userLocation, targetLocation)
                                .color(Color.BLUE)
                                .width(10f));

                        // Calculate the distance between user and target location
                        float[] results = new float[1];
                        Location.distanceBetween(
                                userLocation.latitude, userLocation.longitude,
                                targetLocation.latitude, targetLocation.longitude,
                                results
                        );

                        String distanceText = String.format("Distance: %.2f meters", results[0]);

                        // Update the TextView with distance information
                        if (results[0] > 30) {
                            jarak.setTextColor(Color.RED); // Red if distance > 30 meters
                        } else {
                            jarak.setTextColor(Color.GREEN); // Green if distance <= 30 meters
                        }

                        langitude = String.valueOf(userLocation.latitude);
                        longitude = String.valueOf(userLocation.longitude);
                        jarak.setText(distanceText);

                        // Adjust the camera to show both user and target locations
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(userLocation)
                                .include(targetLocation)
                                .build();

                        if (bounds.southwest.latitude != bounds.northeast.latitude ||
                                bounds.southwest.longitude != bounds.northeast.longitude) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // 100px padding
                        }
                    } else if (targetLocation == null) {
                        Log.e("addUserMarker", "Target location is null");
                    }
                });

                // Repeat location updates every 2 seconds
                handler.postDelayed(this, 2000);
            }
        };

        // Start the periodic location updates
        handler.post(updateLocation);
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null); // Stop updates when activity is paused
    }


    private void drawRoute(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyAJaSEqauB21zOnX5r1DcgZPKcM4Ws2gF0"; // Replace with your Directions API key
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        System.out.println(url);

        // Fetch directions using HTTP request
        new Thread(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = IOUtils.toString(inputStream, "UTF-8");
                JSONObject jsonObject = new JSONObject(response);

                // Parse the route from the JSON response
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String encodedPoints = overviewPolyline.getString("points");

                    // Decode the polyline and draw it on the map
                    List<LatLng> polylinePoints = decodePolyline(encodedPoints);
                    runOnUiThread(() -> mMap.addPolyline(new PolylineOptions()
                            .addAll(polylinePoints)
                            .color(Color.BLUE)
                            .width(10f)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Decode a polyline string into a list of LatLng points
    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            polyline.add(p);
        }
        return polyline;
    }



    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}