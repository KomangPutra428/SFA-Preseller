package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class pelanggan_baru_pembayaran extends AppCompatActivity {
    EditText urutan;
    static EditText top;
    static AutoCompleteTextView pembayaran;
    static AutoCompleteTextView listdepo;
    EditText pengiriman;
    String[] jenis = {"Tunai", "Kredit"};

    static String kodedms;

    ArrayList<String> id_depo = new ArrayList<>();
    ArrayList<String> namadepo = new ArrayList<>();

    Button batal, lanjutkan;

    private SimpleDateFormat dateFormatter;
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_pelanggan_baru_pembayaran);
        dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        urutan = findViewById(R.id.urutan);
        top = findViewById(R.id.top);
        pembayaran = findViewById(R.id.pembayaran);
        listdepo = findViewById(R.id.depo);
        pengiriman = findViewById(R.id.pengiriman);
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
                if(urutan.getText().toString().length() == 0){
                    urutan.setError("Isi Urutan");
                } else if(top.getText().toString().length() == 0){
                    top.setError("Isi Top");
                } else if(pembayaran.getText().toString().length() ==0){
                    pembayaran.setError("Pilih Pembayaran");
                } else if(listdepo.getText().toString().length() ==0){
                    listdepo.setError("Pilih Depo");
                } else if(pengiriman.getText().toString().length() ==0){
                    pengiriman.setError("Isi Tanggal Pengiriman");
                } else {
                    Intent intent = new Intent(getApplicationContext(), produk_potensial.class);
                    startActivity(intent);
                }
            }
        });

        pengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                Calendar twoDaysAgo = (Calendar) currentDate.clone();
                twoDaysAgo.add(Calendar.DATE, 0);

                date = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date.set(year, monthOfYear, dayOfMonth);

                        pengiriman.setText(dateFormatter.format(date.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(pelanggan_baru_pembayaran.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(twoDaysAgo.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, jenis);
        pembayaran.setAdapter(adapter2);

        listdepo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kodedms = id_depo.get(position);
            }
        });

        StringRequest depo = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server/master/lokasi/index",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    id_depo.add(jsonObject1.getString("kode_dms"));
                                    namadepo.add(jsonObject1.getString("depo_nama"));

                                }
                            }
                            listdepo.setAdapter(new ArrayAdapter<String>(pelanggan_baru_pembayaran.this, android.R.layout.simple_expandable_list_item_1, namadepo));
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
        depo.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestkota = Volley.newRequestQueue(this);
        requestkota.getCache().clear();
        requestkota.add(depo);

    }
}