package com.tvip.sfa.menu_persiapan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.tvip.sfa.R;
import com.tvip.sfa.pojo.data_pelanggan_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.View.GONE;
import static com.tvip.sfa.menu_persiapan.callplan.szDocId;
import static com.tvip.sfa.survei.ExpandableListDataPump.context;

public class daftar_kunjungan extends AppCompatActivity implements OnMapReadyCallback, OnMapsSdkInitializedCallback {

    ListView listdaftarkunjungan;
    List<data_pelanggan_pojo> dataPelangganPojos = new ArrayList<>();
    List<data_pelanggan_pojo> dataPelangganPojos_2 = new ArrayList<>();
    SharedPreferences sharedPreferences;

    ListViewAdapterDaftarKunjungan adapter;

    TabLayout tablayout;
    private GoogleMap mMap;
    RelativeLayout linearmap;
    TextView outlet, nogeotag;
    BottomSheetBehavior sheetBehavior;
    BottomSheetDialog sheetDialog;
    View bottom_sheet;
    SearchView caripelanggan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_daftar_kunjungan);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, this);



        listdaftarkunjungan = findViewById(R.id.listdaftarkunjungan);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        caripelanggan = findViewById(R.id.caripelanggan);



        tablayout = findViewById(R.id.tablayout);
        listdaftarkunjungan = findViewById(R.id.listdaftarkunjungan);
        linearmap = findViewById(R.id.linearmap);
        outlet = findViewById(R.id.outlet);
        nogeotag = findViewById(R.id.nogeotag);


        listdaftarkunjungan.setAdapter(adapter);

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position == 0){
                    listdaftarkunjungan.setVisibility(View.VISIBLE);
                    caripelanggan.setVisibility(View.VISIBLE);
                    linearmap.setVisibility(GONE);
                } else {
                    listdaftarkunjungan.setVisibility(GONE);
                    caripelanggan.setVisibility(View.GONE);
                    linearmap.setVisibility(View.VISIBLE);

                }

            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    listdaftarkunjungan.setVisibility(View.VISIBLE);
                    caripelanggan.setVisibility(View.VISIBLE);
                    linearmap.setVisibility(GONE);
                } else {
                    listdaftarkunjungan.setVisibility(GONE);
                    caripelanggan.setVisibility(GONE);
                    linearmap.setVisibility(View.VISIBLE);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.listmap);
        mapFragment.getMapAsync(this);



        listpelanggan();

        listdaftarkunjungan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                String langitude = ((data_pelanggan_pojo) parent.getItemAtPosition(position)).getSzLangitude();
                String longitude = ((data_pelanggan_pojo) parent.getItemAtPosition(position)).getSzLongitude();
                String namatoko = ((data_pelanggan_pojo) parent.getItemAtPosition(position)).getSzName();
                String alamat = ((data_pelanggan_pojo) parent.getItemAtPosition(position)).getSzAddress();
                String szId = ((data_pelanggan_pojo) parent.getItemAtPosition(position)).getSzId();



                if(langitude.length() == 0){
                    new SweetAlertDialog(daftar_kunjungan.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Toko Ini Tidak Ada GeoTag")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                } else {
                    Intent i = new Intent(getBaseContext(), map_kunjungan.class);
                    i.putExtra("langitude", langitude);
                    i.putExtra("longitude", longitude);
                    i.putExtra("namatoko", namatoko);
                    i.putExtra("szId", szId);
                    i.putExtra("address", alamat);

                    startActivity(i);
                }



            }
        });

    }

    private void listpelanggan() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_List_Pelanggan?surat_tugas=" + szDocId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_pelanggan_pojo movieItem = new data_pelanggan_pojo(
                                        movieObject.getString("szId"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szAddress"),
                                        movieObject.getString("szLatitude"),
                                        movieObject.getString("szLongitude"));

                                tablayout.getTabAt(0).setText(movieArray.length() + " Pelanggan");
                                outlet.setText(String.valueOf(movieArray.length()));

                                if (movieObject.getString("szLatitude").equals(""))
                                    number++;  {
                                    nogeotag.setText(String.valueOf(number));
                                }
                                dataPelangganPojos.add(movieItem);

                            }
                            adapter = new ListViewAdapterDaftarKunjungan(dataPelangganPojos, getApplicationContext());
                            listdaftarkunjungan.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

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

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {

    }

    public class ListViewAdapterDaftarKunjungan extends BaseAdapter implements Filterable {
        private final List<data_pelanggan_pojo>data_pelanggan_pojos;
        private List<data_pelanggan_pojo> data_pelanggan_pojoFiltered;
        private final Context context;

        public ListViewAdapterDaftarKunjungan(List<data_pelanggan_pojo> data_pelanggan_pojos, Context context) {
            this.data_pelanggan_pojos = data_pelanggan_pojos;
            this.data_pelanggan_pojoFiltered = data_pelanggan_pojos;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data_pelanggan_pojoFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return data_pelanggan_pojoFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View listViewItem = getLayoutInflater().inflate(R.layout.list_daftarkunjungan, null, true);

            TextView namatoko = listViewItem.findViewById(R.id.namatoko);
            TextView alamat = listViewItem.findViewById(R.id.alamat);
            TextView noSO = listViewItem.findViewById(R.id.noSO);



            namatoko.setText(data_pelanggan_pojoFiltered.get(position).getSzName());
            alamat.setText(data_pelanggan_pojoFiltered.get(position).getSzAddress());
            noSO.setText(data_pelanggan_pojoFiltered.get(position).getSzId());

            return listViewItem;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if(constraint == null || constraint.length() == 0){
                        filterResults.count = data_pelanggan_pojos.size();
                        filterResults.values = data_pelanggan_pojos;

                    }else{
                        List<data_pelanggan_pojo> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toUpperCase();

                        for(data_pelanggan_pojo itemsModel:data_pelanggan_pojos){
                            if(itemsModel.getSzId().contains(searchStr) || itemsModel.getSzName().contains(searchStr)){
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

                    data_pelanggan_pojoFiltered = (List<data_pelanggan_pojo>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_List_Pelanggan?surat_tugas=" + szDocId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_pelanggan_pojo movieItem = new data_pelanggan_pojo(
                                        movieObject.getString("szId"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szAddress"),
                                        movieObject.getString("szLatitude"),
                                        movieObject.getString("szLongitude"));


                                dataPelangganPojos_2.add(movieItem);

                                if(!dataPelangganPojos_2.get(i).getSzLangitude().isEmpty()){
                                    Double langitude = Double.valueOf(dataPelangganPojos_2.get(i).getSzLangitude());
                                    Double longitude = Double.valueOf(dataPelangganPojos_2.get(i).getSzLongitude());



                                    LatLng zoom = new LatLng(langitude, longitude);

                                    mMap.addMarker(new MarkerOptions().position(new LatLng(langitude,longitude)).title(dataPelangganPojos_2.get(i).getSzId() + " (" + dataPelangganPojos_2.get(i).getSzName() + ")"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(zoom));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                                    int finalI = i;
                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            String szId = marker.getTitle();
                                            String[] parts = szId.split(" ");
                                            String szIdSlice = parts[0];
                                            showBottomSheetDialog(szIdSlice);
                                        }
                                    });
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

    private void showBottomSheetDialog(String szIdSlice) {
        View view = getLayoutInflater().inflate(R.layout.keterangan_pelanggan, null);

        TextView namatoko = view.findViewById(R.id.namatoko);
        TextView kode = view.findViewById(R.id.kode);
        TextView alamat = view.findViewById(R.id.alamat);
        TextView term_payment = view.findViewById(R.id.term_payment);
        TextView limit_kredit = view.findViewById(R.id.limit_kredit);
        TextView piutang = view.findViewById(R.id.piutang);
        TextView sisa_limit_kredit = view.findViewById(R.id.sisa_limit_kredit);
        TextView channel = view.findViewById(R.id.channel);
        TextView status = view.findViewById(R.id.status);
        Button direction = view.findViewById(R.id.direction);

        kode.setText(szIdSlice);

        StringRequest channel_status = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Detail_Pelanggan?szId=" + szIdSlice,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject biodatas = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                biodatas = movieArray.getJSONObject(i);
                                namatoko.setText(biodatas.getString("szName"));
                                alamat.setText(biodatas.getString("szAddress"));

                                term_payment.setText(biodatas.getString("szPaymetTermId"));
                                limit_kredit.setText(biodatas.getString("decCreditLimit"));

                                channel.setText(biodatas.getString("Nama_Channel"));
                                status.setText(biodatas.getString("szStatus"));

                                Double langitude = Double.valueOf(biodatas.getString("szLatitude"));
                                Double longitude = Double.valueOf(biodatas.getString("szLongitude"));

                                direction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse("https://maps.google.com?q="+langitude+","+longitude));
                                        startActivity(i);
                                    }
                                });



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
        channel_statusQueue.getCache().clear();
        channel_statusQueue.add(channel_status);



        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        sheetDialog.show();
        sheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sheetDialog = null;
            }
        });
    }
}