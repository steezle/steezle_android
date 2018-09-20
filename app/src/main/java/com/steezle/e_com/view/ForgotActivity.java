package com.steezle.e_com.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;

import org.json.JSONObject;

import com.steezle.e_com.networking.CommonValidation;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.ForgotPasswordService;
import com.steezle.e_com.utils.Constant;

/**
 * Created by juli on 4/12/17.
 */

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ForgotActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private ImageView iv_forgot_back;
    private ProgressDialog pDialog;
    private Button btn_forgot_submit;
    private RequestQueue requestQueue1;
    private EditText edt_forgot_email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_forgot);

        //Initlization
        setUpView();


        //Register Button Click Event
        iv_forgot_back.setOnClickListener(this);
        btn_forgot_submit.setOnClickListener(this);


    }


    public void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btn_forgot_submit = (Button) findViewById(R.id.btn_forgot_submit);
        iv_forgot_back = (ImageView) findViewById(R.id.iv_forgot_back);
        requestQueue1 = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        edt_forgot_email = (EditText) findViewById(R.id.edt_forgot_email);
        btn_forgot_submit.setOnClickListener(this);
        iv_forgot_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_forgot_back:
                finish();
                break;

            case R.id.btn_forgot_submit:
                if (checkValidation()) {
                    forgotPassword();
                }
                //forgotapi();
                break;
        }

    }



    private boolean checkValidation() {

        if (CommonValidation.isEdittextEmpty(edt_forgot_email)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotActivity.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(R.string.error_empty_email);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } else if (CommonValidation.isEmailIdInvalid(edt_forgot_email)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotActivity.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(R.string.error_valid_email);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {

            return true;
        }
        return false;
    }



    private void forgotPassword() {

        //progressBar.setVisibility(View.VISIBLE);
        ForgotPasswordService.forgotPassword(
                this,
                edt_forgot_email.getText().toString(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "ForgotPasswordRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString(Constant.ForgotPassword.STATUS).equals("S")) {

                                    progressBar.setVisibility(View.GONE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotActivity.this);

                                    builder.setTitle(getResources().getString(R.string.app_name));
                                    builder.setMessage(getString(R.string.passwordreset));
                                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.ForgotPassword.MESSAGE), Toast.LENGTH_SHORT).show();
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



    /*private void forgotapi() {
        showpDialog();
        final String email = edt_forgot_email.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.Forgot_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getString("status").equals("S")) {


                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotActivity.this);

                                builder.setTitle(getResources().getString(R.string.app_name));
                                builder.setMessage(getString(R.string.passwordreset));

                                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();

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

                                finish();


                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(this);
        requestQueue1.add(stringRequest);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/
}
