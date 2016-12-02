package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAssignedNumbers;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Events;
import com.alumni.ssgmce.classes.Joinee;
import com.alumni.ssgmce.starts.Activity_Home;
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

public class Activity_Activities extends AppCompatActivity implements View.OnClickListener {

    private int mTopValue = 5;
    private ListView listActivities;
    private Joinee joinee;
    ArrayList<Joinee> joineeArrayList;
    private SetJoineeAdapter setJoineeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        Initialize();

        getLatestActivities();
    }

    private void Initialize() {
        listActivities = (ListView) findViewById(R.id.listActivities);
        TextView txtViewMore = (TextView) findViewById(R.id.txtViewMore);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        joineeArrayList = new ArrayList<>();

        txtViewMore.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    public void getLatestActivities() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("topVal", mTopValue);

        client.post(Constant.GET_ALL_ACTIVITIES, requestParams, new AsyncHttpResponseHandler() {

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
                        Constant.flag = 1;
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
                    Constant.showToast(Activity_Activities.this, errorMessage);
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
        String result = obj01.getString("result");

        if (result.equalsIgnoreCase("Success")) {
            joineeArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getNewJoinee");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String memberID = obj10.optString("memberID");
                String fullName = obj10.optString("fullName");
                String Date = obj10.optString("Date");
                String monthYear = obj10.optString("monthYear");
                String signupmembers = obj10.optString("signupmembers");

                joinee = new Joinee(memberID,fullName,Date,monthYear,signupmembers);
                joineeArrayList.add(joinee);
            }
            setJoineeAdapter = new SetJoineeAdapter(Activity_Activities.this, joineeArrayList);
            listActivities.setAdapter(setJoineeAdapter);

        } else {
            Constant.showToast(Activity_Activities.this, "Problem occurred while getting data.");
            Constant.showToast(Activity_Activities.this, "Please try again later");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtViewMore:
                mTopValue = mTopValue+5;
                getLatestActivities();
                break;

            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    private class SetJoineeAdapter extends BaseAdapter
    {
        ArrayList<Joinee> joineeArrayList;
        Context context;

        public SetJoineeAdapter(Activity_Activities activity_activities, ArrayList<Joinee> joineeArrayList) {
            this.context = activity_activities;
            this.joineeArrayList = joineeArrayList;
        }

        @Override
        public int getCount() {
            return joineeArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return joineeArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_activities, null);

            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            TextView txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
            TextView txtActivity = (TextView) convertView.findViewById(R.id.txtActivity);

            txtDate.setText(joineeArrayList.get(position).getDate()+joineeArrayList.get(position).getMonthYear());
            txtUsername.setText(joineeArrayList.get(position).getFullName());
            txtActivity.setText(joineeArrayList.get(position).getSignupmembers());

            return convertView;
        }
    }

}
