package com.tvip.sfa.survei;

import static com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan.no_surat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.material.card.MaterialCardView;
import com.tvip.sfa.R;
import com.tvip.sfa.menu_mulai_perjalanan.FotoSelesai;
import com.tvip.sfa.menu_mulai_perjalanan.cek_posm;
import com.tvip.sfa.menu_mulai_perjalanan.foto_materi;
import com.tvip.sfa.menu_mulai_perjalanan.history_posm;
import com.tvip.sfa.menu_mulai_perjalanan.menu_pelanggan;
import com.tvip.sfa.menu_mulai_perjalanan.mulai_perjalanan;
import com.tvip.sfa.pojo.imagePosm_pojo;
import com.tvip.sfa.pojo.survei_foto_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class upload_foto_outlet extends AppCompatActivity {
    ListView list_survey;
    Button batal, lanjutkan;

    private RequestQueue requestQueue5;
    
    static List<survei_foto_pojo> surveiFotoPojos = new ArrayList<>();

    SharedPreferences sharedPreferences;

    SweetAlertDialog pDialog;
    static ListViewAdapterSoalSurvey adapter;

    Bitmap bitmap;

    ContentValues cv;
    Uri imageUri;

    int request;

    static String header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_foto_outlet);

        list_survey = findViewById(R.id.list_survey);
        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        list_survey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, position);
                request = position;
            }
        });

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i <= surveiFotoPojos.size() - 1; i++){
                    if(adapter.getItem(i).getbMandatory().equals("1")){
                        if(adapter.getItem(i).getFoto() == null){
                            new SweetAlertDialog(upload_foto_outlet.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(adapter.getItem(i).getSzName() + " Wajib Di Foto")
                                    .setConfirmText("OK")
                                    .show();
                            break;
                        } else {
                            if(i == surveiFotoPojos.size() - 1){
                                getDoc();
                                break;
                            }
                        }
                    } else if(adapter.getItem(i).getbMandatory().equals("0")){
                        if(adapter.getItem(i).getFoto() == null){
                            adapter.getItem(i).setFoto("0");
                        } else {
                            if(i == surveiFotoPojos.size() - 1){
                                getDoc();
                                break;
                            }
                        }
                    }

                }

            }
        });
        
        getSoalPhoto();
    }

    private void getDoc() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szDocCall", null);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime2 = sdf2.format(new Date());

        pDialog = new SweetAlertDialog(upload_foto_outlet.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_All_Data?szEmployeeId=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");


                                    header = "DOC-" + String.format("%05d",jsonArray.length() + 1) + "_" +  nik_baru + "_" + currentDateandTime2;


                                    postHeader();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        header = "DOC-00001" + "_" +  nik_baru + "_" + currentDateandTime2;
                        postHeader();
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
        };
        rest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestkota = Volley.newRequestQueue(upload_foto_outlet.this);
        requestkota.getCache().clear();
        requestkota.add(rest);
    }

    private void postHeader() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_mdbaSurveyheader",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        postSurveyItem();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("szDocCall", null);
                String[] parts2 = nik_baru.split("-");
                String restnomor2 = parts2[0];

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("szDocId", header);
                params.put("szRespondenId", no_surat);
                params.put("szEmployeeId", nik_baru);
                params.put("szSurveyId", tambah_survei.id);

                params.put("dtmStart", currentDateandTime2);
                params.put("szDocCallId", mulai_perjalanan.id_pelanggan);

                if(restnomor2.equals("321") || restnomor2.equals("336") || restnomor2.equals("324") || restnomor2.equals("317") || restnomor2.equals("036")){
                    params.put("szBranchId", restnomor2);
                    params.put("szCompanyId", "ASA");
                } else {
                    params.put("szBranchId", restnomor2);
                    params.put("szCompanyId", "TVIP");
                }


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
        RequestQueue requestQueue2 = Volley.newRequestQueue(upload_foto_outlet.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    private void postSurveyItem() {
        for(int i = 0; i <= surveiFotoPojos.size() - 1; i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_mdbaSurveyItem",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if(finalI == surveiFotoPojos.size() - 1){
                                postSurveyItemDetail();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(finalI == surveiFotoPojos.size() - 1){
                        postSurveyItemDetail();
                    }
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);


                    params.put("szDocId", header);
                    params.put("intItemNumber", adapter.getItem(finalI).getIntItemNumber());
                    params.put("szQuestionId", adapter.getItem(finalI).getSzQuestionId());

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
            RequestQueue requestQueue2 = Volley.newRequestQueue(upload_foto_outlet.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void postSurveyItemDetail() {
        for(int i = 0; i <= surveiFotoPojos.size() - 1; i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_mdbaSurveyItemDetail",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if(finalI == surveiFotoPojos.size() - 1){
                                uploadGambar();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(finalI == surveiFotoPojos.size() - 1){
                        uploadGambar();
                    }
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);


                    params.put("szDocId", header);
                    params.put("intItemNumber", adapter.getItem(finalI).getIntItemNumber());
                    params.put("intItemDetailNumber", String.valueOf(finalI));
                    params.put("intQuestionItemNumber", adapter.getItem(finalI).getQuestionItem());

                    params.put("szQuestionId", adapter.getItem(finalI).getSzQuestionId());
                    params.put("szAnswerValue", "0");

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
            RequestQueue requestQueue2 = Volley.newRequestQueue(upload_foto_outlet.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void uploadGambar() {
        for(int i = 0; i <= surveiFotoPojos.size() - 1; i++) {
            int finalI = i;
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_upload_gambar_survey",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            uploadKeServer(adapter.getItem(finalI).getFoto(), adapter.getItem(finalI).getSzName());
                            if(finalI == surveiFotoPojos.size() - 1){
                                pDialog.dismissWithAnimation();
                                Intent intent = new Intent(getApplicationContext(), input_numberlist.class);
                                startActivity(intent);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(finalI == surveiFotoPojos.size() - 1){
                        pDialog.dismissWithAnimation();
                        Intent intent = new Intent(getApplicationContext(), input_numberlist.class);
                        startActivity(intent);
                    }
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
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("szDocCall", null);

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                    String currentDateandTime2 = sdf2.format(new Date());

                    String[] parts = nik_baru.split("-");
                    String restnomor = parts[0];
                    String restnomorbaru = restnomor.replace(" ", "");

                    params.put("iId", currentDateandTime2);
                    params.put("szId", mulai_perjalanan.id_pelanggan);

                    params.put("szImageType", "SURVEY");
                    params.put("intItemNumber", String.valueOf(finalI));

                    params.put("szImage", adapter.getItem(finalI).getFoto());
                    params.put("szCustomerId", menu_pelanggan.no_surat);

                    params.put("szBranchId", restnomorbaru);
                    params.put("szUserCreatedId", nik_baru);
                    params.put("szUserUpdatedId", nik_baru);

                    params.put("dtmCreated", currentDateandTime2);
                    params.put("dtmLastUpdated", currentDateandTime2);

                    params.put("szSurveyId", tambah_survei.id);
                    params.put("szQuestionId", adapter.getItem(finalI).getSzQuestionId());


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
            RequestQueue requestQueue2 = Volley.newRequestQueue(upload_foto_outlet.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(stringRequest2);
        }
    }

    private void uploadKeServer(String foto, String szName) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/mobile_eis_2/upload_sfa.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

                String gambar = foto;

                params.put("nik", header + "_" + no_surat + "_" + szName);
                params.put("foto", gambar);
                params.put("nama_folder", "foto_survey");


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

        if (requestQueue5 == null) {
            requestQueue5 = Volley.newRequestQueue(upload_foto_outlet.this);
            requestQueue5.add(stringRequest2);
        } else {
            requestQueue5.add(stringRequest2);
        }
    }

    private void getSoalPhoto() {
        pDialog = new SweetAlertDialog(upload_foto_outlet.this, SweetAlertDialog.PROGRESS_TYPE);
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

                                String szQuestionId = null;

                                JSONArray replyArr = movieObject.getJSONArray("detail");
                                for (int j = 0; j < replyArr.length(); ++j) {
                                    JSONObject childObj = replyArr.getJSONObject(j);
                                    szQuestionId = (childObj.getString("szQuestionId"));
                                }
                                final survei_foto_pojo movieItem = new survei_foto_pojo(
                                        intItemNumber,
                                        questionItem,
                                        szName,
                                        bMandatory,
                                        szAnswerType,
                                        szQuestionId);

                                surveiFotoPojos.add(movieItem);

                                adapter = new ListViewAdapterSoalSurvey(surveiFotoPojos, getApplicationContext());
                                list_survey.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if(!movieObject.getString("szAnswerType").contains("IMAGE")){
                                    surveiFotoPojos.remove(movieItem);
                                    adapter.notifyDataSetChanged();
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
            TextView produk;
            ImageView uploadgambar;
        }

        List<survei_foto_pojo> dataPosmPojoList;
        private final Context context;

        public ListViewAdapterSoalSurvey(List<survei_foto_pojo> dataPosmPojoList, Context context) {
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
            survei_foto_pojo movieItem = dataPosmPojoList.get(position);
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

            viewHolder.produk.setText(movieItem.getSzName());
            if(movieItem.getFoto() == null){

            } else if(!movieItem.getFoto().equals("0")){
                viewHolder.uploadgambar.setImageBitmap(StringToBitMap(movieItem.getFoto()));
            }

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        adapter.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
            int width=720;
            int height=720;
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            adapter.getItem(request).setFoto(imagetoString(bitmap));
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imagetoString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}