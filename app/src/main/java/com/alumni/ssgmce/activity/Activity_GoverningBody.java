package com.alumni.ssgmce.activity;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.alumni.ssgmce.R;

import dmax.dialog.SpotsDialog;

public class Activity_GoverningBody extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_governing_body);

        WebView webGoverningBody = (WebView) findViewById(R.id.webGoverningBody);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(this);

        webGoverningBody.getSettings().setJavaScriptEnabled(true);
        webGoverningBody.setWebViewClient(new AppWebViewClients());
        webGoverningBody.loadUrl("http://ssgmcealumni.nxglabs.in/web/mshowDetails.aspx?id=2");
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
            progressDialog = new SpotsDialog(Activity_GoverningBody.this, R.style.Custom);
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
