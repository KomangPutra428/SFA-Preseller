package com.tvip.sfa.menu_mulai_perjalanan;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.tvip.sfa.R;
import com.tvip.sfa.pojo.imagePosm_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class history_posm extends AppCompatActivity {
    ListView list_HistoryPosm;
    List<imagePosm_pojo> imagePosm_pojoList = new ArrayList<>();
    ListViewAdapterFotoCek adapter;
    SweetAlertDialog pDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_history_posm);
        list_HistoryPosm = findViewById(R.id.list_HistoryPosm);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        getImagePosm();

    }

    private void getImagePosm() {
        pDialog = new SweetAlertDialog(history_posm.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Persiapan/index_Posm?szCustomerId=" +no_surat,
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
                                final imagePosm_pojo movieItem = new imagePosm_pojo(
                                        movieObject.getString("iId"),
                                        movieObject.getString("szImage"),
                                        movieObject.getString("szSurveyId"));

                                imagePosm_pojoList.add(movieItem);

                                adapter = new ListViewAdapterFotoCek(imagePosm_pojoList, getApplicationContext());
                                list_HistoryPosm.setAdapter(adapter);
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

    public class ListViewAdapterFotoCek extends ArrayAdapter<imagePosm_pojo> {

        private class ViewHolder {
            TextView produk;
            ImageView uploadgambar;
        }

        List<imagePosm_pojo> dataPosmPojoList;
        private final Context context;

        public ListViewAdapterFotoCek(List<imagePosm_pojo> dataPosmPojoList, Context context) {
            super(context, R.layout.list_uploadfoto, dataPosmPojoList);
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
            final ViewHolder viewHolder;
            imagePosm_pojo movieItem = dataPosmPojoList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_uploadfoto, parent, false);
                viewHolder.produk = convertView.findViewById(R.id.produk);
                viewHolder.uploadgambar = convertView.findViewById(R.id.uploadgambar);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.produk.setText(movieItem.getSzSurveyId());

            String base64String = "data:image/jpeg;base64," + movieItem.getSzImage();
            String base64Image = base64String.split(",")[1];
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            viewHolder.uploadgambar.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 500, 500, false));


            return convertView;
        }
    }



}