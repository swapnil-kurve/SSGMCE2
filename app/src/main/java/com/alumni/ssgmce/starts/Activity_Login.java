package com.alumni.ssgmce.starts;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Activity_Login extends AppCompatActivity implements View.OnClickListener {
    EditText edtEmail, edtPassword;
    TextView txtForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialize();
    }

    private void Initialize() {
        edtEmail = (EditText) findViewById(R.id.edtEmailId);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnSignIn:
                getData();
            break;

            case R.id.txtForgotPassword:
                startActivity(new Intent(this, Activity_ForgotPassword.class));
                break;
        }
    }

    private void getData() {
        String mEmail = edtEmail.getText().toString().trim();
        String mPassword = edtPassword.getText().toString();

        if(mEmail.equals(""))
        {
            Constant.showToast(this, "Enter your email id");
        }else if(mPassword.equals(""))
        {
            Constant.showToast(this, "Enter your Password");
        }else
        {
            checkUser(mEmail, mPassword);
        }
    }


    /**
     * check users credentials
     * @param mEmail
     * @param mPassword
     */

    private void checkUser(String mEmail, String mPassword) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("EmailId",mEmail);
        requestParams.put("password",mPassword);

        client.post(Constant.STUDENT_HISTORY_URL,requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200)
                {
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
                    Constant.showToast(Activity_Login.this, errorMessage);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });
    }


    /**
     *
     * @param str // Parse data
     */
    private void parseResult(String str) throws JSONException {

        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if(response.equals("Success"))
        {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getMemberLoginDetails = obj1.getJSONArray("getMemberLoginDetails");

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
                String lastSignIn = obj10.optString("lastSignIn1");
                String approvalDate = obj10.optString("approvalDate1");
                String Branch = obj10.optString("Branch");
                String CourseName = obj10.optString("CourseName");

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
                editor.putString("IsLoggedIn", "Yes");
                editor.putString("Branch", Branch);
                editor.putString("CourseName", CourseName);

                editor.apply();

                Intent intent = new Intent(this, Activity_Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }else{
            Constant.showToast(Activity_Login.this, "Invalid email id or Password");
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Activity_SignUp.class));
        finish();
    }
}
