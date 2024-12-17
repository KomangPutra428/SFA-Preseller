package com.tvip.sfa.menu_login;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.sfa.menu_utama.MainActivity;
import com.tvip.sfa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class login extends AppCompatActivity {
    EditText nikbaru;
    public static EditText editpassword;
    Button login;
    SweetAlertDialog success;
    SharedPreferences sharedPreferences;
    TextView tekslupalogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_login);

        nikbaru = findViewById(R.id.nikbaru);
        editpassword = findViewById(R.id.editpassword);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        login = findViewById(R.id.login);

        if (ActivityCompat.checkSelfPermission(
                login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                login.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nikbaru.getText().toString().length() == 0 && editpassword.getText().toString().length() == 0) {
                    nikbaru.setError("NIK diperlukan!");
                    editpassword.setError("Password diperlukan!");
                } else if (nikbaru.getText().toString().length() == 0) {
                    nikbaru.setError("NIK diperlukan!");
                } else if (editpassword.getText().toString().length() == 0) {
                    editpassword.setError("Password diperlukan!");
                } else {
                    sendLogin();
                }
            }
        });
    }

    private void sendLogin() {

        final SweetAlertDialog pDialog = new SweetAlertDialog(login.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");

        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server/api/login/index?nik_baru=" + nikbaru.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {

                                    final JSONObject movieObject = movieArray.getJSONObject(i);
                                    pDialog.cancel();

                                    if (movieObject.getString("password").equals(md5(editpassword.getText().toString()))) {
                                        final String lokasi = movieObject.getString("lokasi_struktur");
                                        final String jabatan = movieObject.getString("jabatan_struktur");
                                        final String level = movieObject.getString("level_jabatan_karyawan");
                                        final String nama = movieObject.getString("nama_karyawan_struktur");
                                        final String password = editpassword.getText().toString();

                                        if(movieObject.getString("szBranch").equals("336") || movieObject.getString("szBranch").equals("321") || movieObject.getString("szBranch").equals("324")){
                                            getDoccall(lokasi, jabatan, level, nama, password, "rest_server_sfa_asa_dummy");
                                        } else {
                                            getDoccall(lokasi, jabatan, level, nama, password, "rest_server_sfa_tvip_dummy");
                                        }


                                    } else if (!movieObject.getString("password").equals(md5(editpassword.getText().toString()))) {
                                        new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE)
                                                .setContentText("Oops... NIK / Password Salah")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation();
                                                    }
                                                })
                                                .show();
                                    }

                                }

                            } else {
                                new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Oops... NIK / Password Salah")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                                pDialog.cancel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.cancel();
                        if (error instanceof ServerError) {
                            new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Nik anda salah!")
                                    .show();
                        } else {
                            new SweetAlertDialog(login.this, SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Jaringan sedang bermasalah!")
                                    .show();
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
        };
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(login.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void getDoccall(String lokasi, String jabatan, String level, String nama, String password, String link) {
        StringRequest biodata = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/"+link+"/utilitas/Mulai_Perjalanan/index_nik_Doccall?nik_baru=" + nikbaru.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                success = new SweetAlertDialog(login.this, SweetAlertDialog.SUCCESS_TYPE);
                                success.setContentText("Selamat Datang");
                                success.setCancelable(false);
                                success.setConfirmText("OK");
                                success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        Intent intent = new Intent(login.this, MainActivity.class);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("nik_baru", nikbaru.getText().toString());
                                        try {
                                            editor.putString("szDocCall", movieObject.getString("szId"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        editor.putString("lokasi", lokasi);
                                        editor.putString("jabatan", jabatan);
                                        editor.putString("level", level);
                                        editor.putString("nama", nama);
                                        editor.putString("password", password);
                                        editor.putString("link", link);

                                        editor.apply();
                                        sDialog.dismissWithAnimation();
                                        startActivity(intent);
                                    }
                                })
                                        .show();




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
    }

    public String md5(String s) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash)
            {
                sb.append(String.format("%02x", b&0xff));
            }
            digest = sb.toString();
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digest;
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(login.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakah anda yakin?")
                .setContentText("Anda akan keluar dari aplikasi ini")
                .setConfirmText("Yes")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        finishAffinity();
                        finish();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }
}