package com.tvip.sfa.survei;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.sfa.pojo.survei_foto_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListDataPump {
    public static Context context;



//    public static HashMap<String, List<String>> getData() {
////        context = this;
//        List<survei_foto_pojo> surveiFotoPojos = new ArrayList<>();
//        List<String> detaildata = new ArrayList<String>();
//        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
//
//
//        return expandableListDetail;StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Item_survey?szId=" + tambah_survei.id,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            int number = 0;
//                            JSONObject obj = new JSONObject(response);
//                            final JSONArray movieArray = obj.getJSONArray("data");
//                            for (int i = 0; i < movieArray.length(); i++) {
//                                final JSONObject movieObject = movieArray.getJSONObject(i);
//                                final survei_foto_pojo movieItem = new survei_foto_pojo(
//                                        movieObject.getString("szName"),
//                                        movieObject.getString("bMandatory"),
//                                        movieObject.getString("szAnswerType"));
//
//                                surveiFotoPojos.add(movieItem);
//
//
//                                JSONArray files = movieObject.getJSONArray("detail");
//                                if(files != null && files.length() > 0 ) {
//                                    for(int j=0 ; j<files.length() ; j++)
//                                    {
//                                        JSONObject Jsonfilename = files.getJSONObject(j);
//                                        String filename = Jsonfilename.getString("szQuestionId");
//                                        detaildata.add(filename);
//                                    }
//                                }
////                                    expandableListDetail.put(movieObject.getString("szName"), detaildata);
////                                    adapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
////                                    expandableListViewSample.setAdapter(adapter);
////                                    expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
//
//
////                                    if (movieObject.getString("szAnswerType").contains("IMAGE")) {
////                                        surveiFotoPojos.remove(movieItem);
////                                        adapter.notifyDataSetChanged();
////                                    }
//
//                                expandableListDetail.put(movieObject.getString("szName"), detaildata);
//
//
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s", "admin", "Databa53");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//
//        stringRequest.setRetryPolicy(
//                new DefaultRetryPolicy(
//                        5000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                ));
//
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
//        requestQueue.add(stringRequest);
//    }
}
