package com.alumni.ssgmce.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Batchmates;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.SendMessage;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class Activity_SendMessage extends AppCompatActivity implements View.OnClickListener {

    private static int RESULT_LOAD_IMG = 1;
    private int LOAD_IMG = 0;
    private File actualImage;
    private File compressedImage;
    ImageView imgAttachment;
    EditText edtMessage;
    Batchmates batchmates;
    String mMessage, mMemberId, mToMemberId, mMessageId = "0";
    private EditText edtFullName, edtCourseName, edtBranch, edtJoiningYear, edtCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Initialize();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("ToMemberId"))
        {
            mToMemberId = bundle.getString("ToMemberId");
            mMessageId = bundle.getString("MessageID");
            if(bundle.containsKey("Object")) {
                batchmates = (Batchmates) bundle.get("Object");

                edtFullName.setText(batchmates.getFullName());
                edtBranch.setText(batchmates.getBranch());
                edtCourseName.setText(batchmates.getCourseName());
                edtJoiningYear.setText(batchmates.getJoiningYear());
                edtCurrentLocation.setText(batchmates.getCurrentLocation());

                edtFullName.setEnabled(false);
                edtBranch.setEnabled(false);
                edtCourseName.setEnabled(false);
                edtJoiningYear.setEnabled(false);
                edtCurrentLocation.setEnabled(false);
            }else{
                ((View)findViewById(R.id.lyDetails)).setVisibility(View.GONE);
            }
        }

    }

    private void Initialize() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        ImageView imgAttach = (ImageView) findViewById(R.id.imgAttach);
        TextView txtSend = (TextView) findViewById(R.id.txtSend);
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        imgAttachment = (ImageView) findViewById(R.id.imgAttachment);
        edtFullName = (EditText) findViewById(R.id.edtFullName);
        edtBranch = (EditText) findViewById(R.id.edtBranch);
        edtCourseName = (EditText) findViewById(R.id.edtCourse);
        edtJoiningYear = (EditText) findViewById(R.id.edtJoiningYr);
        edtCurrentLocation = (EditText) findViewById(R.id.edtCurrentLocation);

        imgBack.setOnClickListener(this);
        imgAttach.setOnClickListener(this);
        txtSend.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        mMemberId = preferences.getString("MemberId", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgAttach:
                loadImagefromGallery();
                break;

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.txtSend:
                if(Constant.mIsApproved.equalsIgnoreCase("true")) {
                    sendMessage();
                }else{
                    Constant.showToast(Activity_SendMessage.this, "You are not approved by Admin yet");
                 }
                break;
        }
    }

    private void sendMessage() {
        mMessage = edtMessage.getText().toString().trim();

        if(mMessage.equalsIgnoreCase(""))
        {
            Constant.showToast(this, "Enter some text to send");
        }else
        {
            SendMessage sendMessage = new SendMessage(this, imgAttachment, compressedImage, "SendMessage");
            if(LOAD_IMG == 0)
                sendMessage.execute(mMemberId, mToMemberId, mMessage, mMessageId, "");
            else
                sendMessage.execute(mMemberId, mToMemberId, mMessage, mMessageId, actualImage.getName());
        }
    }


    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    public void clearData()
    {
        edtMessage.setText("");
        imgAttachment.setImageBitmap(null);

        onBackPressed();
    }

    /**
     * When Image is selected from Gallery
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            if (data == null) {
                Constant.showToast(Activity_SendMessage.this, "Failed to open picture!");
                return;
            }
            try {
                LOAD_IMG = 1;
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                actualImage = FileUtil.from(this, data.getData());
                imgAttachment.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                imgAttachment.setVisibility(View.VISIBLE);
                compressImage();
            } catch (IOException e) {
                Constant.showToast(Activity_SendMessage.this, "Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }


    public void compressImage() {
        if (actualImage == null) {
            Constant.showToast(Activity_SendMessage.this, "Please choose an image!");
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
        imgAttachment.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        imgAttachment.setVisibility(View.VISIBLE);
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
    }
}
