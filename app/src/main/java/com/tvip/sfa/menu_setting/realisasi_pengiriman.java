package com.tvip.sfa.menu_setting;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tvip.sfa.Perangkat.HttpsTrustManager;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_mulai_perjalanan.Utility;
import com.tvip.sfa.menu_mulai_perjalanan.pengiriman_info;
import com.tvip.sfa.menu_mulai_perjalanan.summary_order;
import com.tvip.sfa.menu_mulai_perjalanan.summary_penjualan;
import com.tvip.sfa.menu_splash.splash;
import com.tvip.sfa.pojo.data_penjualan_terakhir_pojo;
import com.tvip.sfa.pojo.data_product_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class realisasi_pengiriman extends AppCompatActivity {
    EditText tanggalkirim;
    private SimpleDateFormat dateFormatter;
    private Calendar date;

    SweetAlertDialog pDialog;

    ListView list_pengiriman_terakhir;
    List<data_penjualan_terakhir_pojo> dataPenjualanTerakhirPojos = new ArrayList<>();
    ListViewAdapterRealisasiPengiriman adapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HttpsTrustManager.allowAllSSL();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realisasi_pengiriman);
        tanggalkirim = findViewById(R.id.tanggalkirim);
        list_pengiriman_terakhir = findViewById(R.id.list_pengiriman_terakhir);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        tanggalkirim.setOnClickListener(new View.OnClickListener() {
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

                        tanggalkirim.setText(dateFormatter.format(date.getTime()));
                        
                        getPenjualanTerakhir();
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(realisasi_pengiriman.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(twoDaysAgo.getTimeInMillis());
                datePickerDialog.show();

            }
        });
    }

    private void getPenjualanTerakhir() {
        pDialog = new SweetAlertDialog(realisasi_pengiriman.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        adapter = new ListViewAdapterRealisasiPengiriman(dataPenjualanTerakhirPojos, getApplicationContext());
        dataPenjualanTerakhirPojos.clear();
        adapter.notifyDataSetChanged();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoPerEmployee?szEmployeeId="+nik_baru+"&dtmDoc=" + convertFormat2(tanggalkirim.getText().toString()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list_pengiriman_terakhir.setVisibility(View.VISIBLE);

                        pDialog.dismissWithAnimation();

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_penjualan_terakhir_pojo movieItem = new data_penjualan_terakhir_pojo(
                                        movieObject.getString("szDocId"),
                                        movieObject.getString("dtmDoc"),
                                        movieObject.getString("szCustomerId"),
                                        movieObject.getString("szEmployeeId"),
                                        movieObject.getString("decAmount"),
                                        movieObject.getString("szDocCallId"));

                                dataPenjualanTerakhirPojos.add(movieItem);
                            }

                            list_pengiriman_terakhir.setAdapter(adapter);
                            adapter.notifyDataSetChanged();



                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
                        dataPenjualanTerakhirPojos.clear();
                        adapter.notifyDataSetChanged();
                        list_pengiriman_terakhir.setVisibility(View.GONE);

                        new SweetAlertDialog(realisasi_pengiriman.this, SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Realisasi untuk tanggal " + tanggalkirim.getText().toString() + " tidak ada")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
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
        requestQueue.add(stringRequest);
    }
    public class ListViewAdapterRealisasiPengiriman extends ArrayAdapter<data_penjualan_terakhir_pojo> {

        private class ViewHolder {
            TextView no_so, tanggal, no_do;
            ImageView down, up;
            LinearLayout listproduk_layout;
            ListView listproduk;

            ListViewAdapterRealisasiPengirimanSub adapter;
            List<data_product_pojo> dataProductPojos = new ArrayList<>();

        }

        List<data_penjualan_terakhir_pojo> data_penjualan_terakhir_pojos;
        private final Context context;

        public ListViewAdapterRealisasiPengiriman(List<data_penjualan_terakhir_pojo> data_penjualan_terakhir_pojos, Context context) {
            super(context, R.layout.list_pengiriman_terakhir, data_penjualan_terakhir_pojos);
            this.data_penjualan_terakhir_pojos = data_penjualan_terakhir_pojos;
            this.context = context;

        }

        public int getCount() {
            return data_penjualan_terakhir_pojos.size();
        }

        public data_penjualan_terakhir_pojo getItem(int position) {
            return data_penjualan_terakhir_pojos.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public long getItemId(int position) {
            return 0;
        }


        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            data_penjualan_terakhir_pojo movieItem = data_penjualan_terakhir_pojos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_pengiriman_terakhir, parent, false);
                viewHolder.tanggal = convertView.findViewById(R.id.tanggal);
                viewHolder.no_so = convertView.findViewById(R.id.no_so);
                viewHolder.no_do = convertView.findViewById(R.id.no_do);
                viewHolder.down = convertView.findViewById(R.id.down);
                viewHolder.up = convertView.findViewById(R.id.up);
                viewHolder.listproduk_layout = convertView.findViewById(R.id.listproduk_layout);
                viewHolder.listproduk = convertView.findViewById(R.id.listproduk);
                viewHolder.dataProductPojos = new ArrayList<>();
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.dataProductPojos.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DocSoPerEmployeeSub?szDocId=" + movieItem.getSzDocId(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                int number = 0;
                                JSONObject obj = new JSONObject(response);
                                final JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {
                                    final JSONObject movieObject = movieArray.getJSONObject(i);
                                    final data_product_pojo movieItem = new data_product_pojo(
                                            movieObject.getString("szName"),
                                            movieObject.getString("decQty"));

                                    viewHolder.dataProductPojos.add(movieItem);
                                }

                                viewHolder.adapter = new ListViewAdapterRealisasiPengirimanSub(viewHolder.dataProductPojos, getApplicationContext());
                                viewHolder.listproduk.setAdapter(viewHolder.adapter);
                                viewHolder.adapter.notifyDataSetChanged();
                                Utility.setListViewHeightBasedOnChildren(viewHolder.listproduk);


                            } catch(JSONException e){
                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        pDialog.dismissWithAnimation();
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
            RequestQueue requestQueue = Volley.newRequestQueue(realisasi_pengiriman.this);
            requestQueue.add(stringRequest);

            viewHolder.tanggal.setText(convertFormat(movieItem.getDtmDoc()));
            viewHolder.no_so.setText("NO SO : " + movieItem.getSzDocId());
            viewHolder.no_do.setText("NO DO : " + movieItem.getSzDocCallId());

            viewHolder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.down.setVisibility(View.GONE);
                    viewHolder.up.setVisibility(View.VISIBLE);
                    viewHolder.listproduk_layout.setVisibility(View.VISIBLE);
                }
            });

            viewHolder.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.down.setVisibility(View.VISIBLE);
                    viewHolder.up.setVisibility(View.GONE);
                    viewHolder.listproduk_layout.setVisibility(View.GONE);
                }
            });
            return convertView;
        }
    }

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd/MM/yy");
        return convetDateFormat.format(date);
    }

    public static String convertFormat2(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return convetDateFormat.format(date);
    }

    public static class ListViewAdapterRealisasiPengirimanSub extends ArrayAdapter<data_product_pojo> {
        private final List<data_product_pojo> data_pelanggans;

        private final Context context;

        public ListViewAdapterRealisasiPengirimanSub(List<data_product_pojo> data_pelanggans, Context context) {
            super(context, R.layout.list_penjualan_sub, data_pelanggans);
            this.data_pelanggans = data_pelanggans;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_penjualan_sub, null, true);

            TextView produk = listViewItem.findViewById(R.id.produk);
            TextView qty = listViewItem.findViewById(R.id.qty);

            data_product_pojo data = data_pelanggans.get(position);

            produk.setText(data.getSzName());

            String szId = data.getDecQty();
            String[] parts = szId.split("\\.");
            String szIdSlice = parts[0];

            qty.setText(szIdSlice + " Qty");

            return listViewItem;
        }
    }
}