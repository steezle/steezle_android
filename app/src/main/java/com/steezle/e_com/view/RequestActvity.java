package com.steezle.e_com.view;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import com.steezle.e_com.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class RequestActvity extends AppCompatActivity {

    private ImageView ivback;
    private ArrayList<NameValuePair> params16;
    private String page_id = "3";
    private String jresult;
    private WebView mywebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_more);
        mywebview = (WebView) findViewById(R.id.exp_terms);

        String text = "<html><body>" + "<p align=\"justify\">" + getString(R.string.html) + "</p>" + "</body></html>";
        mywebview.loadData(text, "text/html", "utf-8");
        mywebview.setBackgroundColor(Color.TRANSPARENT);

/*
        String htmlAsString = getString(R.string.html);
        Spanned htmlAsSpanned = Html.fromHtml(htmlAsString); // used by TextView

        JustifyTextView tv_aboutText = (JustifyTextView) findViewById(R.id.tv_aboutText);
        tv_aboutText.setText(htmlAsSpanned.toString());*/

        ImageView back = (ImageView) findViewById(R.id.back_click_clothing);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}