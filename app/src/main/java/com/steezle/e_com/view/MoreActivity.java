package com.steezle.e_com.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.invitereferrals.invitereferrals.InviteReferralsApi;
import com.invitereferrals.invitereferrals.InviteReferralsApi;
import com.steezle.e_com.R;
import com.steezle.e_com.database.DatabaseHandler;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.LogOutService;
import com.steezle.e_com.utils.Constant;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by juli on 13/2/18.
 */

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MoreActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private LinearLayout ll_terms, ll_about, ll_referral, ll_faqReturnPolicy, ll_contact;
    private List<NameValuePair> params11;
    private User user;
    private RelativeLayout rl_btn_logout;
    private ImageView back_click_clothing;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more);

        //Initlization
        setUpView();
        user = AppSharedPreferences.getSharePreference(this).getUser();
        databaseHandler = new DatabaseHandler(this);

        //Register Button Click Event
        back_click_clothing.setOnClickListener(this);
        rl_btn_logout.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        ll_terms.setOnClickListener(this);
        ll_referral.setOnClickListener(this);
        ll_contact.setOnClickListener( this );
        ll_faqReturnPolicy.setOnClickListener(this);
    }

    public void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ll_terms = (LinearLayout) findViewById(R.id.ll_terms);
        ll_faqReturnPolicy = (LinearLayout) findViewById(R.id.ll_faqReturnPolicy);
        ll_about = (LinearLayout) findViewById(R.id.ll_about);
        back_click_clothing = (ImageView) findViewById(R.id.back_click_clothing);
        rl_btn_logout = (RelativeLayout) findViewById(R.id.rl_btn_logout);
        ll_referral = (LinearLayout) findViewById(R.id.ll_referral);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click_clothing:
                finish();
                break;

            case R.id.ll_terms:
                Intent intent = new Intent(MoreActivity.this, TermsActvity.class);
                startActivity(intent);
                break;

            case R.id.ll_faqReturnPolicy:
                Intent intent1 = new Intent(MoreActivity.this, FAQReturnPolicyActvity.class);
                startActivity(intent1);
                break;

            case R.id.ll_about:
                Intent intent2 = new Intent(MoreActivity.this, RequestActvity.class);
                startActivity(intent2);
                break;

            case R.id.rl_btn_logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(MoreActivity.this);

                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getString(R.string.logout_text));

                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        //onLogoutSuccess();
                    }
                });

                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.ll_referral:
                //shareAction();
               shareActionAutoLogin();
                break;

            case R.id.ll_contact:
                Intent intent3 = new Intent(MoreActivity.this, ContactUsActivity.class);
                startActivity(intent3);
                break;

        }
    }


    public void shareActionAutoLogin() {
        try {
            user = AppSharedPreferences.getSharePreference(this).getUser();
            Log.e( "User name",""+user.getFirst_name()+" "+user.getLast_name() );
            Log.e( "Email address",""+user.getUser_email() );
//            InviteReferralsApi.getInstance( this ).userDetails( "Tom", "tom@gmail.com", "9812546723", 18509, null, null );
            InviteReferralsApi.getInstance( this ).userDetails( ""+user.getFirst_name()+" "+user.getLast_name(), ""+user.getUser_email(), ""+user.getContact_number(), 18509, null, null );
//        InviteReferralsApi.getInstance(this).userDetails("Test", "test@tagnpin.com", null, 18043, null, null);
            InviteReferralsApi.getInstance( MoreActivity.this ).inline_btn( 18509 );
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void logout() {

       // progressBar.setVisibility(View.VISIBLE);
        LogOutService.logout(
                this,
                user.getUser_id(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {

                        if (response != null) {

                            Log.e(TAG, "LogOutRes--->" + response);

                            try {

                                JSONObject json = new JSONObject(response);

                                if (json.getString(Constant.LogOut.STATUS).equals("S")) {

                                    progressBar.setVisibility(View.GONE);
                                    databaseHandler.removeAllData();

                                    try {
                                        AppSharedPreferences.getSharePreference( getApplicationContext() ).setUser( null );
                                        AppSharedPreferences.getSharePreference( getApplicationContext() ).logOut();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    Intent ii = new Intent(MoreActivity.this, LoginActivity.class);
                                    ii.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                    startActivity(ii);
                                    finish();


                                } else {
                                    Toast.makeText(getApplicationContext(), json.getString(Constant.LogOut.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    }
                }
        );
    }

}


