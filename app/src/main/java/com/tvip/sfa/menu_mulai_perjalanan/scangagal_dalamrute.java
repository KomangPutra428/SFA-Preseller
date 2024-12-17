package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
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
import com.tvip.sfa.menu_utama.MainActivity;
import com.tvip.sfa.pojo.data_pelanggan_dalam_rute_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class scangagal_dalamrute extends AppCompatActivity {
    TabLayout tablayout;
    ListView list_dalamrute;
    RequestQueue requestQueue, requestQueue2;
    SharedPreferences sharedPreferences;
    List<data_pelanggan_dalam_rute_pojo> dataPelangganDalamRutePojos = new ArrayList<>();
    ListViewAdapterDaftarDalamPelanggan adapter;
    SearchView caripelanggan;
    SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_scangagal_dalamrute);
        tablayout = findViewById(R.id.tablayout);
        list_dalamrute = findViewById(R.id.list_dalamrute);
        caripelanggan = findViewById(R.id.caripelanggan);
        tablayout.getTabAt(0).getOrCreateBadge().setNumber(0);
        tablayout.getTabAt(1).getOrCreateBadge().setNumber(0);
        tablayout.getTabAt(2).getOrCreateBadge().setNumber(0);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        ListDatapelangganDalamRute("0");

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                tablayout.setEnabled(false);
                caripelanggan.setQuery("", false);
                caripelanggan.clearFocus();

                int position = tab.getPosition();
                if(position == 0){
                    ListDatapelangganDalamRute("0");
                } else if (position == 1){
                    ListDatapelangganDalamRute("1");
                } else {
                    ListDatapelangganDalamRute("2");
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        list_dalamrute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                String nama = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzName();
                String kode = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzCustomerId();
                String alamat = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzAddress();

                String langitude = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzLatitude();
                String longitude = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getSzLongitude();
                String intItemNumber = ((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getIntItemNumber();


                if(longitude.equals("")){
                    new SweetAlertDialog(scangagal_dalamrute.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Toko Tidak Ada Longlat")
                            .setConfirmText("OK")
                            .show();
                } else if (((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getbPostPone().equals("1")){
                    pDialog = new SweetAlertDialog(scangagal_dalamrute.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_latlong_after",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    updateSFADoccall(kode);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    updateSFADoccall(kode);

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

                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateandTime2 = sdf2.format(new Date());

                            params.put("szDocId", mulai_perjalanan.id_pelanggan);
                            params.put("szCustomerId", kode);
                            params.put("szLangitude", MainActivity.latitude);
                            params.put("szLongitude", MainActivity.longitude);

                            params.put("dtmStart", currentDateandTime2);




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
                    if (requestQueue == null) {
                        requestQueue = Volley.newRequestQueue(scangagal_dalamrute.this);
                        requestQueue.getCache().clear();
                        requestQueue.add(stringRequest2);
                    } else {
                        requestQueue.add(stringRequest2);
                    }

                } else if(((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getBsuccess().equals("1")){
                    new SweetAlertDialog(scangagal_dalamrute.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Toko Sudah Selesai Dikunjungi")
                            .setConfirmText("OK")
                            .show();
                } else if (((data_pelanggan_dalam_rute_pojo) parent.getItemAtPosition(position)).getbStarted().equals("1")){
                    pDialog = new SweetAlertDialog(scangagal_dalamrute.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_latlong_after",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    updateSFADoccall(kode);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    updateSFADoccall(kode);

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

                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateandTime2 = sdf2.format(new Date());

                            params.put("szDocId", mulai_perjalanan.id_pelanggan);
                            params.put("szCustomerId", kode);
                            params.put("szLangitude", MainActivity.latitude);
                            params.put("szLongitude", MainActivity.longitude);

                            params.put("dtmStart", currentDateandTime2);




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
                    if (requestQueue == null) {
                        requestQueue = Volley.newRequestQueue(scangagal_dalamrute.this);
                        requestQueue.getCache().clear();
                        requestQueue.add(stringRequest2);
                    } else {
                        requestQueue.add(stringRequest2);
                    }
                } else {
                    Intent i = new Intent(getBaseContext(), detailscangagal_dalamrute.class);

                    i.putExtra("nama", nama);
                    i.putExtra("kode", kode);
                    i.putExtra("alamat", alamat);
                    i.putExtra("langitude", langitude);
                    i.putExtra("longitude", longitude);
                    i.putExtra("intItemNumber", intItemNumber);
                    i.putExtra("jenispelanggan", "Pelanggan Dalam Rute");


                    startActivity(i);


                }






            }
        });
    }

    private void updateSFADoccall(String kode) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_DoccallItem_longlat",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Intent i = new Intent(getBaseContext(), menu_pelanggan.class);
                        i.putExtra("kode", kode);
                        startActivity(i);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent i = new Intent(getBaseContext(), menu_pelanggan.class);
                        i.putExtra("kode", kode);
                        startActivity(i);
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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("szDocId", mulai_perjalanan.id_pelanggan);
                params.put("szCustomerId", kode);
                params.put("szLangitude", MainActivity.latitude);
                params.put("szLongitude", MainActivity.longitude);

                params.put("dtmStart", currentDateandTime2);




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
        if (requestQueue2 == null) {
            requestQueue2 = Volley.newRequestQueue(scangagal_dalamrute.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        } else {
            requestQueue2.add(stringRequest2);
        }
    }

    private void ListDatapelangganDalamRute(String s) {
        pDialog = new SweetAlertDialog(scangagal_dalamrute.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        adapter = new ListViewAdapterDaftarDalamPelanggan(dataPelangganDalamRutePojos, getApplicationContext());
        list_dalamrute.setAdapter(adapter);
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
                                        movieObject.getString("bVisited"),
                                        movieObject.getString("bFinisihed"),
                                        movieObject.getString("bScanBarcode"),
                                        movieObject.getString("bPostPone"),
                                        movieObject.getString("szRefDocId"));

                                dataPelangganDalamRutePojos.add(movieItem);

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
                                    if(!movieObject.getString("bPostPone").equals("0") || !movieObject.getString("bsuccess").equals("0") || !movieObject.getString("bFinisihed").equals("0")) {
                                        dataPelangganDalamRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if(s.equals("1")){
                                    if(movieObject.getString("bPostPone").equals("0")) {
                                        dataPelangganDalamRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if(s.equals("2")){
                                    pDialog.dismissWithAnimation();
                                    if(!(movieObject.getString("bFinisihed").equals("1") && movieObject.getString("bPostPone").equals("0"))) {
                                        dataPelangganDalamRutePojos.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }

                                }
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
                        tablayout.setEnabled(false);
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
}