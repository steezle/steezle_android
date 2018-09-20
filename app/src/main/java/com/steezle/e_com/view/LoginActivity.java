package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.steezle.e_com.R;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.networking.CommonValidation;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.LoginService;
import com.steezle.e_com.services.SocialLoginService;
import com.steezle.e_com.utils.Constant;


public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = LoginActivity.class.getSimpleName();
    public static LoginActivity actDashBroadRef;
    private ProgressBar progressBar;
    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;
    private int SIGN_IN = 30;
    private GoogleSignInOptions gso;
    private LinearLayout ll_btn_googleSignIn, ll_btn_facebookSignIn;
    TextView tv_skip_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //Initlization
        setUpView();
        initGoogleClient();
        initFaceboock();

        //Register Button Click Event
        ll_btn_facebookSignIn.setOnClickListener(this);
        ll_btn_googleSignIn.setOnClickListener(this);

        tv_skip_login.setOnClickListener( this );

    }

    public void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ll_btn_googleSignIn = (LinearLayout) findViewById(R.id.ll_btn_googleSignIn);
        ll_btn_facebookSignIn = (LinearLayout) findViewById(R.id.ll_btn_facebookSignIn);
        tv_skip_login=(TextView)findViewById( R.id.tv_skip_login );

    }

    private void initFaceboock() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_btn_facebookSignIn:
                //progressBar.setVisibility(View.VISIBLE);
                faceBookSetting();
                break;

            case R.id.ll_btn_googleSignIn:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, SIGN_IN);
                break;


            case  R.id.tv_skip_login:
                AppSharedPreferences.getSharePreference(getApplicationContext()).ClearLogout();
                Intent intent1 = new Intent(LoginActivity.this, TabActivity.class);
                startActivity(intent1);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(true);
            pDialog.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {

        Log.e(TAG, "GoogleResult--->" + result);

        if (result.isSuccess()) {

            final GoogleSignInAccount acct = result.getSignInAccount();

            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

            if (signInAccount != null) {

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

            }

            Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                @Override
                public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {

                    Log.e(TAG, "GooglePeopleResult--->" + loadPeopleResult);

                    JSONObject jsonObject = new JSONObject();

                    try {

                        jsonObject.put("first_name", acct.getDisplayName());
                        jsonObject.put("socialid", acct.getId());
                        jsonObject.put("email", acct.getEmail());
                        jsonObject.put("profile_pic", acct.getPhotoUrl());
                        jsonObject.put("gender", "");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    getSocialLogin(
                            jsonObject.toString(),
                            acct.getEmail(),
                            acct.getId(),
                            "GOOGLE");
                }
            });

        } else {
            //Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);

    }

    private void faceBookSetting() {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        Log.e(TAG, "FacebookLogin--->" + response);

                                        String name = response.getJSONObject().optString("name");
                                        String userID = response.getJSONObject().optString("id");
                                        String email = response.getJSONObject().optString("email");
                                        String gender = response.getJSONObject().optString("gender");
                                        String profileUrl = "https://graph.facebook.com/" + userID + "/picture?type=large";
                                        String[] nameFull = name.split(" ");
                                        String gen;
                                        if (gender.equals("male")) {
                                            gen = "M";
                                        } else if (gender.equals("female")) {
                                            gen = "F";
                                        } else {
                                            gen = "U";
                                        }

                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("first_name", name);
                                            jsonObject.put("socialid", userID);
                                            jsonObject.put("email", email);
                                            jsonObject.put("profile_pic", profileUrl);
                                            jsonObject.put("gender", gen);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        getSocialLogin(jsonObject.toString(),
                                                email,
                                                userID,
                                                "FACEBOOK");

                                        Log.v("LoginActivity", response.toString());
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    /**
                     * Called when the dialog finishes with an error.
                     *
                     * @param error The error that occurred
                     */
                    @Override
                    public void onError(FacebookException error) {
                        Log.e("", error.toString());
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

    }


    private void getSocialLogin(final String socialData,
                                final String email,
                                final String socilId,
                                final String socialTypeFrom) {

        SocialLoginService.getSocialLogin(
                this,
                socialData,
                email,
                socilId,
                socialTypeFrom,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "SocialLoginRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString(Constant.SocialLogin.STATUS).equals("S")) {

                                    JSONObject jsonObject = obj.optJSONObject(Constant.SocialLogin.DATA);

                                    User user = new User(jsonObject);

                                    AppSharedPreferences.getSharePreference(getApplicationContext()).setUser(user);

//                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.SocialLogin.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                    AppSharedPreferences.getSharePreference(getApplicationContext()).setIsLogin(true);
                                    AppSharedPreferences.getSharePreference(getApplicationContext()).setLoginType(false);

                                    if (!AppSharedPreferences.getSharePreference(getApplicationContext()).isFirstTime()) {
                                        if (LoginActivity.actDashBroadRef == null) {
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.putExtra("From", "Login");
                                            startActivity(intent);

                                        }
                                    } else {
                                        //Toast.makeText(getApplicationContext(), jsonObject.getString(Constant.Login.MESSAGE), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                                        startActivity(intent);

                                    }
                                    //startActivity(new Intent(getApplicationContext(), TabActivity.class));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }


        );
    }


    private void initGoogleClient() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API
        // and the options specified by gGoogleSignInOptions.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

    }

}




