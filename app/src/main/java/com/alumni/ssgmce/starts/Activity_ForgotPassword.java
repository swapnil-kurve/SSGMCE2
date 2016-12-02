package com.alumni.ssgmce.starts;

import android.app.AlertDialog;
import android.content.Intent;
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

public class Activity_ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

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
                    String email = edtEmail.getText().toString().trim();
                    sendMail(email);
                }
                break;
        }
    }

    private void sendMail(String email) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("EmailId", email);

        client.post(Constant.FORGET_PASSWORD, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_ForgotPassword.this, errorMessage);
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
        JSONArray rowsResponse = obj0.getJSONArray("ForgetPassword");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if (response.equals("Success")) {
            Constant.showToast(this, "Reset Password Password Link has been Sent to your Email.");
            onBackPressed();
        }else{
            Constant.showToast(this, "Email id not registered.");
            onBackPressed();
        }
    }
}
