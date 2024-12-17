package com.tvip.sfa.menu_mulai_perjalanan;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.google.android.material.tabs.TabLayout;
import com.tvip.sfa.R;
import com.tvip.sfa.pojo.data_pelanggan_luar_rute_pojo;
import com.tvip.sfa.pojo.data_posm_pojo;
import com.tvip.sfa.pojo.data_product_pojo;
import com.tvip.sfa.pojo.laporan_penjualan_pojo;
import com.tvip.sfa.pojo.laporan_penjualan_product_pojo;
import com.tvip.sfa.pojo.summary_SKU_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class laporan_penjualan extends AppCompatActivity {
    ListView listlaporanpenjualan;
    List<laporan_penjualan_pojo> laporanPenjualanPojoList = new ArrayList<>();
    List<summary_SKU_pojo> summarySkuPojos = new ArrayList<>();

    ListViewAdapterLaporanPenjualan adapter;
    ListViewAdapterSummarySKU adapter2;
    SharedPreferences sharedPreferences;
    ScrollView totalpenjualan;
    TabLayout tablayout;

    ListView summarySKU;
    TextView qty;

    private int jumlahSKU = 0;
    private int SKUvalue;

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_laporan_penjualan);
        listlaporanpenjualan = findViewById(R.id.listlaporanpenjualan);
        totalpenjualan = findViewById(R.id.totalpenjualan);
        tablayout = findViewById(R.id.tablayout);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        summarySKU = findViewById(R.id.summarySKU);
        qty = findViewById(R.id.qty);

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position == 0){
                    listlaporanpenjualan.setVisibility(View.VISIBLE);
                    totalpenjualan.setVisibility(GONE);
                } else if(position == 1){
                    listlaporanpenjualan.setVisibility(GONE);
                    totalpenjualan.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    listlaporanpenjualan.setVisibility(View.VISIBLE);
                    totalpenjualan.setVisibility(GONE);
                } else if(position == 1){
                    listlaporanpenjualan.setVisibility(GONE);
                    totalpenjualan.setVisibility(VISIBLE);
                }
            }
        });

        getTotalPenjualan();


    }

    private void getSummaryPenjualan() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_SummarySKU?szEmployeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final summary_SKU_pojo movieItem = new summary_SKU_pojo(
                                        movieObject.getString("szName"),
                                        movieObject.getString("decQty"));

                                summarySkuPojos.add(movieItem);


                                SKUvalue = Integer.parseInt(movieObject.getString("decQty"));
                                jumlahSKU+=SKUvalue;
                                qty.setText(String.valueOf(jumlahSKU));

                                adapter2 = new ListViewAdapterSummarySKU(summarySkuPojos, getApplicationContext());
                                summarySKU.setAdapter(adapter2);
                                Utility.setListViewHeightBasedOnChildren(summarySKU);


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

    private void getTotalPenjualan() {
        pDialog = new SweetAlertDialog(laporan_penjualan.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LaporanPenjualan?szEmployeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getSummaryPenjualan();
                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final laporan_penjualan_pojo movieItem = new laporan_penjualan_pojo(
                                        movieObject.getString("szDocId"),
                                        movieObject.getString("szCustomerId"),
                                        movieObject.getString("szName"));

                                laporanPenjualanPojoList.add(movieItem);

                                adapter = new ListViewAdapterLaporanPenjualan(laporanPenjualanPojoList, getApplicationContext());
                                listlaporanpenjualan.setAdapter(adapter);

                                tablayout.getTabAt(0).getOrCreateBadge().setNumber(movieArray.length());



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

    public class ListViewAdapterLaporanPenjualan extends ArrayAdapter<laporan_penjualan_pojo> {

        private class ViewHolder {
            TextView namatoko;
            ImageView down, up;
            LinearLayout listproduk_layout;
            ListView listproduk;
            List<laporan_penjualan_product_pojo> laporanPenjualanPojoList;
            ListViewAdapterLaporanPenjualanSub adapter;
            TextView total;
        }

        List<laporan_penjualan_pojo> laporan_penjualan_pojos;
        private final Context context;

        public ListViewAdapterLaporanPenjualan(List<laporan_penjualan_pojo> laporan_penjualan_pojos, Context context) {
            super(context, R.layout.list_laporan_penjualan, laporan_penjualan_pojos);
            this.laporan_penjualan_pojos = laporan_penjualan_pojos;
            this.context = context;

        }

        public int getCount() {
            return laporan_penjualan_pojos.size();
        }

        public laporan_penjualan_pojo getItem(int position) {
            return laporan_penjualan_pojos.get(position);
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
            laporan_penjualan_pojo movieItem = laporan_penjualan_pojos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_laporan_penjualan, parent, false);
                viewHolder.namatoko = convertView.findViewById(R.id.namatoko);
                viewHolder.down = convertView.findViewById(R.id.down);
                viewHolder.up = convertView.findViewById(R.id.up);
                viewHolder.listproduk = convertView.findViewById(R.id.listproduk);
                viewHolder.listproduk_layout = convertView.findViewById(R.id.listproduk_layout);
                viewHolder.total = convertView.findViewById(R.id.total);

                viewHolder.laporanPenjualanPojoList = new ArrayList<>();
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }




            viewHolder.namatoko.setText(movieItem.getSzName());
            viewHolder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.down.setVisibility(View.GONE);
                    viewHolder.up.setVisibility(View.VISIBLE);
                    viewHolder.listproduk_layout.setVisibility(View.VISIBLE);
                    String aString =movieItem.getSzDocId();
                    String cutString = aString.substring(0, 11);
                    viewHolder.laporanPenjualanPojoList.clear();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LaporanPenjualan_Product?szDocId=" + cutString,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    int total = 0;
                                    System.out.println("https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_LaporanPenjualan_Product?szDocId=" + cutString);
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        final JSONArray movieArray = obj.getJSONArray("data");
                                        for (int i = 0; i < movieArray.length(); i++) {
                                            final JSONObject movieObject = movieArray.getJSONObject(i);
                                            final laporan_penjualan_product_pojo movieItem = new laporan_penjualan_product_pojo(
                                                    movieObject.getString("szName"),
                                                    movieObject.getString("decQty"),
                                                    movieObject.getString("decPrice"),
                                                    movieObject.getString("decAmount"));

                                            viewHolder.laporanPenjualanPojoList.add(movieItem);


                                            int totalvalue;

                                            String szId = movieObject.getString("decAmount");
                                            String[] parts = szId.split("\\.");
                                            String szIdSlice = parts[0];

                                            totalvalue = Integer.parseInt(szIdSlice);
                                            total+=totalvalue;

                                            viewHolder.adapter = new ListViewAdapterLaporanPenjualanSub(viewHolder.laporanPenjualanPojoList, getApplicationContext());
                                            viewHolder.listproduk.setAdapter(viewHolder.adapter);
                                            Utility.setListViewHeightBasedOnChildren(viewHolder.listproduk);
                                        }

                                        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                                        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
                                        formatRp.setCurrencySymbol("Rp. ");
                                        formatRp.setMonetaryDecimalSeparator(',');
                                        formatRp.setGroupingSeparator('.');
                                        kursIndonesia.setDecimalFormatSymbols(formatRp);


                                        viewHolder.total.setText(kursIndonesia.format(total));





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
                    RequestQueue requestQueue = Volley.newRequestQueue(laporan_penjualan.this);
                    requestQueue.getCache().clear();
                    requestQueue.add(stringRequest);
                }
            });

            viewHolder.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.down.setVisibility(View.VISIBLE);
                    viewHolder.up.setVisibility(View.GONE);
                    viewHolder.listproduk_layout.setVisibility(View.GONE);
                    viewHolder.laporanPenjualanPojoList.clear();
                }
            });

            return convertView;
        }
    }

    public static class ListViewAdapterLaporanPenjualanSub extends ArrayAdapter<laporan_penjualan_product_pojo> {
        private final List<laporan_penjualan_product_pojo> laporan_penjualan_product_pojos;

        private final Context context;

        public ListViewAdapterLaporanPenjualanSub(List<laporan_penjualan_product_pojo> laporan_penjualan_product_pojos, Context context) {
            super(context, R.layout.list_penjualan_sub_2, laporan_penjualan_product_pojos);
            this.laporan_penjualan_product_pojos = laporan_penjualan_product_pojos;
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


            laporan_penjualan_product_pojo data = laporan_penjualan_product_pojos.get(position);

            produk.setText(data.getSzName());

            String szId = data.getDecPrice();
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

    public static class ListViewAdapterSummarySKU extends ArrayAdapter<summary_SKU_pojo> {
        private final List<summary_SKU_pojo> summarySkuPojos;

        private final Context context;

        public ListViewAdapterSummarySKU(List<summary_SKU_pojo> summarySkuPojos, Context context) {
            super(context, R.layout.list_penjualan_sub, summarySkuPojos);
            this.summarySkuPojos = summarySkuPojos;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_penjualan_sub, null, true);

            TextView produk = listViewItem.findViewById(R.id.produk);
            TextView qty = listViewItem.findViewById(R.id.qty);

            summary_SKU_pojo data = summarySkuPojos.get(position);

            produk.setText(data.getSzName());
            qty.setText(data.getDecQty());


            return listViewItem;
        }
    }
}