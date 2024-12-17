package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.tvip.sfa.pojo.data_product_potensial_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class produk_potensial extends AppCompatActivity {
    ListView products;
    List<data_product_potensial_pojo> dataUtama = new ArrayList<>();
    static List<data_product_potensial_pojo> dataDraft = new ArrayList<>();
    SweetAlertDialog pDialog;
    ListViewAdapteProductPotensial adapter;
    SharedPreferences sharedPreferences;
    Button batal, selanjutnya;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_produk_potensial);
        products = findViewById(R.id.products);

        batal = findViewById(R.id.batal);
        selanjutnya = findViewById(R.id.selanjutnya);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), produk_potensial_draft.class);
                startActivity(intent);

                for(int i = 0; i < dataUtama.size(); i++){
                    if (!adapter.getItem(i).getQty().equals("0")){
                        dataDraft.add(new data_product_potensial_pojo(
                                adapter.getItem(i).getSzId(),
                                adapter.getItem(i).getSzName(),
                                adapter.getItem(i).getQty()));
                    }

                }

            }
        });

        getProductPotensial();

    }

    private void getProductPotensial() {
        pDialog = new SweetAlertDialog(produk_potensial.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);
        String[] parts = nik_baru.split("-");
        String restnomor = parts[0];
        String restnomorbaru = restnomor.replace(" ", "");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Product_Penjualan?depo=" + restnomorbaru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_product_potensial_pojo movieItem = new data_product_potensial_pojo(
                                        movieObject.getString("szId"),
                                        movieObject.getString("szName"),
                                        "0");

                                dataUtama.add(movieItem);

                                adapter = new ListViewAdapteProductPotensial(dataUtama, getApplicationContext());
                                products.setAdapter(adapter);


                            }


                            pDialog.dismissWithAnimation();

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

    public class ListViewAdapteProductPotensial extends ArrayAdapter<data_product_potensial_pojo> {

        private class ViewHolder {
            TextView namaproduk;
            ImageButton refresh, qtyorderminus, qtyorderadd;
            EditText qtyorder;
            int count;
        }

        List<data_product_potensial_pojo> dataProductPotensialPojos;
        private final Context context;

        public ListViewAdapteProductPotensial(List<data_product_potensial_pojo> dataProductPotensialPojos, Context context) {
            super(context, R.layout.list_productpotensial, dataProductPotensialPojos);
            this.dataProductPotensialPojos = dataProductPotensialPojos;
            this.context = context;

        }

        public int getCount() {
            return dataProductPotensialPojos.size();
        }

        public data_product_potensial_pojo getItem(int position) {
            return dataProductPotensialPojos.get(position);
        }

        @Override
        public int getViewTypeCount() {
            int count;
            if (dataProductPotensialPojos.size() > 0) {
                count = getCount();
            } else {
                count = 1;
            }
            return count;
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
            data_product_potensial_pojo data = dataProductPotensialPojos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_productpotensial, parent, false);


                viewHolder.namaproduk = convertView.findViewById(R.id.namaproduk);

                viewHolder.refresh = convertView.findViewById(R.id.refresh);
                viewHolder.qtyorderminus = convertView.findViewById(R.id.qtyorderminus);
                viewHolder.qtyorderadd = convertView.findViewById(R.id.qtyorderadd);

                viewHolder.qtyorder = convertView.findViewById(R.id.qtyorder);
                viewHolder.qtyorder.setText("0");
                getItem(position).setQty("0");
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.namaproduk.setText(data.getSzName());


            viewHolder.refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItem(position).setQty("0");
                    viewHolder.count = 0;
                    viewHolder.qtyorder.setText(String.valueOf(viewHolder.count));
                }
            });

            viewHolder.qtyorderadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.count++;
                    viewHolder.qtyorder.setText(String.valueOf(viewHolder.count));
                }
            });

            viewHolder.qtyorder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    getItem(position).setQty(s.toString());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    getItem(position).setQty(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    getItem(position).setQty(s.toString());
                }
            });

            viewHolder.qtyorderminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.qtyorder.setText(String.valueOf(viewHolder.count));
                    if (viewHolder.count == 0) {
                        return;
                    }
                    viewHolder.count--;
                }
            });

            return convertView;
        }
    }
}