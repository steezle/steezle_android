package com.steezle.e_com.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AlignmentSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.steezle.e_com.adapter.SliderAdapter;
import com.steezle.e_com.model.SizeItem;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.networking.DialogUtility;
import com.steezle.e_com.networking.VolleySingleton;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.AddToCartService;
import com.steezle.e_com.services.ProductDetailsService;
import com.steezle.e_com.utils.ColorView;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.utils.ExtendedViewPager;
import com.steezle.e_com.utils.SizeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.blogc.android.views.ExpandableTextView;
import me.biubiubiu.justifytext.library.JustifyTextView;

public class ProductDetailActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String TAG = ProductDetailActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView tv_iv_favourite;
    private LinearLayout ll_btn_addToBag;
    private ImageView iv_productDetail_back;
    private ExtendedViewPager vp_slider;
    private LinearLayout ll_dots;
    private SliderAdapter sliderPagerAdapter;
    private ArrayList<String> slider_image_list;
    private TextView[] dots;
    private User user;
    private TextView tv_brandName, tv_price;
    private WebView exv_description;
    private TextView tv_showmore;
    private RequestQueue requestQueue;
    private String varioutiontype;
    private TextView tv_productName;
    private String productIdTinder;
    private List<String> dynamicAttributes = new ArrayList<>();
    private List<String> dynamicTitle = new ArrayList<>();
    private List<String> dynamicAttributesALL = new ArrayList<>();
    private Map<String, List<SizeItem>> dynamicSubList = new HashMap<>();
    private Intent intent;
    private CardView card_size, card_color;
    private RelativeLayout ll_description;
    SpannableStringBuilder mSSBuilder;
    String mText = "Yung2";
    String is_in_whishlist;
    RecyclerView recyler_dynamic, recycler_size, recycler_color;
    List<String> list_send = new ArrayList<>();
    List<SizeItem> sizeItems = new ArrayList<>();
    List<SizeItem> colorItems = new ArrayList<>();
    String sizeSelected = "";
    String colorSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.act_product_detail );
        requestQueue = Volley.newRequestQueue( ProductDetailActivity.this );
        //sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        intent = getIntent();

        //getcartModel = (GetcartModel) intent.getSerializableExtra(Constant.GETFVTMODEL);
        productIdTinder = intent.getStringExtra( Constant.ProductDetail.PRODUCT_ID );

        //Initlization
        setUpView();
        slider_image_list = new ArrayList<>();
        user = AppSharedPreferences.getSharePreference( this ).getUser();
        try {
            if (user.getUser_id().length() == 0) {
                getProductDetails( "" );
            } else
                getProductDetails( user.getUser_id() );

        } catch (Exception e) {
            e.printStackTrace();
            getProductDetails( "" );
        }

        //Register Button Click Event
        iv_productDetail_back.setOnClickListener( this );
        tv_iv_favourite.setOnClickListener( this );
        ll_btn_addToBag.setOnClickListener( this );
//        tv_showmore.setOnClickListener( this );

        vp_slider.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots( position );
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        } );

        addBottomDots( 0 );


    }

    private void setUpView() {

        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        ll_btn_addToBag = (LinearLayout) findViewById( R.id.ll_btn_addToBag );
        iv_productDetail_back = (ImageView) findViewById( R.id.iv_productDetail_back );
        tv_iv_favourite = (TextView) findViewById( R.id.tv_iv_favourite );
        tv_price = (TextView) findViewById( R.id.tv_price );
        tv_productName = (TextView) findViewById( R.id.tv_productName );
        tv_brandName = (TextView) findViewById( R.id.tv_brandName );
        vp_slider = (ExtendedViewPager) findViewById( R.id.vp_slider );

        ll_dots = (LinearLayout) findViewById( R.id.ll_dots );
        exv_description = (WebView) findViewById( R.id.exv_description );
        tv_showmore = (TextView) findViewById( R.id.tv_showmore );
        card_size = (CardView) findViewById( R.id.card_size );
        card_color = (CardView) findViewById( R.id.card_color );
        ll_description = (RelativeLayout) findViewById( R.id.ll_description );
        recyler_dynamic = (RecyclerView) findViewById( R.id.recyler_dynamic );
        recycler_color = (RecyclerView) findViewById( R.id.recycler_color );
        recycler_size = (RecyclerView) findViewById( R.id.recycler_size );
        exv_description.setBackgroundColor( getResources().getColor( R.color.transparent ) );
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_productDetail_back:
                finish();
                break;

            case R.id.tv_iv_favourite:

                try {
                    if (user.getUser_id().equalsIgnoreCase( "" )) {
                        openDialog( "add products to My Steez" );
                    } else {
                        if (is_in_whishlist.equalsIgnoreCase( "not_in_whishlist" ))
                            addfvtlist( productIdTinder );

                        else {
                            final Dialog alertDialog = new Dialog( ProductDetailActivity.this, R.style.AppTheme_Trans );
                            alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                            alertDialog.setCancelable( false );
                            alertDialog.setContentView( R.layout.view_dialog_addbag );
                            TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                            dialogMessage.setText( "Already Added to Steeze" );
//                            alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                            alertDialog.show();
                            new Handler().postDelayed( new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        alertDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 2000 );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    openDialog( "add products to My Steez" );
                }

                break;

            case R.id.ll_btn_addToBag:
                try {
                    if (user.getUser_id().equalsIgnoreCase( "" )) {
                        openDialog( "add products to Bag" );
                    } else {

                        try {
                            if (varioutiontype.equalsIgnoreCase( "simple" )) {
                                String temp = "[{\"product_id\":" + productIdTinder + "}]";
                                addToCart( temp, user.getUser_id() );

                            } else {
                                TotalData();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    openDialog( "add products to Bag" );
                }
                break;
        }
    }


    private void addBottomDots(int currentPage) {

        dots = new TextView[slider_image_list.size()];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView( this );
            dots[i].setCompoundDrawablePadding( 7 );
            dots[i].setTextSize( 35 );
            dots[i].setCompoundDrawablesWithIntrinsicBounds( R.drawable.roundblack, 0, 0, 0 );
            ll_dots.addView( dots[i] );
        }

        if (dots.length > 0)
            dots[currentPage].setCompoundDrawablesWithIntrinsicBounds( R.drawable.round, 0, 0, 0 );
    }


    private void getProductDetails(String userId) {
        //progressBar.setVisibility(View.VISIBLE);
        ProductDetailsService.getProductDetails(
                this,
                userId,
                productIdTinder,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e( TAG, "ProductDetailsResponse--->" + response );
                        if (response != null) {
                            try {
                                JSONObject obj = new JSONObject( response );
                                if (obj.get( Constant.ProductDetail.STATUS ).equals( "S" )) {
                                    JSONObject dataObject = obj.getJSONObject( Constant.ProductDetail.DATA );
                                    JSONArray imagesArray = dataObject.getJSONArray( Constant.ProductDetail.IMAGES );

                                    for (int i = 0; i < imagesArray.length(); i++) {
                                        slider_image_list.add( imagesArray.get( i ).toString() );
                                    }

                                    if (slider_image_list.size() == 0)
                                        slider_image_list.add( dataObject.getString( "main_image" ) );

                                    tv_productName.setText( dataObject.getString( Constant.ProductDetail.TITLE ) );

                                    varioutiontype = dataObject.getString( "type" );
                                    is_in_whishlist = dataObject.getString( "is_in_whishlist" );
                                    Log.e( "is_in_whishlist", "" + is_in_whishlist );

                                    if (is_in_whishlist.equalsIgnoreCase( "in_whishlist" ))
                                        tv_iv_favourite.setBackgroundResource( R.drawable.my_steez_hover );

                                    //For Young2 BRAND.....................
                                    if (dataObject.getString( Constant.ProductDetail.BRANDS ).equalsIgnoreCase( mText )) {
                                        // Initialize a new SpannableStringBuilder instance
                                        mSSBuilder = new SpannableStringBuilder( mText );
                                        // Initialize a new SuperscriptSpan instance
                                        SuperscriptSpan superscriptSpan = new SuperscriptSpan();

                                        // Apply the SuperscriptSpan
                                        mSSBuilder.setSpan(
                                                superscriptSpan,
                                                mText.indexOf( "2" ),
                                                mText.indexOf( "2" ) + String.valueOf( "2" ).length(),
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                        );

                                        showSmallSizeText( "2" );
                                        tv_brandName.setText( mSSBuilder );
                                    } else
                                        tv_brandName.setText( dataObject.getString( Constant.ProductDetail.BRANDS ) );
                                    //////////////////////////////////////////////////////////

                                    tv_price.setText( dataObject.getString( Constant.ProductDetail.PRICE ) );
                                    if (dataObject.getString( Constant.ProductDetail.DESCRIPTION ).equals( "" )) {
                                        ll_description.setVisibility( View.GONE );
                                    } else {
                                        ll_description.setVisibility( View.VISIBLE );

                                        final String desc = dataObject.getString( Constant.ProductDetail.DESCRIPTION );
                                        Log.e( TAG + " Description length: ", "" +
                                                dataObject.getString( Constant.ProductDetail.DESCRIPTION ).toString().length() );
                                        if (dataObject.getString( Constant.ProductDetail.DESCRIPTION ).toString().length() > 100) {
                                            String data = "<html><head></head><body style='text-align:justify;' >" +
                                                    desc.substring( 0, 99 ) + "..." +
                                                    "<a href=\"hrupin://second_activity\">Read More</a></body></html>";
                                            exv_description.loadData( data, "text/html", "utf-8" );
                                            exv_description.setWebViewClient( new WebViewClient() {
                                                // Override URL
                                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                                    if (url.equalsIgnoreCase( "hrupin://second_activity" )) {
                                                        Log.e( "Clicked", "Read more" );

                                                        String data = "<html><head></head><body style='text-align:justify;' >" +
                                                                desc;
                                                        exv_description.loadData( data, "text/html", "utf-8" );
                                                    }

                                                    Log.e( "URL", "URL " + url );
                                                    return true;
                                                }
                                            } );

                                        } else {
                                            tv_showmore.setText( "" );
                                            exv_description.loadData( (dataObject.getString( Constant.ProductDetail.DESCRIPTION )), "text/html", "utf-8" );
                                        }
                                    }

                                    try {
                                        //Add your code here
                                        JSONObject ob_attr = dataObject.getJSONObject( "attributes" );
                                        JSONArray array_variations = ob_attr.getJSONArray( "variations" );
                                        for (int i = 0; i < array_variations.length(); i++) {
                                            JSONObject object = array_variations.getJSONObject( i );

                                            if (!object.getString( "value" ).equalsIgnoreCase( "pa_size" )
                                                    && !object.getString( "value" ).equalsIgnoreCase( "pa_color" )) {
                                                dynamicAttributes.add( object.getString( "value" ) );
                                                dynamicTitle.add( object.getString( "title" ) );
                                            }

                                            dynamicAttributesALL.add( object.getString( "value" ) );
                                        }


                                        JSONObject object = ob_attr.getJSONObject( "variation_attributes" );
                                        for (int i = 0; i < dynamicAttributesALL.size(); i++) {
                                            List<SizeItem> dynamicItems = new ArrayList<>();

//                                        Log.e( "Variations",""+ dynamicAttributes.get( i )  );
                                            JSONArray array = object.getJSONArray( "" + dynamicAttributesALL.get( i ) );
                                            for (int j = 0; j < array.length(); j++) {
                                                SizeItem item = new SizeItem();
                                                JSONObject ob = array.getJSONObject( j );
                                                item.setTitle_name( ob.getString( "title" ) );
                                                //FOr size display
                                                try {
                                                    item.setTitle_id( ob.getString( "display_title" ) );
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                //For color display
                                                try {
                                                    item.setColor_code( ob.getString( "color_hex" ) );

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                Log.e( "dynamicAttributes v:", "" + dynamicAttributesALL.get( i ) );
                                                if (dynamicAttributesALL.get( i ).equalsIgnoreCase( "pa_size" ))
                                                    sizeItems.add( item );

                                                else if (dynamicAttributesALL.get( i ).equalsIgnoreCase( "pa_color" ))
                                                    colorItems.add( item );

                                                else
                                                    dynamicItems.add( item );
                                            }
                                            if (!dynamicAttributesALL.get( i ).equalsIgnoreCase( "pa_size" )
                                                    && !dynamicAttributesALL.get( i ).equalsIgnoreCase( "pa_color" ))
                                                dynamicSubList.put( dynamicAttributesALL.get( i ), dynamicItems );
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                try {
                                    if (sizeItems.size() > 0) {
                                        card_size.setVisibility( View.VISIBLE );
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext(), LinearLayoutManager.HORIZONTAL, false );
                                        recycler_size.setLayoutManager( linearLayoutManager );
                                        // call the constructor of CustomAdapter to send the reference and data to Adapter
                                        SizeDynamicAdapter customAdapter = new SizeDynamicAdapter( sizeItems, ProductDetailActivity.this );
                                        recycler_size.setAdapter( customAdapter );
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (colorItems.size() > 0) {
                                        card_color.setVisibility( View.VISIBLE );
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext(), LinearLayoutManager.HORIZONTAL, false );
                                        recycler_color.setLayoutManager( linearLayoutManager );
                                        // call the constructor of CustomAdapter to send the reference and data to Adapter
                                        ColorDynamicAdapter customAdapter = new ColorDynamicAdapter( colorItems, ProductDetailActivity.this );
                                        recycler_color.setAdapter( customAdapter );
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (dynamicAttributes.size() > 0) {
                                        recyler_dynamic.setVisibility( View.VISIBLE );
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
                                        recyler_dynamic.setLayoutManager( linearLayoutManager );
                                        // call the constructor of CustomAdapter to send the reference and data to Adapter
                                        DynamicAdapter customAdapter = new DynamicAdapter( dynamicAttributes, dynamicSubList, ProductDetailActivity.this );
                                        recyler_dynamic.setAdapter( customAdapter );
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                sliderPagerAdapter = new SliderAdapter( ProductDetailActivity.this, slider_image_list );
                                vp_slider.setAdapter( sliderPagerAdapter );
//                                vp_slider.startCircular();
                                sliderPagerAdapter.notifyDataSetChanged();
                                addBottomDots( 0 );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    protected void showSmallSizeText(String textToSmall) {
        // Initialize a new RelativeSizeSpan instance
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan( .5f );

        // Apply the RelativeSizeSpan to display small text
        mSSBuilder.setSpan(
                relativeSizeSpan,
                mText.indexOf( textToSmall ),
                mText.indexOf( textToSmall ) + String.valueOf( textToSmall ).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

    }

    private void variation_check(final String productId, final String Variations) {
        progressBar.setVisibility( View.VISIBLE );
        StringRequest stringRequest = new StringRequest( Request.Method.POST,
                APIs.variation_check,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e( "response variation_check", "" + response );
                            JSONObject object = new JSONObject( response );
                            String status = object.getString( "status" );
                            if (status.equalsIgnoreCase( "S" )) {
                                JSONObject object1 = object.getJSONObject( "data" );
                                String variation_id = object1.getString( "variation_id" );
                                Log.e( "variation_id", "" + variation_id );
                                progressBar.setVisibility( View.GONE );
                                String respon = AddCartParam( productId, variation_id, Variations );
                                addToCart( respon, user.getUser_id() );
                            } else if (status.equalsIgnoreCase( "F" )) {
                                Log.e( "Inside", "F" );
                                DialogUtility.
                                        alertErrorMessage( ProductDetailActivity.this,
                                                "" + object.getString( "message" ) );
                           /*     AlertDialog.Builder builder=new AlertDialog.Builder( ProductDetailActivity.this );
                                builder.setTitle( "Steezle" );
                                builder.setCancelable( false );
                                builder.setMessage( ""+object.getString( "message" ) );
                                builder.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                } ).show();*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText( ProductDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
                        progressBar.setVisibility( View.GONE );
                    }
                } ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put( "user_id", user.getUser_id() );
                    params.put( "product_id", "" + productId );
                    params.put( "variations", "" + Variations );

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return params;
            }

        };
        VolleySingleton.getInstance( ProductDetailActivity.this );
        requestQueue.add( stringRequest );

    }

    private void addfvtlist(final String id) {

        progressBar.setVisibility( View.VISIBLE );
        StringRequest stringRequest = new StringRequest( Request.Method.POST,
                APIs.add_fvt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e( TAG, "" + response );
                            JSONObject obj = new JSONObject( response );

                            AppSharedPreferences.getSharePreference( ProductDetailActivity.this ).setCartCount( obj.getString( "bag_count" ) );
                            AppSharedPreferences.getSharePreference( ProductDetailActivity.this ).setTotalSteez( obj.getString( "total_steez" ) );

                            if (obj.get( "status" ).equals( "S" )) {
                                progressBar.setVisibility( View.GONE );

                                try {
                                    final Dialog alertDialog = new Dialog( ProductDetailActivity.this, R.style.AppTheme_Trans );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.view_dialog_addbag );
                                    alertDialog.setCancelable( false );
                                    TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                                    dialogMessage.setText( "Added to Steeze" );
//                                    alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                    alertDialog.show();
                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                alertDialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            finish();
                                        }
                                    }, 1000 );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            } else {
                                progressBar.setVisibility( View.GONE );

                                try {
                                    final Dialog alertDialog = new Dialog( ProductDetailActivity.this, R.style.AppTheme_Trans );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.view_dialog_addbag );
                                    alertDialog.setCancelable( false );
                                    TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                                    dialogMessage.setText( "Added to Steeze" );
//                                    alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                    alertDialog.show();
                                    new Handler().postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                alertDialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            finish();
                                        }
                                    }, 1000 );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility( View.GONE );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText( ProductDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
                        progressBar.setVisibility( View.GONE );
                    }
                } ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put( "user_id", user.getUser_id() );
                params.put( "product_id", id );

                return params;
            }

        };
        VolleySingleton.getInstance( ProductDetailActivity.this );
        requestQueue.add( stringRequest );

    }

    private void addToCart(final String variations, String UserId) {
        //progressBar.setVisibility(View.VISIBLE);
        AddToCartService.addToCart(
                this,
                UserId,
                variations,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e( TAG, "AddToCartResponse--->" + response );

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject( response );

                                String data = obj.getString( Constant.AddToCart.DATA );

                                AppSharedPreferences.getSharePreference( ProductDetailActivity.this ).setCartCount( obj.getString( "bag_count" ) );
                                AppSharedPreferences.getSharePreference( ProductDetailActivity.this ).setTotalSteez( obj.getString( "total_steez" ) );

                                Log.e( "cart", "" + AppSharedPreferences.getSharePreference( getApplicationContext() ).getCartCount() );
                                Log.e( "Steeze", "" + AppSharedPreferences.getSharePreference( getApplicationContext() ).getTotalSteez() );

                                if (obj.getString( Constant.AddToCart.STATUS ).equals( "S" )) {
                                    try {
                                        final Dialog alertDialog = new Dialog( ProductDetailActivity.this, R.style.AppTheme_Trans );
//                                        alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                        alertDialog.setContentView( R.layout.view_dialog_addbag );
                                        alertDialog.setCancelable( false );
                                        alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                        alertDialog.show();

                                        new Handler().postDelayed( new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    alertDialog.dismiss();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                finish();
                                            }
                                        }, 1000 );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
//                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.AddToCart.MESSAGE), Toast.LENGTH_LONG).show();


                                } else {
                                    try {
                                        final Dialog alertDialog = new Dialog( ProductDetailActivity.this, R.style.AppTheme_Trans );
                                        alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                        alertDialog.setContentView( R.layout.view_dialog_addbag );
                                        alertDialog.setCancelable( false );

//                                        alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                        alertDialog.show();

                                        new Handler().postDelayed( new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    alertDialog.dismiss();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                finish();
                                            }
                                        }, 1000 );
//                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.AddToCart.MESSAGE), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility( View.GONE );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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

    public void openDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder( ProductDetailActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert );
        builder.setTitle( "Steezle" );
        builder.setMessage( "Sign in or Sign Up to " + text );
        builder.setCancelable( false );
        builder.setPositiveButton( "Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Call Login screen
                Intent i = new Intent( getApplicationContext(), LoginActivity.class );
                i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity( i );

            }
        } );
        builder.setNegativeButton( "Not Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        } );

        builder.show();

    }

    public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.HeroViewHolder> {

        Map<String, List<SizeItem>> brandArrayList;
        List<String> dynamicAttr;
        private Context context;
        ArrayList<String> list_temp;

        public DynamicAdapter(List<String> dynamicAttr, Map<String, List<SizeItem>> brandArrayList, Context context) {
            this.brandArrayList = brandArrayList;
            this.context = context;
            this.dynamicAttr = dynamicAttr;
            list_temp = new ArrayList<>();
        }

        @Override
        public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.raw_dynamic, parent, false );
            return new HeroViewHolder( v );
        }

        @Override
        public void onBindViewHolder(final HeroViewHolder holder, final int position) {

            holder.tv_title.setText( dynamicTitle.get( position ) );

            list_send.add( brandArrayList.get( "" + dynamicAttr.get( position ) ).get( 0 ).getTitle_name() );
            holder.tv_detail.setText( brandArrayList.get( "" + dynamicAttr.get( position ) ).get( 0 ).getTitle_name() );
            holder.tv_detail.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list_temp.clear();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>( context, android.R.layout.simple_list_item_1 );
                    for (int i = 0; i < brandArrayList.get( "" + dynamicAttr.get( position ) ).size(); i++) {
                        Log.e( "brandlist", "" + brandArrayList.get( "" + dynamicAttr.get( position ) ).get( i ).getTitle_name() );
                        adapter.add( brandArrayList.get( "" + dynamicAttr.get( position ) ).get( i ).getTitle_name() );
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder( context );
                    builder.setTitle( "Select " + dynamicTitle.get( position ) );

                    builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            list_send.set( position, brandArrayList.get( "" + dynamicAttr.get( position ) ).get( item ).getTitle_name() );

                            holder.tv_detail.setText( brandArrayList.get( "" + dynamicAttr.get( position ) ).get( item ).getTitle_name() );
                            dialog.dismiss();

                        }
                    } );

                    builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    } );
                    builder.show();
                }
            } );
        }

        @Override
        public int getItemCount() {
            return dynamicAttr.size();
        }

        public class HeroViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_title;
            TextView tv_detail;

            HeroViewHolder(View itemView) {
                super( itemView );
                tv_title = (TextView) itemView.findViewById( R.id.tv_title );
                tv_detail = (TextView) itemView.findViewById( R.id.tv_detail );

            }

        }
    }

    public class SizeDynamicAdapter extends RecyclerView.Adapter<SizeDynamicAdapter.HeroViewHolder> {

        List<SizeItem> dynamicAttr;

        ArrayList<String> list_temp;
        int row_index = -1;

        public SizeDynamicAdapter(List<SizeItem> dynamicAttr, Context context) {
            this.dynamicAttr = dynamicAttr;
            list_temp = new ArrayList<>();
        }

        @Override
        public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.size_view, parent, false );
            return new HeroViewHolder( v );
        }

        @Override
        public void onBindViewHolder(final HeroViewHolder holder, final int position) {

            holder.tvSize.setText( dynamicAttr.get( position ).getTitle_id() );
            Drawable drawable2 = getResources().getDrawable( R.drawable.square_border_black );
            holder.tvSize.setBackground( drawable2 );
            holder.tvSize.setTextColor( Color.BLACK );

            holder.tvSize.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    row_index = position;
                    notifyDataSetChanged();

                }
            } );

            if (row_index==-1 && position==0){
                sizeSelected = dynamicAttr.get( position ).getTitle_name();
                Drawable drawable1 = getResources().getDrawable( R.drawable.square_border_black );
                holder.tvSize.setBackground( drawable1 );
                holder.tvSize.setTextColor( Color.BLACK );

            }
          else  if (row_index == position) {
                sizeSelected = dynamicAttr.get( position ).getTitle_name();
                Drawable drawable1 = getResources().getDrawable( R.drawable.square_border_black );
                holder.tvSize.setBackground( drawable1 );
                holder.tvSize.setTextColor( Color.BLACK );

            } else {
                Drawable drawable = getResources().getDrawable( R.drawable.square_border );
                holder.tvSize.setBackground( drawable );
                holder.tvSize.setTextColor( Color.GRAY );
            }
        }

        @Override
        public int getItemCount() {
            return dynamicAttr.size();
        }

        public class HeroViewHolder extends RecyclerView.ViewHolder {

            private TextView tvSize;

            HeroViewHolder(View itemView) {
                super( itemView );
                tvSize = (TextView) itemView.findViewById( R.id.tvSize );
            }
        }
    }

    public class ColorDynamicAdapter extends RecyclerView.Adapter<ColorDynamicAdapter.HeroViewHolder> {

        List<SizeItem> dynamicAttr;
        private Context context;
        ArrayList<String> list_temp;
        int row_index = -1;

        public ColorDynamicAdapter(List<SizeItem> dynamicAttr, Context context) {
            this.context = context;
            this.dynamicAttr = dynamicAttr;
            list_temp = new ArrayList<>();
        }

        @Override
        public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.color_view, parent, false );
            return new HeroViewHolder( v );
        }

        @Override
        public void onBindViewHolder(final HeroViewHolder holder, final int position) {

            Log.e( "color code", "" + dynamicAttr.get( position ).getColor_code() );
            holder.tvColor.setBackgroundColor( Color.parseColor( dynamicAttr.get( position ).getColor_code() ) );

            holder.tvColor.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    row_index = position;
                    notifyDataSetChanged();

                }
            } );

            if (row_index==-1 && position==0){
                colorSelected = dynamicAttr.get( position ).getTitle_name();
                holder.iv_colorMark.setVisibility( View.VISIBLE );
                holder.iv_backGround.setBackgroundResource( R.drawable.square_border_black_tansparent );
            }

           else if (row_index == position) {
                colorSelected = dynamicAttr.get( position ).getTitle_name();
                holder.iv_colorMark.setVisibility( View.VISIBLE );
                holder.iv_backGround.setBackgroundResource( R.drawable.square_border_black_tansparent );

            } else {
                holder.iv_colorMark.setVisibility( View.GONE );
                holder.iv_backGround.setBackgroundResource( R.drawable.square_border_tansparent );
            }
        }

        @Override
        public int getItemCount() {
            return dynamicAttr.size();
        }

        public class HeroViewHolder extends RecyclerView.ViewHolder {

            private TextView tvColor;
            ImageView iv_colorMark, iv_backGround;

            HeroViewHolder(View itemView) {
                super( itemView );
                tvColor = (TextView) itemView.findViewById( R.id.tvColor );
                iv_colorMark = (ImageView) itemView.findViewById( R.id.iv_colorMark );
                iv_backGround = (ImageView) itemView.findViewById( R.id.iv_backGround );
            }
        }
    }

    public void TotalData() {

        String temp = "";
        //For Color+size+dynamic(If>0)
        if (colorItems.size() > 0 && sizeItems.size() > 0) {
            //For Size
            if (sizeSelected.length() > 0) {
                dynamicAttributes.add( "pa_size" );
                list_send.add( sizeSelected );

                //For color
                if (colorSelected.length() > 0) {
                    dynamicAttributes.add( "pa_color" );
                    list_send.add( colorSelected );

                    for (int i = 0; i < dynamicAttributes.size(); i++) {
                        if (i == 0) {
                            if (dynamicAttributes.size() == 1) {
                                temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                            } else
                                temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                        } else if (i == (dynamicAttributes.size() - 1)) {
                            temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                        } else {
                            temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                        }
                    }
                    Log.e( "Send attributes S+C", "" + temp );

                    variation_check( productIdTinder, temp );

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder( ProductDetailActivity.this );
                    builder.setTitle( "Steezle" );
                    builder.setMessage( "Select Atleast One Color" );
                    builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    } ).show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder( ProductDetailActivity.this );
                builder.setTitle( "Steezle" );
                builder.setMessage( "Select Atleast One Size" );
                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                } ).show();
            }
        }
        //For only size + dynamic(if>0)
        else if (sizeItems.size() > 0) {
            if (sizeSelected.length() > 0) {
                dynamicAttributes.add( "pa_size" );
                list_send.add( sizeSelected );

                for (int i = 0; i < dynamicAttributes.size(); i++) {
                    if (i == 0) {
                        if (dynamicAttributes.size() == 1) {
                            temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                        } else
                            temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                    } else if (i == (dynamicAttributes.size() - 1)) {
                        temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                    } else {
                        temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                    }
                }
                Log.e( "Send attributes size", "" + temp );

                variation_check( productIdTinder, temp );

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder( ProductDetailActivity.this );
                builder.setTitle( "Steezle" );
                builder.setMessage( "Select Atleast One Size" );
                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                } ).show();
            }
        }
        //For only color +dynamic(if>0 )
        else if (colorItems.size() > 0) {
            if (colorSelected.length() > 0) {
                dynamicAttributes.add( "pa_color" );
                list_send.add( colorSelected );

                for (int i = 0; i < dynamicAttributes.size(); i++) {
                    if (i == 0) {
                        if (dynamicAttributes.size() == 1) {
                            temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                        } else
                            temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                    } else if (i == (dynamicAttributes.size() - 1)) {
                        temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                    } else {
                        temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                    }
                }
                Log.e( "Send attributes color", "" + temp );

                variation_check( productIdTinder, temp );

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder( ProductDetailActivity.this );
                builder.setTitle( "Steezle" );
                builder.setMessage( "Select Atleast One Color" );
                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                } ).show();
            }
        } else {
            for (int i = 0; i < dynamicAttributes.size(); i++) {
                if (i == 0) {
                    if (dynamicAttributes.size() == 1) {
                        temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                    } else
                        temp = "{" + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                } else if (i == (dynamicAttributes.size() - 1)) {
                    temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + "}";
                } else {
                    temp = temp + "\"" + dynamicAttributes.get( i ) + "\"" + ":" + "\"" + list_send.get( i ) + "\"" + ",";
                }
            }
            Log.e( "Send attributes dynamic", "" + temp );

            variation_check( productIdTinder, temp );

        }
    }

    public String AddCartParam(String ProductId, String VariationID, String Variations) {


        String temp = "[{\"product_id\"" + ":" + ProductId + ",\"variation_id\"" + ":" + VariationID
                + ",\"variations\"" + ":" + Variations + "}]";

        Log.e( "Addtocart parameter", "" + temp );
        return temp;
    }


}


