package com.steezle.e_com.view;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.ApplyCouponService;
import com.steezle.e_com.utils.Constant;

import org.json.JSONObject;


public class ApplayCouponActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ApplayCouponActivity.class.getSimpleName();

    private ImageView iv_back_mycoupon;
    private Button btn_applayCoupon;
    private EditText edt_couponcode;
    private ProgressBar progressBar;
    private String discount;
    private RequestQueue requestQueue;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_popcopun);
        requestQueue = Volley.newRequestQueue(ApplayCouponActivity.this);

        //Initlization
        setUpView();
        user = AppSharedPreferences.getSharePreference(this).getUser();


        //Register Button Click Event
        btn_applayCoupon.setOnClickListener(this);
        iv_back_mycoupon.setOnClickListener(this);


    }


    public void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btn_applayCoupon = (Button) findViewById(R.id.btn_applayCoupon);
        iv_back_mycoupon = (ImageView) findViewById(R.id.iv_back_mycoupon);
        edt_couponcode = (EditText) findViewById(R.id.edt_couponcode);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_applayCoupon:
                applyCoupon();
                break;

            case R.id.iv_back_mycoupon:
                finish();
                break;
        }


    }


    private void applyCoupon() {

        //progressBar.setVisibility(View.VISIBLE);
        ApplyCouponService.applyCoupan(
                this,
                user.getUser_id(),
                edt_couponcode.getText().toString(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "ApplyCouponRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString(Constant.ApplyCoupon.STATUS).equals("S")) {

                                    JSONObject obj1 = obj.getJSONObject(Constant.ApplyCoupon.DATA);

                                    discount = obj1.getString(Constant.ApplyCoupon.DISCOUNT);

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("PREF_NAME", discount);
                                    editor.commit();

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.ApplyCoupon.MESSAGE), Toast.LENGTH_SHORT).show();
                                    finish();

                                } else if (obj.getString(Constant.ApplyCoupon.STATUS).equals("F")) {

                                    progressBar.setVisibility(View.GONE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplayCouponActivity.this);
                                    builder.setTitle(getResources().getString(R.string.app_name));
                                    builder.setMessage(obj.getString(Constant.ApplyCoupon.MESSAGE));
                                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {

                                            edt_couponcode.setText("");

                                        }
                                    });

                                } else {

                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.ApplyCoupon.MESSAGE), Toast.LENGTH_SHORT).show();
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



   /* public void couponcodeAPI() {
        //showpDialog();
        final String codes = edt_couponcode.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.applaycoupon,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            JSONObject obj1 = obj.getJSONObject("data");
                            code = obj1.getString("discount");

                            String msg = obj.getString("message");
                            String status = obj.getString("status");
                            //if no error in response
                            if (status.equals("S")) {
                                Intent i = new Intent(ApplayCouponActivity.this, PaymentActivity.class);
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("PREF_NAME", code);
                                editor.commit();

                                startActivity(i);
                                finish();

                            } else if (status.equals("F")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ApplayCouponActivity.this);
                                builder.setTitle(getResources().getString(R.string.app_name));
                                builder.setMessage(msg);
                                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {

                                        edt_couponcode.setText("");

                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        //hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                user = AppSharedPreferences.getSharePreference(ApplayCouponActivity.this).getUser();
                params.put("user_id", user.getUser_id());
                params.put("code", codes);
                return params;
            }

        };

        stringRequest.setShouldCache(false);
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(this);
        requestQueue.add(stringRequest);
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTvResult.setText(error.getMessage());
            }
        };
    }*/

    /*private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/


}