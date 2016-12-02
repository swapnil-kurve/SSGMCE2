package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Groups;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Activity_MyGroups extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Groups groups;
    private ArrayList<Groups> groupsArrayList;
    private String mMemberId;
    private SetGroupAdapter setGroupAdapter;
    private ListView listMyGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        Initialize();

        getGroupList();
    }

    private void Initialize() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        listMyGroups = (ListView) findViewById(R.id.listMyGroups);

        imgBack.setOnClickListener(this);

        groupsArrayList = new ArrayList<>();

        listMyGroups.setOnItemClickListener(this);

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }



    public void getGroupList() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("MemberId", mMemberId);

        client.post(Constant.GET_MY_GROUP, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        String str = new String(responseBody, "UTF-8");
                        progressDialog.dismiss();
                        parseResult(str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    progressDialog.dismiss();
                    String errorMessage = new String(responseBody, "UTF-8");
                    Constant.showToast(Activity_MyGroups.this, errorMessage);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d("AsyncAndro", "AsyncAndroid retryNo:" + retryNo);
            }

        });
    }

    private void parseResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equalsIgnoreCase("Success")) {
            groupsArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getMySubscribeGroup");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String GrpId = obj10.optString("GrpId");
                String Groups = obj10.optString("Groups");
                String status = obj10.optString("status");

                groups = new Groups(GrpId,Groups,status);
                groupsArrayList.add(groups);
            }
            setGroupAdapter = new SetGroupAdapter(Activity_MyGroups.this, groupsArrayList);
            listMyGroups.setAdapter(setGroupAdapter);

        } else {
            Constant.showToast(Activity_MyGroups.this, "You are not subscribed to any group.");
//            Constant.showToast(Activity_MyGroups.this, "Please try again later");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.listMyGroups)
        {
            Activity_GroupPosts.mGroupId = groupsArrayList.get(position).getGrpId();
            Activity_GroupPosts.mGroupName = groupsArrayList.get(position).getGroups();
            startActivity(new Intent(this, Activity_GroupPosts.class));
        }
    }

    private class SetGroupAdapter extends BaseAdapter
    {
        ArrayList<Groups> groupsArrayList;
        Context context;

        public SetGroupAdapter(Activity_MyGroups activity_groups, ArrayList<Groups> groupsArrayList) {
            this.context = activity_groups;
            this.groupsArrayList = groupsArrayList;
        }

        @Override
        public int getCount() {
            return groupsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return groupsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_group_name, null);

            TextView txtGroupName = (TextView) convertView.findViewById(R.id.txtGroupName);
            final TextView txtGroupType = (TextView) convertView.findViewById(R.id.txtGroupType);

            txtGroupName.setText(groupsArrayList.get(position).getGroups());
            txtGroupType.setVisibility(View.GONE);

            return convertView;
        }
    }
}
