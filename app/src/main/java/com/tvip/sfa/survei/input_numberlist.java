package com.tvip.sfa.survei;

import static com.tvip.sfa.survei.upload_foto_outlet.header;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.inputmethod.EditorInfo;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class input_numberlist extends AppCompatActivity {
    List<Group> list;
    ExpandableListView listView;
    Button lanjutkan;
    SharedPreferences sharedPreferences;
    ExpAdapter adapter;
    SweetAlertDialog success, pDialog;

    int survey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_numberlist);
        listView = findViewById(R.id.expandableListViewSample);
        lanjutkan = findViewById(R.id.lanjutkan);

        survey = upload_foto_outlet.surveiFotoPojos.size() - 1;

        listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        list = getList();

        listView.setItemsCanFocus(true);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        survey = upload_foto_outlet.surveiFotoPojos.size() - 1;

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                outerloop:
                for(int i = 0; i <= list.size() - 1; i++) {
                    int finalI = i;
                    for (int z = 0; z <= list.get(i).getChildList().size()-1; z++) {
                        int finalZ = z;
                        if(list.get(finalI).getChildList().get(finalZ).getAnswer() == null){
                            new SweetAlertDialog(input_numberlist.this, SweetAlertDialog.WARNING_TYPE)
                                    .setContentText("Data Kurang Lengkap, Silahkan Dilengkapi")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                            break outerloop;
                        } else if(finalI == list.size() - 1 && finalZ == list.get(i).getChildList().size()-1){
                            postSurveyItem();
                        }
                    }
                }
            }
        });

    }

    private void postSurveyItem() {
        pDialog = new SweetAlertDialog(input_numberlist.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        for(int i = 0; i <= list.size() -1; i++) {
            int finalI = i;

                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_mdbaSurveyItem",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                if(finalI == list.size() - 1){
                                    postSurveyItemDetail();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(finalI == list.size() - 1){
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
                        params.put("intItemNumber", list.get(finalI).intItemNumber);
                        params.put("szQuestionId", list.get(finalI).getChildList().get(0).comment);

                        System.out.println("Params = " + params);

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
                RequestQueue requestQueue2 = Volley.newRequestQueue(input_numberlist.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);
            }

    }

    private void postSurveyItemDetail() {
        for(int i = 0; i <= list.size() - 1; i++) {
            int finalI = i;
            for (int z = 0; z <= list.get(i).getChildList().size() -1; z++) {
                int finalZ = z;
                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_mdbaSurveyItemDetail",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                if(finalI == list.size() - 1 && finalZ == list.get(finalI).getChildList().size() -1){
                                    pDialog.dismissWithAnimation();
                                    success = new SweetAlertDialog(input_numberlist.this, SweetAlertDialog.SUCCESS_TYPE);
                                    success.setContentText("Data Sudah Disimpan");
                                    success.setCancelable(false);
                                    success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();


                                            Intent intent = new Intent(getBaseContext(), tambah_survei.class);
                                            intent.putExtra("id", tambah_survei.id);
                                            intent.putExtra("szName", tambah_survei.name);
                                            intent.putExtra("szDescription", tambah_survei.Keterangan_topik.getText().toString());

                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            upload_foto_outlet.adapter.clear();
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    success.show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(finalI == list.size() - 1 && finalZ == list.get(finalI).getChildList().size() -1){
                            pDialog.dismissWithAnimation();
                            success = new SweetAlertDialog(input_numberlist.this, SweetAlertDialog.SUCCESS_TYPE);
                            success.setContentText("Data Sudah Disimpan");
                            success.setCancelable(false);
                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();


                                    Intent intent = new Intent(getBaseContext(), tambah_survei.class);
                                    intent.putExtra("id", tambah_survei.id);
                                    intent.putExtra("szName", tambah_survei.name);
                                    intent.putExtra("szDescription", tambah_survei.Keterangan_topik.getText().toString());

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    upload_foto_outlet.adapter.clear();
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            success.show();
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

                        int angka = survey++;

                        params.put("szDocId", header);
                        params.put("intItemNumber", list.get(finalI).getChildList().get(finalZ).surveyItem);
                        params.put("intItemDetailNumber", String.valueOf(angka));

//                        params.put("intItemDetailNumber", String.valueOf((upload_foto_outlet.surveiFotoPojos.size() - 1) + (finalZ + finalI)));

                        params.put("szQuestionId", list.get(finalI).getChildList().get(finalZ).comment);
                        params.put("intQuestionItemNumber", list.get(finalI).getChildList().get(finalZ).questionItem);

                        params.put("szAnswerValue", list.get(finalI).getChildList().get(finalZ).getAnswer());

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
                RequestQueue requestQueue2 = Volley.newRequestQueue(input_numberlist.this);
                requestQueue2.getCache().clear();
                requestQueue2.add(stringRequest2);

            }
        }
    }

    public class Group {
        String questionItem;
        String intItemNumber;
        String name;
        private List<Child> childList;

        void setChildList(List<Child> list) {
            childList = list;
        }
        List<Child> getChildList() {
            return childList;
        }
    }

    public class Child {
        String surveyItem;
        String questionItem;
        String name;
        String comment;
        String answer;

        public String getSurveyItem() {return surveyItem;}

        public String getQuestionItem() {return questionItem;}

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    private ArrayList<Group> getList() {
        ArrayList<Group> list = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/" + sharedPreferences.getString("link", null) + "/utilitas/Mulai_Perjalanan/index_Item_survey?szId=" + tambah_survei.id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray groupArr;
                        ArrayList<Child> childList;
                        try {
                            JSONObject obj = new JSONObject(response);
                            groupArr = obj.getJSONArray("data");

                            for (int i = 0; i < groupArr.length(); ++i) {
                                JSONObject groupObj= groupArr.getJSONObject(i);
                                Group group = new Group();
                                group.name = groupObj.getString("szName");
                                group.intItemNumber = groupObj.getString("intItemNumber");
                                group.questionItem = groupObj.getString("questionItem");

                                childList = new ArrayList<>();
                                JSONArray replyArr = groupObj.getJSONArray("detail");
                                for (int j = 0; j < replyArr.length(); ++j) {
                                    JSONObject childObj = replyArr.getJSONObject(j);
                                    Child child = new Child();
                                    child.name = (childObj.getString("szAnswerText"));
                                    child.comment = (childObj.getString("szQuestionId"));
                                    child.questionItem = (childObj.getString("intItemNumber"));
                                    child.surveyItem = (childObj.getString("szId"));

                                    childList.add(child);
                                }

                                group.setChildList(childList);


                                list.add(group);

                                if(groupObj.getString("szAnswerType").contains("IMAGE")){
                                    list.remove(group);
                                }

                                adapter = new ExpAdapter(input_numberlist.this, list);
                                listView.setAdapter(adapter);


                            }
//                            for (int i = 0; i < listView.getExpandableListAdapter().getGroupCount(); i++) {
//                                listView.expandGroup(i);
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(input_numberlist.this, "error", Toast.LENGTH_SHORT).show();
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

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));

        RequestQueue requestQueue = Volley.newRequestQueue(input_numberlist.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);



        return list;
    }



    public class ExpAdapter extends BaseExpandableListAdapter implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        private final class ViewHolder {
            TextView textLabel, listTitle;
            EditText qty_stock;
        }

        private final List<Group> itemList;
        private final LayoutInflater inflater;

        private ExpAdapter(Context context, List<Group> itemList) {
            this.inflater = LayoutInflater.from(context);
            this.itemList = itemList;
        }

        @Override
        public Child getChild(int groupPosition, int childPosition) {

            return itemList.get(groupPosition).getChildList().get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return itemList.get(groupPosition).getChildList().size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                                 final ViewGroup parent) {

            convertView = inflater.inflate(R.layout.list_item, null, true);
            TextView textLabel = convertView.findViewById(R.id.text1);
            EditText qty_stock = convertView.findViewById(R.id.qty_stock);

            final Child item = getChild(groupPosition, childPosition);
            qty_stock.getTag();

            if(adapter.getChild(groupPosition, childPosition).getAnswer() == null){
                adapter.getChild(groupPosition, childPosition).setAnswer(null);
            } else {
                qty_stock.setText(adapter.getChild(groupPosition, childPosition).getAnswer());
            }

            qty_stock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        System.out.println(String.valueOf(childPosition + 1) + " Child = " + String.valueOf(getChildrenCount(groupPosition)));
                        if(childPosition + 1 == getChildrenCount(groupPosition)){
                            qty_stock.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        }
                    }

                }
            });
            qty_stock.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(groupPosition).childList.get(childPosition).setAnswer(s.toString());
                    //adapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });



            textLabel.setText(item.getName());


            return convertView;
        }

        @Override
        public Group getGroup(int groupPosition) {
            return itemList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return itemList.size();
        }

        @Override
        public long getGroupId(final int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View theConvertView, ViewGroup parent) {
            View resultView = theConvertView;
            ViewHolder holder;

            if (resultView == null) {
                resultView = inflater.inflate(R.layout.list_group, null);
                holder = new ViewHolder();
                holder.listTitle = resultView.findViewById(R.id.listTitle);
                resultView.setTag(holder);
            } else {
                holder = (ViewHolder) resultView.getTag();
            }

            final Group item = getGroup(groupPosition);

            holder.listTitle.setText(item.name);

            return resultView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public void onBackPressed() {

    }
}