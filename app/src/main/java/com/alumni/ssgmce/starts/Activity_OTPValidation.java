package com.alumni.ssgmce.starts;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Activity_OTPValidation extends AppCompatActivity implements View.OnClickListener {

    EditText edtOTP;
    String mEmailId, mNavFrom = "", mMemberId;
    public static String mData, mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);

        Initialize();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("Email"))
        {
            mEmailId = bundle.getString("Email","");
        }
        if(bundle != null && bundle.containsKey("NavFrom"))
        {
            mNavFrom = bundle.getString("NavFrom","");
        }
    }

    private void Initialize() {
        edtOTP = (EditText) findViewById(R.id.edtOtp);

        TextView txtNext = (TextView) findViewById(R.id.txtNext);

        txtNext.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mEmailId = preferences.getString("EmailId", "");
        mMemberId = preferences.getString("MemberId", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtNext:
                if(mNavFrom.equalsIgnoreCase("FromProfile")){
                    VerifyOTPP_profile(edtOTP.getText().toString().trim());
                }else {
                    VerifyOTP(edtOTP.getText().toString().trim());
                }
                break;
        }
    }


    private void VerifyOTPP_profile(String otp) {

        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("MemberId", mMemberId);
        requestParams.put("OTPFor", mType);
        requestParams.put("EmailIdOrMobile", mData);
        requestParams.put("OTP", otp);

        client.post(Constant.VERIFY_OTP, requestParams, new AsyncHttpResponseHandler() {

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
                        parseOTPResult(str);
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
                    Constant.showToast(Activity_OTPValidation.this, errorMessage);
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

    private void parseOTPResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if(response.equals("Success"))
        {
            SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(mType.equalsIgnoreCase("Email"))
                editor.putString("EmailId", mData);
            else
                editor.putString("MobileNo", mData);
            editor.apply();
            onBackPressed();
        }else
        {
            Constant.showToast(Activity_OTPValidation.this, "OTP does not match");
        }
    }

    private void VerifyOTP(String otp) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("EmailId", mEmailId);
        requestParams.put("OTP", otp);

        client.post(Constant.VERIFY_OTP_FOR_EMAIL, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_OTPValidation.this, errorMessage);
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
        String response = obj01.getString("response");

        if(response.equals("Success"))
        {
            getUserDetails(mEmailId);
        }else
        {
            Constant.showToast(Activity_OTPValidation.this, "OTP does not match");
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
                        parseResult1(str);
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
                    Constant.showToast(Activity_OTPValidation.this, errorMessage);
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



    private void parseResult1(String str) throws JSONException {
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

                editor.putString("IsLoggedIn", "Yes");
                editor.apply();

                Intent intent = new Intent(this, Activity_Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if(mNavFrom.equalsIgnoreCase("FromEmail")){
//            startActivity(new Intent(this, Activity_EmailValidation.class));
//            finish();
            super.onBackPressed();
        }else if(mNavFrom.equalsIgnoreCase("FromRegistration")){
            startActivity(new Intent(this, Activity_Registration.class));
        }else
        {
            super.onBackPressed();
        }
    }
}
