package com.alumni.ssgmce.starts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.activity.Activity_PersonalDetails;
import com.alumni.ssgmce.classes.Constant;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Activity_SignUp extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host
            + "/v1/people/~:(first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";

    private static final String TAG = Activity_SignUp.class.getSimpleName();
    private final static int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 1111;
    ImageView imgFacebook, imgGmail, imgLinkedIn;
    AlertDialog pd;

    String mSocialUsername = "",mSocialId = "",mSocialProfile = "", mSocialDOB = "", mSocialEmail = "";

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcmObj;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Initialize();
        registerInBackground();
        /**
         * Run time permissions for Android M
         */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int hasReadExternalStorage = checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE );
            int hasCameraPermission = checkSelfPermission( Manifest.permission.CAMERA );
            int hasWriteExternalStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<String>();
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }

            if( hasReadExternalStorage != PackageManager.PERMISSION_GRANTED ) {
                permissions.add( Manifest.permission.READ_EXTERNAL_STORAGE );
            }

            if( hasWriteExternalStorage != PackageManager.PERMISSION_GRANTED ) {
                permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }

        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    private void Initialize() {
        TextView txtNewUser = (TextView) findViewById(R.id.txtNewUser);
        TextView txtSignIn = (TextView) findViewById(R.id.txtSignIn);
        imgFacebook = (ImageView) findViewById(R.id.imgFacebook);
        imgGmail = (ImageView) findViewById(R.id.imgGmail);
        imgLinkedIn = (ImageView) findViewById(R.id.imgLinkedIn);

        txtNewUser.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgGmail.setOnClickListener(this);
        imgLinkedIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtNewUser:
                startActivity(new Intent(this, Activity_EmailValidation.class));
                finish();
                break;

            case R.id.txtSignIn:
                startActivity(new Intent(this, Activity_Login.class));
                finish();
                break;

            case R.id.imgFacebook:
                Activity_Registration.fromSocial = "FB";
                clearFBSession();
                getFacebookUserInfo();
                break;

            case R.id.imgGmail:
                Activity_Registration.fromSocial = "Google+";
                signIn();
                break;

            case R.id.imgLinkedIn:
                Activity_Registration.fromSocial = "LinkedIn";
                login();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        Log.d( "Permissions", "Permission Granted: " + permissions[i] );
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void clearFBSession()
    {
        com.facebook.Session session = com.facebook.Session.getActiveSession();
        if(session != null)
            session.closeAndClearTokenInformation();
    }


    /**
     * Facebook
     */
    private void getFacebookUserInfo() {
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {
                boolean isPermissionAvailable = false;
                if (session.isOpened()) {

                    pd = new SpotsDialog(Activity_SignUp.this, R.style.Custom);
                    isPermissionAvailable = true;

                    Request.newMeRequest(session,
                            new Request.GraphUserCallback() {

                                @Override
                                public void onCompleted(
                                        final GraphUser user,
                                        Response response) {

                                    if (user != null) {
                                        pd.dismiss();
                                        getUserInfoFromFacebook(user);
                                    } else {
                                        pd.dismiss();
                                    }
                                }
                            }).executeAsync();

                    if (!isPermissionAvailable)
                        getPermissionFromFacebook();
                }
            }
        });
    }


    private void getUserInfoFromFacebook(final GraphUser user) {

        mSocialUsername = user.getName();
        mSocialId = user.getId();
        mSocialDOB = user.getBirthday();
        mSocialEmail = user.getInnerJSONObject().optString("email").toString();
        mSocialDOB = user.getBirthday();

        checkUser(mSocialId, mSocialEmail);

    }

    private void getPermissionFromFacebook() {
        String[] permissions = {"public_profile", "basic_info", "email", "user_birthday", "user_photos"};
        Session.getActiveSession().requestNewReadPermissions(
                new Session.NewPermissionsRequest(Activity_SignUp.this, Arrays
                        .asList(permissions)));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Session.getActiveSession() != null) {
            Session.getActiveSession().onActivityResult(this, requestCode,
                    resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        LISessionManager.getInstance(getApplicationContext())
                .onActivityResult(this,
                        requestCode, resultCode, data);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            mSocialUsername = acct.getDisplayName();
            mSocialId = acct.getId();
            mSocialEmail = acct.getEmail();
            Uri uri = acct.getPhotoUrl();
            if(uri != null)
                mSocialProfile = acct.getPhotoUrl().toString();

            checkUser(mSocialId, mSocialEmail);

        }
    }


    public void checkUser(String userID, String email) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("UserId", userID);
        requestParams.put("EmailId", email );

        client.post(Constant.CHECK_USER, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_SignUp.this, errorMessage);
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

    private void parseResult(String str) throws JSONException  {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getMemberLoginDetails = obj1.getJSONArray("CheckUsers");

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
                String isEventreg = obj10.optString("isEventreg");
                String isEventExist = obj10.optString("isEventExist");

                SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("MemberId", MemberId);
                editor.putString("MemberType", MemberType);
                editor.putString("FirstName", FirstName);
                editor.putString("MiddleName", MiddleName);
                editor.putString("LastName", LastName);
                editor.putString("MobileNo", MobileNo);
                editor.putString("Designation", Designation);
                editor.putString("Department", Department);
                editor.putString("JoiningYear", JoiningYear);
                editor.putString("CourseId", CourseId);
                editor.putString("ProfilePhoto", ProfilePhoto);
                editor.putString("InstituteId", InstituteId);
                editor.putString("CurrentLocation", CurrentLocation);
                editor.putString("EmailIdAsUserName", EmailIdAsUserName);
                editor.putString("CoverPhoto", CoverPhoto);
                editor.putString("InsertDate", InsertDate);
                editor.putString("InsertBy", InsertBy);
                editor.putString("EmailId", EmailId);
                editor.putString("UserId", UserId);
                editor.putString("SiteStatus", SiteStatus);
                editor.putString("BranchId", BranchId);
                editor.putString("DateofBirth", DateofBirth);
                editor.putString("State", State);
                editor.putString("Country", Country);
                editor.putString("Gender", Gender);
                editor.putString("MemberDay", MemberDay);
                editor.putString("MemberMonth", MemberMonth);
                editor.putString("MemberYear", MemberYear);
                editor.putString("AdminApproved", AdminApproved);
                editor.putString("lastSignIn", lastSignIn);
                editor.putString("approvalDate", approvalDate);
                editor.putString("IsLoggedIn", "Yes");
                editor.putString("Branch", Branch);
                editor.putString("CourseName", CourseName);
                editor.putString("isEventreg", isEventreg);
                editor.putString("isEventExist", isEventExist);

                editor.apply();

                Intent intent = new Intent(this, Activity_Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }else{
            Intent intent = new Intent(this, Activity_Registration.class);
            intent.putExtra("UserId", mSocialId);
            intent.putExtra("UserDOB", mSocialDOB);
            intent.putExtra("UserEmail", mSocialEmail);
            intent.putExtra("UserProfile", mSocialProfile);
            intent.putExtra("Username", mSocialUsername);
            intent.putExtra("NavFrom", "SignUp");
            startActivity(intent);

        }
    }


    /**
     * Gmail
     */

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }




    // Authenticate with linkedin and intialize Session.
    public void login() {
        LISessionManager.getInstance(getApplicationContext())
                .init(this, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        linkededinApiHelper();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Log.e("LinkedIn Failed",error.toString());
                        Toast.makeText(getApplicationContext(), "failed "
                                        + error.toString(),
                                Toast.LENGTH_LONG).show();

                    }
                }, true);
    }


    // set the permission to retrieve basic information of User's linkedIn account
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }


    public void linkededinApiHelper() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(Activity_SignUp.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    showResult(result.getResponseDataAsJson());
                    pd.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError error) {

                Intent i=new Intent(Activity_SignUp.this,Activity_Registration.class );
                i.putExtra("fromLinkedIn",true);
                startActivity(i);
            }
        });
    }

    public void showResult(JSONObject response) {

        try {
            mSocialEmail = response.get("emailAddress").toString();
            mSocialUsername = response.get("formattedName").toString();
            Object uri = response.opt("pictureUrl");
            if(uri != null)
                mSocialProfile = response.get("pictureUrl").toString();

            mSocialId = response.optString("PersonID");

            checkUser(mSocialId, mSocialEmail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // AsyncTask to register Device in GCM Server
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(getApplicationContext());
                    }
                    regId = gcmObj
                            .register(Constant.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    // Store RegId created by GCM Server in SharedPref
                    storeRegIdinSharedPref(regId);
                }
            }
        }.execute(null, null, null);
    }

    // Store  RegId and Email entered by User in SharedPref
    private void storeRegIdinSharedPref(String regId) {
        SharedPreferences prefs = getSharedPreferences(Constant.LOGIN_PREF,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("REG_ID", regId);
        editor.commit();

    }


    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    // When Application is resumed, check for Play services support to make sure app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

    }
}
