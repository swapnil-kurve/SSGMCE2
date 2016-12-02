package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.AddNewJob;
import com.alumni.ssgmce.classes.AvailableJobs;
import com.alumni.ssgmce.classes.Branches;
import com.alumni.ssgmce.classes.Companies;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.PostedJobs;
import com.alumni.ssgmce.classes.UpdateJob;
import com.bumptech.glide.Glide;
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

public class Activity_Jobs extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private TextView txtAvailableJobs, txtMyPostedJobs;
    private static int RESULT_LOAD_IMG = 1;
    private File actualImage;
    private File compressedImage;
    String mMemberId;
    AvailableJobs availableJobs;
    PostedJobs postedJobs;
    TextView txtAddNewJob, txtSave, txtCancel, txtAttachment, txtLastDate;
    Branches branches;
    Companies companies;
    ArrayList<Companies> companiesArrayList;
    ArrayList<Branches> branchesArrayList;
    EditText edtJobTitle, edtJobDescription, edtMinExp, edtMaxExp, edtTechnology, edtLocation, edtRegistrationLink, edtCompany;
    String mJobTitle, mJobDescription, mMinExp, mMaxExp, mTechnology, mLocation, mLastDate, mCompany, mBranch, mRegLink, mOrganization;
    Spinner spinCompany, spinBranch;
    private View lyInclude, lyMain, lyCompany;
    private ArrayList<AvailableJobs> availableJobsArrayList;
    private ArrayList<PostedJobs> postedJobsArrayList;
    private SetAvailableJobsAdapter setAvailableJobsAdapter;
    private SetPostedJobsAdapter setPostedJobsAdapter;
    private ListView listJobs;
    private ImageView imgSelected;
    private int flag = 0, edtFlag = 0, pos;
    private String mJobId = "";
    private CheckBox chkNoOrganization;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        Initialize();
        getAvailableJobs();

        getAllBranches();
        getAllCompany();

        txtLastDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Activity_Jobs.this, selectedDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        edtMinExp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtMinExp.getText().toString().equalsIgnoreCase("") && !edtMaxExp.getText().toString().equalsIgnoreCase("")) {
                    if (Integer.parseInt(edtMinExp.getText().toString()) > Integer.parseInt(edtMaxExp.getText().toString())) {
                        Constant.showToast(Activity_Jobs.this, "Minimum experience should not be greater than Maximum experience.");
                    }
                }
            }
        });

        edtMaxExp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtMinExp.getText().toString().equalsIgnoreCase("") && !edtMaxExp.getText().toString().equalsIgnoreCase("")) {
                    if (Integer.parseInt(edtMinExp.getText().toString()) > Integer.parseInt(edtMaxExp.getText().toString())) {
                        Constant.showToast(Activity_Jobs.this, "Minimum experience should not be greater than Maximum experience.");
                    }
                }
            }
        });


    }

    private void getAllBranches() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();

        client.get(Constant.GET_ALL_BRANCHES, requestParams, new AsyncHttpResponseHandler() {

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
                        parseBranchResult(str);
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
                    Constant.showToast(Activity_Jobs.this, errorMessage);
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


    private void Initialize() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        txtAvailableJobs = (TextView) findViewById(R.id.txtAvailableJobs);
        txtMyPostedJobs = (TextView) findViewById(R.id.txtMyPostedJob);
        listJobs = (ListView) findViewById(R.id.listJobs);
        txtAddNewJob = (TextView) findViewById(R.id.txtAddNewJob);
        txtSave = (TextView) findViewById(R.id.txtSave);
        txtCancel = (TextView) findViewById(R.id.txtCancel);
        lyInclude = findViewById(R.id.lyInclude);
        lyMain = findViewById(R.id.lyMain);
        imgSelected = new ImageView(this);
        chkNoOrganization = (CheckBox) findViewById(R.id.chkNoOrganization);

        spinBranch = (Spinner) findViewById(R.id.spinBranch);
        spinCompany = (Spinner) findViewById(R.id.spinCompany);
        txtAttachment = (TextView) findViewById(R.id.txtAttachment);
        edtJobTitle = (EditText) findViewById(R.id.edtJobTitle);
        edtJobDescription = (EditText) findViewById(R.id.edtJobDescription);
        edtMinExp = (EditText) findViewById(R.id.edtMinExp);
        edtMaxExp = (EditText) findViewById(R.id.edtMaxExperience);
        edtTechnology = (EditText) findViewById(R.id.edtTechnology);
        edtLocation = (EditText) findViewById(R.id.edtLocation);
        txtLastDate = (TextView) findViewById(R.id.txtLastDate);
        edtRegistrationLink = (EditText) findViewById(R.id.edtRegistrationLink);
        edtCompany = (EditText) findViewById(R.id.edtCompany);
        lyCompany = findViewById(R.id.lyCompany);

        availableJobsArrayList = new ArrayList<>();
        postedJobsArrayList = new ArrayList<>();
        branchesArrayList = new ArrayList<>();
        companiesArrayList = new ArrayList<>();

        chkNoOrganization.setChecked(false);

        imgBack.setOnClickListener(this);
        txtAvailableJobs.setOnClickListener(this);
        txtMyPostedJobs.setOnClickListener(this);
        txtAddNewJob.setOnClickListener(this);
        txtSave.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtAttachment.setOnClickListener(this);
        chkNoOrganization.setOnCheckedChangeListener(this);
        spinBranch.setOnItemSelectedListener(this);
        spinCompany.setOnItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");
    }


    private void changeToAvailableJobs() {
        txtAvailableJobs.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtAvailableJobs.setTextColor(getResources().getColor(R.color.theme_blue));
        txtMyPostedJobs.setBackgroundDrawable(null);
        txtMyPostedJobs.setTextColor(Color.WHITE);
        txtAddNewJob.setVisibility(View.GONE);

        getAvailableJobs();
    }

    private void getAvailableJobs() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("MemberId", mMemberId);

        client.post(Constant.GET_AVAILABLE_JOBS, requestParams, new AsyncHttpResponseHandler() {

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
                        parseAvailableResult(str);
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
                    Constant.showToast(Activity_Jobs.this, errorMessage);
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

    private void parseAvailableResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            availableJobsArrayList.clear();
            listJobs.setVisibility(View.VISIBLE);
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getAvailableJobs");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String JobId = obj10.optString("JobId");
                String JobTitle = obj10.optString("JobTitle");
                String MinExperience = obj10.optString("MinExperience");
                String MaxExperience = obj10.optString("MaxExperience");
                String Location = obj10.optString("Location");
                String lastDate = obj10.optString("lastDate");
                String MemberName = obj10.optString("MemberName");
                String PostedDate = obj10.optString("PostedDate");
                String Attachement = obj10.optString("Attachement");

                availableJobs = new AvailableJobs(JobId, JobTitle, MinExperience, MaxExperience, Location, lastDate, MemberName, PostedDate, Attachement);
                availableJobsArrayList.add(availableJobs);
            }
            setAvailableJobsAdapter = new SetAvailableJobsAdapter(Activity_Jobs.this, availableJobsArrayList);
            listJobs.setAdapter(setAvailableJobsAdapter);
            setAvailableJobsAdapter.notifyDataSetChanged();
        }else
        {
            listJobs.setVisibility(View.GONE);
            Constant.showToast(this, "No details found");
        }
    }

    private void changeToPostedJob() {
        txtMyPostedJobs.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtMyPostedJobs.setTextColor(getResources().getColor(R.color.theme_blue));
        txtAvailableJobs.setBackgroundDrawable(null);
        txtAvailableJobs.setTextColor(Color.WHITE);

        txtAddNewJob.setVisibility(View.VISIBLE);

        getPostedJobs();
    }

    public void getPostedJobs() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("MemberId", mMemberId);

        client.post(Constant.GET_POSTED_JOBS, requestParams, new AsyncHttpResponseHandler() {

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
                        parsePostedResult(str);
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
                    Constant.showToast(Activity_Jobs.this, errorMessage);
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

    private void parsePostedResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            postedJobsArrayList.clear();
            listJobs.setVisibility(View.VISIBLE);
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getMemberAddedJobs");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String JobId = obj10.optString("JobId");
                String JobTitle = obj10.optString("JobTitle");
                String MinExperience = obj10.optString("MinExperience");
                String MaxExperience = obj10.optString("MaxExperience");
                String Location = obj10.optString("Location");
                String InsertDate = obj10.optString("InsertDate");
                String lastDate = obj10.optString("lastDate");
                String JobDescription = obj10.optString("JobDescription");
                String Attachement = obj10.optString("Attachement");
                String OrganizationId = obj10.optString("OrganizationId");
                String Organization = obj10.optString("Organization");
                String Branch = obj10.optString("Branch");
                String regLink = obj10.optString("regLink");
                String technology = obj10.optString("technology");

                postedJobs = new PostedJobs(JobId, JobTitle, MinExperience, MaxExperience, Location, InsertDate, lastDate,JobDescription,Attachement,OrganizationId,Organization,Branch,regLink,technology);
                postedJobsArrayList.add(postedJobs);
            }
            setPostedJobsAdapter = new SetPostedJobsAdapter(Activity_Jobs.this, postedJobsArrayList);
            listJobs.setAdapter(setPostedJobsAdapter);
            setPostedJobsAdapter.notifyDataSetChanged();
        }else
        {
            listJobs.setVisibility(View.GONE);
            Constant.showToast(this, "No details found");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtAvailableJobs:
                changeToAvailableJobs();
                break;

            case R.id.txtMyPostedJob:
                changeToPostedJob();
                break;

            case R.id.txtAddNewJob:
                if(Constant.mIsApproved.equalsIgnoreCase("true")){
                    edtFlag = 0;
                    clearData();
                    showLayout();
                    setSpinBranches("");
                    setSpinCompanies("");
                }else{
                    Constant.showToast(Activity_Jobs.this, "You are not approved by Admin yet");
                }
                break;

            case R.id.txtCancel:
                hideLayout();
                break;

            case R.id.txtSave:
                hideKeyboard();
                getDataFromForm();
                break;

            case R.id.txtAttachment:
                loadImagefromGallery();
                break;
        }
    }

    public void hideLayout() {
        flag = 0;
        lyMain.setVisibility(View.VISIBLE);
        lyInclude.setVisibility(View.GONE);
    }

    private void showLayout() {
        flag = 1;
        lyMain.setVisibility(View.GONE);
        lyInclude.setVisibility(View.VISIBLE);
    }

    private void clearData()
    {
        edtJobTitle.setText("");
        edtJobDescription.setText("");
        edtMinExp.setText("");
        edtMaxExp.setText("");
        edtTechnology.setText("");
        edtLocation.setText("");
        txtLastDate.setText("");
        edtRegistrationLink.setText("");
        txtAttachment.setText("");
    }

    private void getDataFromForm() {
        mJobTitle = edtJobTitle.getText().toString().trim();
        mJobDescription = edtJobDescription.getText().toString().trim();
        mOrganization = spinCompany.getSelectedItem().toString().trim();
        mMinExp = edtMinExp.getText().toString().trim();
        mMaxExp = edtMaxExp.getText().toString().trim();
        mTechnology = edtTechnology.getText().toString().trim();
        mLocation = edtLocation.getText().toString().trim();
        mLastDate = txtLastDate.getText().toString().trim();
        mRegLink = edtRegistrationLink.getText().toString().trim();
        mCompany = edtCompany.getText().toString().trim();


        if (mJobTitle.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Job Title");
        } else if (mJobDescription.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Job Description");
        } else if (mOrganization.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please select organization");
        } else if (mMinExp.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Minimum Experience");
        } else if (mMaxExp.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Maximum Experience");
        }else if (Integer.parseInt(mMinExp) > Integer.parseInt(mMaxExp)) {
            Constant.showToast(Activity_Jobs.this, "Minimum experience should not be greater than Maximum experience.");
        }else if (mBranch.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Branch");
        } else if (mTechnology.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Technology");
        } else if (mLocation.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Location");
        } else if (mLastDate.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Last Date");
        } else if (mRegLink.equalsIgnoreCase("")) {
            Constant.showToast(Activity_Jobs.this, "Please enter Registration Link");
        }else if (chkNoOrganization.isChecked()) {
            if (mCompany.equalsIgnoreCase(""))
                Constant.showToast(Activity_Jobs.this, "Please enter Company Name");
            else {
                mOrganization = "Other";
                if(edtFlag == 0) {
                    AddNewJob addNewJob = new AddNewJob(this, imgSelected, compressedImage);
                    addNewJob.execute(mMemberId, mJobTitle, mJobDescription, mOrganization, mCompany, mMinExp, mMaxExp, mTechnology, mLocation, mLastDate, mBranch, mRegLink);
                }else{
                    UpdateJob updateJob = new UpdateJob(this, imgSelected, compressedImage);
                    updateJob.execute(mMemberId, mJobTitle, mJobDescription, mOrganization, mCompany, mMinExp, mMaxExp, mTechnology, mLocation, mLastDate, mBranch, mRegLink, mJobId);
                }
            }
        } else {
            if(edtFlag == 0) {
                AddNewJob addNewJob = new AddNewJob(this, imgSelected, compressedImage);
                addNewJob.execute(mMemberId, mJobTitle, mJobDescription, mOrganization, mCompany, mMinExp, mMaxExp, mTechnology, mLocation, mLastDate, mBranch, mRegLink);
            }else{
                UpdateJob updateJob = new UpdateJob(this, imgSelected, compressedImage);
                updateJob.execute(mMemberId, mJobTitle, mJobDescription, mOrganization, mCompany, mMinExp, mMaxExp, mTechnology, mLocation, mLastDate, mBranch, mRegLink, mJobId);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            getAllCompany();
            lyCompany.setVisibility(View.VISIBLE);
            spinCompany.setEnabled(false);
        } else {
            lyCompany.setVisibility(View.GONE);
            spinCompany.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            hideKeyboard();
        }

        if (parent.getId() == R.id.spinBranch) {
            mBranch = branchesArrayList.get(position).getBranchId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class SetAvailableJobsAdapter extends BaseAdapter {
        Context context;
        ArrayList<AvailableJobs> availableJobsArrayList;

        public SetAvailableJobsAdapter(Activity_Jobs activity_jobs, ArrayList<AvailableJobs> availableJobsArrayList) {
            this.context = activity_jobs;
            this.availableJobsArrayList = availableJobsArrayList;
        }

        @Override
        public int getCount() {
            return availableJobsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return availableJobsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_available_jobs, null);

            TextView txtJobTitle = (TextView) convertView.findViewById(R.id.txtJobTitle);
            TextView txtLocation = (TextView) convertView.findViewById(R.id.txtLocation);
            TextView txtPostedOn = (TextView) convertView.findViewById(R.id.txtPostedOn);
            TextView txtMinExp = (TextView) convertView.findViewById(R.id.txtMinExp);
            TextView txtMaxExp = (TextView) convertView.findViewById(R.id.txtMaxExp);
            TextView txtJobPostedBy = (TextView) convertView.findViewById(R.id.txtJobPostedBy);
            ImageView imgAttachment = (ImageView) convertView.findViewById(R.id.imgAttachment);

            txtJobTitle.setText(availableJobsArrayList.get(position).getJobTitle());
            txtLocation.setText(availableJobsArrayList.get(position).getLocation());
            txtPostedOn.setText(availableJobsArrayList.get(position).getPostedDate());
            txtMinExp.setText(availableJobsArrayList.get(position).getMinExperience() + "yr");
            txtMaxExp.setText(availableJobsArrayList.get(position).getMaxExperience() + "yr");
            txtJobPostedBy.setText(availableJobsArrayList.get(position).getMemberName());

            if(!availableJobsArrayList.get(position).getAttachement().equalsIgnoreCase(""))
            {
                imgAttachment.setVisibility(View.VISIBLE);
                Glide.with(context).load(Constant.IMG_PATH+availableJobsArrayList.get(position).getAttachement()).into(imgAttachment);
            }

            return convertView;
        }
    }


    private class SetPostedJobsAdapter extends BaseAdapter {
        Context context;
        ArrayList<PostedJobs> postedJobsArrayList;

        public SetPostedJobsAdapter(Activity_Jobs activity_jobs, ArrayList<PostedJobs> postedJobsArrayList) {
            this.context = activity_jobs;
            this.postedJobsArrayList = postedJobsArrayList;
        }

        @Override
        public int getCount() {
            return postedJobsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return postedJobsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_posted_jobs, null);

            TextView txtJobTitle = (TextView) convertView.findViewById(R.id.txtJobTitle);
            TextView txtLocation = (TextView) convertView.findViewById(R.id.txtLocation);
            TextView txtPostedOn = (TextView) convertView.findViewById(R.id.txtPostedOn);
            TextView txtMinExp = (TextView) convertView.findViewById(R.id.txtMinExp);
            TextView txtMaxExp = (TextView) convertView.findViewById(R.id.txtMaxExp);
            TextView txtDelete = (TextView) convertView.findViewById(R.id.txtDelete);
            TextView txtEdit = (TextView) convertView.findViewById(R.id.txtEdit);
            ImageView imgAttachment = (ImageView) convertView.findViewById(R.id.imgAttachment);

            txtJobTitle.setText(postedJobsArrayList.get(position).getJobTitle());
            txtLocation.setText(postedJobsArrayList.get(position).getLocation());
            txtPostedOn.setText(postedJobsArrayList.get(position).getInsertDate().split("\\s+")[0]);
            txtMinExp.setText(postedJobsArrayList.get(position).getMinExperience() + "yr");
            txtMaxExp.setText(postedJobsArrayList.get(position).getMaxExperience() + "yr");

            if(!postedJobsArrayList.get(position).getAttachement().equalsIgnoreCase(""))
            {
                imgAttachment.setVisibility(View.VISIBLE);
                Glide.with(context).load(Constant.IMG_PATH+postedJobsArrayList.get(position).getAttachement()).into(imgAttachment);
            }

            txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Exit Application")
                            .setMessage("Are you sure you want to Exit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    DeleteSelectedJob(postedJobsArrayList.get(position).getJobId());
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });


            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLayout();
                    edtFlag = 1;
                    pos = position;
                    mJobId = postedJobsArrayList.get(position).getJobId();

                    edtJobTitle.setText(postedJobsArrayList.get(position).getJobTitle());
                    edtLocation.setText(postedJobsArrayList.get(position).getLocation());
                    edtMinExp.setText(postedJobsArrayList.get(position).getMinExperience());
                    edtMaxExp.setText(postedJobsArrayList.get(position).getMaxExperience());
                    txtLastDate.setText(postedJobsArrayList.get(position).getLastDate());
                    txtAttachment.setText(postedJobsArrayList.get(position).getAttachement());
                    edtTechnology.setText(postedJobsArrayList.get(position).getTechnology());
                    edtRegistrationLink.setText(postedJobsArrayList.get(position).getRegLink());
                    edtJobDescription.setText(postedJobsArrayList.get(position).getJobDescription());
                    setSpinBranches(postedJobsArrayList.get(position).getBranch());
                    setSpinCompanies(postedJobsArrayList.get(position).getOrganization());


                }
            });

            return convertView;
        }
    }

    private void DeleteSelectedJob(String jobId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("JobId", jobId);

        client.post(Constant.DELETE_JOBS, requestParams, new AsyncHttpResponseHandler() {

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
                        parseDeleteJobResult(str);
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
                    Constant.showToast(Activity_Jobs.this, errorMessage);
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

    private void parseDeleteJobResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            Constant.showToast(Activity_Jobs.this, "Deleted job successfully.");
            getPostedJobs();
        } else {
            Constant.showToast(Activity_Jobs.this, "Error occurred while deleting job.");
            Constant.showToast(Activity_Jobs.this, "Please try again later.");
        }
    }


    private void parseBranchResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            postedJobsArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getAllBranches");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String CourseId = obj10.optString("CourseId");
                String BranchId = obj10.optString("BranchId");
                String Branch = obj10.optString("Branch");

                branches = new Branches(CourseId, BranchId, Branch);
                branchesArrayList.add(branches);
            }
            setSpinBranches("");
        }
    }

    private void setSpinBranches(String branch) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < branchesArrayList.size(); i++) {
            if (i == 0) {
                if(branch.equalsIgnoreCase(""))
                    list.add("Branch");
                else
                    list.add(branch);
            }
            else
                list.add(branchesArrayList.get(i).getBranch());
        }
        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinBranch.setAdapter(dataAdapter);
    }


    private void getAllCompany() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();

        client.get(Constant.GET_ALL_COMPANY, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_Jobs.this, errorMessage);
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
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getOrganizations");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String OrganizationId = obj10.optString("OrganizationId");
                String Organization = obj10.optString("Organization");

                companies = new Companies(OrganizationId, Organization);
                companiesArrayList.add(companies);
            }
            setSpinCompanies("");
        }
    }

    private void setSpinCompanies(String company) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < companiesArrayList.size(); i++) {
            if (i == 0) {
                if(company.equalsIgnoreCase(""))
                    list.add("Organization");
                else
                    list.add(company);
            }
            else
                list.add(companiesArrayList.get(i).getOrganization());
        }
        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinCompany.setAdapter(dataAdapter);
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
                Constant.showToast(Activity_Jobs.this, "Failed to open picture!");
                return;
            }
            try {
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                actualImage = FileUtil.from(this, data.getData());
                imgSelected.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                txtAttachment.setText(actualImage.getAbsolutePath());
                compressImage();
            } catch (IOException e) {
                Constant.showToast(Activity_Jobs.this, "Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }


    public void compressImage() {
        if (actualImage == null) {
            Constant.showToast(Activity_Jobs.this, "Please choose an image!");
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


    @Override
    public void onBackPressed() {
        if(flag == 1){
            hideKeyboard();
            hideLayout();
        }else{
            super.onBackPressed();
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
        String calDate = sdf.format(myCalendar.getTime());
        try {
//                if (!sdf.parse(currentDateAndTime).before(sdf.parse(calDate)) && !sdf.parse(currentDateAndTime).equals(sdf.parse(calDate))) {
            txtLastDate.setText(sdf.format(myCalendar.getTime()));

//                } else {
//                    Constants.showToast(getActivity(), getString(R.string.from_date_should_not_less));
//                }

        } catch (Exception e) {


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


}
