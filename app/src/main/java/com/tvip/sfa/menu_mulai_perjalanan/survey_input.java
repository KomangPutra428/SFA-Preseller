package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.tvip.sfa.Perangkat.HttpsTrustManager;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.tvip.sfa.R;
import com.tvip.sfa.pojo.data_pelanggan_dalam_rute_pojo;
import com.tvip.sfa.pojo.data_survey_header_pojo;
import com.tvip.sfa.survei.tambah_survei;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class survey_input extends AppCompatActivity {
    ListView header_survey;
    List<data_survey_header_pojo> dataSurveyHeaderPojoList =  new ArrayList<>();
    SweetAlertDialog pDialog;
    SharedPreferences sharedPreferences;
    ListViewAdapterSurveyHeader adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_survey_input);
        header_survey = findViewById(R.id.header_survey);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        getHeaderSurvey();

        header_survey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), tambah_survei.class);
                intent.putExtra("id", dataSurveyHeaderPojoList.get(position).getSzId());
                intent.putExtra("szName", dataSurveyHeaderPojoList.get(position).getSzName());
                intent.putExtra("szDescription", dataSurveyHeaderPojoList.get(position).getSzDescription());

                startActivity(intent);
            }
        });

    }

    private void getHeaderSurvey() {
        pDialog = new SweetAlertDialog(survey_input.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Header_survey?szEmployeeIdList=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        try {

                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {

                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final data_survey_header_pojo movieItem = new data_survey_header_pojo(
                                        movieObject.getString("szId"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("szDescription"));

                                dataSurveyHeaderPojoList.add(movieItem);


                                adapter = new ListViewAdapterSurveyHeader(dataSurveyHeaderPojoList, getApplicationContext());
                                header_survey.setAdapter(adapter);
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

    public class ListViewAdapterSurveyHeader extends BaseAdapter  {
        private final List<data_survey_header_pojo> dataSurveyHeaderPojoList;

        private final Context context;

        public ListViewAdapterSurveyHeader(List<data_survey_header_pojo> dataSurveyHeaderPojoList, Context context) {
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

            View listViewItem = getLayoutInflater().inflate(R.layout.list_survey_header, null, true);

            TextView title_header = listViewItem.findViewById(R.id.title_header);
            TextView Keterangan = listViewItem.findViewById(R.id.Keterangan);

            title_header.setText(dataSurveyHeaderPojoList.get(position).getSzName());
            Keterangan.setText(dataSurveyHeaderPojoList.get(position).getSzDescription());


            return listViewItem;
        }

    }
}