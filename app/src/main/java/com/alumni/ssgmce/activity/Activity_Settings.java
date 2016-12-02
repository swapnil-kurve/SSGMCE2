package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Activity_Settings extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    String mMemberId;
    String EmailIdVisibility,MobileNoVisibility;
    Switch switchMobile, switchEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Initialize();
        GetSettings();
    }

    private void Initialize() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        TextView txtSaveAccount = (TextView) findViewById(R.id.txtSaveAccount);
        switchEmail = (Switch) findViewById(R.id.switchEmail);
        switchMobile = (Switch) findViewById(R.id.switchMobile);

        imgBack.setOnClickListener(this);
        txtSaveAccount.setOnClickListener(this);
        switchEmail.setOnCheckedChangeListener(this);
        switchMobile.setOnCheckedChangeListener(this);

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtSaveAccount:
                SaveSettings();
                break;
        }
    }


    public void GetSettings() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("MemberId", mMemberId);

        client.post(Constant.GET_SETTINGS, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_Settings.this, errorMessage);
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
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equalsIgnoreCase("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getPrivacySettingByMemberID");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                EmailIdVisibility = obj10.optString("EmailIdVisibility");
                MobileNoVisibility = obj10.optString("MobileNoVisibility");

            }

            if(EmailIdVisibility.equalsIgnoreCase("O"))
                switchEmail.setChecked(true);//addSpinnerItems(1,"Email");
            else
                switchEmail.setChecked(false);//addSpinnerItems(0,"Email");

            if(MobileNoVisibility.equalsIgnoreCase("O"))
                switchMobile.setChecked(true); //addSpinnerItems(1,"Mobile");
            else
                switchMobile.setChecked(false); //addSpinnerItems(0,"Mobile");
        }
    }


    private void SaveSettings(){
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("memberID", mMemberId);
        requestParams.put("EmailIdVisibility", EmailIdVisibility);
        requestParams.put("MobileNoVisibility", MobileNoVisibility);

        client.post(Constant.SAVE_SETTINGS, requestParams, new AsyncHttpResponseHandler() {

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
                        parseSettingsResult(str);
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
                    Constant.showToast(Activity_Settings.this, errorMessage);
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

    private void parseSettingsResult(String str) throws JSONException{
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String response = obj01.getString("response");

        if (response.equalsIgnoreCase("Success")) {
            Constant.showToast(Activity_Settings.this, "Settings saved successfully.");
        }else if(response.equalsIgnoreCase("Fail"))
        {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray addUpdatePrivacySetting = obj1.getJSONArray("addUpdatePrivacySetting");
            JSONObject obj11 = addUpdatePrivacySetting.getJSONObject(0);
            String result = obj11.getString("result");

            Constant.showToast(Activity_Settings.this, result);

            switchEmail.setChecked(false);
            switchMobile.setChecked(false);
        }else
        {
            Constant.showToast(Activity_Settings.this, "Failed to update settings");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId())
        {
            case R.id.switchEmail:
                if(isChecked)
                    EmailIdVisibility = "O";
                else
                    EmailIdVisibility = "E";
                break;

            case R.id.switchMobile:
                if(isChecked)
                    MobileNoVisibility = "O";
                else
                    MobileNoVisibility = "E";
                break;
        }
    }
}
