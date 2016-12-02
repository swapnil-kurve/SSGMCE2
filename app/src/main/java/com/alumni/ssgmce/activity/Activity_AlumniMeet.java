package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Events;
import com.alumni.ssgmce.starts.Activity_Registration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Activity_AlumniMeet extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private TextView txtArrivalDate, txtDepartureDate, txtArrivalTime, txtDepartureTime, txtEvent;
    private RadioGroup rdGroupMember;
    private Spinner spinNoOfFamily, spinModeOfTravel;
    private RadioButton rdBtnMember;
    public static String mEventName,mEventid;
    private String var = "",currentDateAndTime = "", mEvent = "",mNoOfFamily = "0", mModeOfTravel, withFamily = "Y",mMemberId,mArrivalDate, mDepartureDate, mArrivalTime, mDepartureTime, isEventreg,EventEndDate,EventDate;
    private int mEventPos;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_meet);

        Initialize();
        getUpcomingEventsData();
        setSpinNoOfFamily("");
        setSpinTransport("");
        txtEvent.setText(mEventName);

    }


    private void setSpinTransport(String transport) {
        List<String> list = new ArrayList<>();
        if(transport.equalsIgnoreCase(""))
            list.add("Select Mode of transport");
        else
            list.add(transport);
        list.add("By Road");
        list.add("By Train");

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinModeOfTravel.setAdapter(dataAdapter);
    }

    private void setSpinNoOfFamily(String noOfFamily) {
        List<String> list = new ArrayList<>();
        if(noOfFamily.equalsIgnoreCase(""))
            list.add("Select No of family member");
        else
            list.add(noOfFamily);
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinNoOfFamily.setAdapter(dataAdapter);
    }

    private void Initialize() {

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");
        isEventreg = preferences.getString("isEventreg", "");

        txtEvent = (TextView) findViewById(R.id.txtEvent);
        txtArrivalDate = (TextView) findViewById(R.id.txtArrivalDate);
        txtDepartureDate = (TextView) findViewById(R.id.txtDepartureDate);
        txtArrivalTime = (TextView) findViewById(R.id.txtArrivalTime);
        txtDepartureTime = (TextView) findViewById(R.id.txtDepartureTime);
        rdGroupMember = (RadioGroup) findViewById(R.id.rdGroupMember);
        spinNoOfFamily = (Spinner) findViewById(R.id.spinNoOfFamily);
        spinModeOfTravel = (Spinner) findViewById(R.id.spinModeOfTravel);

        TextView txtRegister = (TextView) findViewById(R.id.txtRegister);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        txtRegister.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        if(isEventreg.equalsIgnoreCase("0"))
            txtRegister.setText("Register");
        else
            txtRegister.setText("Update");

        txtArrivalDate.setOnClickListener(this);
        txtDepartureDate.setOnClickListener(this);
        txtArrivalTime.setOnClickListener(this);
        txtDepartureTime.setOnClickListener(this);

        rdGroupMember.setOnCheckedChangeListener(this);

        spinNoOfFamily.setOnItemSelectedListener(this);
        spinModeOfTravel.setOnItemSelectedListener(this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        currentDateAndTime = sdf.format(new Date());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtRegister:
                registerUserForEvent();
            break;

            case R.id.txtArrivalDate:
                var = "AD";

                new DatePickerDialog(Activity_AlumniMeet.this, selectedDate, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                break;

            case R.id.txtDepartureDate:
                var = "DD";

                new DatePickerDialog(Activity_AlumniMeet.this, selectedDate, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.txtArrivalTime:
                var = "AT";

                new TimePickerDialog(Activity_AlumniMeet.this, selectedTime, myCalendar.get(Calendar.HOUR_OF_DAY),
                            myCalendar.get(Calendar.MINUTE), false).show();

                break;

            case R.id.txtDepartureTime:
                var = "DT";

                new TimePickerDialog(Activity_AlumniMeet.this, selectedTime, myCalendar.get(Calendar.HOUR_OF_DAY),
                            myCalendar.get(Calendar.MINUTE), false).show();

                break;
        }
    }

    private void registerUserForEvent() {
        mEvent = txtEvent.getText().toString();
        mArrivalDate = txtArrivalDate.getText().toString();
        mDepartureDate = txtDepartureDate.getText().toString();
        mArrivalTime = txtArrivalTime.getText().toString();
        mDepartureTime = txtDepartureTime.getText().toString();

        int selectedId = rdGroupMember.getCheckedRadioButtonId();
        rdBtnMember = (RadioButton)findViewById(selectedId);

        if(mEvent.equalsIgnoreCase("")) {
            Constant.showToast(this, "Please select event for registration");
        }else if(mArrivalDate.equalsIgnoreCase("")) {
            Constant.showToast(this, "Please select arrival date");
        }else if(mDepartureDate.equalsIgnoreCase("")){
            Constant.showToast(this, "Please select departure date");
        }else if(mArrivalTime.equalsIgnoreCase("")){
            Constant.showToast(this, "Please select arrival time");
        }else if(mDepartureTime.equalsIgnoreCase("")){
            Constant.showToast(this, "Please select departure time");
        }else if(withFamily.equalsIgnoreCase("Y")){
            if (mNoOfFamily.equalsIgnoreCase("0"))
            {
                Constant.showToast(this, "Please select no of family members");
            }else if(mModeOfTravel.equalsIgnoreCase(""))
            {
                Constant.showToast(this, "Please select mode of transport");
            }else{
                registerMemberForEvent();
            }
        }else if(mModeOfTravel.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Please select mode of transport");
        }else {
            registerMemberForEvent();
        }
    }

    private void registerMemberForEvent() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("MemberId",mMemberId);
        requestParams.add("Eventid", mEventid);
        requestParams.add("ArrivalDate", mArrivalDate);
        requestParams.add("DepartureDate",mDepartureDate);
        requestParams.add("ArrivalTime",mArrivalTime);
        requestParams.add("DepartureTime",mDepartureTime);
        requestParams.add("TravelMode",mModeOfTravel);
        requestParams.add("WithFamily", withFamily);
        requestParams.add("NoOfMembers",mNoOfFamily);

        client.get(Constant.ADD_EVENT_ATTENDANCE, requestParams, new AsyncHttpResponseHandler() {

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
                        parseRegistrationResult(str);
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
                    Constant.showToast(Activity_AlumniMeet.this, errorMessage);
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

    private void parseRegistrationResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            Constant.showToast(this, "Successful !");
            onBackPressed();
        }else
        {
            Constant.showToast(this, "Failed !");
            onBackPressed();
        }
    }



    DatePickerDialog.OnDateSetListener selectedDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }

    };


    private void updateDateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String calDate = sdf.format(myCalendar.getTime());

        try {
            Date eventStartDate = sdf.parse(EventDate);
            Date eventEndDate = sdf.parse(EventEndDate);

            if(var.equalsIgnoreCase("AD")){
//                if (sdf.parse(currentDateAndTime).before(sdf.parse(calDate)) || sdf.parse(currentDateAndTime).equals(sdf.parse(calDate))) {
                if (sdf.parse(currentDateAndTime).before(sdf.parse(calDate)) || sdf.parse(currentDateAndTime).equals(sdf.parse(calDate))) {
                    if(sdf.parse(currentDateAndTime).before(eventStartDate) || sdf.parse(calDate).equals(eventStartDate)  || sdf.parse(calDate).before(eventStartDate)) {
                        txtArrivalDate.setText(sdf.format(myCalendar.getTime()));
                    }else
                    {
                        Constant.showToast(this, "Please select valid arrival date");
                    }

                } else {
                    Constant.showToast(this, "Please select valid date");
                }
            }
            else{
                if(txtArrivalDate.getText().toString().equals("")){
                    Constant.showToast(this, "Please select arrival date first");
                }else {
                    if (sdf.parse(calDate).after(sdf.parse(txtArrivalDate.getText().toString())) && sdf.parse(calDate).after(sdf.parse(currentDateAndTime))) {
                        if(sdf.parse(calDate).equals(eventEndDate) || sdf.parse(calDate).after(eventEndDate)) {
                            txtDepartureDate.setText(sdf.format(myCalendar.getTime()));
                        }else{
                            Constant.showToast(this, "Please select valid departure date");
                        }
                    } else {
                        Constant.showToast(this, "Please select valid date");
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    TimePickerDialog.OnTimeSetListener selectedTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            if(var.equalsIgnoreCase("AT"))
                txtArrivalTime.setText(String.valueOf(hourOfDay) +":"+String.valueOf(minute));
            else
                txtDepartureTime.setText(String.valueOf(hourOfDay) +":"+String.valueOf(minute));
        }
    };

    private  ArrayAdapter<String> adapterForSpinner(List<String> list)
    {
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
        {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return dataAdapter;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group.getId() == R.id.rdGroupMember){
            int id = rdGroupMember.getCheckedRadioButtonId();
            if(id == R.id.rdWithoutFamily)
            {
                spinNoOfFamily.setEnabled(false);
                withFamily = "N";
                mNoOfFamily = "";
            }else
            {
                spinNoOfFamily.setEnabled(true);
                withFamily = "Y";
            }
        }
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spinNoOfFamily:
                mNoOfFamily = spinNoOfFamily.getSelectedItem().toString();
                break;

            case R.id.spinModeOfTravel:
                mModeOfTravel = spinModeOfTravel.getSelectedItem().toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void getEventIfRegistered() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("MemberId", mMemberId);
        requestParams.add("Eventid", mEventid);

        client.get(Constant.GET_EVENT_IF_REGISTER, requestParams, new AsyncHttpResponseHandler() {

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
                        parseResultEvent(str);
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
                    Constant.showToast(Activity_AlumniMeet.this, errorMessage);
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

    private void parseResultEvent(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equalsIgnoreCase("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getEventIfRegistered");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                mEventid = obj10.optString("Eventid");
                String ArrivalDate = obj10.optString("ArrivalDate");
                String DepartureDate = obj10.optString("DepartureDate");
                String ArrivalTime = obj10.optString("ArrivalTime");
                String DepartureTime = obj10.optString("DepartureTime");
                String MemberId = obj10.optString("MemberId");
                String TravelMode = obj10.optString("TravelMode");
                String WithFamily = obj10.optString("WithFamily");
                String NoOfMembers = obj10.optString("NoOfMembers");

                txtArrivalDate.setText(ArrivalDate);
                txtDepartureDate.setText(DepartureDate);
                txtArrivalTime.setText(ArrivalTime);
                txtDepartureTime.setText(DepartureTime);
                txtArrivalDate.setText(ArrivalDate);

                if (WithFamily.equalsIgnoreCase("Y")) {
                    ((RadioButton) findViewById(R.id.rdWithFamily)).setChecked(true);
                    ((RadioButton) findViewById(R.id.rdWithoutFamily)).setChecked(false);
                }
                else {
                    ((RadioButton) findViewById(R.id.rdWithoutFamily)).setChecked(true);
                    ((RadioButton) findViewById(R.id.rdWithFamily)).setChecked(false);
                }

                setSpinTransport(TravelMode);
                setSpinNoOfFamily(NoOfMembers);
            }

        }
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
                    Constant.showToast(Activity_AlumniMeet.this, errorMessage);
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
                EventDate = obj10.optString("EventDate");
                EventEndDate = obj10.optString("EventEndDate");

                txtEvent.setText(Event);
                mEventid = Eventid;

            }

        }

        if(isEventreg.equalsIgnoreCase("1"))
            getEventIfRegistered();
    }


}
