package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Batchmates;
import com.alumni.ssgmce.classes.Branches;
import com.alumni.ssgmce.classes.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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

public class Activity_People extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    TextView txtMyBatch, txtJunior, txtSenior, txtFaculty;
    EditText edtName, edtLocation;
    Spinner spinCourse, spinBranch;
    String mMemberId = "", mJoiningYear = "", mUsername = "", mLocation = "", mCourse = "", mBranch = "";
    Batchmates batchmates;
    private int flag = 0;
    Branches branches;
    ArrayList<Branches> branchesArrayList;
    private String mSearchURL = Constant.SEARCH_BATCHMATES;
    private  TextView txtLabel;
    private String mGroupId = "";
    View lySearch;
    ArrayList<Batchmates> batchmatesArrayList;
    ListView listPeoples;
    private SetBatchmatesAdapter setBatchmatesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        Initialize();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("Search"))
        {
            String searchValue = bundle.getString("Search");
            if(searchValue.equalsIgnoreCase("Yes"))
            {
                lySearch.setVisibility(View.VISIBLE);
            }
            if(bundle.containsKey("GroupId")){
                mGroupId = bundle.getString("GroupId");
            }
        }

        getMyBatchmates(Constant.GET_ALL_BATCHMATES, 0);

        getAllBranches();
        getAllCourses();
    }

    private void Initialize() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        ImageView imgSearch = (ImageView) findViewById(R.id.imgSearch);
        TextView txtSearch = (TextView) findViewById(R.id.txtSearch);
        TextView txtCancel = (TextView) findViewById(R.id.txtCancel);
        txtLabel = (TextView) findViewById(R.id.txtLabel);
        txtMyBatch = (TextView) findViewById(R.id.txtMyBatch);
        txtJunior = (TextView) findViewById(R.id.txtJunior);
        txtSenior = (TextView) findViewById(R.id.txtSenior);
        txtFaculty = (TextView) findViewById(R.id.txtFaculty);
        listPeoples = (ListView) findViewById(R.id.listPeoples);
        lySearch = findViewById(R.id.lySearch);
        edtName = (EditText) findViewById(R.id.edtName);
        edtLocation = (EditText) findViewById(R.id.edtLocation);
        spinBranch = (Spinner) findViewById(R.id.spinBranch);
        spinCourse = (Spinner) findViewById(R.id.spinCourse);

        imgBack.setOnClickListener(this);
        txtMyBatch.setOnClickListener(this);
        txtJunior.setOnClickListener(this);
        txtSenior.setOnClickListener(this);
        txtFaculty.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        txtSearch.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        spinCourse.setOnItemSelectedListener(this);
        spinBranch.setOnItemSelectedListener(this);
        batchmatesArrayList = new ArrayList<>();
        branchesArrayList = new ArrayList<>();
        listPeoples.setOnItemClickListener(this);

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");
        mJoiningYear = preferences.getString("JoiningYear", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtMyBatch:
                txtLabel.setText("My Batch");
                flag = 0;
                changeToMyBatch();
                break;

            case R.id.txtJunior:
                txtLabel.setText("Juniors");
                flag = 1;
                changeToJunior();
                break;

            case R.id.txtSenior:
                txtLabel.setText("Seniors");
                flag = 2;
                changeToSenior();
                break;

            case R.id.txtFaculty:
                txtLabel.setText("Faculty");
                flag = 3;
                changeToFaculty();
                break;

            case R.id.imgSearch:
                lySearch.setVisibility(View.VISIBLE);
                break;

            case R.id.txtCancel:
                lySearch.setVisibility(View.GONE);
                break;

            case R.id.txtSearch:
                if(flag == 0)
                    mSearchURL = Constant.SEARCH_BATCHMATES;
                else if(flag == 1)
                    mSearchURL = Constant.SEARCH_JUNIORS;
                else if(flag == 2)
                    mSearchURL = Constant.SEARCH_SENIORS;
                else
                    mSearchURL = Constant.SEARCH_FACULTY;

                getDataForSearch();

                break;
        }
    }

    private void getDataForSearch() {
        if(edtName.getText().toString().trim().equalsIgnoreCase("") &&
                edtName.toString().trim().equalsIgnoreCase("") &&
                mCourse.equalsIgnoreCase("") && mBranch.equalsIgnoreCase(""))
        {
            Constant.showToast(Activity_People.this, "Please enter at least one of the above field");
        }else {
            mUsername = edtName.getText().toString().trim();
            mLocation = edtLocation.getText().toString().trim();
            if(mBranch.equalsIgnoreCase("Branch"))
                mBranch = "";
            if(mCourse.equalsIgnoreCase("Course"))
                mCourse = "";
            SearchPeople(mSearchURL, flag);
        }
    }

    private void changeToMyBatch() {
        txtMyBatch.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtMyBatch.setTextColor(getResources().getColor(R.color.theme_blue));
        txtJunior.setBackgroundDrawable(null);
        txtJunior.setTextColor(Color.WHITE);
        txtSenior.setBackgroundDrawable(null);
        txtSenior.setTextColor(Color.WHITE);
        txtFaculty.setBackgroundDrawable(null);
        txtFaculty.setTextColor(Color.WHITE);

        getMyBatchmates(Constant.GET_ALL_BATCHMATES, 0);
}

    private void changeToJunior() {
        txtJunior.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtJunior.setTextColor(getResources().getColor(R.color.theme_blue));
        txtMyBatch.setBackgroundDrawable(null);
        txtMyBatch.setTextColor(Color.WHITE);
        txtSenior.setBackgroundDrawable(null);
        txtSenior.setTextColor(Color.WHITE);
        txtFaculty.setBackgroundDrawable(null);
        txtFaculty.setTextColor(Color.WHITE);

        getMyBatchmates(Constant.GET_ALL_JUNIORS, 1);
    }

    private void changeToSenior() {
        txtSenior.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtSenior.setTextColor(getResources().getColor(R.color.theme_blue));
        txtJunior.setBackgroundDrawable(null);
        txtJunior.setTextColor(Color.WHITE);
        txtMyBatch.setBackgroundDrawable(null);
        txtMyBatch.setTextColor(Color.WHITE);
        txtFaculty.setBackgroundDrawable(null);
        txtFaculty.setTextColor(Color.WHITE);

        getMyBatchmates(Constant.GET_ALL_SENIORS, 2);
    }

    private void changeToFaculty() {
        txtFaculty.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtFaculty.setTextColor(getResources().getColor(R.color.theme_blue));
        txtJunior.setBackgroundDrawable(null);
        txtJunior.setTextColor(Color.WHITE);
        txtSenior.setBackgroundDrawable(null);
        txtSenior.setTextColor(Color.WHITE);
        txtMyBatch.setBackgroundDrawable(null);
        txtMyBatch.setTextColor(Color.WHITE);

        getMyBatchmates(Constant.GET_ALL_FACULTY, 3);
    }


    private void getMyBatchmates(String url, final int i)
    {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        if(i == 0) {
            requestParams.put("memberId", mMemberId);
            requestParams.put("JoiningYear", mJoiningYear);
            requestParams.put("grpId", mGroupId);
        }else if(i == 1)
        {
            requestParams.put("JoiningYear", mJoiningYear);
            requestParams.put("grpId", mGroupId);
        }else if(i == 2)
        {
            requestParams.put("JoiningYear", mJoiningYear);
            requestParams.put("grpId", mGroupId);
        }else {
            requestParams.put("memberId", mMemberId);
            requestParams.put("JoiningYear", mMemberId);
            requestParams.put("grpId", mGroupId);
        }

        client.post(url, requestParams, new AsyncHttpResponseHandler() {

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
                        parseMyBatchResult(str, i);
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
                    Constant.showToast(Activity_People.this, errorMessage);
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

    private void parseMyBatchResult(String str, int val) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            listPeoples.setVisibility(View.VISIBLE);
            if(batchmatesArrayList.size() > 0)
                batchmatesArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts;
            if(val == 0) {
                getWallPosts = obj1.getJSONArray("getBatchmates");
            }else if(val == 1)
            {
                getWallPosts = obj1.getJSONArray("getJuniors");
            }else if(val == 2)
            {
                getWallPosts = obj1.getJSONArray("getSeniors");
            }else {
                getWallPosts = obj1.getJSONArray("getFaculty");
            }

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String memberID = obj10.optString("memberID");
                String FullName = obj10.optString("FullName");
                String CourseName = obj10.optString("CourseName");
                String CurrentLocation = obj10.optString("CurrentLocation");
                String profilepic = obj10.optString("profilepic");
                String Branch = obj10.optString("Branch");
                String JoiningYear = obj10.optString("JoiningYear");

                batchmates = new Batchmates(memberID,FullName,CourseName,CurrentLocation,profilepic,Branch,JoiningYear);
                batchmatesArrayList.add(batchmates);
            }
            setBatchmatesAdapter = new SetBatchmatesAdapter(Activity_People.this, batchmatesArrayList);
            listPeoples.setAdapter(setBatchmatesAdapter);
        }else
        {
            Constant.showToast(Activity_People.this, "No result Found");
            listPeoples.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinBranch)
        {
            mBranch = spinBranch.getSelectedItem().toString();
        }else if(parent.getId() == R.id.spinCourse)
        {
            mCourse = spinCourse.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.listPeoples)
        {
            Intent intent = new Intent(this, Activity_SendMessage.class);
            intent.putExtra("ToMemberId", batchmatesArrayList.get(position).getMemberID());
            intent.putExtra("MessageID", "0");
            intent.putExtra("Object", batchmatesArrayList.get(position));
            startActivity(intent);
        }
    }


    private class SetBatchmatesAdapter extends BaseAdapter
    {
        Context context;
        ArrayList<Batchmates> batchmatesArrayList;
        public SetBatchmatesAdapter(Activity_People activity_people, ArrayList<Batchmates> batchmatesArrayList) {
            this.context = activity_people;
            this.batchmatesArrayList = batchmatesArrayList;
        }

        @Override
        public int getCount() {
            return batchmatesArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return batchmatesArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_people_list,null);

            final ImageView imgPeople = (ImageView) convertView.findViewById(R.id.imgPeople);
            TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
            TextView txtQualification = (TextView) convertView.findViewById(R.id.txtQualification);
            TextView txtLocation = (TextView) convertView.findViewById(R.id.txtLocation);

            if(!batchmatesArrayList.get(position).getProfilepic().equalsIgnoreCase("")) {
                Glide.with(Activity_People.this).load(Constant.IMG_PATH + batchmatesArrayList.get(position).getProfilepic()).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgPeople) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(90);
                        imgPeople.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }

            txtName.setText(batchmatesArrayList.get(position).getFullName());
            txtLocation.setText(batchmatesArrayList.get(position).getCurrentLocation());
            txtQualification.setText(batchmatesArrayList.get(position).getCourseName());

            return convertView;
        }
    }



    private void SearchPeople(String url, final int i)
    {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("JoiningYear", mJoiningYear);
        requestParams.put("FullName", mUsername);
        requestParams.put("CourseName", mCourse);
        requestParams.put("Branch", mBranch);
        requestParams.put("CurrentLocation", mLocation);
        requestParams.put("grpId", mGroupId);
        if(i == 0 || i == 3) {
            requestParams.put("memberId", mMemberId);
        }

        client.post(url, requestParams, new AsyncHttpResponseHandler() {

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
                        parseSearchResult(str, i);
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
                    Constant.showToast(Activity_People.this, errorMessage);
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


    private void parseSearchResult(String str, int val) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            listPeoples.setVisibility(View.VISIBLE);
            if(batchmatesArrayList.size() > 0)
                batchmatesArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts;
            if(val == 0) {
                getWallPosts = obj1.getJSONArray("getBatchmatesbysearch");
            }else if(val == 1)
            {
                getWallPosts = obj1.getJSONArray("getJuniorsbysearch");
            }else if(val == 2)
            {
                getWallPosts = obj1.getJSONArray("getSeniorsbysearch");
            }else {
                getWallPosts = obj1.getJSONArray("getFacultybysearch");
            }

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String memberID = obj10.optString("memberID");
                String FullName = obj10.optString("FullName");
                String CourseName = obj10.optString("CourseName");
                String CurrentLocation = obj10.optString("CurrentLocation");
                String profilepic = obj10.optString("profilepic");
                String Branch = obj10.optString("Branch");
                String JoiningYear = obj10.optString("JoiningYear");

                batchmates = new Batchmates(memberID,FullName,CourseName,CurrentLocation,profilepic,Branch,JoiningYear);
                batchmatesArrayList.add(batchmates);
            }
            setBatchmatesAdapter = new SetBatchmatesAdapter(Activity_People.this, batchmatesArrayList);
            listPeoples.setAdapter(setBatchmatesAdapter);
            lySearch.setVisibility(View.GONE);
        }else
        {
            Constant.showToast(Activity_People.this, "No result Found");
            listPeoples.setVisibility(View.GONE);
        }
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
                    Constant.showToast(Activity_People.this, errorMessage);
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


    private void parseBranchResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getAllBranches");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String CourseId = obj10.optString("CourseId");
                String BranchId = obj10.optString("BranchId");
                String Branch = obj10.optString("Branch");

                branches = new Branches(CourseId,BranchId,Branch);
                branchesArrayList.add(branches);
            }
            setSpinBranches();
        }
    }


    private void setSpinBranches() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < branchesArrayList.size(); i++) {
            if(i == 0)
                list.add("Branch");
            else
                list.add(branchesArrayList.get(i).getBranch());
        }
        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinBranch.setAdapter(dataAdapter);
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
                    Constant.showToast(Activity_People.this, errorMessage);
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
            ArrayList courseArrayList = new ArrayList();
            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String CourseName = obj10.optString("CourseName");
                courseArrayList.add(CourseName);
            }
            setSpinCourses(courseArrayList);
        }
    }


    private void setSpinCourses(ArrayList courseArrayList) {
        List<String> list = new ArrayList<>();
        list.add("Course");
        list.addAll(courseArrayList);

        ArrayAdapter<String> dataAdapter = adapterForSpinner(list);
        spinCourse.setAdapter(dataAdapter);
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


}
