package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_persiapan.detail_outlet_kritis;
import com.tvip.sfa.pojo.SO_pojo;
import com.tvip.sfa.pojo.piutang_pojo;
import com.tvip.sfa.pojo.so_sub_pojio;
import com.tvip.sfa.pojo.trend_produk_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

public class info_pelanggan extends AppCompatActivity {
    ScrollView tipetoko;
    LinearLayout trenpenjualan;
    RelativeLayout historypenjualan;
    TabLayout tablayout;
    TextView namaSO, namatoko, kode, alamat, term_payment, limit_kredit, piutang, sisa_limit_kredit, channel, status;
    ListView trendpenjualan, list_historypenjualan, infopiutang;
    ImageView show, hide;

    List<SO_pojo> soPojos = new ArrayList<>();
    ListViewAdapterSO adapter;

    List<piutang_pojo> piutang_pojos = new ArrayList<>();
    ListViewAdapterinfoPiutangs adapter2;

    List<trend_produk_pojo> trendProdukPojos = new ArrayList<>();
    ListViewAdapterTrendPenjualan adapter3;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_info_pelanggan);
        getCacheDir().delete();
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        namatoko = findViewById(R.id.namatoko);

        kode = findViewById(R.id.kode);
        alamat = findViewById(R.id.alamat);
        term_payment = findViewById(R.id.term_payment);
        limit_kredit = findViewById(R.id.limit_kredit);
        piutang = findViewById(R.id.piutang);
        sisa_limit_kredit = findViewById(R.id.sisa_limit_kredit);
        channel = findViewById(R.id.channel);
        status = findViewById(R.id.status);
        trenpenjualan = findViewById(R.id.trenpenjualan);
        historypenjualan = findViewById(R.id.historypenjualan);
        list_historypenjualan = findViewById(R.id.list_historypenjualan);
        trendpenjualan = findViewById(R.id.trendpenjualan);

        tipetoko = findViewById(R.id.tipetoko);
        tablayout = findViewById(R.id.tablayout);
        infopiutang = findViewById(R.id.infopiutang);

        getSOHistory();
        getTrend();

        hide = findViewById(R.id.hide);

        namatoko.setText(getIntent().getStringExtra("pelanggan"));
        kode.setText(getIntent().getStringExtra("SzId"));
        alamat.setText(getIntent().getStringExtra("alamat"));

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position == 0){
                    tipetoko.setVisibility(View.VISIBLE);
                    historypenjualan.setVisibility(GONE);
                    trenpenjualan.setVisibility(View.GONE);
                    infopiutang.setVisibility(GONE);
                } else if(position == 1){
                    tipetoko.setVisibility(GONE);
                    historypenjualan.setVisibility(VISIBLE);
                    trenpenjualan.setVisibility(View.GONE);
                    infopiutang.setVisibility(GONE);
                } else if(position == 2){
                    tipetoko.setVisibility(View.GONE);
                    historypenjualan.setVisibility(GONE);
                    trenpenjualan.setVisibility(View.VISIBLE);
                    infopiutang.setVisibility(GONE);
                }else if(position == 3){
                    tipetoko.setVisibility(View.GONE);
                    historypenjualan.setVisibility(GONE);
                    trenpenjualan.setVisibility(View.GONE);
                    infopiutang.setVisibility(VISIBLE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    tipetoko.setVisibility(View.VISIBLE);
                    historypenjualan.setVisibility(GONE);
                    trenpenjualan.setVisibility(View.GONE);
                } else if(position == 1){
                    tipetoko.setVisibility(GONE);
                    historypenjualan.setVisibility(VISIBLE);
                    trenpenjualan.setVisibility(View.GONE);
                } else if(position == 2){
                    tipetoko.setVisibility(View.GONE);
                    historypenjualan.setVisibility(GONE);
                    trenpenjualan.setVisibility(View.VISIBLE);
                }else if(position == 3){
                    tipetoko.setVisibility(View.GONE);
                    historypenjualan.setVisibility(GONE);
                    trenpenjualan.setVisibility(View.GONE);
                }
            }
        });
        getDetailPelanggan();

        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Detail_Pelanggan?szId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getPiutang();

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
                        getPiutang();
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

    }

    private void getTrend() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -2);
                Date date = calendar.getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String dateOutput = format.format(date);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_TrendSalesListProduk?tanggal="+dateOutput+"&szCustomerId=" + kode.getText().toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    int number = 0;
                                    JSONObject obj = new JSONObject(response);
                                    final JSONArray movieArray = obj.getJSONArray("data");
                                    for (int i = 0; i < movieArray.length(); i++) {
                                        final JSONObject movieObject = movieArray.getJSONObject(i);
                                        final trend_produk_pojo movieItem = new trend_produk_pojo(
                                                movieObject.getString("szProductId"),
                                                movieObject.getString("szName"),
                                                movieObject.getString("minggu_1"),
                                                movieObject.getString("minggu_2"),
                                                movieObject.getString("minggu_3"),
                                                movieObject.getString("minggu_4"),
                                                movieObject.getString("minggu_5"),
                                                movieObject.getString("minggu_6"),
                                                movieObject.getString("minggu_7"),
                                                movieObject.getString("minggu_8"));


                                        trendProdukPojos.add(movieItem);

                                        adapter3 = new ListViewAdapterTrendPenjualan(trendProdukPojos, getApplicationContext());
                                        trendpenjualan.setAdapter(adapter3);
                                        adapter3.notifyDataSetChanged();




                                    }
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
                RequestQueue requestQueue = Volley.newRequestQueue(info_pelanggan.this);
                requestQueue.add(stringRequest);
            }
        }, 2000);

    }

    private void getPiutang() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_InfoPiutang?szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final piutang_pojo movieItem = new piutang_pojo(
                                        movieObject.getString("szDocId"),
                                        movieObject.getString("szDocStatus"),
                                        movieObject.getString("szDescription"),
                                        movieObject.getString("decAmount"));

                                piutang_pojos.add(movieItem);

                                piutang.setText(movieObject.getString("decAmount"));

                                adapter2 = new ListViewAdapterinfoPiutangs(piutang_pojos, getApplicationContext());
                                infopiutang.setAdapter(adapter2);
                                adapter2.notifyDataSetChanged();



                            }
                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        piutang.setText("0.0000");
                        sisa_limit_kredit.setText(limit_kredit.getText().toString());
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

    private void getDetailPelanggan() {
        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Detail_Pelanggan?szCustomerId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);

                                kode.setText(biodatas.getString("szCustomerId"));
                                namatoko.setText(biodatas.getString("szName"));

                                alamat.setText(biodatas.getString("szAddress"));
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
        channel_statusQueue.add(channel_status);
    }

    private void getSOHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_HistoryPenjualanRevisi?szCustomerId=" + no_surat + "&szEmployeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final SO_pojo movieItem = new SO_pojo(
                                        movieObject.getString("dtmDoc"),
                                        movieObject.getString("szDocId"));

                                soPojos.add(movieItem);

                                adapter = new ListViewAdapterSO(soPojos, getApplicationContext());
                                list_historypenjualan.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }
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
        requestQueue.add(stringRequest);
    }

    public class ListViewAdapterSO extends ArrayAdapter<SO_pojo> {

        private class ViewHolder {
            TextView tanggal, noso;
            ListView List_sub_penjualan;
            List<so_sub_pojio> soSubPojios;
            ListViewAdapterSOSub adapter;
            ImageButton unhide, hide;
        }

        List<SO_pojo> SO_pojos;
        private final Context context;

        public ListViewAdapterSO(List<SO_pojo> SO_pojos, Context context) {
            super(context, R.layout.list_history_penjualan, SO_pojos);
            this.SO_pojos = SO_pojos;
            this.context = context;

        }

        public int getCount() {
            return SO_pojos.size();
        }

        public SO_pojo getItem(int position) {
            return SO_pojos.get(position);
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
            final ListViewAdapterSO.ViewHolder viewHolder;
            SO_pojo movieItem = SO_pojos.get(position);
            if (convertView == null) {
                viewHolder = new ListViewAdapterSO.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_history_penjualan, parent, false);
                viewHolder.tanggal = convertView.findViewById(R.id.tanggal);
                viewHolder.noso = convertView.findViewById(R.id.noso);
                viewHolder.List_sub_penjualan = convertView.findViewById(R.id.List_sub_penjualan);
                viewHolder.soSubPojios = new ArrayList<>();

                viewHolder.unhide = convertView.findViewById(R.id.unhide);
                viewHolder.hide = convertView.findViewById(R.id.hide);



                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ListViewAdapterSO.ViewHolder) convertView.getTag();
            }

            viewHolder.unhide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.unhide.setVisibility(GONE);
                    viewHolder.hide.setVisibility(VISIBLE);
                    viewHolder.List_sub_penjualan.setVisibility(VISIBLE);
                }
            });

            viewHolder.hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.unhide.setVisibility(VISIBLE);
                    viewHolder.hide.setVisibility(GONE);
                    viewHolder.List_sub_penjualan.setVisibility(GONE);
                }
            });



            viewHolder.soSubPojios.clear();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_HistoryPenjualanSub?szDocId=" + movieItem.getSzDocId(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                int number = 0;
                                JSONObject obj = new JSONObject(response);
                                final JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {
                                    final JSONObject movieObject = movieArray.getJSONObject(i);
                                    final so_sub_pojio movieItem = new so_sub_pojio(
                                            movieObject.getString("decQty"),
                                            movieObject.getString("decAmount"));

                                    viewHolder.soSubPojios.add(movieItem);

                                    viewHolder.adapter = new ListViewAdapterSOSub(viewHolder.soSubPojios, getApplicationContext());
                                    viewHolder.List_sub_penjualan.setAdapter(viewHolder.adapter);
                                    viewHolder.adapter.notifyDataSetChanged();
                                    Utility.setListViewHeightBasedOnChildren(viewHolder.List_sub_penjualan);


//                                    adapter = new ListViewAdapterSO(soPojos, getApplicationContext());
//                                    list_historypenjualan.setAdapter(adapter);
//                                    adapter.notifyDataSetChanged();

                                }
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
            RequestQueue requestQueue = Volley.newRequestQueue(info_pelanggan.this);
            requestQueue.add(stringRequest);

            viewHolder.tanggal.setText(convertFormat(movieItem.getDtmDoc()));
            viewHolder.noso.setText("NO SO : " + movieItem.getSzDocId());


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

    public class ListViewAdapterSOSub extends ArrayAdapter<so_sub_pojio> {
        private final List<so_sub_pojio> soSubPojios;

        private final Context context;

        public ListViewAdapterSOSub(List<so_sub_pojio> soSubPojios, Context context) {
            super(context, R.layout.list_total_detail_penjualan, soSubPojios);
            this.soSubPojios = soSubPojios;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_total_detail_penjualan, null, true);

            TextView qty = listViewItem.findViewById(R.id.qty);
            TextView qty_order = listViewItem.findViewById(R.id.totalbelanja);

            so_sub_pojio data = soSubPojios.get(position);

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
            qty_order.setText(kursIndonesia.format(Integer.parseInt(szIdSlice)));

            return listViewItem;
        }
    }

    public class ListViewAdapterinfoPiutangs extends ArrayAdapter<piutang_pojo> {
        private final List<piutang_pojo> piutang_pojos;

        private final Context context;

        public ListViewAdapterinfoPiutangs(List<piutang_pojo> piutang_pojos, Context context) {
            super(context, R.layout.list_piutang, piutang_pojos);
            this.piutang_pojos = piutang_pojos;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_piutang, null, true);

            TextView so = listViewItem.findViewById(R.id.so);
            TextView keterangan = listViewItem.findViewById(R.id.keterangan);
            TextView piutang = listViewItem.findViewById(R.id.piutang);
            TextView status = listViewItem.findViewById(R.id.status);
            MaterialCardView warna = listViewItem.findViewById(R.id.warna);

            piutang_pojo data = piutang_pojos.get(position);

            if(data.getSzDocStatus().equals("Applied")){
                warna.setCardBackgroundColor(Color.parseColor("#1EB547"));
            } else {
                warna.setCardBackgroundColor(Color.parseColor("#FF0000"));
            }

            so.setText(data.getSzCustomerId());
            keterangan.setText(data.getSzDescription());
            piutang.setText(data.getDecAmount());
            status.setText(data.getSzDocStatus());

            return listViewItem;
        }
    }

    public class ListViewAdapterTrendPenjualan extends ArrayAdapter<trend_produk_pojo> {

        private class ViewHolder {
            TextView produk;
            LinearLayout progress;
            ImageButton unhide, hide;
            ImageView minggu1, minggu2, minggu3, minggu4, minggu5, minggu6, minggu7, minggu8;

        }

        List<trend_produk_pojo> trend_produk_pojos;
        private final Context context;

        public ListViewAdapterTrendPenjualan(List<trend_produk_pojo> trend_produk_pojos, Context context) {
            super(context, R.layout.list_trend_sub, trend_produk_pojos);
            this.trend_produk_pojos = trend_produk_pojos;
            this.context = context;

        }

        public int getCount() {
            return trend_produk_pojos.size();
        }

        public trend_produk_pojo getItem(int position) {
            return trend_produk_pojos.get(position);
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
            trend_produk_pojo movieItem = trend_produk_pojos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_trend_sub, parent, false);
                viewHolder.produk = convertView.findViewById(R.id.produk);
                viewHolder.progress = convertView.findViewById(R.id.progress);

                viewHolder.unhide = convertView.findViewById(R.id.unhide);
                viewHolder.hide = convertView.findViewById(R.id.hide);

                viewHolder.minggu1 = convertView.findViewById(R.id.minggu1);
                viewHolder.minggu2 = convertView.findViewById(R.id.minggu2);
                viewHolder.minggu3 = convertView.findViewById(R.id.minggu3);
                viewHolder.minggu4 = convertView.findViewById(R.id.minggu4);
                viewHolder.minggu5 = convertView.findViewById(R.id.minggu5);
                viewHolder.minggu6 = convertView.findViewById(R.id.minggu6);
                viewHolder.minggu7 = convertView.findViewById(R.id.minggu7);
                viewHolder.minggu8 = convertView.findViewById(R.id.minggu8);


                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.produk.setText(movieItem.getSzName());

            viewHolder.unhide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.unhide.setVisibility(GONE);
                    viewHolder.hide.setVisibility(VISIBLE);
                    viewHolder.progress.setVisibility(VISIBLE);
                }
            });

            viewHolder.hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.unhide.setVisibility(VISIBLE);
                    viewHolder.hide.setVisibility(GONE);
                    viewHolder.progress.setVisibility(GONE);
                }
            });

            //  minggu 1 //
            if(movieItem.getMinggu_1().equals("0.00")){
                viewHolder.minggu1.setImageResource(R.drawable.down);
            } else {
                viewHolder.minggu1.setImageResource(R.drawable.up);
            }
            //  minggu 1 //


            //  minggu 2 //
            double minggu1 = Double.parseDouble(movieItem.getMinggu_1());
            double minggu2 = Double.parseDouble(movieItem.getMinggu_2());

            if (movieItem.getMinggu_2().equals("0.00")){
                viewHolder.minggu2.setImageResource(R.drawable.down);
            } else if((int)minggu1 <= (int)minggu2){
                viewHolder.minggu2.setImageResource(R.drawable.up);
            } else {
                viewHolder.minggu2.setImageResource(R.drawable.down);
            }
            // minggu 2 //


            //  minggu 3 //
            double minggu3 = Double.parseDouble(movieItem.getMinggu_3());

            if (movieItem.getMinggu_3().equals("0.00")){
                viewHolder.minggu3.setImageResource(R.drawable.down);
            } else if((int)minggu2 <= (int)minggu3){
                viewHolder.minggu3.setImageResource(R.drawable.up);
            } else {
                viewHolder.minggu3.setImageResource(R.drawable.down);
            }
            // minggu 3 //


            //  minggu 4 //
            double minggu4 = Double.parseDouble(movieItem.getMinggu_4());

            if (movieItem.getMinggu_4().equals("0.00")){
                viewHolder.minggu4.setImageResource(R.drawable.down);
            } else if((int)minggu3 <= (int)minggu4){
                viewHolder.minggu4.setImageResource(R.drawable.up);
            } else {
                viewHolder.minggu4.setImageResource(R.drawable.down);
            }
            // minggu 4 //


            //  minggu 5 //
            double minggu5 = Double.parseDouble(movieItem.getMinggu_5());

            if (movieItem.getMinggu_5().equals("0.00")){
                viewHolder.minggu5.setImageResource(R.drawable.down);
            } else if((int)minggu4 <= (int)minggu5){
                viewHolder.minggu5.setImageResource(R.drawable.up);
            } else {
                viewHolder.minggu5.setImageResource(R.drawable.down);
            }
            // minggu 5 //


            //  minggu 6 //
            double minggu6 = Double.parseDouble(movieItem.getMinggu_6());

            if (movieItem.getMinggu_6().equals("0.00")){
                viewHolder.minggu6.setImageResource(R.drawable.down);
            } else if((int)minggu5 <= (int)minggu6){
                viewHolder.minggu6.setImageResource(R.drawable.up);
            } else {
                viewHolder.minggu6.setImageResource(R.drawable.down);
            }
            // minggu 6 //


            //  minggu 7 //
            double minggu7 = Double.parseDouble(movieItem.getMinggu_7());

            if (movieItem.getMinggu_7().equals("0.00")){
                viewHolder.minggu7.setImageResource(R.drawable.down);
            } else if((int)minggu6 <= (int)minggu7){
                viewHolder.minggu7.setImageResource(R.drawable.up);
            } else {
                viewHolder.minggu7.setImageResource(R.drawable.down);
            }
            // minggu 7 //


            //  minggu 8 //
            double minggu8 = Double.parseDouble(movieItem.getMinggu_8());

            if (movieItem.getMinggu_8().equals("0.00")){
                viewHolder.minggu8.setImageResource(R.drawable.down);
            } else if((int)minggu7 <= (int)minggu8){
                viewHolder.minggu8.setImageResource(R.drawable.up);
            } else {
                viewHolder.minggu8.setImageResource(R.drawable.down);
            }
            // minggu 8 //





            return convertView;
        }
    }


}