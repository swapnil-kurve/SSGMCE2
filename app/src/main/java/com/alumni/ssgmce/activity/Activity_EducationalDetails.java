package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Companies;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Education;
import com.alumni.ssgmce.starts.Activity_Registration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Activity_EducationalDetails extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    Education education;
    ArrayList<Education> educationArrayList;
    ArrayList yearArrayList;
    ListView listEducations;
    View lyMain, lyEducation;
    private EditText  edtCollege, edtCourse, edtBranch, edtLocation;
    private Spinner spinFromMonth, spinFromYear, spinToMonth, spinToYear;
    String mMemberId, mCollege, mCourse, mBranch, mLocation, mFromMonth = "", mFromYear = "", mToMonth = "", mToYear = "",EduId;
    private int edtFlag = 0, validated = 1, pos;
    SetEducationAdapter setEducationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_details);

        Initialize();

        getEducationList();
        setSpinMonths("","");
        getAllYears();
    }

    private void Initialize() {
        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        TextView txtAddEducation = (TextView) findViewById(R.id.txtAddEducation);
        TextView txtSaveEducation = (TextView) findViewById(R.id.txtSave);
        TextView txtCancel = (TextView) findViewById(R.id.txtCancel);
        lyMain = findViewById(R.id.lyMain);
        lyEducation = findViewById(R.id.lyInclude);
        listEducations = (ListView) findViewById(R.id.listEducation);

        edtCollege = (EditText) findViewById(R.id.edtCollege);
        edtBranch = (EditText) findViewById(R.id.edtBranch);
        edtCourse = (EditText) findViewById(R.id.edtCourse);
        edtLocation = (EditText) findViewById(R.id.edtLocation);

        spinFromMonth = (Spinner) findViewById(R.id.spinFromMonth);
        spinFromYear = (Spinner) findViewById(R.id.spinFromYear);
        spinToMonth = (Spinner) findViewById(R.id.spinToMonth);
        spinToYear = (Spinner) findViewById(R.id.spinToYear);

        educationArrayList = new ArrayList<>();
        imgBack.setOnClickListener(this);
        txtAddEducation.setOnClickListener(this);
        txtSaveEducation.setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        spinFromMonth.setOnItemSelectedListener(this);
        spinFromYear.setOnItemSelectedListener(this);
        spinToMonth.setOnItemSelectedListener(this);
        spinToYear.setOnItemSelectedListener(this);

        listEducations.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtAddEducation:
                edtFlag = 0;
                clearData();
                showLayout();
                setSpinMonths("","");
                break;

            case R.id.txtSave:
                hideKeyboard();
                getData();
                break;

            case R.id.txtCancel:
                hideLayout();
                break;

        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if(inputManager.isAcceptingText()) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getData() {
        mCollege = edtCollege.getText().toString().trim();
        mCourse = edtCourse.getText().toString().trim();
        mBranch = edtBranch.getText().toString().trim();
        mLocation = edtLocation.getText().toString().trim();

        if(mFromMonth.equalsIgnoreCase("") || mFromMonth.equalsIgnoreCase("0"))
        {
            mFromMonth = educationArrayList.get(pos).getFromMonth();
        }

        if(mToMonth.equalsIgnoreCase("") || mToMonth.equalsIgnoreCase("0"))
        {
            mToMonth = educationArrayList.get(pos).getFromMonth();
        }


        if(mCollege.equalsIgnoreCase("")) {
            Constant.showToast(Activity_EducationalDetails.this, "Enter college name");
        }else if(mCourse.equalsIgnoreCase("")) {
            Constant.showToast(Activity_EducationalDetails.this, "Enter course");
        }else if(mBranch.equalsIgnoreCase("")) {
            Constant.showToast(Activity_EducationalDetails.this, "Enter branch");
        }else if(mFromMonth.equalsIgnoreCase("")) {
            Constant.showToast(Activity_EducationalDetails.this, "Enter from month");
        }else if(mFromYear.equalsIgnoreCase("")) {
            Constant.showToast(Activity_EducationalDetails.this, "Enter from year");
        }else if(mToMonth.equalsIgnoreCase("")) {
            Constant.showToast(Activity_EducationalDetails.this, "Enter to month");
        }else if(mToYear.equalsIgnoreCase("")) {
            Constant.showToast(Activity_EducationalDetails.this, "Enter to year");
        }else if(mLocation.equalsIgnoreCase("")){
            Constant.showToast(Activity_EducationalDetails.this, "Enter location");
        }else{
            validateMonthYear();
            if(validated == 1) {
                if (edtFlag == 0) {
                    AddEducation(Constant.ADD_EDUCATION, "Add");
                } else {
                    AddEducation(Constant.UPDATE_EDUCATION, "Update");
                }
            }
        }
    }

    private void hideLayout() {
        lyMain.setVisibility(View.VISIBLE);
        lyEducation.setVisibility(View.GONE);

        getEducationList();
    }

    private void showLayout() {
        lyMain.setVisibility(View.GONE);
        lyEducation.setVisibility(View.VISIBLE);
    }


    private void getEducationList() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("MemberId", mMemberId);

        client.get(Constant.GET_EDUCATION, requestParams, new AsyncHttpResponseHandler() {

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
                        parseCompanyResult(str);
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
                    Constant.showToast(Activity_EducationalDetails.this, errorMessage);
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


    private void parseCompanyResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            educationArrayList.clear();
            listEducations.setVisibility(View.VISIBLE);
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getMemberEducation");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String EduId = obj10.optString("EduId");
                String College = obj10.optString("College");
                String Course = obj10.optString("Course");
                String Branch = obj10.optString("Branch");
                String Location = obj10.optString("Location");
                String FromMonth = obj10.optString("FromMonth");
                String FromYear = obj10.optString("FromYear");
                String ToMonth = obj10.optString("ToMonth");
                String ToYear = obj10.optString("ToYear");

                education = new Education(EduId,College,Course,Branch,Location,FromMonth,FromYear,ToMonth,ToYear);
                educationArrayList.add(education);
            }

            setEducationAdapter = new SetEducationAdapter(Activity_EducationalDetails.this, educationArrayList);
            listEducations.setAdapter(setEducationAdapter);
        }else {
            listEducations.setVisibility(View.GONE);
            Constant.showToast(Activity_EducationalDetails.this, "No details found");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        edtFlag = 1;
        pos = position;
        clearData();
        EduId = educationArrayList.get(position).getEduId();
        edtCollege.setText(educationArrayList.get(position).getCollege());
        edtBranch.setText(educationArrayList.get(position).getBranch());
        edtCourse.setText(educationArrayList.get(position).getCourse());
        edtLocation.setText(educationArrayList.get(position).getLocation());

        mFromMonth = educationArrayList.get(position).getFromMonth();
        mToMonth = educationArrayList.get(position).getToMonth();

        setSpinMonths(Constant.getMonth(Integer.parseInt(educationArrayList.get(position).getFromMonth())),Constant.getMonth(Integer.parseInt(educationArrayList.get(position).getToMonth())));
        setYearBranches(yearArrayList,educationArrayList.get(position).getFromYear(),educationArrayList.get(position).getToYear());
        showLayout();
    }

    private class SetEducationAdapter extends BaseAdapter{

        Context context;
        ArrayList<Education> educationArrayList;

        public SetEducationAdapter(Activity_EducationalDetails activity_educationalDetails, ArrayList<Education> educationArrayList) {
            this.context = activity_educationalDetails;
            this.educationArrayList = educationArrayList;
        }

        @Override
        public int getCount() {
            return educationArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return educationArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_education, null);

            TextView txtCollege = (TextView) convertView.findViewById(R.id.txtCollege);
            TextView txtCourse = (TextView) convertView.findViewById(R.id.txtCourse);
            TextView txtBranch = (TextView) convertView.findViewById(R.id.txtBranch);
            TextView txtLocation = (TextView) convertView.findViewById(R.id.txtLocation);
            ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);

            txtCollege.setText(educationArrayList.get(position).getCollege());
            txtCourse.setText(educationArrayList.get(position).getCourse());
            txtBranch.setText(educationArrayList.get(position).getBranch());
            txtLocation.setText(educationArrayList.get(position).getLocation());

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWork(educationArrayList.get(position).getEduId());
                }
            });

            return convertView;
        }
    }


    private void deleteWork(String eduId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("EduId", eduId);
        requestParams.add("MemberId", mMemberId);

        client.get(Constant.DELETE_EDUCATION_EXP, requestParams, new AsyncHttpResponseHandler() {

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
                        parseDeleteEducationResult(str);
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
                    Constant.showToast(Activity_EducationalDetails.this, errorMessage);
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

    private void parseDeleteEducationResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            Constant.showToast(this, "Delete Successfully !");
            getEducationList();
        }else{
            Constant.showToast(this, "Failed to delete !");
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spinFromMonth:
                mFromMonth = String.valueOf(spinFromMonth.getSelectedItemPosition());
                break;

            case R.id.spinFromYear:
                mFromYear = spinFromYear.getSelectedItem().toString();
                break;

            case R.id.spinToMonth:
                mToMonth = String.valueOf(spinToMonth.getSelectedItemPosition());
                break;

            case R.id.spinToYear:
                mToYear = spinToYear.getSelectedItem().toString();
                break;
        }

        validateMonthYear();

    }

    private void validateMonthYear()
    {
        if (!mFromMonth.equalsIgnoreCase("0") && !mFromYear.equalsIgnoreCase("From Year") &&
                !mToMonth.equalsIgnoreCase("0") && !mToYear.equalsIgnoreCase("To Year")) {
            validated = 1;
            if(Integer.parseInt(mFromYear) > Integer.parseInt(mToYear)){

                Constant.showToast(this, "From year should not be greater than To year");
                validated = 0;
            }else if(Integer.parseInt(mFromYear) == Integer.parseInt(mToYear))
            {
                if(Integer.parseInt(mFromMonth) >= Integer.parseInt(mToMonth))
                {
                    Constant.showToast(this, "From month should not be greater than To month");
                    validated = 0;
                }
            }

        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


private void setSpinMonths(String from, String to){
    List<String> list = new ArrayList<>();
    if(from.equalsIgnoreCase(""))
        list.add("From Month");
    else
        list.add(from);
    list.add("January");
    list.add("February");
    list.add("March");
    list.add("April");
    list.add("May");
    list.add("June");
    list.add("July");
    list.add("August");
    list.add("September");
    list.add("October");
    list.add("November");
    list.add("December");

    ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
    spinFromMonth.setAdapter(dataAdapter);

    List<String> list1 = new ArrayList<>();
    if(to.equalsIgnoreCase(""))
        list1.add("To Month");
    else
        list1.add(to);
    list1.add("January");
    list1.add("February");
    list1.add("March");
    list1.add("April");
    list1.add("May");
    list1.add("June");
    list1.add("July");
    list1.add("August");
    list1.add("September");
    list1.add("October");
    list1.add("November");
    list1.add("December");

    dataAdapter = adapterForSpinner(list1);
    spinToMonth.setAdapter(dataAdapter);
}


    private ArrayAdapter<String> adapterForSpinner(List<String> list) {
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return dataAdapter;
    }


    private void getAllYears() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();

        client.get(Constant.GET_YEAR, requestParams, new AsyncHttpResponseHandler() {

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
                        parseJoiningYearResult(str);
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
                    Constant.showToast(Activity_EducationalDetails.this, errorMessage);
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


    private void parseJoiningYearResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getYear");
            yearArrayList = new ArrayList();
            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String Year = obj10.optString("Year");

                yearArrayList.add(Year);
            }
            setYearBranches(yearArrayList,"","");
        }
    }


    private void setYearBranches(ArrayList courseArrayList, String from, String to) {
        List<String> list = new ArrayList<>();
        if(from.equalsIgnoreCase(""))
            list.add("From Year");
        else
            list.add(from);
        list.addAll(courseArrayList);

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinFromYear.setAdapter(dataAdapter);

        List<String> list1 = new ArrayList<>();
        if(to.equalsIgnoreCase(""))
            list1.add("To Year");
        else
            list1.add(to);
        list1.addAll(courseArrayList);

        dataAdapter = adapterForSpinner(list1);
        spinToYear.setAdapter(dataAdapter);
    }



    private void AddEducation(String url, String type) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("MemberId", mMemberId);
        requestParams.add("College", mCollege);
        requestParams.add("Course", mCourse);
        requestParams.add("Branch", mBranch);
        requestParams.add("Location", mLocation);
        requestParams.add("FromMonth", mFromMonth);
        requestParams.add("FromYear", mFromYear);
        requestParams.add("ToMonth", mToMonth);
        requestParams.add("ToYear", mToYear);
        requestParams.add("CurrentlyHere", "");
        if(type.equalsIgnoreCase("Update"))
            requestParams.add("EduId", EduId);

        client.get(url, requestParams, new AsyncHttpResponseHandler() {

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
                        parseEducationResult(str);
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
                    Constant.showToast(Activity_EducationalDetails.this, errorMessage);
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

    private void parseEducationResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            Constant.showToast(Activity_EducationalDetails.this, "Successful!");
            hideLayout();
        }else
        {
            Constant.showToast(Activity_EducationalDetails.this, "Failed to add education");
            hideLayout();
        }
    }


    private void clearData(){
        edtCourse.setText("");
        edtLocation.setText("");
        edtBranch.setText("");
        edtCollege.setText("");
        edtLocation.setText("");

        setSpinMonths("","");
        setYearBranches(yearArrayList,"","");
    }


    @Override
    public void onBackPressed() {

        if(lyMain.getVisibility() == View.GONE){
            hideKeyboard();
            hideLayout();
        }else
        {
            super.onBackPressed();
        }
    }
}
