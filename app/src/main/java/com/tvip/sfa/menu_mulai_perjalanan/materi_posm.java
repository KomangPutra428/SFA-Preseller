package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.tvip.sfa.pojo.data_pelanggan_dalam_rute_pojo;
import com.tvip.sfa.pojo.data_posm_foto_pojo;
import com.tvip.sfa.pojo.data_posm_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class materi_posm extends AppCompatActivity {
    ListView list_materiposm;
    List<data_posm_pojo> dataPosmPojoList = new ArrayList<>();
    Button batal, lanjutkan;
    ListViewAdapterMateriPosm adapter;
    static List<data_posm_foto_pojo> dataPosmFotoPojoList = new ArrayList<>();
    private CountDownTimer countDownTimer;
    private int click_duble = 1;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_materi_posm);
        list_materiposm = findViewById(R.id.list_materiposm);
        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer == null) {
                    float Second= (float) 0.25; //Detecting the type of event within a quarter of a second
                    countDownTimer= new CountDownTimer((long) (Second * 1000), 50) {
                        @Override public void onTick(long l){}
                        @Override
                        public void onFinish() {
                            if (click_duble >= 2) {

                            } else {
                                for(int i = 0; i <= dataPosmPojoList.size() - 1;i++){
                                    if(!adapter.getItem(i).getQty().equals("0")){
                                        dataPosmFotoPojoList.add(new data_posm_foto_pojo(adapter.getItem(i).getSzId(), adapter.getItem(i).getSzName(), adapter.getItem(i).getQty(), "0", adapter.getItem(i).getSzUomId()));
                                    }

                                    if(i == dataPosmPojoList.size() -1){
                                        Intent fotomateri = new Intent(getBaseContext(), foto_materi.class);
                                        fotomateri.putExtra("POSM", "materi");
                                        startActivity(fotomateri);
                                    }
                                }
                            }
                            click_duble = 1;
                            countDownTimer = null;
                        }};countDownTimer.start();
                }else {
                    click_duble += 1;
                }


            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Product?jenis=POSM",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_posm_pojo movieItem = new data_posm_pojo(
                                        movieObject.getString("szId"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szUomId"));

                                dataPosmPojoList.add(movieItem);

                                adapter = new ListViewAdapterMateriPosm(dataPosmPojoList, getApplicationContext());
                                list_materiposm.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                int finalI = i;


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

    public class ListViewAdapterMateriPosm extends ArrayAdapter<data_posm_pojo> {

        private class ViewHolder {
            TextView produk;
            ImageButton btn_increase, btn_decrease;
            int count;
            TextView jumlahqty;

        }

        List<data_posm_pojo> data_posm_pojos;
        private final Context context;

        public ListViewAdapterMateriPosm(List<data_posm_pojo> data_posm_pojos, Context context) {
            super(context, R.layout.list_materiposm, data_posm_pojos);
            this.data_posm_pojos = data_posm_pojos;
            this.context = context;

        }

        public int getCount() {
            return data_posm_pojos.size();
        }

        public data_posm_pojo getItem(int position) {
            return data_posm_pojos.get(position);
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
            data_posm_pojo movieItem = data_posm_pojos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_materiposm, parent, false);
                viewHolder.produk = convertView.findViewById(R.id.produk);

                viewHolder.btn_decrease = convertView.findViewById(R.id.btn_decrease);
                viewHolder.btn_increase = convertView.findViewById(R.id.btn_increase);
                viewHolder.jumlahqty = convertView.findViewById(R.id.jumlahqty);


                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.produk.setText(movieItem.getSzName());
            getItem(position).setQty("0");

            viewHolder.jumlahqty.addTextChangedListener(new TextWatcher() {
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

            viewHolder.btn_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.count++;
                    viewHolder.jumlahqty.setText(String.valueOf(viewHolder.count));
                }

            });

            viewHolder.btn_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.jumlahqty.setText(String.valueOf(viewHolder.count));
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