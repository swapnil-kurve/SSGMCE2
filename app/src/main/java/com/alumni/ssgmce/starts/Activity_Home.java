package com.alumni.ssgmce.starts;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.activity.Activity_Activities;
import com.alumni.ssgmce.activity.Activity_AlumniContribution;
import com.alumni.ssgmce.activity.Activity_AlumniMeet;
import com.alumni.ssgmce.activity.Activity_GoverningBody;
import com.alumni.ssgmce.activity.Activity_Groups;
import com.alumni.ssgmce.activity.Activity_Jobs;
import com.alumni.ssgmce.activity.Activity_LatestEvents;
import com.alumni.ssgmce.activity.Activity_Messages;
import com.alumni.ssgmce.activity.Activity_MyGroups;
import com.alumni.ssgmce.activity.Activity_People;
import com.alumni.ssgmce.activity.Activity_Profile;
import com.alumni.ssgmce.activity.Activity_Settings;
import com.alumni.ssgmce.classes.Comments;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.ImagePost;
import com.alumni.ssgmce.classes.NonScrollListView;
import com.alumni.ssgmce.classes.WallPost;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class Activity_Home extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static int RESULT_LOAD_IMG = 1;
    private static final int CAMERA_REQUEST = 1888;

    WallPost wallPost;
    ArrayList<WallPost> wallPostArrayList;
    Comments comments;
    ArrayList<Comments> commentsArrayList;
    File destination;
    NonScrollListView listWallData;
    ListView mDrawerList,listComments;
    DrawerLayout mDrawerLayout;
    private String[] navigationItemList;
    private int[] navigation_icons = {R.mipmap.update_profile, R.mipmap.activity, R.mipmap.group,
            R.mipmap.job, R.mipmap.people, R.mipmap.messages, R.mipmap.my_group,
            R.mipmap.events, R.mipmap.body, R.mipmap.settings, R.mipmap.contribution, R.mipmap.logout};
    SharedPreferences preferences;
    private String mMemberId, mDataToPost, selectedPath,wallId ="", isEventExist, isEventreg;
    private int mTopValue = 5;
    private SetWallPostAdapter wallPostAdapter;
    ImageView imgSelected, imgUserProfile,imgCrossIcon;
    TextView txtViewMore;
    private File actualImage;
    private File compressedImage;
    View lyTakePhoto, lyOpenGallery;
    Button btnPostData;
    EditText edtTextToPost;
    private NavigationItemAdapter navigationItemAdapter;
    private SetCommentAdater setCommentAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Initialize();

        getWallPostData();

        /*Adapter pass Navigation menues list*/
        navigationItemAdapter = new NavigationItemAdapter();
        mDrawerList.setAdapter(navigationItemAdapter);

        /*if(isEventExist.equalsIgnoreCase("1") && isEventreg.equalsIgnoreCase("0")){
            registerForMeet();
        }*/

    }

    /**
     * Initialize data here
     */
    private void Initialize() {
        imgUserProfile = (ImageView) findViewById(R.id.imgProfile);
        imgSelected = (ImageView) findViewById(R.id.imgSelected);
        btnPostData = (Button) findViewById(R.id.btnPostWall);
        edtTextToPost = (EditText) findViewById(R.id.edtTextToPost);
        lyTakePhoto = findViewById(R.id.lyTakePhoto);
        lyOpenGallery = findViewById(R.id.lyOpenGallery);
        listWallData = (NonScrollListView) findViewById(R.id.listWallData);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationItemList = getResources().getStringArray(R.array.navigation_menu);
        mDrawerList = (ListView) findViewById(R.id.listDrawer);
        txtViewMore = (TextView) findViewById(R.id.txtViewMore);
        ImageView imgNavMenu = (ImageView) findViewById(R.id.imgNavMenu);
        ImageView imgSearch = (ImageView) findViewById(R.id.imgSearch);
        imgCrossIcon = (ImageView) findViewById(R.id.imgCrossIcon);

        wallPostArrayList = new ArrayList<>();
        commentsArrayList = new ArrayList<>();

        lyTakePhoto.setOnClickListener(this);
        lyOpenGallery.setOnClickListener(this);
        btnPostData.setOnClickListener(this);
        txtViewMore.setOnClickListener(this);
        imgNavMenu.setOnClickListener(this);
        mDrawerList.setOnItemClickListener(this);
        imgCrossIcon.setOnClickListener(this);
        imgSearch.setOnClickListener(this);

        updateProfile();
    }

    private void updateProfile() {
        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("IsLoggedIn", "Yes");
        editor.apply();

        mMemberId = preferences.getString("MemberId", "");
        Constant.mIsApproved = preferences.getString("AdminApproved","false");
        String profilePath = preferences.getString("ProfilePhoto", "");
        isEventreg = preferences.getString("isEventreg", "");
        isEventExist = preferences.getString("isEventExist", "");

        if(!preferences.getString("Upload","").equalsIgnoreCase("Done")) {
            String mREG_ID = preferences.getString("REG_ID", "");
            updateGCMId(mREG_ID);
        }
        if(profilePath.length() > 40  && !profilePath.startsWith("/uploads")){
            Glide.with(this).load(profilePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgUserProfile) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCornerRadius(90);
                    imgUserProfile.setImageDrawable(circularBitmapDrawable);
                }
            });

        }else {
            if (profilePath.startsWith("/uploads")) {
                Glide.with(this).load(Constant.IMG_PATH + profilePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgUserProfile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(90);
                        imgUserProfile.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else if (profilePath.startsWith("/data")) {
                Glide.with(this).load(profilePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgUserProfile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(90);
                        imgUserProfile.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }
    }

    /**
     * get Wall Post Data
     */
    public void getWallPostData() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("topVal", mTopValue);
        requestParams.put("memberId", mMemberId);

        client.post(Constant.WALL_POST_URL, requestParams, new AsyncHttpResponseHandler() {

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
                        Constant.PostWallFlag = 0;
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
                    Constant.showToast(Activity_Home.this, errorMessage);
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


    /**
     * Parse Wall Post Result
     *
     * @param str
     * @throws JSONException
     */

    private void parseResult(String str) throws JSONException {

        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("result");

        if (result.equals("Success")) {
            wallPostArrayList.clear();
            listWallData.setVisibility(View.VISIBLE);
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getWallPosts");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String RowNumber = obj10.optString("RowNumber");
                String MemberId = obj10.optString("MemberId");
                String MemberType = obj10.optString("MemberType");
                String FullName = obj10.optString("FullName");
                String SignUpMembers = obj10.optString("SignUpMembers");
                String Designation = obj10.optString("Designation");
                String Department = obj10.optString("Department");
                String ProfilePhoto = obj10.optString("ProfilePhoto");
                String InsertDate = obj10.optString("InsertDate");
                String Date = obj10.optString("Date");
                String MonthYear = obj10.optString("MonthYear");
                String WallId = obj10.optString("WallId");
                String WallSeqNo = obj10.optString("WallSeqNo");
                String WallPhotoVideo = obj10.optString("WallPhotoVideo");
                String WallImageName = obj10.optString("WallImageName");
                String RecordType = obj10.optString("RecordType");
                String imgDisp = obj10.optString("imgDisp");
                String dispVid = obj10.optString("dispVid");
                String audDisp = obj10.optString("audDisp");
                String EditDeletePostDisp = obj10.optString("EditDeletePostDisp");
                String isUpdate = obj10.optString("isUpdate");
                String commentcount  = obj10.optString("commentcount");

                wallPost = new WallPost(RowNumber, MemberId, MemberType, FullName, SignUpMembers, Designation, Department, ProfilePhoto, InsertDate, Date, MonthYear, WallId, WallSeqNo, WallPhotoVideo, WallImageName, RecordType, imgDisp, dispVid, audDisp, EditDeletePostDisp, isUpdate,commentcount);
                wallPostArrayList.add(wallPost);
            }

            if(wallPostArrayList.size() > 0) {
                wallPostAdapter = new SetWallPostAdapter(Activity_Home.this, wallPostArrayList);
                listWallData.setAdapter(wallPostAdapter);
            }else
            {
                txtViewMore.setVisibility(View.GONE);
                listWallData.setVisibility(View.GONE);
                Constant.showToast(this, "No post found");
            }
        }else
        {
            txtViewMore.setVisibility(View.GONE);
            listWallData.setVisibility(View.GONE);
            Constant.showToast(this, "No post found");
        }
    }


    /**
     * Handle click event
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyTakePhoto:
                TakePhotoFromCamera();
                break;

            case R.id.lyOpenGallery:
                loadImagefromGallery();
                break;

            case R.id.btnPostWall:
                hideKeyboard();
                if(Constant.mIsApproved.equalsIgnoreCase("true")) {
                    if(Constant.PostWallFlag == 0){
                    if (edtTextToPost.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter text for post", Toast.LENGTH_SHORT).show();
                    } else {
                            mDataToPost = edtTextToPost.getText().toString();
                            postWallData();
                            imgSelected.setImageBitmap(null);
                            imgSelected.setVisibility(View.GONE);
                            edtTextToPost.setText("");
                        }
                    } else {
                        mDataToPost = edtTextToPost.getText().toString();
                        postWallData();
                        imgSelected.setImageBitmap(null);
                        imgSelected.setVisibility(View.GONE);
                        edtTextToPost.setText("");
                    }
                }else{
                    Constant.showToast(Activity_Home.this, "You are not approved by Admin yet");
                }
                break;

            case R.id.txtViewMore:
                mTopValue = mTopValue + 5;
                getWallPostData();
                break;

            case R.id.imgNavMenu:
                hideKeyboard();
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;

            case R.id.imgCrossIcon:
                hideKeyboard();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;

            case R.id.imgSearch:
                Intent intent = new Intent(this, Activity_People.class);
                intent.putExtra("Search","Yes");
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position)
        {
            case 0:
                startActivity(new Intent(this, Activity_Profile.class));
                break;

            case 1:
                startActivity(new Intent(this, Activity_Activities.class));
                break;

            case 2:
                startActivity(new Intent(this, Activity_Groups.class));
                break;

            case 3:
                startActivity(new Intent(this, Activity_Jobs.class));
                break;

            case 4:
                startActivity(new Intent(this, Activity_People.class));
                break;

            case 5:
                startActivity(new Intent(this, Activity_Messages.class));
                break;

            case 6:
                startActivity(new Intent(this, Activity_MyGroups.class));
                break;

            case 7:
                startActivity(new Intent(this, Activity_LatestEvents.class));
                break;

            case 8:
                startActivity(new Intent(this, Activity_GoverningBody.class));
                break;

            case 9:
                startActivity(new Intent(this, Activity_Settings.class));
                break;

            case 10:
                startActivity(new Intent(this, Activity_AlumniContribution.class));
                break;

            case 11:
                preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("Upload", "");
                editor.apply();

                logout();
                break;
        }

    }

    private void logout() {
        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("IsLoggedIn","No");
        editor.apply();

        Constant.showToast(this, "Logged out successfully!");

        startActivity(new Intent(this, Activity_Login.class));
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(commentsArrayList.get(position).getMemberId().equalsIgnoreCase(mMemberId)){
            new AlertDialog.Builder(Activity_Home.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete comment")
                    .setMessage("Are you sure to delete this comment?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deleteWallPost(Constant.DELETE_WALL_COMMENT, commentsArrayList.get(position).getWallId(), commentsArrayList.get(position).getWallReplySeqNo(), "Comment");
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

            return false;
        }else
            return true;
    }


    /**
     * Set Data to Wall Post List
     */
    class SetWallPostAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList<WallPost> wallPostArrayList;
        public ArrayList<ListItem> myItems = new ArrayList();

        public SetWallPostAdapter(Context context, ArrayList<WallPost> wallPostArrayList) {
            this.context = context;
            this.wallPostArrayList = wallPostArrayList;

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < wallPostArrayList.size(); i++) {
                ListItem listItem = new ListItem();
                listItem.edtData = "Caption" + i;
                myItems.add(listItem);
            }
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return wallPostArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return wallPostArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_row_fo_wallpost, null);

                holder.imgBanner = (ImageView) view.findViewById(R.id.imgBanner);
                holder.imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
                holder.txtUserName = (TextView) view.findViewById(R.id.txtUsername);
                holder.txtAddedText = (TextView) view.findViewById(R.id.txtTextAdded);
                holder.txtDatePosted = (TextView) view.findViewById(R.id.txtDatePosted);
                holder.btnPostComment = (Button) view.findViewById(R.id.btnPost);
                holder.lyComment = view.findViewById(R.id.lyComment);
                holder.edtCommentToPost = (EditText) view.findViewById(R.id.edtComment);
                holder.imgDeletePost = (ImageView) view.findViewById(R.id.imgDeletePost);
                holder.txtCommentCount = (TextView) view.findViewById(R.id.txtCommentCount);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.edtCommentToPost.setId(position);
            holder.txtUserName.setId(position);
            holder.txtAddedText.setId(position);
            holder.txtDatePosted.setId(position);
            holder.imgProfile.setId(position);
            holder.imgBanner.setId(position);
            holder.lyComment.setId(position);
            holder.btnPostComment.setId(position);
            holder.imgDeletePost.setId(position);
            holder.txtCommentCount.setId(position);

            holder.edtCommentToPost.clearFocus();

            //we need to update adapter once we finish with editing
            holder.edtCommentToPost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        myItems.get(position).edtData = Caption.getText().toString();
                    }
                }
            });

            if(mMemberId.equalsIgnoreCase(wallPostArrayList.get(position).getMemberId()))
                holder.imgDeletePost.setVisibility(View.VISIBLE);
            else
                holder.imgDeletePost.setVisibility(View.GONE);

            holder.btnPostComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();

                    if(Constant.mIsApproved.equalsIgnoreCase("true")){
                        if (holder.edtCommentToPost.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Please enter text for post", Toast.LENGTH_SHORT).show();

                        } else {
                            mDataToPost = holder.edtCommentToPost.getText().toString();
                            postDataComments(wallPostArrayList.get(position).getWallId(), mDataToPost,"list");
                            holder.edtCommentToPost.setText("");
                        }
                    }else{
                        Constant.showToast(Activity_Home.this, "You are not approved by Admin yet");
                    }
                }
            });
            holder.txtUserName.setText(wallPostArrayList.get(position).getFullName());
            holder.txtAddedText.setText(wallPostArrayList.get(position).getSignUpMembers());
            holder.txtCommentCount.setText("("+wallPostArrayList.get(position).getCommentcount()+")");
            holder.txtDatePosted.setText(wallPostArrayList.get(position).getDate() + " " + wallPostArrayList.get(position).getMonthYear());

            if(!wallPostArrayList.get(position).getProfilePhoto().equalsIgnoreCase("")) {
                Glide.with(context).load(Constant.IMG_PATH + wallPostArrayList.get(position).getProfilePhoto()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.imgProfile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(90);
                        holder.imgProfile.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }

            holder.imgDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(Activity_Home.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete post")
                            .setMessage("Are you sure to delete this post?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteWallPost(Constant.DELETE_WALL_POST,wallPostArrayList.get(position).getWallId(), wallPostArrayList.get(position).getWallSeqNo(), "Post");
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                }
            });

            holder.lyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wallId = wallPostArrayList.get(position).getWallId();
                    ShowComments(wallId);
                }
            });


            if (wallPostArrayList.get(position).getWallImageName().equals("")) {
                 holder.imgBanner.setVisibility(View.GONE);
            }
            else {
                Glide.with(context).load(Constant.IMG_PATH + wallPostArrayList.get(position).getWallImageName()).into(holder.imgBanner);
                holder.imgBanner.setVisibility(View.VISIBLE);
            }


            return view;
        }

        class ViewHolder {
            ImageView imgProfile, imgBanner, imgDeletePost;
            TextView txtUserName, txtAddedText, txtDatePosted, txtCommentCount;
            Button btnPostComment;
            View lyComment;
            EditText edtCommentToPost;
        }

        class ListItem {
            String edtData;
        }
    }

    private void deleteWallPost(String URL, final String wallId, String wallSeqNo, final String type) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("WallId", wallId);
        if(type.equalsIgnoreCase("Post"))
            requestParams.put("WallSeqNo", wallSeqNo);
        else
            requestParams.put("WallReplySeqNo", wallSeqNo);

        client.post(URL, requestParams, new AsyncHttpResponseHandler() {

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
                        parseDeletePostResult(str, wallId, type);
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
                    Constant.showToast(Activity_Home.this, errorMessage);
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

    private void parseDeletePostResult(String str, String wallId, String type) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);

        String response = obj01.getString("response");

        if (response.equalsIgnoreCase("Success")) {
            if(type.equalsIgnoreCase("Post")) {
                Constant.showToast(this, "Post deleted successfully!");
                getWallPostData();
            }
            else {
                Constant.showToast(this, "Comment deleted successfully!");
                getWallComments(wallId);
            }
        }else{
            if(type.equalsIgnoreCase("Post"))
                Constant.showToast(this, "Unable to delete post!");
            else
                Constant.showToast(this, "Unable to delete comment!");
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

    private void ShowComments(final String wallId)
    {
        // custom dialog
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_show_comment);

        final EditText edtComment = (EditText) dialog.findViewById(R.id.edtComment);
        Button btnPost = (Button) dialog.findViewById(R.id.btnPost);
        listComments = (ListView) dialog.findViewById(R.id.listComments);

        listComments.setOnItemLongClickListener(this);
        edtComment.clearFocus();

        getWallComments(wallId);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
                if(Constant.mIsApproved.equalsIgnoreCase("true")){
                    if (edtComment.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter text for post", Toast.LENGTH_SHORT).show();

                    } else {
                        mDataToPost = edtComment.getText().toString();
                        postDataComments(wallId,mDataToPost, "dialog");
                        edtComment.setText("");
                    }
                }else{
                    Constant.showToast(Activity_Home.this, "You are not approved by Admin yet");
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);

        dialog.show();
    }

    private void getWallComments(String wallId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("memberID", mMemberId);
        requestParams.put("WallId", wallId);
        requestParams.put("selected", "All");

        client.post(Constant.GET_WALL_Comment, requestParams, new AsyncHttpResponseHandler() {

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
                        Constant.flag = 1;
                        parseComment(str);
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
                    Constant.showToast(Activity_Home.this, errorMessage);
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

    private void parseComment(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);

        String response = obj01.getString("response");

        if (response.equalsIgnoreCase("Success")) {
            commentsArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPostByMemberId = obj1.getJSONArray("getWallReplyByID");
            for (int i = 0; i < getWallPostByMemberId.length(); i++) {
                JSONObject obj10 = getWallPostByMemberId.getJSONObject(i);
                String WallId = obj10.optString("WallId");
                String WallReplySeqNo = obj10.optString("WallReplySeqNo");
                String WallPostReply = obj10.optString("WallPostReply");
                String MemberId = obj10.optString("MemberId");
                String ProfilePhoto = obj10.optString("ProfilePhoto");
                String FullName = obj10.optString("FullName");
                String commentDate = obj10.optString("commentDate");
                String WallPostReplyDay = obj10.optString("WallPostReplyDay");
                String WallPostReplyMonth = obj10.optString("WallPostReplyMonth");
                String WallPostReplyYear = obj10.optString("WallPostReplyYear");
                String deleteDisp = obj10.optString("deleteDisp");
                String isUpdated = obj10.optString("isUpdated");

                comments = new Comments(WallId,WallReplySeqNo,WallPostReply,MemberId,ProfilePhoto,FullName,commentDate,WallPostReplyDay,WallPostReplyMonth,WallPostReplyYear,deleteDisp,isUpdated);
                commentsArrayList.add(comments);
            }
            setCommentAdater = new SetCommentAdater(Activity_Home.this, commentsArrayList);
            listComments.setAdapter(setCommentAdater);
        }
    }


    private void postDataComments(String wallId, String mDataToPost, final String tag) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("WallId", wallId);
        requestParams.put("ReplyMemberId", mMemberId);
        requestParams.put("WallPostReply", mDataToPost);

        client.post(Constant.ADD_WALL_Comment, requestParams, new AsyncHttpResponseHandler() {

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
                        parseCommentResponse(str, tag);
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
                    Constant.showToast(Activity_Home.this, errorMessage);
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

    private void parseCommentResponse(String str, String tag) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(1);
        JSONArray addWallPost = obj0.getJSONArray("insertWallReply");
        JSONObject obj01 = addWallPost.getJSONObject(0);

        String response = obj01.getString("result");

        if (response.equalsIgnoreCase("Success")) {
            Constant.showToast(Activity_Home.this, "Comment posted successfully");
            if(tag.equalsIgnoreCase("dialog"))
                getWallComments(wallId);
        } else {
            Constant.showToast(Activity_Home.this, "Fail to update, please try again later");
        }
    }


    /**
     * Post Data to  Server
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void postWallData() {
        ImagePost imagePost = new ImagePost(this, imgSelected, compressedImage);
        imagePost.execute(mMemberId, mDataToPost);

    }


    /**
     * Launch Camera
     */
    private void TakePhotoFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    /**
     * Launch Gallery
     */

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
                Constant.showToast(Activity_Home.this, "Failed to open picture!");
                return;
            }
            try {
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                actualImage = FileUtil.from(this, data.getData());
                imgSelected.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                imgSelected.setVisibility(View.VISIBLE);
                compressImage();
            } catch (IOException e) {
                Constant.showToast(Activity_Home.this, "Failed to read picture data!");
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            selectedPath = destination.getPath();
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();

                actualImage = FileUtil.from(this, Uri.fromFile(new File(selectedPath)));
                imgSelected.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                imgSelected.setVisibility(View.VISIBLE);
                compressImage();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void compressImage() {
        if (actualImage == null) {
            Constant.showToast(Activity_Home.this, "Please choose an image!");
        } else {
            Constant.PostWallFlag = 1;
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


    /*Navigation menus list Adapter created*/
    private class NavigationItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return navigationItemList.length;
        }

        @Override
        public Object getItem(int position) {
            return navigationItemList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_layout_for_nav_item, null);

            TextView txtNavMenu = (TextView) convertView.findViewById(R.id.nav_text);
            ImageView imgNavMenu = (ImageView) convertView.findViewById(R.id.nav_icon);

            if (position % 2 == 0) {
                convertView.setBackgroundColor(getResources().getColor(R.color.blue_grey_500));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.blue_grey_700));
            }

            txtNavMenu.setText(navigationItemList[position]);
            Glide.with(Activity_Home.this).load(navigation_icons[position]).into(imgNavMenu);

            return convertView;
        }
    }



    class SetCommentAdater extends BaseAdapter
    {
        ArrayList<Comments> commentsArrayList;
        Context context;

        public SetCommentAdater(Activity_Home activity_home, ArrayList<Comments> commentsArrayList) {
            this.commentsArrayList = commentsArrayList;
            this.context = activity_home;
        }

        @Override
        public int getCount() {
            return commentsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_commets, null);

            TextView txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
            TextView txtComment = (TextView) convertView.findViewById(R.id.txtComment);

            txtUsername.setText(commentsArrayList.get(position).getFullName());
            txtComment.setText(commentsArrayList.get(position).getWallPostReply());

            return convertView;
        }
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit Application")
                    .setMessage("Are you sure you want to Exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            System.exit(0);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }


private void updateGCMId(String mREG_ID) {
    final AlertDialog progressDialog;
    progressDialog = new SpotsDialog(this, R.style.Custom);
    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams requestParams = new RequestParams();
    requestParams.put("MemberId", mMemberId);
    requestParams.put("GCMId", mREG_ID);

    client.post(Constant.UPDATE_GCM_ID, requestParams, new AsyncHttpResponseHandler() {

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
                    parseGCMResult(str);
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
                Constant.showToast(Activity_Home.this, errorMessage);
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

    private void parseGCMResult(String str) throws JSONException  {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray addWallPost = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = addWallPost.getJSONObject(0);

        String response = obj01.getString("result");

        if (response.equalsIgnoreCase("Success")) {
            Log.e("GCM REG ID", "SUCCESS");
            preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("Upload", "Done");

            editor.apply();
        }


    }

    private void registerForMeet() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Alumni meet")
                .setMessage("Register for alumni meet")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(Activity_Home.this, Activity_AlumniMeet.class));
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        updateProfile();
        super.onResume();
    }
}
