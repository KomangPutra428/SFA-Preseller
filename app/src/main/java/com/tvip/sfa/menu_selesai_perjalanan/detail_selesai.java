package com.tvip.sfa.menu_selesai_perjalanan;

import static com.tvip.sfa.menu_utama.MainActivity.conditional;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.tvip.sfa.menu_mulai_perjalanan.Utility;
import com.tvip.sfa.menu_utama.MainActivity;
import com.tvip.sfa.pojo.sales_performance_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class detail_selesai extends AppCompatActivity {
    ListView performance;
    List<sales_performance_pojo>sales_performance_pojos = new ArrayList<>();
    SharedPreferences sharedPreferences;
    ListViewAdapterSalesPerformance adapter;
    TextView qty;
    Button akhir;
    SweetAlertDialog pDialog;

    TextView target, achivment, tanggal, totalperformance;
    TextView extracall, noo;
    TextView cpr, cprtotal;
    TextView ecr, ecrtotal;

    TextView cprpercent, ecrpercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_detail_selesai);
        performance = findViewById(R.id.performance);
        qty = findViewById(R.id.qty);
        akhir = findViewById(R.id.akhir);

        cprpercent = findViewById(R.id.cprpercent);
        ecrpercent = findViewById(R.id.ecrpercent);

        target = findViewById(R.id.target);
        achivment = findViewById(R.id.achivment);
        tanggal = findViewById(R.id.tanggal);
        totalperformance = findViewById(R.id.totalperformance);

        extracall = findViewById(R.id.extracall);
        noo = findViewById(R.id.noo);

        cpr = findViewById(R.id.cpr);
        cprtotal = findViewById(R.id.cprtotal);

        ecr = findViewById(R.id.ecr);
        ecrtotal = findViewById(R.id.ecrtotal);

        if(conditional != null){
            akhir.setVisibility(View.GONE);
        }

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        getSalesPerformance();



        akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Harap Menunggu");
                pDialog.setCancelable(false);
                pDialog.show();

                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru=" + nik_baru,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    final JSONArray movieArray = obj.getJSONArray("data");
                                    for (int i = 0; i < movieArray.length(); i++) {
                                        final JSONObject movieObject = movieArray.getJSONObject(i);
                                        String szDocId = movieObject.getString("szDocId");
                                        pDialog.dismissWithAnimation();
                                        if(movieObject.getString("decKMFinish").equals("0")){
                                            final Dialog dialog = new Dialog(detail_selesai.this);
                                            dialog.setContentView(R.layout.topup_km_akhir);
                                            dialog.setCancelable(false);

                                            final EditText editkm = dialog.findViewById(R.id.editkmakhir);
                                            final TextView keterangan = dialog.findViewById(R.id.keterangan);
                                            Button tidak = dialog.findViewById(R.id.tidak);
                                            Button ya = dialog.findViewById(R.id.ya);
                                            final EditText editkmawal = dialog.findViewById(R.id.editkmawal);


                                            editkmawal.setText(movieObject.getString("decKMStart"));
                                            String awal = movieObject.getString("decKMStart");
                                            keterangan.setText("Isi KM Akhir");

                                            ya.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(MainActivity.Tunda.equals("Ada")){
                                                        new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.WARNING_TYPE)
                                                                .setTitleText("Ada pelanggan yang belum diselesaikan, silahkan cek terlebih dahulu")
                                                                .setConfirmText("OK")
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                        sDialog.dismissWithAnimation();
                                                                    }
                                                                })
                                                                .show();
                                                    } else if(editkm.getText().toString().length()==0){
                                                        new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.WARNING_TYPE)
                                                                .setTitleText("Harap isi KM")
                                                                .setConfirmText("OK")
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                        sDialog.dismissWithAnimation();
                                                                    }
                                                                })
                                                                .show();
                                                    } else if(totalperformance.getText().toString().equals("0.0 %")){
                                                        new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.WARNING_TYPE)
                                                                .setTitleText("Harap Selesaikan Kunjungan Terlebih Dahulu")
                                                                .setConfirmText("OK")
                                                                .show();
                                                    } else if(Integer.parseInt(editkm.getText().toString()) <= Integer.parseInt(awal)){
                                                        new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.WARNING_TYPE)
                                                                .setTitleText("KM Akhir Harus Lebih Besar Daripada KM Awal")
                                                                .setConfirmText("OK")
                                                                .show();
                                                    } else {
                                                        final SweetAlertDialog pDialog2 = new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.PROGRESS_TYPE);
                                                        pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                        pDialog2.setTitleText("Harap Menunggu");
                                                        pDialog2.setCancelable(false);
                                                        pDialog2.show();

                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_KMFinish",
                                                                new Response.Listener<String>() {

                                                                    @Override
                                                                    public void onResponse(String response) {

                                                                        if(cpr.getText().toString().equals(cprtotal.getText().toString())){
                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Selesai_Perjalanan/index_FinishDoccall",
                                                                                    new Response.Listener<String>() {

                                                                                        @Override
                                                                                        public void onResponse(String response) {
                                                                                            pDialog.dismissWithAnimation();
                                                                                            putSFADoccall(editkm.getText().toString());
                                                                                            new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                                    .setContentText("Data Sudah Disimpan")
                                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                                            sDialog.dismissWithAnimation();
                                                                                                            getSurat(szDocId);
                                                                                                            finish();
                                                                                                        }
                                                                                                    })
                                                                                                    .show();

                                                                                        }
                                                                                    },
                                                                                    new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            pDialog.dismissWithAnimation();
                                                                                            new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                                    .setContentText("Data Sudah Disimpan")
                                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                                            sDialog.dismissWithAnimation();
                                                                                                            getSurat(szDocId);
                                                                                                            finish();
                                                                                                        }
                                                                                                    })
                                                                                                    .show();
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
                                                                                    params.put("dtmFinish", currentDateandTime2);
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
                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(detail_selesai.this);
                                                                            requestQueue2.getCache().clear();
                                                                            requestQueue2.add(stringRequest2);

                                                                        } else {
                                                                            pDialog2.dismissWithAnimation();
                                                                            dialog.cancel();
                                                                            new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                    .setContentText("Data Sudah Disimpan")
                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                        @Override
                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                            sDialog.dismissWithAnimation();
                                                                                            Intent intent = new Intent(getApplicationContext(), com.tvip.sfa.menu_selesai_perjalanan.selesai_perjalanan.class);
                                                                                            intent.putExtra("KM", editkm.getText().toString());
                                                                                            startActivity(intent);
                                                                                        }
                                                                                    })
                                                                                    .show();
                                                                            pDialog2.cancel();
                                                                        }


                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                if(cpr.getText().toString().equals(cprtotal.getText().toString())){
                                                                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Selesai_Perjalanan/index_FinishDoccall",
                                                                            new Response.Listener<String>() {

                                                                                @Override
                                                                                public void onResponse(String response) {
                                                                                    pDialog.dismissWithAnimation();
                                                                                    putSFADoccall(editkm.getText().toString());
                                                                                    new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                            .setContentText("Data Sudah Disimpan")
                                                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                @Override
                                                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                                                    sDialog.dismissWithAnimation();
                                                                                                    getSurat(szDocId);
                                                                                                    finish();
                                                                                                }
                                                                                            })
                                                                                            .show();

                                                                                }
                                                                            },
                                                                            new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    pDialog.dismissWithAnimation();
                                                                                    new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                            .setContentText("Data Sudah Disimpan")
                                                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                @Override
                                                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                                                    sDialog.dismissWithAnimation();
                                                                                                    getSurat(szDocId);
                                                                                                    finish();
                                                                                                }
                                                                                            })
                                                                                            .show();
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
                                                                            params.put("dtmFinish", currentDateandTime2);
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
                                                                    RequestQueue requestQueue2 = Volley.newRequestQueue(detail_selesai.this);
                                                                    requestQueue2.getCache().clear();

                                                                    requestQueue2.add(stringRequest2);
                                                                } else {
                                                                    pDialog2.dismissWithAnimation();
                                                                    dialog.cancel();
                                                                    new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                            .setContentText("Data Sudah Disimpan")
                                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                @Override
                                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                                    sDialog.dismissWithAnimation();
                                                                                    Intent intent = new Intent(getApplicationContext(), com.tvip.sfa.menu_selesai_perjalanan.selesai_perjalanan.class);
                                                                                    intent.putExtra("KM", editkm.getText().toString());
                                                                                    startActivity(intent);
                                                                                }
                                                                            })
                                                                            .show();
                                                                    pDialog2.cancel();
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
                                                                params.put("szDocId", szDocId);
                                                                params.put("decKMFinish", editkm.getText().toString());


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
                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(detail_selesai.this);
                                                        requestQueue2.getCache().clear();

                                                        requestQueue2.add(stringRequest2);

                                                    }

                                                }
                                            });

                                            tidak.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            dialog.show();

                                        } else {
                                            pDialog.dismissWithAnimation();
                                            Intent intent = new Intent(getApplicationContext(), com.tvip.sfa.menu_selesai_perjalanan.selesai_perjalanan.class);
                                            intent.putExtra("KM", movieObject.getString("decKMFinish"));
                                            startActivity(intent);
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
                                new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Surat Tugas Belum Ada")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
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
                RequestQueue requestQueue = Volley.newRequestQueue(detail_selesai.this);
                requestQueue.getCache().clear();

                requestQueue.add(stringRequest);
            }
        });

    }


    private void getSurat(String szDocId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_AOPerformance?surat_tugas=" + szDocId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            int number1 = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                                String currentDateandTime2 = sdf2.format(new Date());


                                if(movieObject.getString("bSuccess").equals("1")){
                                    updateDraft(movieObject.getString("szDocSO"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void putSFADoccall(String s) {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                putSFADoccalls(movieObject.getString("szDocId"));


                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }

                    private void putSFADoccalls(String szDocId) {
                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Selesai_Perjalanan/index_SFADoccall",
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


                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateandTime2 = sdf2.format(new Date());

                                params.put("szDocId", szDocId);
                                params.put("dtmFinish", currentDateandTime2);
                                if(cpr.getText().toString().equals(cprtotal.getText().toString())){
                                    params.put("bFinished", "1");
                                } else {
                                    params.put("bFinished", "0");
                                }

                                params.put("decKMFinish", s);
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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(detail_selesai.this);
                        requestQueue2.getCache().clear();
                        requestQueue2.add(stringRequest2);
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

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(detail_selesai.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private void getCallPerformance(String szDocId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_AOPerformance?surat_tugas=" + szDocId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        try {
                            int extra = 0;
                            int noos = 0;
                            int bVisited = 0;
                            int bSuccess = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                if(movieObject.getString("bOutOfRoute").equals("1") &&  movieObject.getString("bSuccess").equals("1")){
                                    extra++;
                                    extracall.setText(String.valueOf(extra));
                                }

                                if(movieObject.getString("bNewCustomer").equals("1")){
                                    noos++;
                                    noo.setText(String.valueOf(noos));
                                }

                                if(movieObject.getString("bVisited").equals("1") && movieObject.getString("bOutOfRoute").equals("0")){
                                    bVisited++;
                                    cpr.setText(String.valueOf(bVisited));
                                }

                                if(movieObject.getString("bSuccess").equals("1") && movieObject.getString("bOutOfRoute").equals("0")){
                                    bSuccess++;
                                    ecr.setText(String.valueOf(bSuccess));
                                }

                                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                                float percent = (Integer.parseInt(cpr.getText().toString()) / (Integer.parseInt(cprtotal.getText().toString()) * 1.0f)) *100;
                                cprpercent.setText(String.format("%.2f",percent) + " %");

                                float percent2 = (Integer.parseInt(ecr.getText().toString()) / (Integer.parseInt(ecrtotal.getText().toString()) * 1.0f)) *100;
                                ecrpercent.setText(String.format("%.2f",percent2) + " %");



                            }

                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void getDataDoccall() {

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_SuratTugas?nik_baru="+ nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                getAOPerformance(movieObject.getString("szDocId"));


                            }


                        } catch(JSONException e){
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

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(detail_selesai.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void getAOPerformance(String szDocId) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_AOPerformance?surat_tugas=" + szDocId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getCallPerformance(szDocId);

                        try {
                            int number = 0;
                            int number1 = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                                String currentDateandTime2 = sdf2.format(new Date());

                                if(movieObject.getString("bOutOfRoute").equals("0")){
                                    number ++;
                                    target.setText(String.valueOf(number));
                                    cprtotal.setText(String.valueOf(number));
                                    ecrtotal.setText(String.valueOf(number));
                                }
//                                if(movieObject.getString("bSuccess").equals("1")){
//                                    updateDraft(movieObject.getString("szDocSO"));
//                                }

                                if(movieObject.getString("bVisited").equals("1") && movieObject.getString("bOutOfRoute").equals("0") && movieObject.getString("bSuccess").equals("1")){
                                    number1 ++;
                                    achivment.setText(String.valueOf(number1));
                                }

                                tanggal.setText(currentDateandTime2);

                                int a = Integer.parseInt(achivment.getText().toString());
                                int b = Integer.parseInt(target.getText().toString());

                                float percent = (a / (b * 1.0f)) *100;

                                totalperformance.setText(String.format("%.2f",percent) + " %");
                                ecrpercent.setText(String.format("%.2f",percent) + " %");



                            }

                        } catch(JSONException e){
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

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void updateDraftMDBA(String szDocSO) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToAppliedMDBA",
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

                params.put("szDocId", szDocSO);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(detail_selesai.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void updateDraft(String szDocSO) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DraftToApplied",
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

                params.put("szDocId", szDocSO);

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(detail_selesai.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void getSalesPerformance() {
        pDialog = new SweetAlertDialog(detail_selesai.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SalesPerformance?szEmployeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getDataDoccall();

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final sales_performance_pojo movieItem = new sales_performance_pojo(
                                        movieObject.getString("decQty"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("decAmount"));

                                sales_performance_pojos.add(movieItem);

                                adapter = new ListViewAdapterSalesPerformance(sales_performance_pojos, getApplicationContext());
                                performance.setAdapter(adapter);
                                Utility.setListViewHeightBasedOnChildren(performance);



                            }
                            int total = 0;
                            int totalvalue;
                            for(int i = 0; i < sales_performance_pojos.size();i++){
                                String szId = adapter.getItem(i).getDecAmount();
                                String[] parts = szId.split("\\.");
                                String szIdSlice = parts[0];

                                totalvalue = Integer.parseInt(szIdSlice);
                                total+=totalvalue;

                                DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                                DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
                                formatRp.setCurrencySymbol("Rp. ");
                                formatRp.setMonetaryDecimalSeparator(',');
                                formatRp.setGroupingSeparator('.');
                                kursIndonesia.setDecimalFormatSymbols(formatRp);

                                qty.setText(kursIndonesia.format(total));

                            }

                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    public static class ListViewAdapterSalesPerformance extends ArrayAdapter<sales_performance_pojo> {
        private final List<sales_performance_pojo> sales_performance_pojos;

        private final Context context;

        public ListViewAdapterSalesPerformance(List<sales_performance_pojo> sales_performance_pojos, Context context) {
            super(context, R.layout.list_penjualan_sub_2, sales_performance_pojos);
            this.sales_performance_pojos = sales_performance_pojos;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_penjualan_sub_2, null, true);

            TextView produk = listViewItem.findViewById(R.id.produk);
            TextView qty = listViewItem.findViewById(R.id.qty);
            TextView subharga = listViewItem.findViewById(R.id.subharga);

            sales_performance_pojo data = sales_performance_pojos.get(position);

            produk.setText(data.getSzName());

            String szId = data.getDecAmount();
            String[] parts = szId.split("\\.");
            String szIdSlice = parts[0];

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            produk.setText(data.getSzName());
            qty.setText(data.getDecQty() + " QTY");
            subharga.setText(kursIndonesia.format(Integer.parseInt(szIdSlice)));

            return listViewItem;
        }
    }
}