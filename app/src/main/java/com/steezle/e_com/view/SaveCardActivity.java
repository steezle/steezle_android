package com.steezle.e_com.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.networking.CommonValidation;

import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.SaveCardService;
import com.steezle.e_com.utils.Constant;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONObject;


public class SaveCardActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = SaveCardActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private ImageView back_click_addcard;
    private RelativeLayout rl_btn_saveCard;
    private RequestQueue requestQueue;
    private User user;
    private String tokens, dis;
    private EditText edt_cardName;
    private CardMultilineWidget card_multiline_widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.act_paymen_settingt );
        requestQueue = Volley.newRequestQueue( this );


        //Initlization
        Intent intent = getIntent();
        dis = intent.getStringExtra( "disount" );
        user = AppSharedPreferences.getSharePreference( this ).getUser();
        setUpView();


        //Register Button Click Event
        back_click_addcard.setOnClickListener( this );
        rl_btn_saveCard.setOnClickListener( this );

    }


    private void setUpView() {

        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        rl_btn_saveCard = (RelativeLayout) findViewById( R.id.rl_btn_saveCard );
        edt_cardName = (EditText) findViewById( R.id.edt_cardName );
        back_click_addcard = (ImageView) findViewById( R.id.back_click_addcard );
        card_multiline_widget = (CardMultilineWidget) findViewById( R.id.card_multiline_widget );

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click_addcard:
                finish();
                break;

            case R.id.rl_btn_saveCard:

                final Stripe stripe = new Stripe( SaveCardActivity.this, "pk_live_ZG4KImX0bCskmlkf2tT5q6q4" );
//                final Stripe stripe = new Stripe( SaveCardActivity.this, "pk_test_uVBDZ0wJcJBrI8LGxY0BdKSH" );

                final Card card = card_multiline_widget.getCard();

                if (CommonValidation.isEdittextEmpty( edt_cardName )) {
                    CommonValidation.showToast( getApplicationContext(), getResources().getString( R.string.cardname ) );
                } else if (card == null) {
                    Toast.makeText( getApplicationContext(), "Invalid Card Data", Toast.LENGTH_LONG ).show();
                }
                else {
                    progressBar.setVisibility( View.VISIBLE );
                    rl_btn_saveCard.setEnabled( false );

                    stripe.createToken(
                            card, new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your
                                    card_multiline_widget.clear();
                                    tokens = token.getId();
                                    saveCard();
                                }

                                public void onError(Exception error) {
                                    // Show localized error message

                                }
                            }
                    );

                }
                break;
        }

    }


    private void saveCard() {

        SaveCardService.saveCard(
                this,
                user.getUser_id(),
                tokens,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {

                        if (response != null) {

                            Log.e( TAG, "SaveCardRes--->" + response );

                            try {

                                JSONObject obj = new JSONObject( response );

                                if (obj.getString( Constant.SaveCard.STATUS ).equals( "S" )) {
                                    progressBar.setVisibility( View.GONE );

                                    final Dialog alertDialog = new Dialog( SaveCardActivity.this );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.view_dialog_addbag );
                                    TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                                    dialogMessage.setText( "Card Saved Successfully" );
                                    alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                    alertDialog.show();
                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    }, 1000 );

                                } else {

                                    progressBar.setVisibility( View.GONE );
                                    final Dialog alertDialog = new Dialog( SaveCardActivity.this );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.view_dialog_addbag );
                                    TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                                    dialogMessage.setText( "Card Saved Successfully" );
                                    alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                    alertDialog.show();
                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    }, 1000 );

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


   /* public void SaveCardAPI() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.savecard,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("null")) {

                            Toast.makeText(SaveCardActivity.this, "Please  try again", Toast.LENGTH_SHORT).show();

                        } else {
                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);
                                String msg = obj.getString("message");
                                String status = obj.getString("status");

                                if (obj.getString("status").equals("S")) {

                                    //if no error in response
                                    if (status.equals("S")) {
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                user = AppSharedPreferences.getSharePreference(SaveCardActivity.this).getUser();
                params.put("user_id", user.getUser_id());
                params.put("token", tokens);

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

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mTvResult.setText(response.getString("one"));
                } catch (JSONException e) {
                    mTvResult.setText("Parse error");
                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTvResult.setText( error.getMessage() );
            }
        };
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/




