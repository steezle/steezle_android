package com.steezle.e_com.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.ApplyCouponService;
import com.steezle.e_com.services.CheckOutService;
import com.steezle.e_com.services.PaymentService;
import com.steezle.e_com.services.RemoveCouponService;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.view.TabActivity;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.steezle.e_com.view.PaymentActivity.tv_favourite;


public class CreditFragement extends android.support.v4.app.Fragment implements APIs, View.OnClickListener {

    public static final String TAG = CreditFragement.class.getSimpleName();

    private ProgressBar progressBar;
    private String newToken;
    private EditText edt_email;
    private TextView tv_marchandise, tv_shippingPrice, tv_taxPrice, tv_totalPrice, tv_discountedPrice, cardname;
    private String sdiscount;
    private CardMultilineWidget mCardInputWidget;
    private RelativeLayout rl_btn_payNow;
    private LinearLayout ll_couponCode;
    private RequestQueue requestQueue;
    private User user;
    private Intent intent;
    private Bundle bundle;
    private String cartProductArray;
    private CheckBox cb_saveCard;
    private Dialog applyCouponDialog;
    private ImageView iv_removeCoupon;

    public static CreditFragement newInstance() {
        CreditFragement fragment = new CreditFragement();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.act_frg_credit, container, false );
        requestQueue = Volley.newRequestQueue( getActivity() );

        //Initlization
        setUpView( v );
        user = AppSharedPreferences.getSharePreference( getActivity() ).getUser();
        edt_email.setText( user.getUser_email() );

        intent = getActivity().getIntent();
        sdiscount = intent.getStringExtra( "discount" );
        bundle = getArguments();
        cartProductArray = intent.getStringExtra( "CartProductArray" );


        checkOutProducts();


        //Register Button Click Event
        rl_btn_payNow.setOnClickListener( this );
        ll_couponCode.setOnClickListener( this );
        iv_removeCoupon.setOnClickListener( this );


        return v;
    }


    private void setUpView(View v) {

        progressBar = (ProgressBar) v.findViewById( R.id.progressBar );
        tv_marchandise = (TextView) v.findViewById( R.id.tv_marchandise );
        tv_shippingPrice = (TextView) v.findViewById( R.id.tv_shippingPrice );
        edt_email = (EditText) v.findViewById( R.id.edt_email );
        tv_taxPrice = (TextView) v.findViewById( R.id.tv_taxPrice );
        tv_totalPrice = (TextView) v.findViewById( R.id.tv_totalPrice );
        tv_discountedPrice = (TextView) v.findViewById( R.id.tv_discountedPrice );
        cardname = (TextView) v.findViewById( R.id.cardname );
        mCardInputWidget = (CardMultilineWidget) v.findViewById( R.id.card_multiline_widget );

        ll_couponCode = (LinearLayout) v.findViewById( R.id.ll_couponCode );
        rl_btn_payNow = (RelativeLayout) v.findViewById( R.id.rl_btn_payNow );
        iv_removeCoupon = (ImageView) v.findViewById( R.id.iv_removeCoupon );
        cb_saveCard = (CheckBox) v.findViewById( R.id.cb_saveCard );

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_couponCode:
                if (tv_discountedPrice.getText().toString().length() == 0) {
                    applyCouponDialogView();
                    break;
                }

            case R.id.rl_btn_payNow:

//                final Stripe stripe = new Stripe( getActivity(), "pk_test_uVBDZ0wJcJBrI8LGxY0BdKSH" );
                final Stripe stripe = new Stripe( getActivity(), "pk_live_ZG4KImX0bCskmlkf2tT5q6q4" );
                final Card card = mCardInputWidget.getCard();

                if (card == null) {
                } else  if (cardname.getText().toString().length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                    builder.setTitle( "Steezle");
                    builder.setMessage("Enter card name!");

                    builder.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    } );
                    builder.show();
                } else {
                    progressBar.setVisibility( View.VISIBLE );
                    stripe.createToken(
                            card, new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your
                                    newToken = token.getId();
                                    progressBar.setVisibility( View.GONE );
                                    makePayment();
                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    progressBar.setVisibility( View.GONE );
                                    Toast.makeText( getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG ).show();
                                }
                            }
                    );
                }
                break;

            case R.id.iv_removeCoupon:

                //progressBar.setVisibility(View.VISIBLE);
                if (tv_discountedPrice.getText().toString().length() == 0) {
                    applyCouponDialogView();
                } else {
                    removeCoupon();
                }
                break;

        }
    }


    private void makePayment() {

        PaymentService.makePayment(
                getActivity(),
                user.getUser_id(),
                newToken,
                "",
                getSavedOrNot(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e( TAG, "PaymentRes--->" + response );

                        if (response != null) {

                            try {

                                JSONObject json = new JSONObject( response );
                                String status = json.getString( "result" );
                                Log.d( "Status:", status );

                                if (status.equals( "success" )) {
                                    progressBar.setVisibility( View.GONE );
                                    final Dialog alertDialog = new Dialog( getActivity() );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.activity_payment_aceept_popup );
                                    alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                    alertDialog.show();

                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            Intent ii = new Intent( getActivity(), TabActivity.class );
                                            ii.putExtra( "From", "order" );
                                            startActivity( ii );
                                        }
                                    }, 1000 );

                                } else if (status.equals( "failure" )) {
                                    progressBar.setVisibility( View.GONE );
                                    final Dialog alertDialog = new Dialog( getActivity() );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.activity_payment_declined_popup );
                                    alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                    alertDialog.show();

                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                        }
                                    }, 1000 );
                                } else {

                                    Toast.makeText( getActivity(), json.getString( Constant.MakePayment.MESSAGE ), Toast.LENGTH_LONG ).show();

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility( View.GONE );
                            }
                        }
                    }
                }

        );

    }


    private String getSavedOrNot() {

        if (cb_saveCard.isChecked()) {
            return "1";
        } else {
            return "0";
        }
    }


    private void checkOutProducts() {

        //progressBar.setVisibility(View.VISIBLE);
        CheckOutService.checkOut(
                getActivity(),
                user.getUser_id(),
                cartProductArray,
                progressBar,
                new APIService.Success<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        Log.e( TAG, "CheckOutRes--->" + response );

                        if (response != null) {

                            try {

                                JSONObject object = new JSONObject( response );

                                tv_favourite.setText( object.getString( Constant.CheckOut.BAG_COUNT ) );

                                if (object.getString( Constant.CheckOut.STATUS ).equals( "S" )) {

                                    JSONObject data = object.getJSONObject( Constant.CheckOut.DATA );

                                    JSONObject cartTotals = data.getJSONObject( Constant.CheckOut.CART_TOTALS );

                                    String marchandise = cartTotals.getString( "cart_subtotal" );
                                    tv_marchandise.setText( "$" + String.format( "%.2f", Double.parseDouble( marchandise ) ) );

                                    String shipping = cartTotals.getString( "shipping" );
                                    tv_shippingPrice.setText( "$" + String.format( "%.2f", Double.parseDouble( shipping ) ) );

                                    String tax = cartTotals.getString( "tax" );
                                    tv_taxPrice.setText( "$" + String.format( "%.2f", Double.parseDouble( tax ) ) );

                                    String cart_total = cartTotals.getString( "cart_total" );
                                    tv_totalPrice.setText( "$" + String.format( "%.2f", Double.parseDouble( cart_total ) ) );

                                    tv_discountedPrice.setText( "" );
                                    progressBar.setVisibility( View.GONE );

                                } else if (object.getString( Constant.CheckOut.STATUS ).equals( "W" )) {

                                    Toast.makeText( getApplicationContext(), object.getString( Constant.CheckOut.MESSAGE ), Toast.LENGTH_SHORT ).show();
                                    progressBar.setVisibility( View.GONE );

                                } else {

                                    Toast.makeText( getApplicationContext(), object.getString( Constant.CheckOut.MESSAGE ), Toast.LENGTH_SHORT ).show();
                                    progressBar.setVisibility( View.GONE );
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility( View.GONE );
                            }

                        } else if (response.equals( "null" )) {

                            Toast.makeText( getActivity(), "No record Found", Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
        );
    }


    private void applyCouponDialogView() {

        applyCouponDialog = new Dialog( getActivity() );
        applyCouponDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        applyCouponDialog.setContentView( R.layout.applycoupon_dialogview );
        applyCouponDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.WHITE ) );
        applyCouponDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        applyCouponDialog.setCanceledOnTouchOutside( true );
        applyCouponDialog.setCancelable( true );
        applyCouponDialog.show();

        Button btn_applayCoupon = (Button) applyCouponDialog.findViewById( R.id.btn_applayCoupon );
        ImageView iv_back_mycoupon = (ImageView) applyCouponDialog.findViewById( R.id.iv_back_mycoupon );
        final EditText edt_couponcode = (EditText) applyCouponDialog.findViewById( R.id.edt_couponcode );

        iv_back_mycoupon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCouponDialog.hide();
            }
        } );

        btn_applayCoupon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //progressBar.setVisibility(View.VISIBLE);
                ApplyCouponService.applyCoupan(
                        getActivity(),
                        user.getUser_id(),
                        edt_couponcode.getText().toString(),
                        progressBar,
                        new APIService.Success<String>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(String response) {
                                Log.e( TAG, "ApplyCouponRes--->" + response );

                                if (response != null) {

                                    try {

                                        JSONObject obj = new JSONObject( response );

                                        if (obj.getString( Constant.ApplyCoupon.STATUS ).equals( "S" )) {

                                            JSONObject obj1 = obj.getJSONObject( Constant.ApplyCoupon.DATA );

                                            String marchandise = obj1.getString( "cart_subtotal" );
                                            tv_marchandise.setText( "$" + Double.parseDouble( marchandise ) );

                                            String cart_total = obj1.getString( "cart_total" );
                                            tv_totalPrice.setText( "$" + Double.parseDouble( cart_total ) );

                                            String shipping = obj1.getString( "shipping" );
                                            tv_shippingPrice.setText( "$" + Double.parseDouble( shipping ) );

                                            String tax = obj1.getString( "tax" );
                                            tv_taxPrice.setText( "$" + Double.parseDouble( tax ) );

                                            String discount = obj1.getString( "discount" );
                                            tv_discountedPrice.setText( "-$" + Double.parseDouble( discount ) );
                                            iv_removeCoupon.setImageResource( R.drawable.ic_remove_coupon );

                                            progressBar.setVisibility( View.GONE );
                                            Toast.makeText( getApplicationContext(), "Coupon apply successfully!", Toast.LENGTH_SHORT ).show();
                                            applyCouponDialog.hide();

                                        } else if (obj.getString( Constant.ApplyCoupon.STATUS ).equals( "F" )) {

                                            progressBar.setVisibility( View.GONE );
                                            AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                                            builder.setTitle( getResources().getString( R.string.app_name ) );
                                            builder.setMessage( obj.getString( Constant.ApplyCoupon.MESSAGE ) );
                                            builder.setPositiveButton( getString( R.string.yes ), new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    edt_couponcode.setText( "" );
                                                }
                                            } );

                                        } else {

                                            Toast.makeText( getApplicationContext(), obj.getString( Constant.ApplyCoupon.MESSAGE ), Toast.LENGTH_SHORT ).show();
                                            progressBar.setVisibility( View.GONE );

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        progressBar.setVisibility( View.GONE );

                                    }
                                }
                            }
                        }
                );
            }
        } );

    }


    private void removeCoupon() {

        RemoveCouponService.removeCoupan(
                getActivity(),
                user.getUser_id(),
                progressBar, new APIService.Success<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {

                        Log.e( TAG, "RemoveCoupon--->" + response );

                        if (response != null) {

                            try {

                                JSONObject jsonObject = new JSONObject( response );

                                if (jsonObject.getString( Constant.RemoveCoupon.STATUS ).equals( "S" )) {

                                    JSONObject object = jsonObject.getJSONObject( Constant.RemoveCoupon.DATA );

                                    String marchandise = object.getString( "cart_subtotal" );
                                    tv_marchandise.setText( "$" + Double.parseDouble( marchandise ) );

                                    String cart_total = object.getString( "cart_total" );
                                    tv_totalPrice.setText( "$" + Double.parseDouble( cart_total ) );

                                    String shipping = object.getString( "shipping" );
                                    tv_shippingPrice.setText( "$" + Double.parseDouble( shipping ) );

                                    String tax = object.getString( "tax" );
                                    tv_taxPrice.setText( "$" + Double.parseDouble( tax ) );

                                    tv_discountedPrice.setText( "" );
                                    iv_removeCoupon.setImageResource( R.drawable.next );

                                    Toast.makeText( getActivity(), "Coupon removed successfully!", Toast.LENGTH_LONG ).show();
                                    progressBar.setVisibility( View.GONE );

                                } else {

                                    Toast.makeText( getActivity(), jsonObject.getString( Constant.RemoveCoupon.MESSAGE ), Toast.LENGTH_LONG ).show();
                                    progressBar.setVisibility( View.GONE );
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility( View.GONE );
                            }
                        }
                    }
                }
        );
    }

}



    /*private void loadHeroes() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.checkout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("null")) {

                            Toast.makeText(getActivity(), "No record Found", Toast.LENGTH_SHORT).show();

                        } else {
                            try {

                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                String myStrValue = prefs.getString("PREF_NAME", "");


                                JSONObject obj = new JSONObject(response);

                                JSONObject obj1 = obj.getJSONObject("data");
                                JSONObject obj2 = obj1.getJSONObject("cart_totals");

                                tv_marchandise.setText("$" + obj2.getString("cart_total"));
                                tv_shippingPrice.setText("$" + obj2.getString("shipping"));
                                tv_taxPrice.setText("$" + obj2.getString("tax"));
                                tv_couponName.setText(myStrValue);


                                if (obj.getString("status").equals("S")) {


                                } else if (obj.getString("status").equals("W")) {

                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                hidepDialog();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        hidepDialog();
                    }
                })

        {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                params.put("updated_cart", "[{\"product_id\":590,\"variation_id\":\"0\",qty\":5}]");
                return params;
            }


        };
        VolleySingleton.getInstance(getActivity());
        requestQueue.add(stringRequest);
    }*/



     /* private void onpaySuccess() {

        user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
        params11 = new ArrayList<NameValuePair>();
        params11.add(new BasicNameValuePair("old_token", ""));
        params11.add(new BasicNameValuePair("new_token", newToken));
        params11.add(new BasicNameValuePair("user_id", user.getUser_id()));
        params11.add(new BasicNameValuePair("saved", "0"));
        new paybackground().execute();
    }


    public class paybackground extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... params) {
            String obj;//new JSONArray();
            try {
                obj = getJSONFromUrl(payment, params11);
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            Log.e("Result", "" + result);

            try {
                JSONObject json = new JSONObject(result);
                String status = json.getString("result");
                Log.d("Status:", status);

                if (status.equals("success")) {
                    Dialog alertDialog = new Dialog(getActivity());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.activity_payment_aceept_popup);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();


                } else if (status.equals("failure")) {
                    Dialog alertDialog = new Dialog(getActivity());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.activity_payment_declined_popup);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();

                } else {

                }
            } catch (JSONException e1) {
                e1.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        }

    }

    public String getJSONFromUrl(String url, List<NameValuePair> params) {
        InputStream is = null;
        String json = "";

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                //sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("JSON", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;

    }*/



      /* private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/