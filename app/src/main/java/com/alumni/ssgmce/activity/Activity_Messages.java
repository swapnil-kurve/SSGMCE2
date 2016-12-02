package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.ImagePost;
import com.alumni.ssgmce.classes.JobReferral;
import com.alumni.ssgmce.classes.Messages;
import com.alumni.ssgmce.classes.ReplyMessage;
import com.alumni.ssgmce.classes.SendMessage;
import com.alumni.ssgmce.classes.WallPost;
import com.alumni.ssgmce.starts.Activity_Home;
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

public class Activity_Messages extends AppCompatActivity implements View.OnClickListener {
    private static int RESULT_LOAD_IMG = 1;
    private File actualImage;
    private File compressedImage;
    private File destination;
    ImageView imgSelected;
    TextView txtPrivate, txtJobReferral;
    ListView listMessages;
    String mMemberId;
    Messages messages;
    JobReferral jobReferral;
    ArrayList<Messages> messagesArrayList;
    ArrayList<JobReferral> jobReferralArrayList;
    private SetMessageAdapter setMessageAdapter;
    private SetJobMessageAdapter setJobMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Initialize();
        getPrivateMessages();
    }

    private void Initialize() {
        txtPrivate = (TextView) findViewById(R.id.txtPrivate);
        txtJobReferral = (TextView) findViewById(R.id.txtJobReferral);
        listMessages = (ListView) findViewById(R.id.listMessages);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        txtPrivate.setOnClickListener(this);
        txtJobReferral.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        imgSelected = new ImageView(this);

        messagesArrayList = new ArrayList<>();
        jobReferralArrayList = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPrivate:
                changeToPrivate();
                break;

            case R.id.txtJobReferral:
                changeToJob();
                break;

            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    private void changeToPrivate() {
        txtPrivate.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtPrivate.setTextColor(getResources().getColor(R.color.theme_blue));
        txtJobReferral.setBackgroundDrawable(null);
        txtJobReferral.setTextColor(Color.WHITE);

        getPrivateMessages();
    }

    private void changeToJob() {
        txtJobReferral.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_white));
        txtJobReferral.setTextColor(getResources().getColor(R.color.theme_blue));
        txtPrivate.setBackgroundDrawable(null);
        txtPrivate.setTextColor(Color.WHITE);

        getJobReferralMessages();
    }


    public void getPrivateMessages() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("ToMemberId", mMemberId);

        client.post(Constant.GET_MESSAGES, requestParams, new AsyncHttpResponseHandler() {

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
                    Constant.showToast(Activity_Messages.this, errorMessage);
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
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            messagesArrayList.clear();
            listMessages.setVisibility(View.VISIBLE);
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getMessages");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String PrivateMsgId = obj10.optString("PrivateMsgId");
                String PvtMsgSeqNo = obj10.optString("PvtMsgSeqNo");
                String FromMemberId = obj10.optString("FromMemberId");
                String ToMemberId = obj10.optString("ToMemberId");
                String PvtMessage = obj10.optString("PvtMessage");
                String PvtMsgDate = obj10.optString("PvtMsgDate");
                String Attachment = obj10.optString("Attachment");
                String Attachment1 = obj10.optString("Attachment1");
                String ReplyToPvtMsgId = obj10.optString("ReplyToPvtMsgId");
                String PvtMsgDay = obj10.optString("PvtMsgDay");
                String ReadMsg = obj10.optString("ReadMsg");
                String displayFile = obj10.optString("displayFile");
                String FullName = obj10.optString("FullName");
                String ProfilePhoto = obj10.optString("ProfilePhoto");
                String MonthYear = obj10.optString("MonthYear");
                String MsgType = obj10.optString("MsgType");
                String JobId = obj10.optString("JobId");
                String MemberName = obj10.optString("MemberName");
                String EmailId = obj10.optString("EmailId");

                messages = new Messages(PrivateMsgId, PvtMsgSeqNo, FromMemberId, ToMemberId, PvtMessage, PvtMsgDate, Attachment, Attachment1, ReplyToPvtMsgId, PvtMsgDay, ReadMsg, displayFile, FullName, ProfilePhoto,
                        MonthYear, MsgType, JobId, MemberName, EmailId);
                messagesArrayList.add(messages);
            }
            setMessageAdapter = new SetMessageAdapter(Activity_Messages.this, messagesArrayList);
            listMessages.setAdapter(setMessageAdapter);
        } else {
            listMessages.setVisibility(View.GONE);
            Constant.showToast(Activity_Messages.this, "No Messages Found");
        }
    }


    private class SetMessageAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList<Messages> messagesArrayList;
        public ArrayList<ListItem> myItems = new ArrayList();

        public SetMessageAdapter(Activity_Messages activity_messages, ArrayList<Messages> messagesArrayList) {
            this.context = activity_messages;
            this.messagesArrayList = messagesArrayList;

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < messagesArrayList.size(); i++) {
                ListItem listItem = new ListItem();
                listItem.edtData = "Caption" + i;
                myItems.add(listItem);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return messagesArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return messagesArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.custom_row_for_messages, null);

                holder.txtFrom = (TextView) convertView.findViewById(R.id.txtFrom);
                holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
                holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                holder.txtReply = (TextView) convertView.findViewById(R.id.txtReply);
                holder.txtDelete = (TextView) convertView.findViewById(R.id.txtDelete);
                holder.imgAttachment = (ImageView) convertView.findViewById(R.id.imgAttachment);
                holder.imgAtt = (ImageView) convertView.findViewById(R.id.imgAtt);
                holder.edtMessage = (EditText) convertView.findViewById(R.id.edtMessage);
                holder.btnSend = (Button) convertView.findViewById(R.id.btnSend);
                holder.lyBottom = convertView.findViewById(R.id.lyBottom);
                holder.lyReply = convertView.findViewById(R.id.lyReply);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtFrom.setId(position);
            holder.txtDate.setId(position);
            holder.txtMessage.setId(position);
            holder.txtDelete.setId(position);
            holder.txtReply.setId(position);
            holder.lyBottom.setId(position);
            holder.lyReply.setId(position);
            holder.btnSend.setId(position);
            holder.edtMessage.setId(position);
            holder.imgAtt.setId(position);
            holder.imgAttachment.setId(position);

            holder.edtMessage.clearFocus();

            //we need to update adapter once we finish with editing
            holder.edtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        myItems.get(position).edtData = Caption.getText().toString();
                    }
                }
            });

            holder.txtFrom.setText(messagesArrayList.get(position).getFullName());
            holder.txtDate.setText(messagesArrayList.get(position).getPvtMsgDate());
            holder.txtMessage.setText(messagesArrayList.get(position).getPvtMessage());

            if (messagesArrayList.get(position).getAttachment().equals("") || messagesArrayList.get(position).getAttachment().endsWith("Image")) {
                holder.imgAtt.setVisibility(View.GONE);
            }
            else {
                Glide.with(context).load(Constant.IMG_PATH + messagesArrayList.get(position).getAttachment()).into(holder.imgAtt);
                holder.imgAtt.setVisibility(View.VISIBLE);
            }


            holder.txtReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constant.mIsApproved.equalsIgnoreCase("true")){
                        /*holder.lyBottom.setVisibility(View.GONE);
                        holder.lyReply.setVisibility(View.VISIBLE);*/
                        Intent intent = new Intent(Activity_Messages.this, Activity_SendMessage.class);
                        intent.putExtra("MessageID", messagesArrayList.get(position).getPrivateMsgId());
                        intent.putExtra("ToMemberId", messagesArrayList.get(position).getFromMemberId());
                        startActivity(intent);
                    }else{
                        Constant.showToast(Activity_Messages.this, "You are not approved by Admin yet");
                    }
                }
            });

            holder.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteSelectedMessage(messagesArrayList.get(position).getPrivateMsgId());
                }
            });

            holder.imgAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadImagefromGallery();
                }
            });

            holder.btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mMessage = holder.edtMessage.getText().toString().trim();
                    if (mMessage.equalsIgnoreCase(""))
                        Constant.showToast(context, "Please enter message to send");
                    else {
                        holder.lyBottom.setVisibility(View.VISIBLE);
                        holder.lyReply.setVisibility(View.GONE);
                        holder.edtMessage.setText("");
                        ReplyMessage replyMessage = new ReplyMessage(Activity_Messages.this, imgSelected, compressedImage, "Activity_Messages");
                        replyMessage.execute(mMemberId, mMessage, messagesArrayList.get(position).getFromMemberId(), messagesArrayList.get(position).getReplyToPvtMsgId());
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView txtFrom, txtDate, txtMessage, txtReply, txtDelete;
            Button btnSend;
            View lyBottom, lyReply;
            ImageView imgAttachment, imgAtt;
            EditText edtMessage;
        }

        class ListItem {
            String edtData;
        }
    }


    private void DeleteSelectedMessage(String msgId) {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("PrivateMsgId", msgId);

        client.post(Constant.DELETE_MESSAGE, requestParams, new AsyncHttpResponseHandler() {

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
                        parseDeleteMessageResult(str);
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
                    Constant.showToast(Activity_Messages.this, errorMessage);
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

    private void parseDeleteMessageResult(String str) throws JSONException {
        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            Constant.showToast(Activity_Messages.this, "Deleted message successfully.");
            getPrivateMessages();
        } else {
            Constant.showToast(Activity_Messages.this, "Error occurred while deleting message.");
            Constant.showToast(Activity_Messages.this, "Please try again later.");
        }
    }


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
                Constant.showToast(Activity_Messages.this, "Failed to open picture!");
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
                Constant.showToast(Activity_Messages.this, "Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }


    public void compressImage() {
        if (actualImage == null) {
            Constant.showToast(Activity_Messages.this, "Please choose an image!");
        } else {
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

    /**
     * Job Referral Messages
     */
    public void getJobReferralMessages() {
        final AlertDialog progressDialog;
        progressDialog = new SpotsDialog(this, R.style.Custom);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("memberId", mMemberId);

        client.post(Constant.GET_MESSAGES_JOB_REFERRAL, requestParams, new AsyncHttpResponseHandler() {

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
                        parseResultJobReferral(str);
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
                    Constant.showToast(Activity_Messages.this, errorMessage);
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


    private void parseResultJobReferral(String str) throws JSONException {

        JSONArray root = new JSONArray(str);
        JSONObject obj0 = root.getJSONObject(0);
        JSONArray rowsResponse = obj0.getJSONArray("rowsResponse");
        JSONObject obj01 = rowsResponse.getJSONObject(0);
        String result = obj01.getString("response");

        if (result.equals("Success")) {
            jobReferralArrayList.clear();
            listMessages.setVisibility(View.VISIBLE);
            JSONObject obj1 = root.getJSONObject(1);
            JSONArray getWallPosts = obj1.getJSONArray("getJobReferralByMemberId");

            for (int i = 0; i < getWallPosts.length(); i++) {
                JSONObject obj10 = getWallPosts.getJSONObject(i);

                String JobId = obj10.optString("JobId");
                String JobTitle = obj10.optString("JobTitle");
                String JobDescription = obj10.optString("JobDescription");
                String OrganizationId = obj10.optString("OrganizationId");
                String Organization = obj10.optString("Companies");
                String MinExperience = obj10.optString("MinExperience");
                String MaxExperience = obj10.optString("MaxExperience");
                String Attachement = obj10.optString("Attachement");
                String Attachement1 = obj10.optString("Attachement1");
                String IsActive = obj10.optString("IsActive");
                String AdminApproved = obj10.optString("AdminApproved");
                String Branch = obj10.optString("Branch");
                String technology = obj10.optString("technology");
                String lastDate = obj10.optString("lastDate");
                String displayFile = obj10.optString("displayFile");
                String InsertDate = obj10.optString("InsertDate");
                String MemberId = obj10.optString("MemberId");
                String MemberApproved = obj10.optString("MemberApproved");
                String IsDelete = obj10.optString("IsDelete");
                String regLink = obj10.optString("regLink");
                String membername = obj10.optString("membername");

                jobReferral = new JobReferral(JobId, JobTitle, JobDescription, OrganizationId, Organization, MinExperience, MaxExperience, Attachement, Attachement1, IsActive, AdminApproved, AdminApproved, Branch,
                        technology, lastDate, displayFile, InsertDate, MemberId, MemberApproved, IsDelete, regLink, membername);
                jobReferralArrayList.add(jobReferral);
            }
            setJobMessageAdapter = new SetJobMessageAdapter(Activity_Messages.this, jobReferralArrayList);
            listMessages.setAdapter(setJobMessageAdapter);
        } else {
            listMessages.setVisibility(View.GONE);
            Constant.showToast(Activity_Messages.this, "No Messages Found");
        }
    }


    private class SetJobMessageAdapter extends BaseAdapter {
        Context context;
        ArrayList<JobReferral> jobReferralArrayList;

        public SetJobMessageAdapter(Activity_Messages activity_messages, ArrayList<JobReferral> jobReferralArrayList) {
            this.context = activity_messages;
            this.jobReferralArrayList = jobReferralArrayList;
        }

        @Override
        public int getCount() {
            return jobReferralArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return jobReferralArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_for_job_referral, null);

            TextView txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            TextView txtLink = (TextView) convertView.findViewById(R.id.txtLink);

            txtUsername.setText("Hi " + jobReferralArrayList.get(position).getMembername());
            txtDescription.setText(jobReferralArrayList.get(position).getJobDescription());
            txtLink.setText(jobReferralArrayList.get(position).getRegLink());

            return convertView;
        }
    }

}
