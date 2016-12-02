package com.alumni.ssgmce.activity;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.GroupComments;
import com.alumni.ssgmce.classes.GroupPost;
import com.alumni.ssgmce.classes.ImagePostGroup;
import com.alumni.ssgmce.classes.NonScrollListView;
import com.alumni.ssgmce.starts.Activity_Home;
import com.alumni.ssgmce.starts.Activity_Login;
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

public class Activity_GroupPosts extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static int RESULT_LOAD_IMG = 1;
    private static final int CAMERA_REQUEST = 1888;

    GroupPost groupPost;
    ArrayList<GroupPost> groupPostArrayList;
    GroupComments comments;
    ArrayList<GroupComments> commentsArrayList;
    File destination;
    NonScrollListView listWallData;
    ListView listComments;
    SharedPreferences preferences;
    private String mMemberId, mDataToPost, selectedPath, grpId = "", grpPostId = "";
    public static String mGroupId, mGroupName;
    private int mTopValue = 5;
    private SetGroupPostAdapter setGroupPostAdapter;
    ImageView imgSelected, imgUserProfile;
    private File actualImage;
    private File compressedImage;
    View lyTakePhoto, lyOpenGallery;
    Button btnPostData;
    EditText edtTextToPost;
    private TextView txtViewMore, txtTitle;
    private SetCommentAdater setCommentAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_post);

        Initialize();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("GroupId"))
            mGroupId = bundle.getString("GroupId");

        getWallPostData();

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
        txtViewMore = (TextView) findViewById(R.id.txtViewMore);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        ImageView imgSearch = (ImageView) findViewById(R.id.imgSearch);


        groupPostArrayList = new ArrayList<>();
        commentsArrayList = new ArrayList<>();

        lyTakePhoto.setOnClickListener(this);
        lyOpenGallery.setOnClickListener(this);
        btnPostData.setOnClickListener(this);
        txtViewMore.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgSearch.setOnClickListener(this);

        updateProfile();

    }



    private void updateProfile() {
        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");
        Constant.mIsApproved = preferences.getString("AdminApproved","false");
        String profilePath = preferences.getString("ProfilePhoto", "");

        if(profilePath.startsWith("/uploads")) {
            Glide.with(this).load(Constant.IMG_PATH + profilePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgUserProfile) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCornerRadius(90);
                    imgUserProfile.setImageDrawable(circularBitmapDrawable);
                }
            });
        }else if(profilePath.startsWith("/data")) {
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


    /**
     * get Wall Post Data
     */
    public void getWallPostData() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("topVal", mTopValue);
        requestParams.put("grpID", mGroupId);
        requestParams.put("memberId", mMemberId);

        client.post(Constant.GROUP_POST_URL, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_GroupPosts.this, errorMessage);
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
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            listWallData.setVisibility(View.VISIBLE);
            groupPostArrayList.clear();
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getGroupPosts");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String GrpPostId = obj10.optString("GrpPostId");
                String GrpPostSeqNo = obj10.optString("GrpPostSeqNo");
                String GrpId = obj10.optString("GrpId");
                String MemberId = obj10.optString("MemberId");
                String Post = obj10.optString("Post");
                String PostAttachment = obj10.optString("PostAttachment");
                String Date = obj10.optString("Date");
                String MonthYear = obj10.optString("MonthYear");
                String Member = obj10.optString("Member");
                String profilepic = obj10.optString("profilepic");
                String ImagePath = obj10.optString("ImagePath");
                String imgDisp = obj10.optString("imgDisp");
                String dispVid = obj10.optString("dispVid");
                String audDisp = obj10.optString("audDisp");
                String onDate1 = obj10.optString("onDate1");
                String EditDeletePostDisp = obj10.optString("EditDeletePostDisp");
                String isUpdate = obj10.optString("isUpdate");
                String commentcount = obj10.optString("commentcount");
                mGroupName = obj10.optString("groupName");

                groupPost = new GroupPost(GrpPostId, GrpPostSeqNo, GrpId, MemberId, Post, PostAttachment, Date, MonthYear, Member, profilepic, ImagePath, imgDisp, dispVid, audDisp, onDate1, EditDeletePostDisp, isUpdate, commentcount);
                groupPostArrayList.add(groupPost);
            }
            txtTitle.setText(mGroupName);
                if(groupPostArrayList.size() > 0) {
                setGroupPostAdapter = new SetGroupPostAdapter(Activity_GroupPosts.this, groupPostArrayList);
                listWallData.setAdapter(setGroupPostAdapter);
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
                if(Constant.mIsApproved.equalsIgnoreCase("true")){
                    if(Constant.postGroupFlag == 0){
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
                    Constant.showToast(Activity_GroupPosts.this, "You are not approved by Admin yet");
                }
                break;

            case R.id.txtViewMore:
                mTopValue = mTopValue + 5;
                getWallPostData();
                break;

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.imgSearch:
                Intent intent = new Intent(this, Activity_People.class);
                intent.putExtra("Search","Yes");
                intent.putExtra("GroupId", mGroupId);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
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
                logout();
                break;
        }

    }

    private void logout() {
        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("IsLoggedIn", "No");
        editor.apply();

        Constant.showToast(this, "Logged out successfully!");

        startActivity(new Intent(this, Activity_Login.class));
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(commentsArrayList.get(position).getReplyMemberId().equalsIgnoreCase(mMemberId)){
            new AlertDialog.Builder(Activity_GroupPosts.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete comment")
                    .setMessage("Are you sure to delete this comment?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deleteGroupPost(Constant.DELETE_GROUP_COMMENT, commentsArrayList.get(position).getGrpPostId(), commentsArrayList.get(position).getGrpPostSRplyeqNo(), "Comment");
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
    class SetGroupPostAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList<GroupPost> groupPostArrayList;
        public ArrayList<ListItem> myItems = new ArrayList();

        public SetGroupPostAdapter(Context context, ArrayList<GroupPost> groupPostArrayList) {
            this.context = context;
            this.groupPostArrayList = groupPostArrayList;

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < groupPostArrayList.size(); i++) {
                ListItem listItem = new ListItem();
                listItem.edtData = "Caption" + i;
                myItems.add(listItem);
            }
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return groupPostArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return groupPostArrayList.get(position);
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

            if(mMemberId.equalsIgnoreCase(groupPostArrayList.get(position).getMemberId()))
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
                            postDataComments(groupPostArrayList.get(position).getGrpId(),groupPostArrayList.get(position).getGrpPostId(), mDataToPost, "list");
                            holder.edtCommentToPost.setText("");
                        }
                    }else{
                        Constant.showToast(Activity_GroupPosts.this, "You are not approved by Admin yet");
                    }
                }
            });
            holder.txtUserName.setText(groupPostArrayList.get(position).getMember());
            holder.txtAddedText.setText(groupPostArrayList.get(position).getPost());
            holder.txtCommentCount.setText("("+groupPostArrayList.get(position).getCommentcount()+")");
            holder.txtDatePosted.setText(groupPostArrayList.get(position).getDate() + " " + groupPostArrayList.get(position).getMonthYear());

            if(!groupPostArrayList.get(position).getProfilepic().equalsIgnoreCase("")) {
                Glide.with(context).load(Constant.IMG_PATH + groupPostArrayList.get(position).getProfilepic()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.imgProfile) {
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
                    new AlertDialog.Builder(Activity_GroupPosts.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete post")
                            .setMessage("Are you sure to delete this post?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteGroupPost(Constant.DELETE_GROUP_POST, groupPostArrayList.get(position).getGrpPostId(), groupPostArrayList.get(position).getGrpPostSeqNo(), "Post");
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                }
            });


            holder.lyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    grpId = groupPostArrayList.get(position).getGrpId();
                    grpPostId = groupPostArrayList.get(position).getGrpPostId();
                    ShowComments(grpId, grpPostId);
                }
            });

            if (groupPostArrayList.get(position).getImagePath().equals("")) {
                holder.imgBanner.setVisibility(View.GONE);
            }
            else {
                Glide.with(context).load(Constant.IMG_PATH + groupPostArrayList.get(position).getImagePath()).into(holder.imgBanner);
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


    private void deleteGroupPost(String URL, final String wallId, String wallSeqNo, final String type) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        if(type.equalsIgnoreCase("Post")) {
            requestParams.put("grpPostId", wallId);
            requestParams.put("GrpPostSeqNo", wallSeqNo);
        }else {
            requestParams.put("GrpPostId", wallId);
            requestParams.put("GrpPostReplySeqNo", wallSeqNo);
        }

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
                    Constant.showToast(Activity_GroupPosts.this, errorMessage);
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

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void ShowComments(final String grpId, final String grpPostId) {
        // custom dialog
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_show_comment);

        final EditText edtComment = (EditText) dialog.findViewById(R.id.edtComment);
        Button btnPost = (Button) dialog.findViewById(R.id.btnPost);
        listComments = (ListView) dialog.findViewById(R.id.listComments);
        listComments.setOnItemLongClickListener(this);
        edtComment.clearFocus();

        getWallComments(grpPostId);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if(Constant.mIsApproved.equalsIgnoreCase("true")){
                    if (edtComment.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter text for post", Toast.LENGTH_SHORT).show();

                    } else {
                        mDataToPost = edtComment.getText().toString();
                        postDataComments(grpId, grpPostId, mDataToPost, "dialog");
                        edtComment.setText("");
                    }
                }else{
                    Constant.showToast(Activity_GroupPosts.this, "You are not approved by Admin yet");
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    private void getWallComments(String grpPostId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("memberId", mMemberId);
        requestParams.put("GrpPostId", grpPostId);
        requestParams.put("type", "All");

        client.post(Constant.GET_GROUP_WALL_Comment, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_GroupPosts.this, errorMessage);
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
            JSONArray getWallPostByMemberId = obj1.getJSONArray("getGroupReplyByID");
            for (int i = 0; i < getWallPostByMemberId.length(); i++) {
                JSONObject obj10 = getWallPostByMemberId.getJSONObject(i);
                String GrpPostId = obj10.optString("GrpPostId");
                String GrpPostSRplyeqNo = obj10.optString("GrpPostSRplyeqNo");
                String GrpId = obj10.optString("GrpId");
                String ReplyMemberId = obj10.optString("ReplyMemberId");
                String GrpPostReplyDate = obj10.optString("GrpPostReplyDate");
                String GroupPostReply = obj10.optString("GroupPostReply");
                String ReplyMemberName = obj10.optString("ReplyMemberName");
                String ReplyProfilePhoto = obj10.optString("ReplyProfilePhoto");
                String deleteDisp = obj10.optString("deleteDisp");
                String isUpdated = obj10.optString("isUpdated");

                comments = new GroupComments(GrpPostId,GrpPostSRplyeqNo,GrpId,ReplyMemberId,GrpPostReplyDate,GroupPostReply,ReplyMemberName,ReplyProfilePhoto,deleteDisp,isUpdated);
                commentsArrayList.add(comments);
            }
            setCommentAdater = new SetCommentAdater(Activity_GroupPosts.this, commentsArrayList);
            listComments.setAdapter(setCommentAdater);
        }
    }


    private void postDataComments(String grpId, String grpPostId, String mDataToPost, final String tag) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("GrpId", grpId);
        requestParams.put("GrpPostId", grpPostId);
        requestParams.put("MemberId", mMemberId);
        requestParams.put("Reply", mDataToPost);

        client.post(Constant.ADD_GROUP_WALL_Comment, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_GroupPosts.this, errorMessage);
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
        JSONArray addWallPost = obj0.getJSONArray("insertGroupPostReply");
        JSONObject obj01 = addWallPost.getJSONObject(0);

        String response = obj01.getString("result");

        if (response.equalsIgnoreCase("Success")) {
            Constant.showToast(Activity_GroupPosts.this, "Comment posted successfully");
            if (tag.equalsIgnoreCase("dialog"))
                getWallComments(grpPostId);
        } else {
            Constant.showToast(Activity_GroupPosts.this, "Fail to update, please try again later");
        }
    }

    /**
     * Post Data to  Server
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void postWallData() {
        ImagePostGroup imagePost = new ImagePostGroup(this, imgSelected, compressedImage);
        imagePost.execute(mMemberId, mDataToPost, mGroupId);

    }

    /**
     * Launch Camera
     */
    private void TakePhotoFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                Constant.showToast(Activity_GroupPosts.this, "Failed to open picture!");
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
                Constant.showToast(Activity_GroupPosts.this, "Failed to read picture data!");
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
            Constant.showToast(Activity_GroupPosts.this, "Please choose an image!");
        } else {
            Constant.postGroupFlag = 1;
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


    class SetCommentAdater extends BaseAdapter {
        ArrayList<GroupComments> commentsArrayList;
        Context context;

        public SetCommentAdater(Activity_GroupPosts activity_home, ArrayList<GroupComments> commentsArrayList) {
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

            txtUsername.setText(commentsArrayList.get(position).getReplyMemberName());
            txtComment.setText(commentsArrayList.get(position).getGroupPostReply());

            return convertView;
        }
    }
}
