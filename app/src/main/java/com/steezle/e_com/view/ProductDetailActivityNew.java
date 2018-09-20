package com.steezle.e_com.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.steezle.e_com.adapter.SliderAdapter;
import com.steezle.e_com.model.SizeItem;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProductDetailActivityNew extends AppCompatActivity implements
        SizeView.SizeClickListener,
        ColorView.ColorClickListener, View.OnClickListener {

    public static final String TAG = ProductDetailActivityNew.class.getSimpleName();
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
    private LinearLayout ll_size, ll_color;
    private String varioutiontype;
    private TextView tv_productName;
    private String productIdTinder, productId, variationId, paColor, paSize, QTY;
    private Map<String, ArrayList<SizeItem>> paSizeAndColorList = new HashMap<>();
    private Intent intent;
    private CardView cardViewSize, cardViewColor;
    private RelativeLayout ll_description;
    SpannableStringBuilder mSSBuilder;
    String mText = "Yung2";
    String is_in_whishlist;
    RecyclerView recyler_dynamic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.act_product_detail_new );
        requestQueue = Volley.newRequestQueue( ProductDetailActivityNew.this );
        //sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        intent = getIntent();

        //getcartModel = (GetcartModel) intent.getSerializableExtra(Constant.GETFVTMODEL);
        productIdTinder = intent.getStringExtra( Constant.ProductDetail.PRODUCT_ID );

        //Initlization
        setUpView();
        slider_image_list = new ArrayList<>();
        user = AppSharedPreferences.getSharePreference( this ).getUser();
        try{
            if (user.getUser_id().length()==0)
            {
                getProductDetails("");
            }
            else
                getProductDetails( user.getUser_id());

        }catch (Exception e){
            e.printStackTrace();
            getProductDetails("");
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
        ll_color = (LinearLayout) findViewById( R.id.ll_color );
        ll_size = (LinearLayout) findViewById( R.id.ll_size );
        tv_price = (TextView) findViewById( R.id.tv_price );
        tv_productName = (TextView) findViewById( R.id.tv_productName );
        tv_brandName = (TextView) findViewById( R.id.tv_brandName );
        vp_slider = (ExtendedViewPager) findViewById( R.id.vp_slider );

        ll_dots = (LinearLayout) findViewById( R.id.ll_dots );
        exv_description = (WebView) findViewById( R.id.exv_description );
        tv_showmore = (TextView) findViewById( R.id.tv_showmore );
        cardViewSize = (CardView) findViewById( R.id.cardViewSize );
        cardViewColor = (CardView) findViewById( R.id.cardViewColor );
        ll_description = (RelativeLayout) findViewById( R.id.ll_description );
        recyler_dynamic=(RecyclerView)findViewById( R.id.recyler_dynamic );
        exv_description.setBackgroundColor(getResources().getColor( R.color.transparent ));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_productDetail_back:
                finish();
                break;

            case R.id.tv_iv_favourite:

                try{
                    if (user.getUser_id().equalsIgnoreCase( "" )){
                        openDialog("add products to My Steez");
                    }
                    else{
                        if (is_in_whishlist.equalsIgnoreCase( "not_in_whishlist" ))
                            addfvtlist( productIdTinder );

                        else
                        {
                            final Dialog alertDialog = new Dialog( ProductDetailActivityNew.this );
                            alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                            alertDialog.setContentView( R.layout.view_dialog_addbag );
                            TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                            dialogMessage.setText( "Already Added to Steeze" );
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
                                }
                            }, 2000 );
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    openDialog("add products to My Steez");
                }

                break;

            case R.id.ll_btn_addToBag:
                try {
                    if (user.getUser_id().equalsIgnoreCase( "" )) {
                        openDialog("add products to Bag");
                    } else {
                        try {
                            JSONArray array = new JSONArray();
                            JSONObject object = new JSONObject();
                            object.put( Constant.ProductDetail.PRODUCT_ID, productIdTinder );
                            object.put( Constant.ProductDetail.VARIATION_ID, variationId );

                            JSONObject object1 = new JSONObject();
                            object1.put( Constant.ProductDetail.PA_SIZE, paSize );
                            object1.put( Constant.ProductDetail.PA_COLOR, paColor );
                            object1.put( Constant.ProductDetail.VARIATION, variationId );

                            object.put( Constant.ProductDetail.QUANTITY, "1" );
                            array.put( object );

                            JSONArray array1 = new JSONArray();
                            JSONObject object3 = new JSONObject();
                            object3.put( Constant.ProductDetail.PRODUCT_ID, productIdTinder );
                            object3.put( Constant.ProductDetail.QUANTITY, "1" );

                            array1.put( object3 ).toString();

                            if (varioutiontype != null && varioutiontype.equals( "simple" )) {
                                addToCart( array1.toString() );
                            } else {
                                addToCart( array.toString() );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    openDialog("add products to Bag");
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

                                    is_in_whishlist=dataObject.getString("is_in_whishlist");
                                    Log.e( "is_in_whishlist",""+is_in_whishlist );

                                    if (is_in_whishlist.equalsIgnoreCase( "in_whishlist" ))
                                    tv_iv_favourite.setBackgroundResource( R.drawable.my_steez_hover);

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
                                        Log.e( TAG+ " Description length: ",""+
                                                dataObject.getString( Constant.ProductDetail.DESCRIPTION ).toString().length()  );
                                        if (dataObject.getString( Constant.ProductDetail.DESCRIPTION ).toString().length() > 100)
                                        {
                                            String data="<html><head></head><body style='text-align:justify;' >"+
                                                    desc.substring( 0,99 )+"..."+
                                                    "<a href=\"hrupin://second_activity\">Read More</a></body></html>";
                                            exv_description.loadData(data, "text/html", "utf-8");
                                            exv_description.setWebViewClient(new WebViewClient()
                                            {
                                                // Override URL
                                                public boolean shouldOverrideUrlLoading(WebView view, String url)
                                                {
                                                    if(url.equalsIgnoreCase("hrupin://second_activity")){
                                                        Log.e( "Clicked","Read more" );

                                                        String data="<html><head></head><body style='text-align:justify;' >"+
                                                                desc;
                                                        exv_description.loadData(data, "text/html", "utf-8");
                                                    }

                                                    Log.e("URL","URL "+url);
                                                    return true;
                                                }
                                            });

                                        } else {
                                            tv_showmore.setText( "" );
                                            exv_description.loadData((dataObject.getString( Constant.ProductDetail.DESCRIPTION )), "text/html", "utf-8");
                                        }
                                    }

                                    if (!dataObject.getString( Constant.ProductDetail.ATTRIBUTES ).equals( "null" ))
                                    {
                                        JSONObject attributes = dataObject.getJSONObject( Constant.ProductDetail.ATTRIBUTES );
                                        JSONArray variations = attributes.getJSONArray( Constant.ProductDetail.VARIATION );
                                        JSONObject variation_attributes = attributes.getJSONObject( Constant.ProductDetail.VARIATION_ATTRIBUTES );
                                        JSONArray pa_size = null,
                                                pa_color = null;
                                        if (variations.length() > 0) {

                                            if (variation_attributes.has( Constant.ProductDetail.PA_SIZE ))
                                                pa_size = variation_attributes.getJSONArray( Constant.ProductDetail.PA_SIZE );

                                            if (variation_attributes.has( Constant.ProductDetail.PA_COLOR )) {
                                                pa_color = variation_attributes.getJSONArray( Constant.ProductDetail.PA_COLOR );

                                                //Steezle.. You can add here other products as well
                                            }
                                        }

                                        if (pa_size != null && pa_size.length() > 0) {

                                            for (int i = 0; i < pa_size.length(); i++) {

                                                JSONObject pa_sizeObject = pa_size.getJSONObject( i );
                                                SizeView sizeView = new SizeView(
                                                        getApplicationContext(),
                                                        pa_sizeObject.getString( Constant.ProductDetail.VARIATION_TITLE ),
                                                        ProductDetailActivityNew.this );

                                                ll_size.addView( sizeView );
                                                ll_size.invalidate();
                                            }
                                        }

                                        JSONObject variations_attributes = attributes.getJSONObject( Constant.ProductDetail.VARIATIONS_ATTRIBUTES );

                                        if (variation_attributes != null)
                                        {
                                            if (variations_attributes.has( Constant.ProductDetail.PA_SIZE ))
                                            {
                                                JSONObject pa_sizeColor = variations_attributes.getJSONObject( Constant.ProductDetail.PA_SIZE );

                                                Iterator<String> iter = pa_sizeColor.keys();

                                                while (iter.hasNext()) {

                                                    String key = iter.next();
                                                    ArrayList<SizeItem> sizeItems = new ArrayList<>();
                                                    try {

                                                        JSONArray value = pa_sizeColor.getJSONArray( key );

                                                        for (int i = 0; i < value.length(); i++) {

                                                            JSONObject jsonObject = value.getJSONObject( i );
                                                            SizeItem sizeItem = new SizeItem();
                                                            sizeItem.setVariation_id( jsonObject.getString( Constant.ProductDetail.VARIATION_ID ) );
                                                            sizeItem.setVariation_display_price( jsonObject.getString( Constant.ProductDetail.VARIATION_DISPLAY_PRICE ) );
                                                            sizeItem.setVariation_reguler_price( jsonObject.getString( Constant.ProductDetail.VARIATION_REGULER_PRICE ) );
                                                            sizeItem.setQty( jsonObject.getString( Constant.ProductDetail.QUANTITY ) );
                                                            sizeItem.setIs_in_stock( jsonObject.getString( Constant.ProductDetail.IS_IN_STOCK ) );
                                                            sizeItem.setAvailability_stock( jsonObject.getString( Constant.ProductDetail.AVAILABILITY_STOCK ) );
                                                            sizeItem.setColor_code( jsonObject.getString( Constant.ProductDetail.COLOR_CODE ) );

                                                            if (jsonObject.has( Constant.ProductDetail.PA_COLOR ))
                                                                sizeItem.setPa_color( jsonObject.getString( Constant.ProductDetail.PA_COLOR ) );
                                                            else
                                                                sizeItem.setPa_color( "" );

                                                            sizeItems.add( sizeItem );

                                                            /*JSONObject pa_sizeObject = pa_size.getJSONObject( i );
                                                            SizeView sizeView = new SizeView(
                                                                    getApplicationContext(),
                                                                    pa_sizeObject.getString( Constant.ProductDetail.VARIATION_TITLE ),
                                                                    ProductDetailActivity.this );

                                                            ll_size.addView( sizeView );
                                                            ll_size.invalidate();*/
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    paSizeAndColorList.put( key, sizeItems );
                                                }
                                            }
                                            //Steezle....For color only
                                            if (variations_attributes.has( Constant.ProductDetail.PA_COLOR )) {

                                                JSONObject pa_sizeColor = variations_attributes.getJSONObject( Constant.ProductDetail.PA_COLOR );

                                                Iterator<String> iter = pa_sizeColor.keys();

                                                while (iter.hasNext()) {

                                                    String key = iter.next();
                                                    ArrayList<SizeItem> sizeItems = new ArrayList<>();

                                                    try {

                                                        JSONArray value = pa_sizeColor.getJSONArray( key );

                                                        for (int i = 0; i < value.length(); i++) {

                                                            JSONObject jsonObject = value.getJSONObject( i );
                                                            SizeItem sizeItem = new SizeItem();
                                                            sizeItem.setVariation_id( jsonObject.getString( Constant.ProductDetail.VARIATION_ID ) );
                                                            sizeItem.setVariation_display_price( jsonObject.getString( Constant.ProductDetail.VARIATION_DISPLAY_PRICE ) );
                                                            sizeItem.setVariation_reguler_price( jsonObject.getString( Constant.ProductDetail.VARIATION_REGULER_PRICE ) );
                                                            sizeItem.setQty( jsonObject.getString( Constant.ProductDetail.QUANTITY ) );
                                                            sizeItem.setIs_in_stock( jsonObject.getString( Constant.ProductDetail.IS_IN_STOCK ) );
                                                            sizeItem.setAvailability_stock( jsonObject.getString( Constant.ProductDetail.AVAILABILITY_STOCK ) );
                                                            sizeItem.setColor_code( jsonObject.getString( Constant.ProductDetail.COLOR_CODE ) );

                                                            if (jsonObject.has( Constant.ProductDetail.PA_COLOR ))
                                                                sizeItem.setPa_color( jsonObject.getString( Constant.ProductDetail.PA_COLOR ) );
                                                            else
                                                                sizeItem.setPa_color( "" );

                                                            sizeItems.add( sizeItem );

                                                            ColorView sizeView = new ColorView(
                                                                    getApplicationContext(),
                                                                    sizeItem,
                                                                    ProductDetailActivityNew.this );

                                                            ll_color.addView( sizeView );
                                                            ll_color.invalidate();
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    paSizeAndColorList.put( key, sizeItems );
                                                }
                                            }

                                            if (!variations_attributes.has( Constant.ProductDetail.PA_COLOR ) &&
                                                    variations_attributes.has( Constant.ProductDetail.PA_SIZE )){

                                            }
                                            //Steezle....If brand id is specific add it here
                                        }

                                    } else {
                                        ll_size.setVisibility( View.GONE );
                                        cardViewSize.setVisibility( View.GONE );
                                        ll_color.setVisibility( View.GONE );
                                        cardViewColor.setVisibility( View.GONE );
                                    }
                                }

                                setFirstSizeColor();
                                sliderPagerAdapter = new SliderAdapter( ProductDetailActivityNew.this, slider_image_list );
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

                            AppSharedPreferences.getSharePreference( ProductDetailActivityNew.this ).setCartCount( obj.getString( "bag_count" ) );
                            AppSharedPreferences.getSharePreference( ProductDetailActivityNew.this ).setTotalSteez( obj.getString( "total_steez" ) );

                            /*try{
                                editor = sharedpreferences.edit();
                                editor.putString("total_steez", obj.getString( "total_steez"));
                                editor.putString("cart", obj.getString( "bag_count"));
                                editor.commit();}
                            catch (Exception e){
                                e.printStackTrace();
                            }*/

                            if (obj.get( "status" ).equals( "S" )) {
                                progressBar.setVisibility( View.GONE );

                                try{
                                    final Dialog alertDialog = new Dialog( ProductDetailActivityNew.this );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.view_dialog_addbag );
                                    TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                                    dialogMessage.setText( "Added to Steeze" );
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
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                            } else {
                                progressBar.setVisibility( View.GONE );

                                try{
                                final Dialog alertDialog = new Dialog( ProductDetailActivityNew.this );
                                alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                alertDialog.setContentView( R.layout.view_dialog_addbag );
                                TextView dialogMessage = (TextView) alertDialog.findViewById( R.id.dialogMessage );
                                dialogMessage.setText( "Added to Steeze" );
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
                                }catch (Exception e){
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
                        Toast.makeText( ProductDetailActivityNew.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
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
        VolleySingleton.getInstance( ProductDetailActivityNew.this );
        requestQueue.add( stringRequest );

    }


    @Override
    public void onSizeClick(String size, TextView textSize) {

        paSize = size;

        if (ll_size.getChildCount() > 0) {
            for (int i = 0; i < ll_size.getChildCount(); i++) {

                View view = (View) ll_size.getChildAt( i );
                TextView tvSize = (TextView) view.findViewById( R.id.tvSize );
                Drawable drawable = getResources().getDrawable( R.drawable.square_border );
                tvSize.setBackground( drawable );
                tvSize.setTextColor( Color.GRAY );
            }

            Drawable drawable = getResources().getDrawable( R.drawable.square_border_black );
            textSize.setBackground( drawable );
            textSize.setTextColor( Color.BLACK );
        }
        ArrayList<SizeItem> sizeItems = paSizeAndColorList.get( size );
        ll_color.removeAllViews();
        ll_color.invalidate();

//..(TOni marlow)
        for (int i = 0; i < sizeItems.size(); i++) {

            if (sizeItems.get( i ).getColor_code() != null && sizeItems.get( i ).getColor_code().length() > 0)
                ll_color.addView( new ColorView( this, sizeItems.get( i ), this ) );
        }

        //Set first color selected when change a any size
        if (sizeItems.size() > 0)
            firstSelectColor( sizeItems.get( 0 ) );
    }


    @Override
    public void onColorClick(String size, String name, String qty, TextView textView, ImageView rightMark,
                             ImageView backGround) {

        QTY = qty;
        paColor = name;
        variationId = size;

        if (ll_color.getChildCount() > 0) {

            for (int j = 0; j < ll_color.getChildCount(); j++) {

                View view = (View) ll_color.getChildAt( j );

                //TextView tvColor = (TextView) view.findViewById(R.id.tvColor);
                ImageView iv_colorMark = (ImageView) view.findViewById( R.id.iv_colorMark );
                iv_colorMark.setVisibility( View.GONE );

                ImageView iv_backGround = (ImageView) view.findViewById( R.id.iv_backGround );

                //Drawable tvDrawable = (Drawable) textView.getBackground();
                //tvColor.setBackground(tvDrawable);

                Drawable drawable1 = getResources().getDrawable( R.drawable.square_border_tansparent );
                iv_backGround.setBackground( drawable1 );
            }

            rightMark.setVisibility( View.VISIBLE );
            Drawable drawable = getResources().getDrawable( R.drawable.square_border_white_tansparent );
            rightMark.setBackground( drawable );

        }
        //Toast.makeText(getApplicationContext(), size, Toast.LENGTH_LONG).show();
    }


    /*@Override
    public void onColorClick(String size, TextView textView) {


    }*/


    private void setFirstSizeColor() {

        if (ll_size.getChildCount() > 0) {
            View view = (View) ll_size.getChildAt( 0 );
            TextView tvSize = (TextView) view.findViewById( R.id.tvSize );
            Drawable drawable = getResources().getDrawable( R.drawable.square_border_black );
            tvSize.setBackground( drawable );
            tvSize.setTextColor( Color.BLACK );
            ArrayList<SizeItem> sizeItems = paSizeAndColorList.get( tvSize.getText().toString() );
            if (sizeItems != null) {
                for (int i = 0; i < sizeItems.size(); i++) {

                    if (sizeItems.get( i ).getColor_code() != null && sizeItems.get( i ).getColor_code().length() > 0)
                        ll_color.addView( new ColorView( getApplicationContext(), sizeItems.get( i ), this ) );
                }
            }

            if (sizeItems.size() > 0)
                firstSelectColor( sizeItems.get( 0 ) );

        }

    }

    private void firstSelectColor(SizeItem sizeItem) {

        if (ll_color.getChildCount() > 0) {

            View view1 = (View) ll_color.getChildAt( 0 );
            ImageView rightMark = (ImageView) view1.findViewById( R.id.iv_colorMark );
            ImageView backGround = (ImageView) view1.findViewById( R.id.iv_backGround );
            rightMark.setVisibility( View.VISIBLE );
            Drawable db = getResources().getDrawable( R.drawable.square_border_white_tansparent );
            rightMark.setBackground( db );

        }

        QTY = sizeItem.getQty();
        paColor = sizeItem.getPa_color();
        variationId = sizeItem.getVariation_id();
    }


    private void addToCart(final String variationId) {

        //progressBar.setVisibility(View.VISIBLE);
        AddToCartService.addToCart(
                this,
                user.getUser_id(),
                variationId,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e( TAG, "AddToCartResponse--->" + response );

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject( response );

                                String data = obj.getString( Constant.AddToCart.DATA );

                                AppSharedPreferences.getSharePreference( ProductDetailActivityNew.this ).setCartCount( obj.getString( "bag_count" ) );
                                AppSharedPreferences.getSharePreference( ProductDetailActivityNew.this ).setTotalSteez( obj.getString( "total_steez" ) );

                                                                if (obj.getString( Constant.AddToCart.STATUS ).equals( "S" )) {
                                    try{
                                    final Dialog alertDialog = new Dialog( ProductDetailActivityNew.this );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.view_dialog_addbag );
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
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
//                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.AddToCart.MESSAGE), Toast.LENGTH_LONG).show();


                                } else {
                                    try{
                                    final Dialog alertDialog = new Dialog( ProductDetailActivityNew.this );
                                    alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
                                    alertDialog.setContentView( R.layout.view_dialog_addbag );
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
//                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.AddToCart.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility( View.GONE );
                                    }catch (Exception e){
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
    public void openDialog(String text){
        AlertDialog.Builder builder=new AlertDialog.Builder( ProductDetailActivityNew.this,R.style.Theme_AppCompat_Light_Dialog_Alert );
        builder.setTitle( "Steezle" );
        builder.setMessage( "Sign in or Sign Up to "+text );
        builder.setCancelable( false );
        builder.setPositiveButton( "Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Call Login screen
                Intent i=new Intent( getApplicationContext(), LoginActivity.class );
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

}


