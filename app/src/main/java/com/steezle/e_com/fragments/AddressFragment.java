package com.steezle.e_com.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.view.ShippingaddressActivty;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.SettingPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


/**
 * Created by Mehul on 03-Dec-2015.
 */
public class
AddressFragment extends SettingPagerAdapter implements View.OnClickListener {

    private static final String TAG = AddressFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private View mainFragmentView;
    private Context context;
    private User users;
    private RequestQueue requestQueue;
    private LinearLayout ll_btn_shippingAddress, ll_btn_BillingAddress;
    private TextView tv_saved_shippingAddress, tv_saved_billingAddress;
    private TextView tv_address_email, tv_address_phone;
    private LinearLayout ll_blackAddressView, ll_addressView, ll_btn_addAddress;

    @Override
    protected View initView() {
        return null;
    }

    @Override
    protected View initView(User user) {
        this.users = user;
        mainFragmentView = View.inflate(mContext, R.layout.act_shippingaddress, null);
        requestQueue = Volley.newRequestQueue(getActivity());

        //Initlization
        setUpView(mainFragmentView);
        users = AppSharedPreferences.getSharePreference(getActivity()).getUser();

        //Register Button Click Event
        ll_btn_shippingAddress.setOnClickListener(this);
        ll_btn_BillingAddress.setOnClickListener(this);


        return mainFragmentView;

    }


    private void setUpView(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        ll_btn_shippingAddress = (LinearLayout) view.findViewById(R.id.ll_btn_shippingAddress);
        ll_btn_BillingAddress = (LinearLayout) view.findViewById(R.id.ll_btn_BillingAddress);
        tv_saved_shippingAddress = (TextView) view.findViewById(R.id.tv_saved_shippingAddress);
        tv_saved_billingAddress = (TextView) view.findViewById(R.id.tv_saved_billingAddress);
        tv_address_email = (TextView) view.findViewById(R.id.tv_address_email);
        tv_address_phone = (TextView) view.findViewById(R.id.tv_address_phone);

        ll_addressView = (LinearLayout) view.findViewById(R.id.ll_addressView);
        ll_blackAddressView = (LinearLayout) view.findViewById(R.id.ll_blackAddressView);
        ll_btn_addAddress = (LinearLayout) view.findViewById(R.id.ll_btn_addAddress);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_btn_shippingAddress:
                Intent i = new Intent(getActivity(), ShippingaddressActivty.class);
                startActivity(i);
                break;

            case R.id.ll_btn_BillingAddress:
                Intent intent = new Intent(getActivity(), ShippingaddressActivty.class);
                startActivity(intent);
                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        new FetchingStudentAsync().execute();

    }

    @SuppressLint("StaticFieldLeak")
    public class FetchingStudentAsync extends AsyncTask<String, String, String> {


        public FetchingStudentAsync() {
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(final String... strings) {

            InputStream inputStream = null;
            String result = "";

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(APIs.getaddress);

                JSONObject jsonObject = new JSONObject();
                users = AppSharedPreferences.getSharePreference(getActivity()).getUser();
                jsonObject.accumulate("user_id", users.getUser_id());

                String json = "";
                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                Log.e( "se_address",""+json.toString() );
                httpPost.setEntity(se);

                httpPost.setHeader("Content-type", "application/json");

//                httpPost.setHeader("application/json", "Accept ");

                HttpResponse httpResponse = httpclient.execute(httpPost);

                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }

                    result = total.toString();
//                    Log.e( "Result address",""+result );
//                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();

                } else
                    result = "Did not work!";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Log.e(TAG, "GetAddress--->" + response);

            progressBar.setVisibility(View.VISIBLE);
            if (response != null) {

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (obj.getString("status").equals("S")) {

                        JSONObject obj1 = obj.getJSONObject("data");

                        JSONObject billingObj = obj1.getJSONObject("billing");

                        if (!billingObj.getString("name").equals(""))
                        {
                            ll_addressView.setVisibility(View.VISIBLE);
                            ll_blackAddressView.setVisibility(View.GONE);
                            String s1 = billingObj.getString("name") + "\n" +
                                    billingObj.getString("address_1") + "\n" +
                                    billingObj.getString("address_2") + "\n" +
                                    billingObj.getString("city") + ", " +
                                    billingObj.getString("postcode") + ", " +
                                    billingObj.getString("country") + ", " +
                                    billingObj.getString("state");
                            tv_saved_billingAddress.setText(s1);

                            tv_address_email.setText(users.getUser_email());
                            tv_address_phone.setText(billingObj.getString("phone"));

                            JSONObject obj3 = obj1.getJSONObject("shipping");

                            String shippingString = obj3.getString("name") + "\n" +
                                    obj3.getString("address_1") + "\n" +
                                    obj3.getString("address_2") + "\n" +
                                    obj3.getString("city") + ", " +
                                    obj3.getString("postcode") + ", " +
                                    obj3.getString("country") + ", " +
                                    obj3.getString("state");

                            tv_saved_shippingAddress.setText(shippingString);

                            progressBar.setVisibility(View.GONE);

                        } else {
                            ll_addressView.setVisibility(View.GONE);
                            ll_blackAddressView.setVisibility(View.VISIBLE);
                            ll_btn_addAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(), ShippingaddressActivty.class);
                                    startActivity(i);
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }

            }
        }
    }


    @Override
    protected void initData() {
        super.initData();

    }

}