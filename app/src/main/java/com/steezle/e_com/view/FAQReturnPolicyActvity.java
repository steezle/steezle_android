package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.steezle.e_com.R;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class FAQReturnPolicyActvity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_click_clothing;
    private ArrayList<NameValuePair> params16;
    private WebView mywebview;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_returnpolicy);

        //Initlization
        setUpView();

        mywebview.setWebViewClient(new myWebClient());
        this.mywebview.getSettings().setJavaScriptEnabled(true);
        this.mywebview.loadUrl("http://steezle.ca/faq_and_return_policy.html");


        //Register Button Click Event
        back_click_clothing.setOnClickListener(this);

    }


    private void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        back_click_clothing = (ImageView) findViewById(R.id.back_click_clothing);
        mywebview = (WebView) findViewById(R.id.exp_terms);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click_clothing:
                finish();
                break;
        }
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mywebview.canGoBack()) {
            mywebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}