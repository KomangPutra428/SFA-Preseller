package com.tvip.sfa.survei;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_mulai_perjalanan.Utility;
import com.tvip.sfa.menu_mulai_perjalanan.info_pelanggan;
import com.tvip.sfa.pojo.answer_pojo;
import com.tvip.sfa.pojo.survei_foto_pojo;
import com.tvip.sfa.pojo.trend_produk_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class detail_survey extends AppCompatActivity {

    TextView waktu, kode_pelanggan, alamat, namatoko;

    ListView detail;

    List<survei_foto_pojo> surveiFotoPojos = new ArrayList<>();

    SharedPreferences sharedPreferences;

    SweetAlertDialog pDialog;
    ListViewAdapterSoalSurvey adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_survey);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        waktu = findViewById(R.id.waktu);
        kode_pelanggan = findViewById(R.id.kode_pelanggan);
        alamat = findViewById(R.id.alamat);
        namatoko = findViewById(R.id.namatoko);
        detail = findViewById(R.id.listdetail);

        waktu.setText(getIntent().getStringExtra("dtmDoc"));
        kode_pelanggan.setText(no_surat);
        alamat.setText(getIntent().getStringExtra("szAddress"));
        namatoko.setText(getIntent().getStringExtra("szName"));

        getDetailPhoto();
    }

    private void getDetailPhoto() {
        pDialog = new SweetAlertDialog(detail_survey.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Item_survey?szId=" + tambah_survei.id,
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

                                String szName = movieObject.getString("szName");
                                String bMandatory = movieObject.getString("bMandatory");
                                String szAnswerType = movieObject.getString("szAnswerType");

                                String intItemNumber = movieObject.getString("intItemNumber");
                                String questionItem = movieObject.getString("questionItem");
                                String szQuestionId = movieObject.getString("szQuestionId");

                                final survei_foto_pojo movieItem = new survei_foto_pojo(
                                        intItemNumber,
                                        questionItem,
                                        szName,
                                        bMandatory,
                                        szAnswerType,
                                        szQuestionId);

                                surveiFotoPojos.add(movieItem);

                                adapter = new ListViewAdapterSoalSurvey(surveiFotoPojos, getApplicationContext());
                                detail.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

//                                if(!movieObject.getString("szAnswerType").contains("IMAGE")){
//                                    surveiFotoPojos.remove(movieItem);
//                                    adapter.notifyDataSetChanged();
//                                }

                                Utility.setListViewHeightBasedOnChildren(detail);



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

    public class ListViewAdapterSoalSurvey extends ArrayAdapter<survei_foto_pojo> {

        private class ViewHolder {
            TextView produk, textupload, stockdetail;
            ImageView uploadgambar;
            LinearLayout detail;
            MaterialCardView linearfoto;
            List<answer_pojo> answer_pojos;
            ListView listdetail;
            ListViewAdapterJawabanSurvey adapter;

        }

        List<survei_foto_pojo> dataPosmPojoList;
        private final Context context;

        public ListViewAdapterSoalSurvey(List<survei_foto_pojo> dataPosmPojoList, Context context) {
            super(context, R.layout.list_detail_survey, dataPosmPojoList);
            this.dataPosmPojoList = dataPosmPojoList;
            this.context = context;

        }

        public int getCount() {
            return dataPosmPojoList.size();
        }

        @Override
        public int getViewTypeCount() {
            int count;
            if (dataPosmPojoList.size() > 0) {
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
            final ListViewAdapterSoalSurvey.ViewHolder viewHolder;
            survei_foto_pojo movieItem = dataPosmPojoList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_detail_survey, parent, false);
                viewHolder.produk = convertView.findViewById(R.id.produk);
                viewHolder.uploadgambar = convertView.findViewById(R.id.uploadgambar);
                viewHolder.textupload = convertView.findViewById(R.id.textupload);
                viewHolder.stockdetail = convertView.findViewById(R.id.stockdetail);
                viewHolder.detail = convertView.findViewById(R.id.detail);
                viewHolder.listdetail = convertView.findViewById(R.id.listdetail);
                viewHolder.linearfoto = convertView.findViewById(R.id.linearfoto);



                viewHolder.answer_pojos = new ArrayList<>();

                viewHolder.adapter = new ListViewAdapterJawabanSurvey(viewHolder.answer_pojos, getApplicationContext());


                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ListViewAdapterSoalSurvey.ViewHolder) convertView.getTag();
            }

            if(!movieItem.getSzAnswerType().contains("IMAGE")){
                viewHolder.linearfoto.setVisibility(GONE);
            } else if(movieItem.getSzAnswerType().contains("IMAGE")){
                viewHolder.detail.setVisibility(GONE);
            }

            viewHolder.textupload.setText("Gambar Kosong");

            viewHolder.stockdetail.setText("Stock " + movieItem.getSzName());
            viewHolder.stockdetail.setVisibility(GONE);

            System.out.println("URL = " + "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Answer?szDocId="+getIntent().getStringExtra("szDocId")+"&szQuestionId=" + movieItem.getSzQuestionId());


            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_List_Answer?szDocId="+getIntent().getStringExtra("szDocId")+"&szQuestionId=" + movieItem.getSzQuestionId(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            viewHolder.adapter.clear();
                            try {
                                JSONObject obj = new JSONObject(response);
                                final JSONArray movieArray = obj.getJSONArray("data");

                                for (int i = 0; i < movieArray.length(); i++) {
                                    final JSONObject movieObject = movieArray.getJSONObject(i);
                                    final answer_pojo movieItem = new answer_pojo(
                                            movieObject.getString("szAnswerText"),
                                            movieObject.getString("szAnswerValue"));
                                    viewHolder.answer_pojos.add(movieItem);

                                    viewHolder.listdetail.setAdapter(viewHolder.adapter);
                                    Utility.setListViewHeightBasedOnChildren(viewHolder.listdetail);
                                    viewHolder.adapter.notifyDataSetChanged();


                                }
                            } catch(JSONException e){
                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            viewHolder.detail.setVisibility(View.GONE);
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
            RequestQueue requestQueue = Volley.newRequestQueue(detail_survey.this);
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

            String mURL = "https://apisec.tvip.co.id/image_sfa_apps/foto_survey/"+getIntent().getStringExtra("szDocId")+"_"+no_surat+"_"+movieItem.getSzName()+".jpeg";

            String creds = String.format("%s:%s", "admin", "Databa53");
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
            GlideUrl glideUrl = new GlideUrl(mURL,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", auth)
                            .build());

            Glide.with(detail_survey.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .error(R.drawable.not_found)
                    .into(viewHolder.uploadgambar);


            viewHolder.produk.setText(movieItem.getSzName());


            return convertView;
        }
    }

    public class ListViewAdapterJawabanSurvey extends ArrayAdapter<answer_pojo> {

        private class ViewHolder {
            TextView soal, jawaban;

        }

        List<answer_pojo> answer_pojos;
        private final Context context;

        public ListViewAdapterJawabanSurvey(List<answer_pojo> answer_pojos, Context context) {
            super(context, R.layout.list_answer_survey, answer_pojos);
            this.answer_pojos = answer_pojos;
            this.context = context;

        }

        public int getCount() {
            return answer_pojos.size();
        }

        public answer_pojo getItem(int position) {
            return answer_pojos.get(position);
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
            answer_pojo movieItem = answer_pojos.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_answer_survey, parent, false);
                viewHolder.soal = convertView.findViewById(R.id.soal);
                viewHolder.jawaban = convertView.findViewById(R.id.jawaban);


                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.soal.setText(movieItem.getSzAnswerText());
            viewHolder.jawaban.setText(movieItem.getSzAnswerValue() + " Item");

            if(movieItem.getSzAnswerValue().equals("0")){
                viewHolder.jawaban.setBackgroundResource(R.drawable.outline_shape_red);
                viewHolder.jawaban.setTextColor(Color.parseColor("#FB4141"));
            } else {
                viewHolder.jawaban.setBackgroundResource(R.drawable.outline_shape_green);
                viewHolder.jawaban.setTextColor(Color.parseColor("#2ECC71"));
            }

            return convertView;
        }
    }
}