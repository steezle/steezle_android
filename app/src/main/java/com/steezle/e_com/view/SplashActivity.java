package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.steezle.e_com.R;

import com.steezle.e_com.networking.AppSharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @SuppressLint("PackageManagerGetSignatures")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launchscreen);


        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.steezle.e_com", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                if (!AppSharedPreferences.getSharePreference(SplashActivity.this).isLogin()) {

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);

                } else {
                    Intent i = new Intent(SplashActivity.this, TabActivity.class);
                    i.putExtra("From", "Login");
                    startActivity(i);
                }
                finish();

            }
        }, SPLASH_TIME_OUT);

    }


}
