package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.SaveCardAdapter;
import com.steezle.e_com.adapter.SaveCardpaymnetAdapter;
import com.steezle.e_com.fragments.CreditFragement;
import com.steezle.e_com.fragments.SavecardFragment;
import com.steezle.e_com.model.SaveCardModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.ApplyCouponService;
import com.steezle.e_com.services.CheckOutService;
import com.steezle.e_com.services.PaymentService;
import com.steezle.e_com.services.RemoveCouponService;
import com.steezle.e_com.utils.Constant;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

import info.hoang8f.android.segmented.SegmentedGroup;

import static com.facebook.FacebookSdk.getApplicationContext;


public class PaymentActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        View.OnClickListener  , SaveCardpaymnetAdapter.OnRemoveCard {
    @Override
    public void onStart() {
        super.onStart();
    }

    private SegmentedGroup segmented_paymentfragment;
    private int position;
    public static TextView tv_favourite;
    private ImageView back_click_clothing;
    //private Stack<Fragment> fragmentStack;
    //Creditcard
    LinearLayout lnr_saved, lnr_credit;
    private ImageView iv_emptyCard;
    //Credit
    private String newToken;
    private EditText edt_email;
    private TextView tv_marchandise, tv_shippingPrice, tv_taxPrice, tv_totalPrice, tv_discountedPrice, cardname;
    private String sdiscount;
    private CardMultilineWidget mCardInputWidget;
    private RelativeLayout rl_btn_payNow;
    private LinearLayout ll_couponCode;
    private User user;
    private Intent intent;
    private Bundle bundle;
    private String cartProductArray;
    private CheckBox cb_saveCard;
    private Dialog applyCouponDialog;
    private ImageView iv_removeCoupon;
    String TAG="PaymentActivity";
    private ProgressBar progressBar;
    private RecyclerView rec_saveCardList;
    private String oldToken;
    private ArrayList<SaveCardModel> saveCardList;
    private SaveCardpaymnetAdapter saveCardpaymnetAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.act_payment );
        intent = getIntent();
        cartProductArray = intent.getStringExtra( "CartProductArray" );

        setUpView();
        user = AppSharedPreferences.getSharePreference( getApplicationContext() ).getUser();
        edt_email.setText( user.getUser_email() );

        checkOut();

        fillSaveCardPaymentAdapter();

        //Register Button Click Event
        segmented_paymentfragment.setOnCheckedChangeListener( this );
        back_click_clothing.setOnClickListener( this );
        rl_btn_payNow.setOnClickListener(this);
        ll_couponCode.setOnClickListener(this);
        iv_removeCoupon.setOnClickListener(this);


//        checkOutProducts();
    }

    private String getSavedOrNot() {

        if (cb_saveCard.isChecked()) {
            return "1";
        } else {
            return "0";
        }
    }


    private void setUpView() {

        segmented_paymentfragment = (SegmentedGroup) findViewById(R.id.segmented_paymentfragment);
        tv_favourite = (TextView) findViewById(R.id.tv_favourite);
        back_click_clothing = (ImageView) findViewById(R.id.back_click_clothing);
        lnr_credit=(LinearLayout)findViewById( R.id.lnr_credit );
        lnr_saved=(LinearLayout)findViewById( R.id.lnr_saved );
        
        //Credit
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        tv_marchandise = (TextView) findViewById( R.id.tv_marchandise );
        tv_shippingPrice = (TextView) findViewById( R.id.tv_shippingPrice );
        edt_email = (EditText) findViewById( R.id.edt_email );
        tv_taxPrice = (TextView) findViewById( R.id.tv_taxPrice );
        tv_totalPrice = (TextView) findViewById( R.id.tv_totalPrice );
        tv_discountedPrice = (TextView) findViewById( R.id.tv_discountedPrice );
        cardname = (TextView) findViewById( R.id.cardname );
        mCardInputWidget = (CardMultilineWidget) findViewById( R.id.card_multiline_widget );
        ll_couponCode = (LinearLayout) findViewById( R.id.ll_couponCode );
        rl_btn_payNow = (RelativeLayout) findViewById( R.id.rl_btn_payNow );
        iv_removeCoupon = (ImageView) findViewById( R.id.iv_removeCoupon );
        cb_saveCard = (CheckBox) findViewById( R.id.cb_saveCard );

        //Saved
        rec_saveCardList = (RecyclerView) findViewById(R.id.rec_saveCardList);
        iv_emptyCard = (ImageView) findViewById(R.id.iv_emptyCard);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click_clothing:
                finish();
                break;
            //Credit
            case R.id.ll_couponCode:
                if (tv_discountedPrice.getText().toString().length() == 0) {
                    applyCouponDialogView();
                }
                break;
            case R.id.rl_btn_payNow:

                Log.e( "position",""+position );
                //Saved
                if (position==1){
                    if (oldToken != null) {
                        makePaymentSaved();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select card!", Toast.LENGTH_LONG).show();
                    }
                }
                //Credit
                else{
//                    final Stripe stripe = new Stripe( getApplicationContext(), "pk_test_uVBDZ0wJcJBrI8LGxY0BdKSH" );
                final Stripe stripe = new Stripe(PaymentActivity.this, "pk_live_ZG4KImX0bCskmlkf2tT5q6q4" );
                    final Card card = mCardInputWidget.getCard();

                    if (card == null) {

                    } else  if (cardname.getText().toString().length() == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder( PaymentActivity.this );
                        builder.setTitle( "Steezle");
                        builder.setMessage("Enter card name!");

                        builder.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } );
                        builder.show();
                    } else {
                        final ProgressDialog progressDialog =new ProgressDialog(PaymentActivity.this, R.style.MyTheme);
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        progressDialog.show();
//                        progressBar.setVisibility( View.VISIBLE );
                        stripe.createToken(
                                card, new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your
                                        newToken = token.getId();
                                        progressDialog.dismiss();
//                                        progressBar.setVisibility( View.GONE );
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
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_creditcard:
                position = 0;
                  lnr_credit.setVisibility(View.VISIBLE);
                lnr_saved.setVisibility(View.GONE);
                break;

            case R.id.radio_savedcard:
                position = 1;
                  lnr_credit.setVisibility(View.GONE);
                lnr_saved.setVisibility(View.VISIBLE);
                break;


            default:
                position = 0;
                break;
        }

    }

    private void checkOutProducts() {

        //progressBar.setVisibility(View.VISIBLE);
        CheckOutService.checkOut(
                PaymentActivity.this,
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

                            Toast.makeText( getApplicationContext(), "No record Found", Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
        );
    }
    @Override
    public void onRemoveCard(SaveCardModel saveCardModel, int pos) {

        removeSaveCard(saveCardModel, pos);

    }

    private void removeSaveCard(final SaveCardModel saveCardModel, final int pos) {

        oldToken = saveCardModel.getToken();
        Log.d("oldToken", oldToken);
    }
    private void makePaymentSaved() {

        //progressBar.setVisibility(View.VISIBLE);
        PaymentService.makePayment(
                PaymentActivity.this,
                user.getUser_id(),
                "",
                oldToken,
                "0",
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "PaymentRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject json = new JSONObject(response);
                                String status = json.getString("result");
                                Log.d("Status:", status);

                                if (status.equals("success")) {
                                    progressBar.setVisibility(View.GONE);
                                    final Dialog alertDialog = new Dialog(PaymentActivity.this);
                                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    alertDialog.setContentView(R.layout.activity_payment_aceept_popup);
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    alertDialog.show();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            Intent ii = new Intent(PaymentActivity.this, TabActivity.class);
                                            ii.putExtra("From", "order");
                                            startActivity(ii);
                                        }
                                    }, 1000);

                                } else if (status.equals("failure")) {
                                    progressBar.setVisibility(View.GONE);
                                    final Dialog alertDialog = new Dialog(PaymentActivity.this);
                                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    alertDialog.setContentView(R.layout.activity_payment_declined_popup);
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    alertDialog.show();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                        }
                                    }, 1000);
                                } else {
                                    Toast.makeText(PaymentActivity.this, json.getString(Constant.MakePayment.MESSAGE), Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void makePayment() {

        PaymentService.makePayment(
                PaymentActivity.this,
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
                                    final Dialog alertDialog = new Dialog(PaymentActivity.this );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.activity_payment_aceept_popup );
                                    alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                    alertDialog.show();

                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            Intent ii = new Intent(PaymentActivity.this, TabActivity.class );
                                            ii.putExtra( "From", "order" );
                                            startActivity( ii );
                                        }
                                    }, 1000 );

                                } else if (status.equals( "failure" )) {
                                    progressBar.setVisibility( View.GONE );
                                    final Dialog alertDialog = new Dialog(PaymentActivity.this );
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

                                    Toast.makeText(PaymentActivity.this, json.getString( Constant.MakePayment.MESSAGE ), Toast.LENGTH_LONG ).show();

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
    private void applyCouponDialogView() {

        applyCouponDialog = new Dialog(PaymentActivity.this );
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
                       PaymentActivity.this,
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
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this );
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
               PaymentActivity.this,
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

                                    Toast.makeText(PaymentActivity.this, "Coupon removed successfully!", Toast.LENGTH_LONG ).show();
                                    progressBar.setVisibility( View.GONE );

                                } else {

                                    Toast.makeText(PaymentActivity.this, jsonObject.getString( Constant.RemoveCoupon.MESSAGE ), Toast.LENGTH_LONG ).show();
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
    private void fillSaveCardPaymentAdapter() {

        saveCardList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(PaymentActivity.this, 1, GridLayoutManager.VERTICAL, false);
        rec_saveCardList.setLayoutManager(manager);
        saveCardpaymnetAdapter = new SaveCardpaymnetAdapter(saveCardList,PaymentActivity.this, PaymentActivity.this);
        rec_saveCardList.setAdapter(saveCardpaymnetAdapter);

    }
    private void checkOut() {

        if (saveCardList != null) {
            saveCardList.clear();
        }

        //progressBar.setVisibility(View.VISIBLE);
        CheckOutService.checkOut(
               PaymentActivity.this,
                user.getUser_id(),
                cartProductArray,
                progressBar,
                new APIService.Success<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "ResCheckout--->" + response);

                        if (response != null) {

                            try {

                                JSONObject jsonObject = new JSONObject(response);

                                tv_favourite.setText(jsonObject.getString(Constant.CheckOut.BAG_COUNT));

                                if (jsonObject.getString(Constant.CheckOut.STATUS).equals("S")) {

                                    JSONObject dataObj = jsonObject.getJSONObject(Constant.CheckOut.DATA);
                                    JSONObject cartTotals = dataObj.getJSONObject(Constant.CheckOut.CART_TOTALS);

                                    String marchandise = cartTotals.getString("cart_subtotal");
                                    tv_marchandise.setText("$" + String.format("%.2f", Double.parseDouble(marchandise)));

                                    String shipping = cartTotals.getString("shipping");
                                    tv_shippingPrice.setText("$" + String.format("%.2f", Double.parseDouble(shipping)));

                                    String tax = cartTotals.getString("tax");
                                    tv_taxPrice.setText("$" + String.format("%.2f", Double.parseDouble(tax)));

                                    String cartTotal = cartTotals.getString("cart_total");
                                    tv_totalPrice.setText("$" + String.format("%.2f", Double.parseDouble(cartTotal)));
                                    tv_discountedPrice.setText("");

                                    JSONArray jarrary = dataObj.getJSONArray("saved_card_data");

                                    for (int i = 0; i < jarrary.length(); i++) {

                                        JSONObject object = jarrary.getJSONObject(i);

                                        SaveCardModel saveCardModel = new SaveCardModel(
                                                object.getString("token"),
                                                object.getString("card_string"));

                                        saveCardList.add(saveCardModel);
                                    }

                                    saveCardpaymnetAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    if (saveCardList.size() > 0) {
                                        iv_emptyCard.setVisibility(View.GONE);
                                    } else {
                                        iv_emptyCard.setVisibility(View.VISIBLE);
                                    }

                                } else if (jsonObject.getString("status").equals("W")) {

                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                } else {

                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else if (response.equals("null")) {

                            Toast.makeText(PaymentActivity.this, "No record Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }


        );
    }

}