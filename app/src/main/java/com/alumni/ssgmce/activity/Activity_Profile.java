package com.alumni.ssgmce.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class Activity_Profile extends AppCompatActivity implements View.OnClickListener {

    ImageView imgUserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Initialize();
    }

    private void Initialize() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        View lyEditPersonal = findViewById(R.id.lyEditPersonal);
        View lyEditEducational = findViewById(R.id.lyEditEducation);
        View lyEditWork = findViewById(R.id.lyEditWork);
        imgUserProfile = (ImageView) findViewById(R.id.imgProfile);

        imgBack.setOnClickListener(this);
        lyEditPersonal.setOnClickListener(this);
        lyEditEducational.setOnClickListener(this);
        lyEditWork.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.lyEditPersonal:
                startActivity(new Intent(Activity_Profile.this, Activity_PersonalDetails.class ));
                break;

            case R.id.lyEditEducation:
                startActivity(new Intent(Activity_Profile.this, Activity_EducationalDetails.class ));
                break;

            case R.id.lyEditWork:
                startActivity(new Intent(Activity_Profile.this, Activity_WorkDetails.class ));
                break;

        }
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences;
        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        Constant.mIsApproved = preferences.getString("AdminApproved","false");
        String profilePath = preferences.getString("ProfilePhoto", "");

        if(profilePath.length() > 40 && !profilePath.startsWith("/uploads")){
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
                        circularBitmapDrawable.setCornerRadius(180);
                        imgUserProfile.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else if (profilePath.startsWith("/data")) {
                Glide.with(this).load(profilePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgUserProfile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(180);
                        imgUserProfile.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }
        super.onResume();
    }
}
