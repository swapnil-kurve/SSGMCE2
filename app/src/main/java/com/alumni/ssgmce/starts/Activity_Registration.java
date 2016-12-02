package com.alumni.ssgmce.starts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.activity.Activity_Jobs;
import com.alumni.ssgmce.activity.Activity_People;
import com.alumni.ssgmce.classes.Branch;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Country;
import com.alumni.ssgmce.classes.Courses;
import com.alumni.ssgmce.classes.MemberRegistration;
import com.alumni.ssgmce.classes.State;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class Activity_Registration extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    Country country;
    ArrayList<Country> countryArrayList;

    State state;
    ArrayList<State> stateArrayList;

    Branch branch;
    ArrayList<Branch> branchArrayList;

    Courses courses;
    ArrayList<Courses> courseArrayList;

    EditText edtFirstName, edtMiddleName, edtLastName, edtMobile, edtEmail, edtPassword, edtConfirmPassword, edtCity, edtDesignation;
    Spinner  spinState, spinCountry, spinCourse, spinJoiningYear, spinBranch, spinDepartment;
    String mFirstName, mMiddleName, mLastName, mDOB, mMobile, mEmail, mPassword, mCity, mState, mCountry, mCourse, mBranch, mJoiningYear, mCPassword, mMemberType, mGender, mBranchId, mCourseId,mSocialId,
    mDesignation="", mDepartment="", mProfilePhoto="";
    RadioGroup rdGender, rdMemberType;
    RadioButton rdBtnGender, rdBtnMemberType;
    TextView txtAttachment, txtDOB;
    private File actualImage;
    private File compressedImage;
    private ImageView imgSelected;
    private static int RESULT_LOAD_IMG = 1;
    private View lyAlumniDetails, lyFacultyDetails;
    private Calendar myCalendar = Calendar.getInstance();
    public static String fromSocial = "Email";
    private int passwordFields = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Initialize();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            mSocialId = bundle.getString("UserId");
            mDOB = bundle.getString("UserDOB","");
            mFirstName = bundle.getString("Username","");
            mEmail = bundle.getString("UserEmail","");
            if(!mDOB.equalsIgnoreCase("")) {
                txtDOB.setText(mDOB);
                txtDOB.setEnabled(false);
            }
            if(!mFirstName.equalsIgnoreCase("")) {
                edtFirstName.setText(mFirstName);
                edtFirstName.setEnabled(false);
            }

            if(!mEmail.equalsIgnoreCase("")) {
                edtEmail.setText(mEmail);
                edtEmail.setEnabled(false);

                if(bundle.containsKey("NavFrom")) {
                    if (!bundle.getString("NavFrom").equalsIgnoreCase("EmailValidation")) {
                        ((View) findViewById(R.id.lyPassword)).setVisibility(View.GONE);
                        ((View) findViewById(R.id.lyCPassword)).setVisibility(View.GONE);
                    }
                }

                passwordFields = 1;
            }

            txtAttachment.setText(bundle.getString("UserProfile", ""));
        }

        getCountry();
        getAllCourses();
        getAllJoiningYear();
        setSpinDepartment();
        setStateCountry(stateArrayList);
        setSpinBranches(branchArrayList);


        txtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Activity_Registration.this, selectedDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void Initialize() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtMiddleName = (EditText) findViewById(R.id.edtMiddleName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        txtDOB = (TextView) findViewById(R.id.txtDOB);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        edtCity = (EditText) findViewById(R.id.edtCity);
        spinCountry = (Spinner) findViewById(R.id.spinCountry);
        spinState = (Spinner) findViewById(R.id.spinState);
        spinCourse = (Spinner) findViewById(R.id.spinCourse);
        spinBranch = (Spinner) findViewById(R.id.spinBranch);
        spinJoiningYear = (Spinner) findViewById(R.id.spinJoiningYear);
        rdGender = (RadioGroup) findViewById(R.id.rdGroupGender);
        rdMemberType = (RadioGroup) findViewById(R.id.rdMemberType);
        txtAttachment = (TextView) findViewById(R.id.txtAttachment);
        edtDesignation = (EditText) findViewById(R.id.edtDesignation);
        spinDepartment = (Spinner) findViewById(R.id.spinDepartment);
        lyAlumniDetails = findViewById(R.id.lyAlumni);
        lyFacultyDetails = findViewById(R.id.lyFaculty);
        imgSelected = new ImageView(this);

        TextView txtSubmit = (TextView) findViewById(R.id.txtSubmit);

        txtSubmit.setOnClickListener(this);
        txtAttachment.setOnClickListener(this);
        spinCountry.setOnItemSelectedListener(this);
        spinCourse.setOnItemSelectedListener(this);
        spinState.setOnItemSelectedListener(this);
        spinBranch.setOnItemSelectedListener(this);
        spinJoiningYear.setOnItemSelectedListener(this);
        rdMemberType.setOnCheckedChangeListener(this);
        imgBack.setOnClickListener(this);

        countryArrayList = new ArrayList<>();
        branchArrayList = new ArrayList<>();
        stateArrayList = new ArrayList<>();
        courseArrayList = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtSubmit:
                getData();
                break;

            case R.id.txtAttachment:
                loadImagefromGallery();
                break;

            case R.id.imgBack:
                startActivity(new Intent(this, Activity_SignUp.class));
                finish();
                break;
        }
    }

    private void getData() {
        mFirstName = edtFirstName.getText().toString().trim();
        mMiddleName = edtMiddleName.getText().toString().trim();
        mLastName = edtLastName.getText().toString().trim();
        mMobile = edtMobile.getText().toString().trim();
        mPassword = edtPassword.getText().toString().trim();
        mCPassword = edtConfirmPassword.getText().toString();
        mDOB = txtDOB.getText().toString().trim();
        mEmail = edtEmail.getText().toString().trim();
        mCity = edtCity.getText().toString().trim();
        mDesignation = edtDesignation.getText().toString().trim();

        int selectedId = rdGender.getCheckedRadioButtonId();
        rdBtnGender = (RadioButton)findViewById(selectedId);

        selectedId = rdMemberType.getCheckedRadioButtonId();
        rdBtnMemberType = (RadioButton) findViewById(selectedId);

        if(passwordFields == 1)
        {
            mPassword = "0000";
            mCPassword = "0000";
        }

        if(mFirstName.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Enter First name");
        }else if(mLastName.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Enter last name");
        }else if(mDOB.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Select Date of Birth");
        }else if(mMobile.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Enter your Mobile number");
        }else if (mEmail.equalsIgnoreCase("")){
            Constant.showToast(this, "Enter your email id");
        }else if(!Constant.isValidEmail(mEmail))
        {
            Constant.showToast(this, "Please enter valid email id");
        }else if(mCountry.equalsIgnoreCase("Country"))
        {
            Constant.showToast(this, "Please select country");
        }else if(mState.equalsIgnoreCase("")){
            Constant.showToast(this, "Please select state");
        }else if(mCity.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Enter your city");
        }else if(mJoiningYear.equalsIgnoreCase("Joining Year"))
        {
            Constant.showToast(this, "Select joining year");
        }else if(mPassword.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Password could not be bank");
        }else if(mCPassword.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Please confirm password");
        }else if(!mPassword.equals(mCPassword))
        {
            Constant.showToast(this, "Password does not match");
        }else if(!rdBtnGender.isChecked())
        {
            Constant.showToast(this, "Select gender");
        }else if(!rdBtnMemberType.isChecked())
        {
            Constant.showToast(this, "Select Member type");
        }else
        {
            if(rdBtnMemberType.getText().equals("Alumni"))
            {
                mMemberType = "A";
                if(mCourse.equalsIgnoreCase("Course"))
                {
                    Constant.showToast(this, "Select course");
                }else if(mBranch.equalsIgnoreCase("Branch"))
                {
                    Constant.showToast(this, "Select branch");
                }
            }else
            {
                mMemberType = "F";
                if(mDesignation.equalsIgnoreCase(""))
                {
                    Constant.showToast(this, "Please enter your designation");
                }
            }

            if(rdBtnGender.getText().equals("Male"))
            {
                mGender = "Male";
            }else
            {
                mGender = "Female";
            }

            registerUser();
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spinCountry:
                mCountry = spinCountry.getSelectedItem().toString().trim();
                if(!mCountry.equalsIgnoreCase("Country")) {
                    String mCountryId = countryArrayList.get(spinCountry.getSelectedItemPosition()).getID();
                    getStates(mCountryId);
                }
                break;

            case R.id.spinState:
                mState = spinState.getSelectedItem().toString().trim();
                break;

            case R.id.spinCourse:
                mCourse = spinCourse.getSelectedItem().toString().trim();
                if(!mCourse.equalsIgnoreCase("Course")) {
                    mCourseId = courseArrayList.get(spinCourse.getSelectedItemPosition()-1).getCourseId();
                    getAllBranches(mCourseId);
                }
                break;

            case R.id.spinBranch:
                mBranch = spinBranch.getSelectedItem().toString().trim();
                if(!mBranch.equalsIgnoreCase("Branch")){
                    mBranchId = branchArrayList.get(spinBranch.getSelectedItemPosition()-1).getBranchId();
                }
                break;

            case R.id.spinJoiningYear:
                mJoiningYear = spinJoiningYear.getSelectedItem().toString().trim();
                break;

            case R.id.spinDepartment:
                mDepartment = spinDepartment.getSelectedItem().toString().trim();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void getCountry() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();

        client.get(Constant.GET_COUNTRY, requestParams, new AsyncHttpResponseHandler() {

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
                        parseCountryResult(str);
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
                    Constant.showToast(Activity_Registration.this, errorMessage);
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

    private void parseCountryResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if(response.equals("Success"))
        {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getMemberLoginDetails = obj1.getJSONArray("getCountryList");

            for (int i = 0; i < getMemberLoginDetails.length(); i++) {
                JSONObject obj10 = getMemberLoginDetails.getJSONObject(i);

                String ID = obj10.optString("ID");
                String mCountryName = obj10.optString("countryName");
                String mCountryId = obj10.optString("countrycode");

                country = new Country(ID,mCountryName,mCountryId);
                countryArrayList.add(country);
            }
            setSpinCountry(countryArrayList);
        }else
        {
            Constant.showToast(Activity_Registration.this, "OTP does not match");
        }
    }


    private void setSpinCountry(ArrayList<Country> countryArrayList) {
        List<String> list = new ArrayList<>();
        list.add("India");
        for (int i = 1; i < countryArrayList.size(); i++) {
            list.add(countryArrayList.get(i).getCountryName());
        }

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinCountry.setAdapter(dataAdapter);
    }


    private void setSpinDepartment() {
        List<String> list = new ArrayList<>();
        list.add("Department");
        list.add("Applied Science and Humanity");
        list.add("Computer Science and Engineering");
        list.add("Electrical Engineering");
        list.add("Electronics and Telecommunication");
        list.add("Information Technology");
        list.add("Master of Business Administration");
        list.add("Mechanical Engineering");
        list.add("Office");
        list.add("SGIARC");
        list.add("Sports");


        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinDepartment.setAdapter(dataAdapter);
    }


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


    private void getStates(String mCountryId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("countryID", mCountryId);

        client.get(Constant.GET_STATE, requestParams, new AsyncHttpResponseHandler() {

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
                        parseStateResult(str);
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
                    Constant.showToast(Activity_Registration.this, errorMessage);
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

    private void parseStateResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if(response.equals("Success"))
        {
            stateArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getMemberLoginDetails = obj1.getJSONArray("getStateList");

            for (int i = 0; i < getMemberLoginDetails.length(); i++) {
                JSONObject obj10 = getMemberLoginDetails.getJSONObject(i);

                String stateId = obj10.optString("stateId");
                String countrycode = obj10.optString("countrycode");
                String stateName = obj10.optString("stateName");

                state = new State(countrycode,stateName,stateId);
                stateArrayList.add(state);
            }
            setStateCountry(stateArrayList);
        }else
        {
            Constant.showToast(Activity_Registration.this, "OTP does not match");
        }
    }


    private void setStateCountry(ArrayList<State> stateArrayList) {
        List<String> list = new ArrayList<>();
        list.add("Maharashtra");
        for (int i = 1; i < stateArrayList.size(); i++) {
            list.add(stateArrayList.get(i).getStateName());
        }

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinState.setAdapter(dataAdapter);
    }


    private void getAllCourses() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();

        client.get(Constant.GET_ALL_COURSES, requestParams, new AsyncHttpResponseHandler() {

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
                        parseCourseResult(str);
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
                    Constant.showToast(Activity_Registration.this, errorMessage);
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


    private void parseCourseResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getAllCourses");
            courseArrayList.clear();
            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String CourseName = obj10.optString("CourseName");
                String CourseId = obj10.optString("CourseId");

                courses = new Courses(CourseId,CourseName);
                courseArrayList.add(courses);
            }
            setSpinCourses(courseArrayList);
        }
    }


    private void setSpinCourses(ArrayList<Courses> courseArrayList) {
        List<String> list = new ArrayList<>();
        list.add("Course");
        for (int i = 0; i < courseArrayList.size(); i++) {
            list.add(courseArrayList.get(i).getCourseName());
        }

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinCourse.setAdapter(dataAdapter);
    }


    private void getAllBranches(String mCourseId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("CourseId", mCourseId);

        client.get(Constant.GET_BRANCHES, requestParams, new AsyncHttpResponseHandler() {

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
                        parseBranchesResult(str);
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
                    Constant.showToast(Activity_Registration.this, errorMessage);
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


    private void parseBranchesResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getBranchByCourseID");
            branchArrayList.clear();
            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String BranchId = obj10.optString("BranchId");
                String Branch = obj10.optString("Branch");

                branch = new Branch(BranchId,Branch);
                branchArrayList.add(branch);
            }
            setSpinBranches(branchArrayList);
        }
    }


    private void setSpinBranches(ArrayList<Branch> branchArrayList) {
        List<String> list = new ArrayList<>();
        list.add("Branch");
        for (int i = 0; i < branchArrayList.size(); i++) {
            list.add(branchArrayList.get(i).getBranch());
        }

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinBranch.setAdapter(dataAdapter);
    }



    private void getAllJoiningYear() {
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
                    Constant.showToast(Activity_Registration.this, errorMessage);
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
            ArrayList yearArrayList = new ArrayList();
            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String Year = obj10.optString("Year");

                yearArrayList.add(Year);
            }
            setYearBranches(yearArrayList);
        }
    }


    private void setYearBranches(ArrayList courseArrayList) {
        List<String> list = new ArrayList<>();
        list.add("Joining Year");
        list.addAll(courseArrayList);

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinJoiningYear.setAdapter(dataAdapter);
    }

    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    /**
     * When Image is selected from Gallery
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            if (data == null) {
                Constant.showToast(Activity_Registration.this, "Failed to open picture!");
                return;
            }
            try {
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                actualImage = FileUtil.from(this, data.getData());
                imgSelected.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                txtAttachment.setText(actualImage.getAbsolutePath());
                mProfilePhoto = actualImage.getAbsolutePath();
                compressImage();
            } catch (IOException e) {
                Constant.showToast(Activity_Registration.this, "Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }


    public void compressImage() {
        if (actualImage == null) {
            Constant.showToast(Activity_Registration.this, "Please choose an image!");
        } else {
            compressedImage = new Compressor.Builder(this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToFile(actualImage);

            setCompressedImage();
        }
    }

    private void setCompressedImage() {
        txtAttachment.setText(compressedImage.getAbsolutePath());
        imgSelected.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
    }


    private void registerUser() {
        if(!fromSocial.equalsIgnoreCase("Email")){
            mProfilePhoto = txtAttachment.getText().toString();
        }
        MemberRegistration memberRegistration = new MemberRegistration(this, imgSelected, compressedImage);
        memberRegistration.execute(mMemberType, mFirstName, mMiddleName, mLastName, mMobile, mDesignation, mDepartment, mJoiningYear,mCourseId,
                mBranchId, mProfilePhoto, mCity, mEmail,mPassword, mState, mCountry, mGender,mDOB,mSocialId );
    }

    public void proceed(String mEmail, String mMemberId)
    {
        if(fromSocial.equalsIgnoreCase("Email")) {
            Intent intent = new Intent(this, Activity_OTPValidation.class);
            intent.putExtra("Email", mEmail);
            intent.putExtra("NavFrom","FromRegistration");
            startActivity(intent);
        }else
        {
            if(edtEmail.isEnabled()){
                SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("EmailId", mEmail);
                editor.apply();
                sendOTPforSocialVerification(mEmail);
            }else {

                getUserDetails(mEmail);
            }
        }
    }

    private void getUserDetails(String mEmail) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("EmailId", mEmail);

        client.post(Constant.CHECK_EMAIL, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_Registration.this, errorMessage);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group.getId() == R.id.rdMemberType){
            int id = rdMemberType.getCheckedRadioButtonId();
            if(id == R.id.rdAlumni)
            {
                lyAlumniDetails.setVisibility(View.VISIBLE);
                lyFacultyDetails.setVisibility(View.GONE);
            }else
            {
                lyAlumniDetails.setVisibility(View.GONE);
                lyFacultyDetails.setVisibility(View.VISIBLE);
            }
        }
    }


    DatePickerDialog.OnDateSetListener selectedDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            txtDOB.setText(sdf.format(myCalendar.getTime()));

        } catch (Exception e) {


        }
    }




    private void sendOTPforSocialVerification(String mEmail) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("emailId", mEmail);

        client.post(Constant.SEND_OTP, requestParams, new AsyncHttpResponseHandler() {

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
                        parseSocialOTPResult(str);
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
                    Constant.showToast(Activity_Registration.this, errorMessage);
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


    private void parseSocialOTPResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if(response.equals("Success"))
        {
            Intent intent = new Intent(Activity_Registration.this, Activity_OTPValidation.class);
            intent.putExtra("NavFrom","FromEmail");
            startActivity(intent);
        }
    }



    private void parseResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if(response.equals("Success"))
        {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getMemberLoginDetails = obj1.getJSONArray("getMemberDetailsbyEmail");

            for (int i = 0; i < getMemberLoginDetails.length(); i++) {
                JSONObject obj10 = getMemberLoginDetails.getJSONObject(i);

                String MemberId = obj10.optString("MemberId");
                String MemberType = obj10.optString("MemberType");
                String FirstName = obj10.optString("FirstName");
                String MiddleName = obj10.optString("MiddleName");
                String LastName = obj10.optString("LastName");
                String MobileNo = obj10.optString("MobileNo");
                String Designation = obj10.optString("Designation");
                String Department = obj10.optString("Department");
                String JoiningYear = obj10.optString("JoiningYear");
                String CourseId = obj10.optString("CourseId");
                String ProfilePhoto = obj10.optString("ProfilePhoto");
                String InstituteId = obj10.optString("InstituteId");
                String CurrentLocation = obj10.optString("CurrentLocation");
                String EmailIdAsUserName = obj10.optString("EmailIdAsUserName");
                String CoverPhoto = obj10.optString("CoverPhoto");
                String InsertDate = obj10.optString("InsertDate");
                String InsertBy = obj10.optString("InsertBy");
                String EmailId = obj10.optString("EmailId");
                String UserId = obj10.optString("UserId");
                String SiteStatus = obj10.optString("SiteStatus");
                String BranchId = obj10.optString("BranchId");
                String DateofBirth = obj10.optString("DateofBirth1");
                String State = obj10.optString("State");
                String Country = obj10.optString("Country");
                String Gender = obj10.optString("Gender");
                String MemberDay = obj10.optString("MemberDay");
                String MemberMonth = obj10.optString("MemberMonth");
                String MemberYear = obj10.optString("MemberYear");
                String AdminApproved = obj10.optString("AdminApproved");
                String lastSignIn = obj10.optString("lastSignIn");
                String approvalDate = obj10.optString("approvalDate1");
                String Branch = obj10.optString("Branch");
                String CourseName = obj10.optString("CourseName");
                String isEventreg = obj10.optString("isEventreg");
                String isEventExist = obj10.optString("isEventExist");

                SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("MemberId",MemberId);
                editor.putString("MemberType",MemberType);
                editor.putString("FirstName",FirstName);
                editor.putString("MiddleName",MiddleName);
                editor.putString("LastName",LastName);
                editor.putString("MobileNo",MobileNo);
                editor.putString("Designation",Designation);
                editor.putString("Department",Department);
                editor.putString("JoiningYear",JoiningYear);
                editor.putString("CourseId",CourseId);
                editor.putString("ProfilePhoto",ProfilePhoto);
                editor.putString("InstituteId",InstituteId);
                editor.putString("CurrentLocation",CurrentLocation);
                editor.putString("EmailIdAsUserName",EmailIdAsUserName);
                editor.putString("CoverPhoto",CoverPhoto);
                editor.putString("InsertDate",InsertDate);
                editor.putString("InsertBy",InsertBy);
                editor.putString("EmailId",EmailId);
                editor.putString("UserId",UserId);
                editor.putString("SiteStatus",SiteStatus);
                editor.putString("BranchId",BranchId);
                editor.putString("DateofBirth",DateofBirth);
                editor.putString("State",State);
                editor.putString("Country",Country);
                editor.putString("Gender",Gender);
                editor.putString("MemberDay",MemberDay);
                editor.putString("MemberMonth",MemberMonth);
                editor.putString("MemberYear",MemberYear);
                editor.putString("AdminApproved",AdminApproved);
                editor.putString("lastSignIn",lastSignIn);
                editor.putString("approvalDate",approvalDate);
                editor.putString("Branch",Branch);
                editor.putString("CourseName",CourseName);
                editor.putString("isEventreg",isEventreg);
                editor.putString("isEventExist",isEventExist);

                editor.apply();

                Intent intent = new Intent(this, Activity_Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }


}
