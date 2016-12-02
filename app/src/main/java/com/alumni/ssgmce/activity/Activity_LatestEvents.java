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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Events;
import com.alumni.ssgmce.classes.NonScrollListView;
import com.alumni.ssgmce.classes.WallPost;
import com.alumni.ssgmce.starts.Activity_Home;
import com.alumni.ssgmce.starts.Activity_Login;
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

public class Activity_LatestEvents extends AppCompatActivity implements View.OnClickListener {

    private NonScrollListView listUpcomingEvents;
    Events events;
    ArrayList<Events> eventsArrayList;
    private SetEventAdapter setEventAdapter;
    private SharedPreferences preferences;
    private String isEventreg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_events);

        Initialize();

        getUpcomingEventsData();
    }

    private void Initialize() {
        listUpcomingEvents = (NonScrollListView) findViewById(R.id.listUpcomingEvents);
        TextView txtRegisterForMeet = (TextView) findViewById(R.id.txtRegister);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        eventsArrayList = new ArrayList<>();

        imgBack.setOnClickListener(this);
        txtRegisterForMeet.setOnClickListener(this);

        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        isEventreg = preferences.getString("isEventreg", "");

        if(isEventreg.equalsIgnoreCase("0"))
            txtRegisterForMeet.setText("Register");
        else
            txtRegisterForMeet.setText("Update");
    }

    public void getUpcomingEventsData() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();

        client.get(Constant.GET_ALL_UPCOMING_EVENTS, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_LatestEvents.this, errorMessage);
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
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getUpcomingEvents");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String Eventid = obj10.optString("Eventid");
                String Event = obj10.optString("Event");
                String EventDescription = obj10.optString("EventDescription");
                String EventScheduleId = obj10.optString("EventScheduleId");
                String DayNo = obj10.optString("DayNo");
                String EventDate = obj10.optString("EventDate");
                String StatrTime = obj10.optString("StatrTime");
                String EndTime = obj10.optString("EndTime");
                String EventEndDate = obj10.optString("EventEndDate");
                String NeedToRegister = obj10.optString("NeedToRegister");

                events = new Events(Eventid, Event, EventDescription, EventScheduleId, DayNo, EventDate, StatrTime, EndTime, EventEndDate, NeedToRegister);
                eventsArrayList.add(events);
            }

            setEventAdapter = new SetEventAdapter(Activity_LatestEvents.this, eventsArrayList);
            listUpcomingEvents.setAdapter(setEventAdapter);

        } else {
            Constant.showToast(Activity_LatestEvents.this, "Problem occurred while getting data.");
            Constant.showToast(Activity_LatestEvents.this, "Please try again later");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtRegister:
                Activity_AlumniMeet.mEventName = eventsArrayList.get(0).getEvent();
                Activity_AlumniMeet.mEventid = eventsArrayList.get(0).getEventid();
                startActivity(new Intent(this, Activity_AlumniMeet.class));
                break;
        }
    }

    private class SetEventAdapter extends BaseAdapter {
        ArrayList<Events> eventsArrayList;
        Context context;

        public SetEventAdapter(Activity_LatestEvents activity_latestEvents, ArrayList<Events> eventsArrayList) {
            this.eventsArrayList = eventsArrayList;
            this.context = activity_latestEvents;
        }

        @Override
        public int getCount() {
            return eventsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return eventsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_events, null);

            TextView txtEventTitle = (TextView) convertView.findViewById(R.id.txtEventTitle);
            TextView txtEventStartsDate = (TextView) convertView.findViewById(R.id.txtStartsDate);
            TextView txtEventEndsDate = (TextView) convertView.findViewById(R.id.txtEndsDate);
            TextView txtEventVenue = (TextView) convertView.findViewById(R.id.txtVenue);
            TextView txtEventDetails = (TextView) convertView.findViewById(R.id.txtDetails);

            txtEventTitle.setText(eventsArrayList.get(position).getEvent());
            txtEventStartsDate.setText(eventsArrayList.get(position).getEventDate());
            txtEventEndsDate.setText(eventsArrayList.get(position).getEventEndDate());
            txtEventVenue.setText("SSGMCE, Shegaon, Maharashra");
            txtEventDetails.setText(eventsArrayList.get(position).getEventDescription());

            return convertView;
        }
    }



}
