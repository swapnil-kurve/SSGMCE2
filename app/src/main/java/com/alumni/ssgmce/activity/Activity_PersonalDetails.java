package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
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

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Branch;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Country;
import com.alumni.ssgmce.classes.Courses;
import com.alumni.ssgmce.classes.State;
import com.alumni.ssgmce.classes.UpdateMember;
import com.alumni.ssgmce.starts.Activity_EmailValidation;
import com.alumni.ssgmce.starts.Activity_OTPValidation;
import com.alumni.ssgmce.starts.Activity_Registration;
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
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class Activity_PersonalDetails extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static int RESULT_LOAD_IMG = 1;
    private File actualImage;
    private File compressedImage;
    ImageView imgSelected;

    Country country;
    ArrayList<Country> countryArrayList;

    State state;
    ArrayList<State> stateArrayList;

    Branch branch;
    ArrayList<Branch> branchArrayList;

    Courses courses;
    ArrayList<Courses> courseArrayList;

    EditText edtFirstName, edtMiddleName, edtLastName, edtDOB, edtMobile, edtEmail, edtCity, edtDesignation;
    String mFirstName, mMiddleName, mLastName, mDOB, mMobile, mEmail, mCity, mState, mCountry, mCourse, mBranch, mJoiningYear, mMemberType, mGender, mBranchId, mCourseId,
            mDesignation="", mDepartment="",mMemberId,mInstituteId, mPassword;
    RadioGroup rdGroupGender;
    TextView txtAttachment;
    RadioButton rdMale, rdFemale;
    Spinner spinState, spinCountry, spinCourse, spinJoiningYear, spinBranch, spinDepartment;
    private View lyAlumniDetails, lyFacultyDetails;
    SharedPreferences preferences;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        Initialize();

        getCountry();
        getAllCourses();
        getAllJoiningYear();
        setSpinDepartment();
        setStateCountry(stateArrayList);
        setSpinBranches(branchArrayList);

        setData();

        edtDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    new DatePickerDialog(Activity_PersonalDetails.this, selectedDate, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    private void Initialize() {
        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        TextView txtUpdate = (TextView) findViewById(R.id.txtUpdate);
        TextView txtUpdateEmail = (TextView) findViewById(R.id.txtUpdateEmail);
        TextView txtUpdateMobile = (TextView) findViewById(R.id.txtUpdateMobile);
        txtAttachment = (TextView) findViewById(R.id.txtAttachment);
        rdGroupGender = (RadioGroup) findViewById(R.id.rdGroupGender);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtMiddleName = (EditText) findViewById(R.id.edtMiddleName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtDOB = (EditText) findViewById(R.id.edtDOB);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtDesignation = (EditText) findViewById(R.id.edtDesignation);
        spinDepartment = (Spinner) findViewById(R.id.spinDepartment);
        lyAlumniDetails = findViewById(R.id.lyAlumni);
        lyFacultyDetails = findViewById(R.id.lyFaculty);

        rdMale = (RadioButton) findViewById(R.id.rdMale);
        rdFemale = (RadioButton) findViewById(R.id.rdFemale);

        spinCountry = (Spinner) findViewById(R.id.spinCountry);
        spinState = (Spinner) findViewById(R.id.spinState);
        spinCourse = (Spinner) findViewById(R.id.spinCourse);
        spinBranch = (Spinner) findViewById(R.id.spinBranch);
        spinJoiningYear = (Spinner) findViewById(R.id.spinJoiningYear);

        imgSelected = new ImageView(this);

        txtUpdate.setOnClickListener(this);
        txtUpdateEmail.setOnClickListener(this);
        txtUpdateMobile.setOnClickListener(this);
        txtAttachment.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        spinCountry.setOnItemSelectedListener(this);
        spinCourse.setOnItemSelectedListener(this);
        spinState.setOnItemSelectedListener(this);
        spinBranch.setOnItemSelectedListener(this);
        spinJoiningYear.setOnItemSelectedListener(this);

        countryArrayList = new ArrayList<>();
        branchArrayList = new ArrayList<>();
        stateArrayList = new ArrayList<>();
        courseArrayList = new ArrayList<>();

    }

    private void setData() {
        mMemberId = preferences.getString("MemberId", "");
        mMemberType = preferences.getString("MemberType","");
        mBranchId = preferences.getString("BranchId","");
        mCourseId = preferences.getString("CourseId","");
        mInstituteId = preferences.getString("InstituteId","");
        txtAttachment.setText(preferences.getString("ProfilePhoto",""));
        mPassword = preferences.getString("","");

        edtFirstName.setText(preferences.getString("FirstName", ""));
        edtFirstName.setEnabled(false);

        edtMiddleName.setText(preferences.getString("MiddleName", ""));
        edtMiddleName.setEnabled(false);

        edtLastName.setText(preferences.getString("LastName", ""));
        edtLastName.setEnabled(false);

        edtDOB.setText(preferences.getString("DateofBirth", ""));
        edtDOB.setEnabled(false);

        mGender = preferences.getString("Gender","");
        if(mGender.equalsIgnoreCase("Male")) {
            rdMale.setChecked(true);
            rdFemale.setEnabled(false);
        }else if(mGender.equalsIgnoreCase("Female"))
        {
            rdFemale.setChecked(true);
            rdMale.setEnabled(false);
        }

        edtEmail.setText(preferences.getString("EmailId",""));
        edtMobile.setText(preferences.getString("MobileNo",""));
        edtCity.setText(preferences.getString("CurrentLocation", ""));
        edtDesignation.setText(preferences.getString("Designation", ""));
        spinDepartment.setPrompt(preferences.getString("Department", ""));


        if(mMemberType.equalsIgnoreCase("F"))
        {
            lyAlumniDetails.setVisibility(View.GONE);
            lyFacultyDetails.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtUpdate:
                getData();
                break;

            case R.id.txtUpdateEmail:
                if(edtEmail.getText().toString().trim().equalsIgnoreCase(""))
                {
                    Constant.showToast(this, "Please enter email id");
                }else
                {
                    updateEmail_Mobile(edtEmail.getText().toString().trim(), "Email");
                    Activity_OTPValidation.mData = edtEmail.getText().toString().trim();
                    Activity_OTPValidation.mType = "Email";
                }
                break;

            case R.id.txtUpdateMobile:
                if(edtMobile.getText().toString().trim().equalsIgnoreCase(""))
                {
                    Constant.showToast(this, "Please enter Mobile Number");
                }else
                {
                    updateEmail_Mobile(edtMobile.getText().toString().trim(), "Mobile");
                    Activity_OTPValidation.mData = edtMobile.getText().toString().trim();
                    Activity_OTPValidation.mType = "Mobile";
                }
                break;

            case R.id.txtAttachment:
                loadImagefromGallery();
                break;

            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private void getData() {

        mEmail = edtEmail.getText().toString().trim();
        mMobile = edtMobile.getText().toString().trim();
        mFirstName = edtFirstName.getText().toString().trim();
        mMiddleName = edtMiddleName.getText().toString().trim();
        mLastName = edtLastName.getText().toString().trim();
        mDOB = edtDOB.getText().toString().trim();
        mCity = edtCity.getText().toString().trim();
        mDesignation = edtDesignation.getText().toString().trim();

        int selectedId = rdGroupGender.getCheckedRadioButtonId();
        RadioButton rdGenderButton = (RadioButton) findViewById(selectedId);
        mGender = rdGenderButton.getText().toString();

        if(mCity.equalsIgnoreCase(""))
        {
            Constant.showToast(Activity_PersonalDetails.this, "Enter City");
        }else if(mState.equalsIgnoreCase(""))
        {
            Constant.showToast(Activity_PersonalDetails.this, "Select state");
        }else if(mCountry.equalsIgnoreCase(""))
        {
            Constant.showToast(Activity_PersonalDetails.this, "Select country");
        }else if(mCourse.equalsIgnoreCase(""))
        {
            Constant.showToast(Activity_PersonalDetails.this, "Select course");
        }else if(mBranch.equalsIgnoreCase(""))
        {
            Constant.showToast(Activity_PersonalDetails.this, "Select branch");
        }else if(mJoiningYear.equalsIgnoreCase("")){
            Constant.showToast(Activity_PersonalDetails.this, "Select year of joining");
        }else
        {
            if(mMemberType.equalsIgnoreCase("F"))
            {
                if(mDesignation.equalsIgnoreCase(""))
                {
                    Constant.showToast(Activity_PersonalDetails.this, "Enter designation");
                }else if(mDepartment.equalsIgnoreCase(""))
                {
                    Constant.showToast(Activity_PersonalDetails.this, "Select department");
                }
            }

            UpdateMember updateMember = new UpdateMember(this, imgSelected, compressedImage);
            updateMember.execute(mMemberType, mFirstName, mMiddleName, mLastName, mMobile, mDesignation, mDepartment, mJoiningYear, mCourseId, mBranchId
                    ,mCity,mEmail, mPassword, "Email", mDOB, mState, mCountry, mGender, mInstituteId, mMemberId);
        }
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
                    Constant.showToast(Activity_PersonalDetails.this, errorMessage);
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
            Constant.showToast(Activity_PersonalDetails.this, "OTP does not match");
        }
    }


    private void setSpinCountry(ArrayList<Country> countryArrayList) {
        List<String> list = new ArrayList<>();
        list.add(preferences.getString("Country", "Country"));
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
                    Constant.showToast(Activity_PersonalDetails.this, errorMessage);
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
            Constant.showToast(Activity_PersonalDetails.this, "OTP does not match");
        }
    }


    private void setStateCountry(ArrayList<State> stateArrayList) {
        List<String> list = new ArrayList<>();
        list.add(preferences.getString("State", "State"));
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
                    Constant.showToast(Activity_PersonalDetails.this, errorMessage);
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
        list.add(preferences.getString("CourseName", "Course"));
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
                    Constant.showToast(Activity_PersonalDetails.this, errorMessage);
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
        list.add(preferences.getString("Branch", "Branch"));
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
                    Constant.showToast(Activity_PersonalDetails.this, errorMessage);
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
        list.add(preferences.getString("JoiningYear", "Joining Year"));
        list.addAll(courseArrayList);

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinJoiningYear.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spinCountry:
                mCountry = spinCountry.getSelectedItem().toString().trim();
                if(!mCountry.equalsIgnoreCase("Country") && (position != 0)) {
                    String mCountryId = countryArrayList.get(position-1).getID();
                    getStates(mCountryId);
                }
                break;

            case R.id.spinState:
                mState = spinState.getSelectedItem().toString().trim();
                break;

            case R.id.spinCourse:
                mCourse = spinCourse.getSelectedItem().toString().trim();
                if(!mCourse.equalsIgnoreCase("Course") && (position != 0)) {
                    mCourseId = courseArrayList.get(position-1).getCourseId();
                    getAllBranches(mCourseId);
                }
                break;

            case R.id.spinBranch:
                mBranch = spinBranch.getSelectedItem().toString().trim();
                if(!mBranch.equalsIgnoreCase("Branch") && (position != 0)){
                    mBranchId = branchArrayList.get(position-1).getBranchId();
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


    private void updateEmail_Mobile(String updateData, String type)
    {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("MemberId", mMemberId);
        requestParams.put("OTPFor", type);
        requestParams.put("EmailIdOrMobile", updateData);

        client.post(Constant.CHECK_EMAIL_MOBILE, requestParams, new AsyncHttpResponseHandler() {

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
                        Constant.showToast(Activity_PersonalDetails.this, "Failed to update");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Constant.showToast(Activity_PersonalDetails.this, "Failed to update");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    progressDialog.dismiss();
                    String errorMessage = new String(responseBody, "UTF-8");
                    Constant.showToast(Activity_PersonalDetails.this, errorMessage);
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

    private void parseResult(String str) throws JSONException{
        hideKeyboard();
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if (response.equalsIgnoreCase("Success")) {
            Intent intent = new Intent(Activity_PersonalDetails.this, Activity_OTPValidation.class);
            intent.putExtra("NavFrom", "FromProfile");
            startActivity(intent);
        }else if(response.equalsIgnoreCase("Fail"))
        {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray addOTPForUpdateEmailMobile = obj1.getJSONArray("addOTPForUpdateEmailMobile");
            JSONObject obj11 = addOTPForUpdateEmailMobile.getJSONObject(0);
            String result = obj11.getString("result");
            Constant.showToast(this, result);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            if (data == null) {
                Constant.showToast(Activity_PersonalDetails.this, "Failed to open picture!");
                return;
            }
            try {
                /*destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");*/
                actualImage = FileUtil.from(this, data.getData());
                imgSelected.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                imgSelected.setVisibility(View.VISIBLE);
                txtAttachment.setText(actualImage.getAbsolutePath());
                compressImage();
            } catch (IOException e) {
                Constant.showToast(Activity_PersonalDetails.this, "Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }


    public void compressImage() {
        if (actualImage == null) {
            Constant.showToast(Activity_PersonalDetails.this, "Please choose an image!");
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
        imgSelected.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
    }


    public void saveData()
    {
        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("MemberType",mMemberType);
        editor.putString("FirstName",mFirstName);
        editor.putString("MiddleName",mMiddleName);
        editor.putString("LastName",mLastName);
        editor.putString("MobileNo",mMobile);
        editor.putString("Designation",mDesignation);
        editor.putString("Department",mDepartment);
        editor.putString("JoiningYear",mJoiningYear);
        editor.putString("CourseId",mCourseId);
        editor.putString("ProfilePhoto",txtAttachment.getText().toString());
        editor.putString("InstituteId",mInstituteId);
        editor.putString("CurrentLocation",mCity);
        editor.putString("EmailId",mEmail);
        editor.putString("BranchId",mBranchId);
        editor.putString("DateofBirth",mDOB);
        editor.putString("State",mState);
        editor.putString("Country",mCountry);
        editor.putString("Gender",mGender);
        editor.putString("Branch", mBranch);
        editor.putString("CourseName", mCourse);

        editor.apply();
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
            edtDOB.setText(sdf.format(myCalendar.getTime()));

        } catch (Exception e) {


        }
    }
}
