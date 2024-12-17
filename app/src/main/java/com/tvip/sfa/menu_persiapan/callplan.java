package com.tvip.sfa.menu_persiapan;


import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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
import com.google.android.material.card.MaterialCardView;
import com.tvip.sfa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class callplan extends AppCompatActivity {
    TextView tanggal_callplan, surattugas;
    MaterialCardView listpelanggan;
    SharedPreferences sharedPreferences;
    static String szDocId, RouteType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_callplan);
        tanggal_callplan = findViewById(R.id.tanggal_callplan);
        surattugas = findViewById(R.id.surattugas);
        listpelanggan = findViewById(R.id.listpelanggan);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        listpelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SweetAlertDialog pDialog = new SweetAlertDialog(callplan.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Harap Menunggu");
                pDialog.show();
                pDialog.setCancelable(false);
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru=" + nik_baru,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    final JSONArray movieArray = obj.getJSONArray("data");
                                    for (int i = 0; i < 1; i++) {
                                        final JSONObject movieObject = movieArray.getJSONObject(i);
                                        if(movieObject.getString("decKMStart").equals("0")){
                                            pDialog.dismissWithAnimation();
                                            final Dialog dialog = new Dialog(callplan.this);
                                            dialog.setContentView(R.layout.topup_km);
                                            dialog.setCancelable(false);

                                            if(!RouteType.equals("TKO")){

                                            } else {
                                                final EditText editkm = dialog.findViewById(R.id.editkm);
                                                Button tidak = dialog.findViewById(R.id.tidak);
                                                Button ya = dialog.findViewById(R.id.ya);

                                                ya.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if(editkm.getText().toString().length()==0){
                                                            new SweetAlertDialog(callplan.this, SweetAlertDialog.WARNING_TYPE)
                                                                    .setTitleText("Harap isi KM")
                                                                    .setConfirmText("OK")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                            sDialog.dismissWithAnimation();
                                                                        }
                                                                    })
                                                                    .show();
                                                        } else {
                                                            final SweetAlertDialog pDialog2 = new SweetAlertDialog(callplan.this, SweetAlertDialog.PROGRESS_TYPE);
                                                            pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                            pDialog2.setTitleText("Harap Menunggu");
                                                            pDialog2.setCancelable(false);
                                                            pDialog2.show();

                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_KMStart",
                                                                    new Response.Listener<String>() {

                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            pDialog2.dismissWithAnimation();
                                                                            new SweetAlertDialog(callplan.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                    .setContentText("Data Sudah Disimpan")
                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                        @Override
                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                            postSFADoccall();
                                                                                            sDialog.dismissWithAnimation();
                                                                                            Intent daftarkunjungan = new Intent(getBaseContext(), daftar_kunjungan.class);
                                                                                            daftarkunjungan.putExtra("szDocId", szDocId);
                                                                                            startActivity(daftarkunjungan);
                                                                                            dialog.cancel();
                                                                                        }

                                                                                        private void postSFADoccall() {
                                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SFADoccall",
                                                                                                    new Response.Listener<String>() {

                                                                                                        @Override
                                                                                                        public void onResponse(String response) {

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


                                                                                                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                                                    String currentDateandTime3 = sdf3.format(new Date());

                                                                                                    SimpleDateFormat dtmDoc = new SimpleDateFormat("yyyy-MM-dd");
                                                                                                    String currentDateandTime4 = dtmDoc.format(new Date());

                                                                                                    String[] parts2 = nik_baru.split("-");
                                                                                                    String restnomor2 = parts2[0];


                                                                                                    params.put("szDocId", szDocId);
                                                                                                    params.put("dtmDoc", currentDateandTime4);
                                                                                                    params.put("dtmStart", currentDateandTime3);
                                                                                                    params.put("dtmFinish", currentDateandTime3);
                                                                                                    params.put("bStarted", "1");
                                                                                                    params.put("bFinisihed", "0");

                                                                                                    params.put("decKMStart", editkm.getText().toString());
                                                                                                    params.put("decKMFinish", "0");

                                                                                                    params.put("szBranchId", restnomor2);


                                                                                                    if(restnomor2.equals("321") || restnomor2.equals("336") || restnomor2.equals("324") || restnomor2.equals("317") || restnomor2.equals("036")){
                                                                                                        params.put("szCompanyId", "ASA");
                                                                                                    } else {
                                                                                                        params.put("szCompanyId", "TVIP");
                                                                                                    }

                                                                                                    params.put("szDocStatus", "Draft");

                                                                                                    params.put("szUserCreatedId", nik_baru);
                                                                                                    params.put("dtmCreated", currentDateandTime3);







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
                                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(callplan.this);
                                                                                            requestQueue2.getCache().clear();
                                                                                            requestQueue2.add(stringRequest2);
                                                                                        }
                                                                                    })
                                                                                    .show();
                                                                            pDialog2.cancel();

                                                                        }
                                                                    }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    pDialog2.dismissWithAnimation();
                                                                    new SweetAlertDialog(callplan.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                            .setContentText("Data Sudah Disimpan")
                                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                @Override
                                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                                    sDialog.dismissWithAnimation();
                                                                                    Intent daftarkunjungan = new Intent(getBaseContext(), daftar_kunjungan.class);
                                                                                    daftarkunjungan.putExtra("szDocId", szDocId);
                                                                                    startActivity(daftarkunjungan);
                                                                                    dialog.cancel();
                                                                                }
                                                                            })
                                                                            .show();
                                                                    pDialog2.cancel();

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


                                                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                    String currentDateandTime2 = sdf2.format(new Date());

                                                                    params.put("szDocId", szDocId);
                                                                    params.put("decKMStart", editkm.getText().toString());

                                                                    params.put("dtmStart", currentDateandTime2);
                                                                    params.put("szUserUpdatedId", nik_baru);
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
                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(callplan.this);
                                                            requestQueue2.getCache().clear();
                                                            requestQueue2.add(stringRequest2);

                                                        }

                                                    }
                                                });

                                                tidak.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if(editkm.getText().toString().length()==0){
                                                            dialog.dismiss();
                                                        }

                                                    }
                                                });

                                                dialog.show();

                                            }


                                        } else {
                                            pDialog.dismissWithAnimation();
                                            Intent daftarkunjungan = new Intent(getBaseContext(), daftar_kunjungan.class);
                                            daftarkunjungan.putExtra("szDocId", szDocId);
                                            startActivity(daftarkunjungan);
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
                                pDialog.dismissWithAnimation();

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

                stringRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        ));
                RequestQueue requestQueue = Volley.newRequestQueue(callplan.this);
                requestQueue.getCache().clear();
                requestQueue.add(stringRequest);


            }
        });


        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        System.out.println("Link CallPlan = " + "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                surattugas.setText(movieObject.getString("szDocId"));
                                szDocId = movieObject.getString("szDocId");
                                RouteType = movieObject.getString("szRouteType");

                                if(!RouteType.equals("TKO")){
                                    surattugas.setText("Tidak Ada Surat Tugas");
                                    listpelanggan.setEnabled(false);
                                } else {
                                    tanggal_callplan.setText("Call Plan : " + movieObject.getString("dtmDoc"));
                                    surattugas.setText(movieObject.getString("szDocId"));
                                    listpelanggan.setEnabled(true);
                                }

                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        surattugas.setText("Tidak Ada Surat Tugas");
                        listpelanggan.setEnabled(false);
                        Toast.makeText(callplan.this, String.valueOf(error), Toast.LENGTH_SHORT).show();

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

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(callplan.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private static String tanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return convetDateFormat.format(date);
    }

}