package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.classes.InternetConnectionDetector;

import dmax.dialog.SpotsDialog;

public class Activity_AlumniContribution extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_contribution);

        WebView webAlumniContribution = (WebView) findViewById(R.id.webAlumniContribution);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        webAlumniContribution.getSettings().setJavaScriptEnabled(true);
        webAlumniContribution.setWebViewClient(new AppWebViewClients());
        webAlumniContribution.loadUrl("http://ssgmcealumni.nxglabs.in/web/mshowDetails.aspx?id=3");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    public class AppWebViewClients extends WebViewClient {
        AlertDialog progressDialog;


        public AppWebViewClients() {
            progressDialog = new SpotsDialog(Activity_AlumniContribution.this, R.style.Custom);
            progressDialog.show();
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressDialog.dismiss();
        }
    }

}
