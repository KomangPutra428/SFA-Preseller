package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
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
import com.google.android.material.tabs.TabLayout;
import com.tvip.sfa.R;
import com.tvip.sfa.pojo.data_pelanggan_dalam_rute_pojo;
import com.tvip.sfa.pojo.data_pelanggan_luar_rute_pojo;
import com.tvip.sfa.pojo.data_pelanggan_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class summary_penjualan extends AppCompatActivity {
    TabLayout tablayout;
    ListView list_summary;
    SharedPreferences sharedPreferences;
    List<data_pelanggan_dalam_rute_pojo> dataPelangganDalamRutePojos = new ArrayList<>();
    ListViewAdapterDaftarDalamPelanggan adapter;

    List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojos = new ArrayList<>();
    ListViewAdapterDaftarLuarPelanggan adapter2;

    SearchView caripelanggan;
    SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_summary_penjualan);
        tablayout = findViewById(R.id.tablayout);
        list_summary = findViewById(R.id.list_summary);
        caripelanggan = findViewById(R.id.caripelanggan);

        tablayout = findViewById(R.id.tablayout);

        tablayout.getTabAt(0).getOrCreateBadge().setNumber(0);
        tablayout.getTabAt(1).getOrCreateBadge().setNumber(0);
        tablayout.getTabAt(2).getOrCreateBadge().setNumber(0);
        tablayout.getTabAt(3).getOrCreateBadge().setNumber(0);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        ListDatapelangganDalamRute("0");
        countLuarRute();
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                caripelanggan.setQuery("", false);
                caripelanggan.clearFocus();

                if(position == 0){
                    ListDatapelangganDalamRute("0");
                    tablayout.setEnabled(false);
                } else if (position == 1){
                    ListDatapelangganDalamRute("1");
                    tablayout.setEnabled(false);
                } else if (position == 2){
                    ListDatapelangganDalamRute("2");
                    tablayout.setEnabled(false);
                } else {
                    ListLuarPelanggan();
                    tablayout.setEnabled(false);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        list_summary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(tablayout.getSelectedTabPosition() == 3){
                    String szName = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzName();
                    String szCustomerId = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzCustomerId();
                    String szAddress = ((data_pelanggan_luar_rute_pojo) parent.getItemAtPosition(position)).getSzAddress();

                    Intent detail = new Intent(getApplicationContext(), detail_penjualan.class);
                    detail.putExtra("szName", szName);
                    detail.putExtra("szCustomerId", szCustomerId);
                    detail.putExtra("szAddress", szAddress);
                    startActivity(detail);
                } else {
                    String szName = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzName();
                    String szCustomerId = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzCustomerId();
                    String szAddress = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzAddress();

                    Intent detail = new Intent(getApplicationContext(), detail_penjualan.class);
                    detail.putExtra("szName", szName);
                    detail.putExtra("szCustomerId", szCustomerId);
                    detail.putExtra("szAddress", szAddress);
                    startActivity(detail);
                }
            }
        });
    }

    private void countLuarRute() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Luar_Pelanggan?surat_tugas=" + mulai_perjalanan.id_pelanggan + "&szId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        try {
                            JSONObject obj = new JSONObject(response);
                            pDialog.dismissWithAnimation();
                            final JSONArray movieArray = obj.getJSONArray("data");
                            tablayout.getTabAt(3).getOrCreateBadge().setNumber(movieArray.length());


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
        requestQueue.add(stringRequest);
    }

    private void ListLuarPelanggan() {
        pDialog = new SweetAlertDialog(summary_penjualan.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        adapter.clear();
        adapter2 = new ListViewAdapterDaftarLuarPelanggan(dataPelangganLuarRutePojos, getApplicationContext());
        list_summary.setAdapter(adapter2);
        adapter2.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Luar_Pelanggan?surat_tugas=" + mulai_perjalanan.id_pelanggan + "&szId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        pDialog.dismissWithAnimation();
                        tablayout.setEnabled(true);
                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            pDialog.dismissWithAnimation();
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_pelanggan_luar_rute_pojo movieItem = new data_pelanggan_luar_rute_pojo(
                                        movieObject.getString("szCustomerId"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szAddress"),
                                        movieObject.getString("szLatitude"),
                                        movieObject.getString("szLongitude"),
                                        movieObject.getString("bStarted"),
                                        movieObject.getString("bFinisihed"),
                                        movieObject.getString("bScanBarcode"),
                                        movieObject.getString("bPostPone"));

                                dataPelangganLuarRutePojos.add(movieItem);

                                caripelanggan.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String text) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        adapter2.getFilter().filter(newText);
                                        return true;
                                    }
                                });

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
                        pDialog.dismissWithAnimation();
                        pDialog.dismissWithAnimation();
                        tablayout.setEnabled(true);
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

    private void ListDatapelangganDalamRute(String s) {
        pDialog = new SweetAlertDialog(summary_penjualan.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        adapter = new ListViewAdapterDaftarDalamPelanggan(dataPelangganDalamRutePojos, getApplicationContext());
        list_summary.setAdapter(adapter);
        adapter.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Dalam_Pelanggan?surat_tugas=" + mulai_perjalanan.id_pelanggan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        tablayout.setEnabled(true);
                        try {
                            int number = 0;
                            int number1 = 0;
                            int number2 = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_pelanggan_dalam_rute_pojo movieItem = new data_pelanggan_dalam_rute_pojo(
                                        movieObject.getString("szCustomerId"),
                                        movieObject.getString("szDocSO"),
                                        movieObject.getString("intItemNumber"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szAddress"),
                                        movieObject.getString("szLatitude"),
                                        movieObject.getString("szLongitude"),
                                        movieObject.getString("bStarted"),
                                        movieObject.getString("bFinisihed"),
                                        movieObject.getString("bScanBarcode"),
                                        movieObject.getString("bPostPone"),
                                        movieObject.getString("szRefDocId"));

                                dataPelangganDalamRutePojos.add(movieItem);

                                if(movieObject.getString("szDocSO").contains("AQ") || movieObject.getString("szDocSO").contains("VT")){
                                    dataPelangganDalamRutePojos.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                }

                                if(movieObject.getString("bPostPone").equals("0") && movieObject.getString("bsuccess").equals("0") && movieObject.getString("bFinisihed").equals("0")) {
                                    number++;
                                    tablayout.getTabAt(0).getOrCreateBadge().setNumber(number);
                                }

                                if(movieObject.getString("bPostPone").equals("1")) {
                                    number1++;
                                    tablayout.getTabAt(1).getOrCreateBadge().setNumber(number1);
                                }

                                if(movieObject.getString("bFinisihed").equals("1") && movieObject.getString("bPostPone").equals("0")) {
                                    number2++;
                                    tablayout.getTabAt(2).getOrCreateBadge().setNumber(number2);
                                }



                                if(s.equals("0")){
                                    if(movieObject.getString("bPostPone").equals("1") || movieObject.getString("bFinisihed").equals("1")) {
                                        dataPelangganDalamRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if(s.equals("1")){
                                    if(!movieObject.getString("bPostPone").equals("1")) {
                                        dataPelangganDalamRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if(s.equals("2")){
                                    if(!(movieObject.getString("bFinisihed").equals("1") && movieObject.getString("bPostPone").equals("0"))) {
                                        dataPelangganDalamRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                caripelanggan.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String text) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        adapter.getFilter().filter(newText);
                                        return true;
                                    }
                                });
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
                        pDialog.dismissWithAnimation();
                        pDialog.dismissWithAnimation();
                        tablayout.setEnabled(true);
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

    public class ListViewAdapterDaftarDalamPelanggan extends BaseAdapter implements Filterable {
        private final List<data_pelanggan_dalam_rute_pojo> dataPelangganDalamRutePojos;
        private List<data_pelanggan_dalam_rute_pojo> dataPelangganDalamRutePojosFiltered;

        private final Context context;

        public ListViewAdapterDaftarDalamPelanggan(List<data_pelanggan_dalam_rute_pojo> dataPelangganDalamRutePojos, Context context) {
            this.dataPelangganDalamRutePojos = dataPelangganDalamRutePojos;
            this.dataPelangganDalamRutePojosFiltered = dataPelangganDalamRutePojos;
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataPelangganDalamRutePojosFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return dataPelangganDalamRutePojosFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void clear() {
            dataPelangganDalamRutePojosFiltered.clear();

        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View listViewItem = getLayoutInflater().inflate(R.layout.list_rute, null, true);

            TextView namatoko = listViewItem.findViewById(R.id.namatoko);
            TextView alamat = listViewItem.findViewById(R.id.alamat);
            TextView status = listViewItem.findViewById(R.id.status);
            TextView noSo = listViewItem.findViewById(R.id.noSo);
            MaterialCardView warna = listViewItem.findViewById(R.id.warna);


            namatoko.setText(dataPelangganDalamRutePojosFiltered.get(position).getSzName());
            alamat.setText(dataPelangganDalamRutePojosFiltered.get(position).getSzAddress());
            noSo.setText(dataPelangganDalamRutePojosFiltered.get(position).getSzCustomerId());

            if(tablayout.getSelectedTabPosition() == 1){
                status.setText("Ditunda");
                warna.setCardBackgroundColor(Color.parseColor("#A2C21D"));
            } else if(tablayout.getSelectedTabPosition() == 2){
                status.setText("Selesai");
                warna.setCardBackgroundColor(Color.parseColor("#1EB547"));
            }

            return listViewItem;
        }
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if(constraint == null || constraint.length() == 0){
                        filterResults.count = dataPelangganDalamRutePojos.size();
                        filterResults.values = dataPelangganDalamRutePojos;

                    }else{
                        List<data_pelanggan_dalam_rute_pojo> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toUpperCase();

                        for(data_pelanggan_dalam_rute_pojo itemsModel:dataPelangganDalamRutePojos){
                            if(itemsModel.getSzName().contains(searchStr) || itemsModel.getSzCustomerId().contains(searchStr)){
                                resultsModel.add(itemsModel);

                            }
                            filterResults.count = resultsModel.size();
                            filterResults.values = resultsModel;
                        }


                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    dataPelangganDalamRutePojosFiltered = (List<data_pelanggan_dalam_rute_pojo>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }

    public class ListViewAdapterDaftarLuarPelanggan extends BaseAdapter implements Filterable {
        private final List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojos;
        private List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojosFiltered;

        private final Context context;

        public ListViewAdapterDaftarLuarPelanggan(List<data_pelanggan_luar_rute_pojo> dataPelangganLuarRutePojos, Context context) {
            this.dataPelangganLuarRutePojos = dataPelangganLuarRutePojos;
            this.dataPelangganLuarRutePojosFiltered = dataPelangganLuarRutePojos;
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataPelangganLuarRutePojosFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return dataPelangganLuarRutePojosFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void clear() {
            dataPelangganLuarRutePojosFiltered.clear();

        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View listViewItem = getLayoutInflater().inflate(R.layout.list_rute, null, true);

            TextView namatoko = listViewItem.findViewById(R.id.namatoko);
            TextView alamat = listViewItem.findViewById(R.id.alamat);
            TextView status = listViewItem.findViewById(R.id.status);
            TextView noSo = listViewItem.findViewById(R.id.noSo);
            MaterialCardView warna = listViewItem.findViewById(R.id.warna);

            namatoko.setText(dataPelangganLuarRutePojosFiltered.get(position).getSzName());
            alamat.setText(dataPelangganLuarRutePojosFiltered.get(position).getSzAddress());
            noSo.setText(dataPelangganLuarRutePojosFiltered.get(position).getSzCustomerId());

            if(dataPelangganLuarRutePojosFiltered.get(position).getbPostPone().equals("1")){
                status.setText("Ditunda");
                warna.setCardBackgroundColor(Color.parseColor("#A2C21D"));
            } else if(dataPelangganLuarRutePojosFiltered.get(position).getBsuccess().equals("1")){
                status.setText("Selesai");
                warna.setCardBackgroundColor(Color.parseColor("#1EB547"));
            }

            return listViewItem;
        }
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if(constraint == null || constraint.length() == 0){
                        filterResults.count = dataPelangganLuarRutePojos.size();
                        filterResults.values = dataPelangganLuarRutePojos;

                    }else{
                        List<data_pelanggan_luar_rute_pojo> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toUpperCase();

                        for(data_pelanggan_luar_rute_pojo itemsModel:dataPelangganLuarRutePojos){
                            if(itemsModel.getSzName().contains(searchStr) || itemsModel.getSzCustomerId().contains(searchStr)){
                                resultsModel.add(itemsModel);

                            }
                            filterResults.count = resultsModel.size();
                            filterResults.values = resultsModel;
                        }


                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    dataPelangganLuarRutePojosFiltered = (List<data_pelanggan_luar_rute_pojo>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}