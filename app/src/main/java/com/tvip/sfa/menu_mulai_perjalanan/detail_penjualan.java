package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.tvip.sfa.pojo.data_penjualan_terakhir_pojo;
import com.tvip.sfa.pojo.data_product_pojo;
import com.tvip.sfa.pojo.so_penjualan_pojo;
import com.tvip.sfa.pojo.so_penjualan_product_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
detail_penjualan extends AppCompatActivity {
    TextView namatoko;
    TextView kode_pelanggan;
    TextView alamat;
    ListView list_detail;
    ListViewAdapterSummaryKunjungan adapter;
    List<so_penjualan_pojo> soPenjualanPojos = new ArrayList<>();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_detail_penjualan);
        namatoko = findViewById(R.id.namatoko);
        kode_pelanggan = findViewById(R.id.kode_pelanggan);
        alamat = findViewById(R.id.alamat);
        list_detail = findViewById(R.id.list_detail);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        namatoko.setText(getIntent().getStringExtra("szName"));
        kode_pelanggan.setText(getIntent().getStringExtra("szCustomerId"));
        alamat.setText(getIntent().getStringExtra("szAddress"));

        getSummaryPenjualan();
    }

    private void getSummaryPenjualan() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SummaryPenjualanRevisi?szCustomerId=" + getIntent().getStringExtra("szCustomerId") + "&szEmployeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final so_penjualan_pojo movieItem = new so_penjualan_pojo(
                                        movieObject.getString("szDocId"),
                                        movieObject.getString("dtmDoc"),
                                        movieObject.getString("decAmount"));

                                soPenjualanPojos.add(movieItem);
                            }

                            adapter = new ListViewAdapterSummaryKunjungan(soPenjualanPojos, getApplicationContext());
                            list_detail.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        pDialog.dismissWithAnimation();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    public class ListViewAdapterSummaryKunjungan extends ArrayAdapter<so_penjualan_pojo> {

        private class ViewHolder {
            TextView no_so, total;
            List<so_penjualan_product_pojo> soPenjualanProductPojos;
            ListViewAdapterDaftarPenjualanSub adapter;
            ListView listproduk;
            ImageView down, up;
            RelativeLayout totallayout;
            LinearLayout listproduk_layout;


        }

        List<so_penjualan_pojo> so_penjualan_pojos;
        private final Context context;

        public ListViewAdapterSummaryKunjungan(List<so_penjualan_pojo> so_penjualan_pojos, Context context) {
            super(context, R.layout.list_so, so_penjualan_pojos);
            this.so_penjualan_pojos = so_penjualan_pojos;
            this.context = context;

        }

        public int getCount() {
            return so_penjualan_pojos.size();
        }

        public so_penjualan_pojo getItem(int position) {
            return so_penjualan_pojos.get(position);
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
            so_penjualan_pojo movieItem = so_penjualan_pojos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_so, parent, false);
                viewHolder.no_so = convertView.findViewById(R.id.no_so);
                viewHolder.soPenjualanProductPojos = new ArrayList<>();
                viewHolder.listproduk = convertView.findViewById(R.id.listproduk);
                viewHolder.up = convertView.findViewById(R.id.up);
                viewHolder.down = convertView.findViewById(R.id.down);
                viewHolder.total = convertView.findViewById(R.id.total);
                viewHolder.totallayout = convertView.findViewById(R.id.totallayout);
                viewHolder.listproduk_layout = convertView.findViewById(R.id.listproduk_layout);



                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);


            viewHolder.total.setText(kursIndonesia.format(Integer.parseInt(movieItem.getDecAmount())));


            viewHolder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.down.setVisibility(View.GONE);
                    viewHolder.up.setVisibility(View.VISIBLE);
                    viewHolder.listproduk_layout.setVisibility(View.VISIBLE);
                    viewHolder.totallayout.setVisibility(View.VISIBLE);
                }
            });

            viewHolder.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.down.setVisibility(View.VISIBLE);
                    viewHolder.up.setVisibility(View.GONE);
                    viewHolder.listproduk_layout.setVisibility(View.GONE);
                    viewHolder.totallayout.setVisibility(View.GONE);

                }
            });

            viewHolder.no_so.setText("NO SO : " + movieItem.getSzDocId() + " • " + convertFormat(movieItem.getDtmDoc()));

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SummaryPenjualanProduct?szProductId=" + movieItem.getSzDocId(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            viewHolder.soPenjualanProductPojos.clear();
                            try {
                                int number = 0;
                                JSONObject obj = new JSONObject(response);
                                final JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {
                                    final JSONObject movieObject = movieArray.getJSONObject(i);
                                    final so_penjualan_product_pojo movieItem = new so_penjualan_product_pojo(
                                            movieObject.getString("decQty"),
                                            movieObject.getString("szName"),
                                            movieObject.getString("decAmount"));

                                    viewHolder.soPenjualanProductPojos.add(movieItem);

                                    viewHolder.adapter = new ListViewAdapterDaftarPenjualanSub(viewHolder.soPenjualanProductPojos, getApplicationContext());
                                    viewHolder.listproduk.setAdapter(viewHolder.adapter);
                                    viewHolder.adapter.notifyDataSetChanged();
                                    Utility.setListViewHeightBasedOnChildren(viewHolder.listproduk);
                                }






                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        pDialog.dismissWithAnimation();
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
            RequestQueue requestQueue = Volley.newRequestQueue(detail_penjualan.this);
            requestQueue.add(stringRequest);


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

    public static class ListViewAdapterDaftarPenjualanSub extends ArrayAdapter<so_penjualan_product_pojo> {
        private final List<so_penjualan_product_pojo> soPenjualanProductPojos;

        private final Context context;

        public ListViewAdapterDaftarPenjualanSub(List<so_penjualan_product_pojo> soPenjualanProductPojos, Context context) {
            super(context, R.layout.list_penjualan_sub_2, soPenjualanProductPojos);
            this.soPenjualanProductPojos = soPenjualanProductPojos;
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

            so_penjualan_product_pojo data = soPenjualanProductPojos.get(position);

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


            qty.setText(data.getDecQty() + " Qty");
            subharga.setText(kursIndonesia.format(Integer.parseInt(szIdSlice)));

            return listViewItem;
        }
    }
}