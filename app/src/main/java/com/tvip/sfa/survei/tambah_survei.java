package com.tvip.sfa.survei;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
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
import com.tvip.sfa.menu_mulai_perjalanan.FotoSelesai;
import com.tvip.sfa.pojo.data_list_survey_pojo;

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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class tambah_survei extends AppCompatActivity {
    Button add;
    TextView topik;
    static TextView Keterangan_topik;
    static String id, name;

    ListView list_history;
    List<data_list_survey_pojo> dataListSurveyPojos =  new ArrayList<>();

    LinearLayout gagal;

    ListViewAdapterSurvey adapter;

    SweetAlertDialog pDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_survei);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        gagal = findViewById(R.id.gagal);
        topik = findViewById(R.id.topik);
        Keterangan_topik = findViewById(R.id.Keterangan_topik);
        list_history = findViewById(R.id.list_history);

        id = getIntent().getStringExtra("id");
        topik.setText(getIntent().getStringExtra("szName"));

        name = getIntent().getStringExtra("szName");
        Keterangan_topik.setText(getIntent().getStringExtra("szDescription"));

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), upload_foto_outlet.class);
                startActivity(intent);
//                pDialog = new SweetAlertDialog(tambah_survei.this, SweetAlertDialog.PROGRESS_TYPE);
//                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                pDialog.setTitleText("Harap Menunggu");
//                pDialog.setCancelable(false);
//                pDialog.show();
//
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Bulan?szRespondenId=" + no_surat,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                pDialog.dismissWithAnimation();
//                                new SweetAlertDialog(tambah_survei.this, SweetAlertDialog.WARNING_TYPE)
//                                        .setTitleText("Data sudah di input, silahkan input di bulan berikutnya")
//                                        .setConfirmText("OK")
//                                        .show();
//
//
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                pDialog.dismissWithAnimation();

//
//                            }
//                        })
//
//                {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        String creds = String.format("%s:%s", "admin", "Databa53");
//                        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                        params.put("Authorization", auth);
//                        return params;
//                    }
//                };
//
//                stringRequest.setRetryPolicy(
//                        new DefaultRetryPolicy(
//                                5000,
//                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                        ));
//                RequestQueue requestQueue = Volley.newRequestQueue(tambah_survei.this);
//                requestQueue.getCache().clear();
//                requestQueue.add(stringRequest);


            }
        });

        list_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), detail_survey.class);
                intent.putExtra("szDocId", dataListSurveyPojos.get(position).getSzDocId());
                intent.putExtra("dtmDoc", dataListSurveyPojos.get(position).getDtmDoc());

                intent.putExtra("szName", dataListSurveyPojos.get(position).getSzName());
                intent.putExtra("szAddress", dataListSurveyPojos.get(position).getSzAddress());

                startActivity(intent);
            }
        });

        getListSurvey();
    }

    private void getListSurvey() {
        pDialog = new SweetAlertDialog(tambah_survei.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Survey?szSurveyId="+id+"&szRespondenId=" + no_surat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        list_history.setVisibility(View.VISIBLE);
                        gagal.setVisibility(View.GONE);

                        try {

                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {

                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_list_survey_pojo movieItem = new data_list_survey_pojo(
                                        movieObject.getString("szDocId"),
                                        convertFormat(movieObject.getString("dtmDoc")),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szAddress"));

                                dataListSurveyPojos.add(movieItem);


                                adapter = new ListViewAdapterSurvey(dataListSurveyPojos, getApplicationContext());
                                list_history.setAdapter(adapter);
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
                        list_history.setVisibility(View.GONE);
                        gagal.setVisibility(View.VISIBLE);
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

    public class ListViewAdapterSurvey extends BaseAdapter {
        private final List<data_list_survey_pojo> dataSurveyHeaderPojoList;

        private final Context context;

        public ListViewAdapterSurvey(List<data_list_survey_pojo> dataSurveyHeaderPojoList, Context context) {
            this.dataSurveyHeaderPojoList = dataSurveyHeaderPojoList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataSurveyHeaderPojoList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataSurveyHeaderPojoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View listViewItem = getLayoutInflater().inflate(R.layout.list_survey, null, true);

            TextView date = listViewItem.findViewById(R.id.date);

            date.setText(dataSurveyHeaderPojoList.get(position).getDtmDoc());


            return listViewItem;
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

        SimpleDateFormat convetDateFormat = new SimpleDateFormat("HH:mm:ss • dd MMMM yyyy");
        return convetDateFormat.format(date);
    }

}