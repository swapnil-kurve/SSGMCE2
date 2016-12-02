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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Companies;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.Experience;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Activity_WorkDetails extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener{

    Experience experience;
    ArrayList<Experience> experienceArrayList;

    Companies companies;
    ArrayList<Companies> companiesArrayList;
    ArrayList yearArrayList;
    private EditText edtCompany, edtDesignation, edtDesignationLevel, edtSector, edtTechnology, edtLocation;
    private String mCompany,mOrganization, mDesignation, mDesignationLevel, mSector, mTechnology, mLocation, mFromMonth = "", mFromYear = "", mToMonth = "", mToYear = "";
    private Spinner spinFromMonth, spinFromYear, spinToMonth, spinToYear, spinCompany;

    ListView listExperience;
    View lyMain, lyExperience,lyCompany;
    String mMemberId, ExpId, mCurrentlyWorking;
    private CheckBox chkNoOrganization, chkCurrentlyWorking;
    private int edtFlag = 0, validated = 1, pos;
    SetExperienceAdapter setExperienceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_details);

        Initialize();

        getExperienceData();
        getAllCompany();
        setSpinMonths("","");
        getAllYears();

    }

    private void Initialize() {
        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        TextView txtAddExperience = (TextView) findViewById(R.id.txtAddExperience);
        TextView txtSaveExperience = (TextView) findViewById(R.id.txtSave);
        TextView txtCancel = (TextView) findViewById(R.id.txtCancel);
        lyMain = findViewById(R.id.lyMain);
        lyExperience = findViewById(R.id.lyInclude);
        listExperience = (ListView) findViewById(R.id.listExperience);
        chkNoOrganization = (CheckBox) findViewById(R.id.chkNoOrganization);
        chkCurrentlyWorking = (CheckBox) findViewById(R.id.chkCurrentlyHere);
        lyCompany = findViewById(R.id.lyCompany);

        edtCompany = (EditText) findViewById(R.id.edtCompany);
        edtDesignation = (EditText) findViewById(R.id.edtDesignation);
        edtDesignationLevel = (EditText) findViewById(R.id.edtDesignationLevel);
        edtSector = (EditText) findViewById(R.id.edtSector);
        edtTechnology = (EditText) findViewById(R.id.edtTechnology);
        edtLocation = (EditText) findViewById(R.id.edtLocation);

        spinFromMonth = (Spinner) findViewById(R.id.spinFromMonth);
        spinFromYear = (Spinner) findViewById(R.id.spinFromYear);
        spinToMonth = (Spinner) findViewById(R.id.spinToMonth);
        spinToYear = (Spinner) findViewById(R.id.spinToYear);
        spinCompany = (Spinner) findViewById(R.id.spinCompany);

        spinFromMonth.setOnItemSelectedListener(this);
        spinFromYear.setOnItemSelectedListener(this);
        spinToMonth.setOnItemSelectedListener(this);
        spinToYear.setOnItemSelectedListener(this);
        spinCompany.setOnItemSelectedListener(this);

        experienceArrayList = new ArrayList<>();
        companiesArrayList = new ArrayList<>();

        imgBack.setOnClickListener(this);
        txtAddExperience.setOnClickListener(this);
        txtSaveExperience.setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        chkNoOrganization.setOnCheckedChangeListener(this);
        chkCurrentlyWorking.setOnCheckedChangeListener(this);

        chkCurrentlyWorking.setChecked(false);
        listExperience.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtAddExperience:
                edtFlag = 0;
                clearData();
                showLayout();
                setSpinCompanies("");
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


    private void hideLayout() {
        lyMain.setVisibility(View.VISIBLE);
        lyExperience.setVisibility(View.GONE);

        getExperienceData();
    }

    private void showLayout() {
        lyMain.setVisibility(View.GONE);
        lyExperience.setVisibility(View.VISIBLE);
    }

    private void getExperienceData() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("MemberId", mMemberId);

        client.get(Constant.GET_WORK, requestParams, new AsyncHttpResponseHandler() {

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
                        parseWorkResult(str);
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
                    Constant.showToast(Activity_WorkDetails.this, errorMessage);
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

    private void parseWorkResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            experienceArrayList.clear();
            listExperience.setVisibility(View.VISIBLE);
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getMemberWorkExperience");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String ExpId = obj10.optString("ExpId");
                String Organization = obj10.optString("Organization");
                String Designation = obj10.optString("Designation");
                String DesignationLevel = obj10.optString("DesignationLevel");
                String Sector = obj10.optString("Sector");
                String Location = obj10.optString("Location");
                String FromMonth = obj10.optString("FromMonth");
                String FromYear = obj10.optString("FromYear");
                String ToMonth = obj10.optString("ToMonth");
                String ToYear = obj10.optString("ToYear");
                String technology = obj10.optString("technology");

                experience = new Experience(ExpId,Organization,Designation,DesignationLevel,Sector,Location,FromMonth,FromYear,ToMonth,ToYear, technology);
                experienceArrayList.add(experience);
            }

            setExperienceAdapter = new SetExperienceAdapter(Activity_WorkDetails.this, experienceArrayList);
            listExperience.setAdapter(setExperienceAdapter);
        } else {
            listExperience.setVisibility(View.GONE);
            Constant.showToast(Activity_WorkDetails.this, "No details found");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
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

            case R.id.spinCompany:
                mOrganization = spinCompany.getSelectedItem().toString();
                break;
        }

        if(!chkCurrentlyWorking.isChecked())
            validateMonthYear();
    }

    private void validateMonthYear()
    {
        if (!mFromMonth.equalsIgnoreCase("") && !mFromYear.equalsIgnoreCase("") &&
                !mToMonth.equalsIgnoreCase("") && !mToYear.equalsIgnoreCase("")) {

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

    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chkNoOrganization:
                if (isChecked) {
                    getAllCompany();
                    spinCompany.setEnabled(false);
                    lyCompany.setVisibility(View.VISIBLE);
                } else {
                    edtCompany.setText("");
                    lyCompany.setVisibility(View.GONE);
                    spinCompany.setEnabled(true);
                }
                break;

            case R.id.chkCurrentlyHere:
                if (isChecked) {
                    mCurrentlyWorking = "true";
                    spinToMonth.setVisibility(View.GONE);
                    spinToYear.setVisibility(View.GONE);
                    mToMonth = "0";
                    mToYear = "0";
                } else {
                    mCurrentlyWorking = "false";
                    spinToMonth.setVisibility(View.VISIBLE);
                    spinToYear.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    private class SetExperienceAdapter extends BaseAdapter {
        Context context;
        ArrayList<Experience> experienceArrayList;

        public SetExperienceAdapter(Activity_WorkDetails activity_workDetails, ArrayList<Experience> experienceArrayList) {
            this.context = activity_workDetails;
            this.experienceArrayList = experienceArrayList;
        }

        @Override
        public int getCount() {
            return experienceArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return experienceArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_work, null);

            TextView txtOrganization = (TextView) convertView.findViewById(R.id.txtOrganization);
            TextView txtDesignation = (TextView) convertView.findViewById(R.id.txtDesignation);
            TextView txtSector = (TextView) convertView.findViewById(R.id.txtSector);
            TextView txtLocation = (TextView) convertView.findViewById(R.id.txtLocation);
            ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);

            txtOrganization.setText(experienceArrayList.get(position).getOrganization());
            txtDesignation.setText(experienceArrayList.get(position).getDesignation());
            txtSector.setText(experienceArrayList.get(position).getSector());
            txtLocation.setText(experienceArrayList.get(position).getLocation());

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWork(experienceArrayList.get(position).getExpId());
                }
            });



            return convertView;
        }
    }

    private void deleteWork(String expId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("ExpId", expId);
        requestParams.add("MemberId", mMemberId);

        client.get(Constant.DELETE_WORK_EXP, requestParams, new AsyncHttpResponseHandler() {

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
                        parseDeleteWorkResult(str);
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
                    Constant.showToast(Activity_WorkDetails.this, errorMessage);
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

    private void parseDeleteWorkResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            Constant.showToast(this, "Delete Successfully !");
            getExperienceData();
        }else{
            Constant.showToast(this, "Failed to delete !");
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        edtFlag = 1;
        clearData();
        pos = position;
        ExpId = experienceArrayList.get(position).getExpId();
        setSpinCompanies(experienceArrayList.get(position).getOrganization());
        edtDesignation.setText(experienceArrayList.get(position).getDesignation());
        edtDesignationLevel.setText(experienceArrayList.get(position).getDesignationLevel());
        edtLocation.setText(experienceArrayList.get(position).getLocation());
        edtSector.setText(experienceArrayList.get(position).getSector());
        edtTechnology.setText(experienceArrayList.get(position).getTechnology());

        setSpinMonths(Constant.getMonth(Integer.parseInt(experienceArrayList.get(position).getFromMonth())),Constant.getMonth(Integer.parseInt(experienceArrayList.get(position).getToMonth())));
        setYearBranches(yearArrayList,experienceArrayList.get(position).getFromYear(),experienceArrayList.get(position).getToYear());

        showLayout();
    }


    private void getData(){
    mCompany = edtCompany.getText().toString().trim();
    mDesignation = edtDesignation.getText().toString().trim();
    mDesignationLevel = edtDesignationLevel.getText().toString().trim();
    mSector = edtSector.getText().toString().trim();
    mTechnology = edtTechnology.getText().toString().trim();
    mLocation = edtLocation.getText().toString().trim();

        if(mFromMonth.equalsIgnoreCase("") || mFromMonth.equalsIgnoreCase("0"))
        {
            mFromMonth = experienceArrayList.get(pos).getFromMonth();
        }

        if(mToMonth.equalsIgnoreCase("") || mToMonth.equalsIgnoreCase("0"))
        {
            mToMonth = experienceArrayList.get(pos).getFromMonth();
        }

    if (mOrganization.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Please select Organization");
    } else if (mDesignation.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Please enter Designation");
    } else if (mDesignationLevel.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Please enter Designation level");
    } else if(mFromMonth.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Enter from month");
    }else if(mFromYear.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Enter from year");
    }else if(mToMonth.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Enter to month");
    }else if(mToYear.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Enter to year");
    }else if (mSector.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Please enter Sector");
    } else if (mTechnology.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Please enter Technology");
    } else if (mLocation.equalsIgnoreCase("")) {
        Constant.showToast(Activity_WorkDetails.this, "Please enter Location");
    }else if (chkNoOrganization.isChecked()) {
        if (mCompany.equalsIgnoreCase(""))
            Constant.showToast(Activity_WorkDetails.this, "Please enter Company Name");
        else {
            if(!chkCurrentlyWorking.isChecked())
                validateMonthYear();
            mOrganization = "Other";
            if(validated == 1) {
                if (edtFlag == 0) {
                    AddExperience(Constant.ADD_WORK, "Add");
                } else {
                    AddExperience(Constant.UPDATE_WORK, "Update");
                }
            }
        }
    } else {
        if(!chkCurrentlyWorking.isChecked())
            validateMonthYear();
        if(validated == 1) {
            if (edtFlag == 0) {
                AddExperience(Constant.ADD_WORK, "Add");
            } else {
                AddExperience(Constant.UPDATE_WORK, "Update");
            }
        }
    }
}




    private void AddExperience(String url, String type) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("MemberId", mMemberId);
        requestParams.add("Organization", mOrganization);
        requestParams.add("CompanyName", mCompany);
        requestParams.add("Designation", mDesignation);
        requestParams.add("DesignationLevel", mDesignationLevel);
        requestParams.add("Sector", mSector);
        requestParams.add("Location", mLocation);
        requestParams.add("FromMonth", mFromMonth);
        requestParams.add("FromYear", mFromYear);
        requestParams.add("ToMonth", mToMonth);
        requestParams.add("ToYear", mToYear);
        requestParams.add("CurrentlyHere", mCurrentlyWorking);
        requestParams.add("technology", mTechnology);
        if(type.equalsIgnoreCase("Update"))
            requestParams.add("ExpId", ExpId);

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
                        parseExperienceResult(str);
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
                    Constant.showToast(Activity_WorkDetails.this, errorMessage);
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

    private void parseExperienceResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            Constant.showToast(Activity_WorkDetails.this, "Successful!");
            hideLayout();
        }else
        {
            Constant.showToast(Activity_WorkDetails.this, "Failed to add work details");
            hideLayout();
        }
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
                    Constant.showToast(Activity_WorkDetails.this, errorMessage);
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

    private void setSpinCompanies(String organization) {
        List<String> list = new ArrayList<>();
        if(!organization.equalsIgnoreCase(""))
            list.add(organization);
        else
            list.add("Organization");
        for (int i = 1; i < companiesArrayList.size(); i++) {
                list.add(companiesArrayList.get(i).getOrganization());
        }
        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinCompany.setAdapter(dataAdapter);
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
                    Constant.showToast(Activity_WorkDetails.this, errorMessage);
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



    private void clearData(){
        edtDesignation.setText("");
        edtDesignationLevel.setText("");
        edtTechnology.setText("");
        edtSector.setText("");
        edtLocation.setText("");
        edtCompany.setText("");

        setSpinMonths("","");
        setSpinCompanies("");
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
