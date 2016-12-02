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
import com.alumni.ssgmce.activity.Activity_Settings;
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

public class Activity_EmailValidation extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_validation);

        Initialize();

    }

    private void Initialize() {
        edtEmail = (EditText) findViewById(R.id.edtEmailId);

        TextView txtNext = (TextView) findViewById(R.id.txtNext);

        txtNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtNext:
                if(edtEmail.getText().toString().trim().equalsIgnoreCase(""))
                {
                  Constant.showToast(this, "Please enter email id");
                }else if(!Constant.isValidEmail(edtEmail.getText().toString().trim())){
                    Constant.showToast(this, "Please enter valid email id");
                }else{
                    mEmail = edtEmail.getText().toString().trim();
                    CheckEmail(mEmail);
                }
                break;
        }
    }

    private void CheckEmail(String email) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("EmailId", email);

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
                    Constant.showToast(Activity_EmailValidation.this, errorMessage);
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


                editor.apply();

                SendOtp(EmailId);
            }
        }else{
            Intent intent = new Intent(Activity_EmailValidation.this, Activity_Registration.class );
            intent.putExtra("UserEmail", mEmail);
            intent.putExtra("NavFrom", "EmailValidation");
            startActivity(intent);
            finish();
        }
    }

    private void SendOtp(String emailId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("emailId", emailId);

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
                    Constant.showToast(Activity_EmailValidation.this, errorMessage);
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
            Intent intent = new Intent(Activity_EmailValidation.this, Activity_OTPValidation.class);
            intent.putExtra("NavFrom","FromEmail");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Activity_SignUp.class));
        finish();
    }
}
