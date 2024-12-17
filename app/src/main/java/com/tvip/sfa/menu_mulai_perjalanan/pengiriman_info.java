package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
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
import com.tvip.sfa.menu_persiapan.daftar_kunjungan;
import com.tvip.sfa.pojo.data_pelanggan_pojo;
import com.tvip.sfa.pojo.data_penjualan_terakhir_pojo;
import com.tvip.sfa.pojo.data_posm_pojo;
import com.tvip.sfa.pojo.data_product_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pengiriman_info extends AppCompatActivity {
    ListView list_pengiriman_terakhir;
    List<data_penjualan_terakhir_pojo> dataPenjualanTerakhirPojos = new ArrayList<>();
    ListViewAdapterDaftarKunjunganMain adapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_pengiriman_info);
        list_pengiriman_terakhir = findViewById(R.id.list_pengiriman_terakhir);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        getPenjualanTerakhir();
    }

    private void getPenjualanTerakhir() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_PenjualanTerakhir?szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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

                            adapter = new ListViewAdapterDaftarKunjunganMain(dataPenjualanTerakhirPojos, getApplicationContext());
                            list_pengiriman_terakhir.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


//                            pDialog.dismissWithAnimation();

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
    public class ListViewAdapterDaftarKunjunganMain extends ArrayAdapter<data_penjualan_terakhir_pojo> {

        private class ViewHolder {
            TextView no_so, tanggal, no_do;
            ImageView down, up;
            LinearLayout listproduk_layout;
            ListView listproduk;
            ListViewAdapterDaftarKunjunganMainSub adapter;

            List<data_product_pojo> dataProductPojos = new ArrayList<>();


        }

        List<data_penjualan_terakhir_pojo> data_penjualan_terakhir_pojos;
        private final Context context;

        public ListViewAdapterDaftarKunjunganMain(List<data_penjualan_terakhir_pojo> data_penjualan_terakhir_pojos, Context context) {
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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_PenjualanTerakhirProduk?surat_tugas=" + movieItem.getSzDocId(),
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

                                viewHolder.adapter = new ListViewAdapterDaftarKunjunganMainSub(viewHolder.dataProductPojos, getApplicationContext());
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
            RequestQueue requestQueue = Volley.newRequestQueue(pengiriman_info.this);
            requestQueue.getCache().clear();
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

    public static class ListViewAdapterDaftarKunjunganMainSub extends ArrayAdapter<data_product_pojo> {
        private final List<data_product_pojo> data_pelanggans;

        private final Context context;

        public ListViewAdapterDaftarKunjunganMainSub(List<data_product_pojo> data_pelanggans, Context context) {
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