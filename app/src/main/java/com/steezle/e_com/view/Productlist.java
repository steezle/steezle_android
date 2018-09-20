package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.BrandlistAdapter;
import com.steezle.e_com.adapter.TinderCard;
import com.steezle.e_com.model.BrandModel;
import com.steezle.e_com.model.TinderItem;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.AddToFavouriteService;
import com.steezle.e_com.services.ProductFilterService;
import com.steezle.e_com.services.ProductListService;
import com.steezle.e_com.services.RemoveFavouriteService;
import com.steezle.e_com.services.SuffleService;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.utils.ProjectUtility;

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
import java.util.HashMap;
import java.util.List;

public class Productlist extends Activity implements TinderCard.OnReject, TinderCard.OnAccept,
        View.OnClickListener, BrandlistAdapter.OnBrandCheck {

    public static final String TAG = Productlist.class.getSimpleName();
    private static final String ACCEPT = "Accept";
    private static final String REJECT = "Reject";

    private ProgressBar progressBar;
    private ProgressBar progressBarFilter;
    private SwipePlaceHolderView swipeView;
    private ImageView iv_userIcon, iv_back_productList, iv_heart, iv_search, iv_favourite;
    private TextView tv_favourite, tv_heart, tv_title;
    private LinearLayout ll_filter;
    private String titleresult, fvt, cart;
    private String brands;
    private User user;
    private ArrayList<TinderItem> tinderItemArrayList = new ArrayList<>();
    private ArrayList<HashMap<String, TinderItem>> undoItems = new ArrayList<>();
    private ImageButton iv_btn_accept, iv_btn_reject;

    private ImageView iv_noProductAvailable;
    private int page = 1, totalPage;
    private LinearLayout ll_reject, ll_undo, ll_accept;
    String titile_product="";
    Intent intent;
    int Filter_count=0;


    Dialog dialog;
    RecyclerView rec_filterList;
    ImageView back_click_clothing;
    ArrayList<BrandModel> filterArrayList;
    String brandID;
    String  minrange, maxrange;
    RelativeLayout rl_btn_applyFilter;
    CrystalRangeSeekbar range_seekbar;
    TextView tv_minimum, tv_maximum;
    List<NameValuePair> params11;
    BrandlistAdapter adapter;
    HashMap<String, String> brandHashMap;
    JSONArray filterArray;
    int i;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tinder_swipe );
        intent=getIntent();
        //Initlization
        user = AppSharedPreferences.getSharePreference(getApplicationContext()).getUser();
        brands = intent.getStringExtra( "ids");
        titleresult = intent.getStringExtra("id");
        setUpView();
        tv_favourite.setText(intent.getStringExtra("bagCount"));
        tv_heart.setText(intent.getStringExtra("totalSteez"));

        //Register Button Click Event
        iv_back_productList.setOnClickListener(this);
        ll_accept.setOnClickListener(this);
        iv_btn_accept.setOnClickListener(this);
        ll_reject.setOnClickListener(this);
        iv_btn_reject.setOnClickListener(this);
        ll_undo.setOnClickListener(this);
        iv_favourite.setOnClickListener(this);
        iv_heart.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_userIcon.setOnClickListener(this);
        ll_filter.setOnClickListener(this);


        swipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        if (intent.getStringExtra("From") != null &&
                intent.getStringExtra("From").equals("Search")) {

            String title = intent.getStringExtra("Key");
            String s = ProjectUtility.toCamelCaseWord(title);
            tv_title.setText(s);
            searchResult();

        } else if (intent.getStringExtra("From") != null &&
                intent.getStringExtra("From").equals("Filter")){

            titile_product = intent.getStringExtra("titile_product");

            if (intent.getStringExtra("titile_product").equals("Shuffle")) {

                String s = intent.getStringExtra("titile_product").toUpperCase();
                //String title = ProjectUtility.toCamelCaseWord(s);
                tv_title.setText(s);

            } else {
                tv_title.setText(intent.getStringExtra("titile_product"));
            }
            searchResult();

        } else  {

            titile_product = intent.getStringExtra("productname");

            String titile = intent.getStringExtra("productname");
            String s = ProjectUtility.toCamelCaseWord(titile);

                if (brands != null && !brands.equals("0")) {

                    tv_title.setText(s);
                    try{
                        if (user.getUser_id().length()==0){
                            getProductList(page,"");
                        }
                        else
                            getProductList(page, user.getUser_id());

                    }catch (Exception e){
                        e.printStackTrace();
                        getProductList(page,"");
                    }
                } else {

                    tv_title.setText("SHUFFLE");
                    try{
                        if (user.getUser_id().length()==0){
                            getSuffle(page,"");
                        }
                        else
                            getSuffle(page, user.getUser_id());

                    }catch (Exception e){
                        e.printStackTrace();
                        getSuffle(page,"");
                    }

                }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Log.e( TAG+" Cart",""+AppSharedPreferences.getSharePreference(getApplicationContext()).getCartCount() );
        Log.e( TAG+" Steeze",""+AppSharedPreferences.getSharePreference(getApplicationContext()).getTotalSteez() );
        tv_favourite.setText(AppSharedPreferences.getSharePreference(getApplicationContext()).getCartCount());
        tv_heart.setText(AppSharedPreferences.getSharePreference(getApplicationContext()).getTotalSteez());
    }

    private void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        iv_favourite = (ImageView) findViewById(R.id.iv_favourite);
        swipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        tv_favourite = (TextView) findViewById(R.id.tv_favourite);
        tv_heart = (TextView) findViewById(R.id.tv_heart);
        iv_back_productList = (ImageView) findViewById(R.id.iv_back_productList);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_noProductAvailable = (ImageView) findViewById(R.id.iv_noProductAvailable);
        iv_btn_accept = (ImageButton) findViewById(R.id.iv_btn_accept);
        iv_btn_reject = (ImageButton) findViewById(R.id.iv_btn_reject);
        iv_heart = (ImageView) findViewById(R.id.iv_heart);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_userIcon = (ImageView) findViewById(R.id.iv_userIcon);
        ll_filter = (LinearLayout) findViewById(R.id.ll_filter);
        ll_reject = (LinearLayout) findViewById(R.id.ll_reject);
        ll_undo = (LinearLayout) findViewById(R.id.ll_undo);
        ll_accept = (LinearLayout) findViewById(R.id.ll_accept);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back_productList:
                Log.e( "iv_back_productList","pressed" );
                Productlist.this.finish();
                break;

            case R.id.ll_accept:
                if (tinderItemArrayList != null && tinderItemArrayList.size() > 0) {
                    try{
                        if (user.getUser_id().equalsIgnoreCase( "" )){
                            openDialog("add products to My Steez");
                        }
                        else
                        {
                            swipeView.doSwipe(true);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        openDialog("add products to My Steez");
                    }


                }
                break;

            case R.id.iv_btn_accept:
                if (tinderItemArrayList != null && tinderItemArrayList.size() > 0) {
                    try{
                        if (user.getUser_id().equalsIgnoreCase( "" )){
                            openDialog("add products to My Steez");
                        }
                        else
                        {
                            swipeView.doSwipe(true);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        openDialog("add products to My Steez");
                    }

//                    swipeView.doSwipe(true);
                }
                break;

            case R.id.ll_reject:
                if (tinderItemArrayList != null && tinderItemArrayList.size() > 0) {
                    swipeView.doSwipe(false);
                    /*try{
                        if (user.getUser_id().equalsIgnoreCase( "" )){
                            openDialog();
                        }
                        else
                        {
                            swipeView.doSwipe(false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        openDialog();
                    }*/
                }
                break;

            case R.id.iv_btn_reject:
                if (tinderItemArrayList != null && tinderItemArrayList.size() > 0) {

                    {
                        swipeView.doSwipe(false);
                        /*try{
                            if (user.getUser_id().equalsIgnoreCase( "" )){
                                openDialog();
                            }
                            else
                            {
                                swipeView.doSwipe(false);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            openDialog();
                        }
*/
                    }
                }
                break;

            case R.id.ll_undo:

                if (undoItems.isEmpty()) {
//                    Toast.makeText(getActivity(), "No item for undo!",Toast.LENGTH_LONG).show();
                }
                else {
                    if (undoItems.get(0).get(ACCEPT) != null) {

                        TinderItem tinderItem = undoItems.get(/*undoItems.size()-1*/0).get(ACCEPT);
                        removeFavourite(tinderItem);

                    } else {

                            TinderItem tinderItem = undoItems.get(/*undoItems.size()-1*/0).get(REJECT);
                            tinderItemArrayList.add(0, tinderItem);

                            if (swipeView != null) {

                                swipeView.removeAllViews();
                                for (int i = 0; i < tinderItemArrayList.size(); i++) {

                                    TinderItem tItem = tinderItemArrayList.get(i);
                                    TinderCard tCard = new TinderCard(Productlist.this, tItem, Productlist.this, Productlist.this);
                                    swipeView.addView(tCard);
                                }
                            }
                            undoItems.remove(undoItems.size()-1);

                    }
                }
                break;


            case R.id.iv_favourite:
                try
                {
                    if (user.getUser_id().equalsIgnoreCase( "" )){

                        openDialog("add product to Bag");
                    }
                    else{
                        Intent i = new Intent(getApplicationContext(), ShoppigbagActivity.class);
                        startActivity(i);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    openDialog("add product to Bag");
                }
                break;

            case R.id.iv_heart:
                try
                {
                    if (user.getUser_id().equalsIgnoreCase( "" )){

                        openDialog("add products to My Steez");
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), MysteezActivity.class);
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    openDialog("add products to My Steez");
                }
                break;

            case R.id.iv_search:

                Intent i1=new Intent( getApplicationContext(),Search.class );
                startActivity( i1 );
                break;

            case R.id.iv_userIcon:
                try
                {
                    if (user.getUser_id().equalsIgnoreCase( "" )){

                        openDialog("go to Profile Page");
                    }
                    else{
                        Intent intent1 = new Intent(getApplicationContext(), TabActivity.class);
                        intent1.putExtra("From", "user");
                        startActivity(intent1);

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    openDialog("go to Profile Page");
                }
                 break;

            case R.id.ll_filter:
              /*  Intent i=new Intent( getApplicationContext(),FilterList.class );
                i.putExtra(Constant.ProductFilter.BRAND_ID, brands);
                i.putExtra(Constant.ProductFilter.CATEGORY_ID, titleresult);
                i.putExtra( "titile_product",""+titile_product );
                startActivity( i );*/

              openFilterDialog();
                break;

        }
    }


    private void getSuffle(int paged, String userID) {

        //progressBar.setVisibility(View.VISIBLE);
        SuffleService.suffleProduct(
                Productlist.this,
                userID,
                String.valueOf(paged),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "SuffleRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                fvt = obj.getString(Constant.Suffle.BAG_COUNT);
                                cart = obj.getString(Constant.Suffle.TOTAL_STEEZ);

                                tv_favourite.setText(fvt);
                                tv_heart.setText(cart);

                                totalPage = obj.getInt(Constant.Suffle.PAGES);

                                if (obj.get(Constant.Suffle.STATUS).equals("S")) {

                                    JSONArray dataArray = obj.getJSONArray(Constant.Suffle.DATA);

                                    for (int i = 0; i < dataArray.length(); i++) {

                                        JSONObject object = dataArray.getJSONObject(i);
                                        TinderItem tinderItem = new TinderItem();
                                        tinderItem.setId(object.getString(Constant.Suffle.ID));
                                        tinderItem.setTitle(object.getString(Constant.Suffle.TITLE));
                                        tinderItem.setPrice(object.getString(Constant.Suffle.PRICE));
                                        tinderItem.setDescription(object.getString(Constant.Suffle.DESCRIPTION));
                                        tinderItem.setMain_image(object.getString(Constant.Suffle.MAIN_IMAGE));
                                        tinderItem.setBrandname(object.getString(Constant.Suffle.BRANDS));

                                        TinderCard tinderCard = new TinderCard(Productlist.this, tinderItem, Productlist.this, Productlist.this);
                                        tinderItemArrayList.add(tinderItem);

                                        if (swipeView != null)
                                            swipeView.addView(tinderCard);
                                    }

                                } else if (obj.getString(Constant.Suffle.STATUS).equals("F")) {

//                                    Toast.makeText(getActivity(), obj.getString(Constant.Suffle.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);

                                } else {

                                    if (obj.getString(Constant.Suffle.STATUS).equals("w")) {
                                        progressBar.setVisibility(View.GONE);
                                    }
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


    private void getProductList(int paged, String userId) {

        //progressBar.setVisibility(View.VISIBLE);
        ProductListService.getProductList(
                Productlist.this,
                userId,
                titleresult,
                String.valueOf(paged),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "ProductListRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                fvt = obj.getString(Constant.ProductList.BAG_COUNT);
                                cart = obj.getString(Constant.ProductList.TOTAL_STEEZ);

                                tv_favourite.setText(fvt);
                                tv_heart.setText(cart);

                                totalPage = obj.getInt(Constant.ProductList.PAGES);

                                if (obj.get(Constant.ProductList.STATUS).equals("S")) {

                                    JSONArray dataArray = obj.getJSONArray(Constant.ProductList.DATA);

                                    for (int i = 0; i < dataArray.length(); i++) {

                                        JSONObject object = dataArray.getJSONObject(i);

                                        TinderItem tinderItem = new TinderItem();

                                        tinderItem.setId(object.getString(Constant.ProductList.ID));
                                        tinderItem.setTitle(object.getString(Constant.ProductList.TITLE));
                                        tinderItem.setPrice(object.getString(Constant.ProductList.PRICE));
                                        tinderItem.setDescription(object.getString(Constant.ProductList.DESCRIPTION));
                                        tinderItem.setMain_image(object.getString(Constant.ProductList.MAIN_IMAGE));
                                        tinderItem.setBrandname(object.getString(Constant.ProductList.BRANDS));

                                        TinderCard tinderCard = new TinderCard(Productlist.this, tinderItem, Productlist.this, Productlist.this);
                                        tinderItemArrayList.add(tinderItem);

                                        if (swipeView != null) {
                                            swipeView.addView(tinderCard);
                                        }
                                    }

                                    progressBar.setVisibility(View.GONE);

                                } else if (obj.getString(Constant.ProductList.STATUS).equals("F")) {
//                                    Toast.makeText(getActivity(), obj.getString(Constant.ProductList.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (obj.getString(Constant.ProductList.STATUS).equals("w")) {
                                        progressBar.setVisibility(View.GONE);
                                    }
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


    private void addToFavourite(String productId) {

        //progressBar.setVisibility(View.VISIBLE);
        AddToFavouriteService.addToFavourite(
                Productlist.this,
                user.getUser_id(),
                productId,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "AddToFavRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                tv_favourite.setText(obj.getString(Constant.AddToFavourite.BAG_COUNT));
                                tv_heart.setText(obj.getString(Constant.AddToFavourite.TOTAL_STEEZ));

                                AppSharedPreferences.getSharePreference(getApplicationContext() ).setCartCount( obj.getString( Constant.AddToFavourite.BAG_COUNT ) );
                                AppSharedPreferences.getSharePreference(getApplicationContext() ).setTotalSteez( obj.getString( Constant.AddToFavourite.TOTAL_STEEZ ) );

                                if (obj.get(Constant.AddToFavourite.STATUS).equals("S")) {
//                                    Toast.makeText(getActivity(), obj.getString(Constant.AddToFavourite.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
//                                    Toast.makeText(getActivity(), obj.getString(Constant.AddToFavourite.MESSAGE), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onAccept(TinderItem tinderItem) {
        try{
            if (user.getUser_id().equalsIgnoreCase( "" )){
                openDialog("add products to My Steez");
            }
            else
            {
                undoItems.clear();

                if (tinderItemArrayList != null && tinderItemArrayList.size() > 0) {

                    TinderItem acceptItem = tinderItemArrayList.remove(0);

                    if (acceptItem != null) {

                        addToFavourite(acceptItem.getId());
                        HashMap<String, TinderItem> hashMap = new HashMap<>();
                        hashMap.put(ACCEPT, acceptItem);
                        undoItems.add(hashMap);
                    }
                }
                loadMoreProduct();

            }
        }catch (Exception e){
            e.printStackTrace();
            openDialog("add products to My Steez");
        }



    }

    @Override
    public void onReject(TinderItem tinderItem) {
            undoItems.clear();

            if (tinderItemArrayList != null && tinderItemArrayList.size() > 0) {

                TinderItem acceptItem = tinderItemArrayList.remove(0);

                if (acceptItem != null) {

                    HashMap<String, TinderItem> hashMap = new HashMap<>();
                    hashMap.put(REJECT, tinderItem);
                    undoItems.add(hashMap);
                }
            }

            loadMoreProduct();
/*
        try{
            if (user.getUser_id().equalsIgnoreCase( "" )){
                openDialog();
            }
            else
            {
                undoItems.clear();

                if (tinderItemArrayList != null && tinderItemArrayList.size() > 0) {

                    TinderItem acceptItem = tinderItemArrayList.remove(0);

                    if (acceptItem != null) {

                        HashMap<String, TinderItem> hashMap = new HashMap<>();
                        hashMap.put(REJECT, tinderItem);
                        undoItems.add(hashMap);
                    }
                }

                loadMoreProduct();

            }
        }catch (Exception e){
            e.printStackTrace();
            openDialog();
        }*/


    }


    private void loadMoreProduct() {
        Log.e( "Inside","loadmoreproduct" );

        if (swipeView.getChildCount() == 1) {
            page++;
            Log.e( "paged",""+page );

            if (page <= totalPage) {

                Log.e( "Inside","page<totalpage" );
                if (brands != null && brands.equals("0")) {
                    Log.e( "Inside IF","brand" );
                    if (Filter_count>0){
                        try{
                            if (user.getUser_id().equalsIgnoreCase( "" ))
                                onFilterSuccess("",""+page);
                            else
                                onFilterSuccess(user.getUser_id(),""+page);
                        }catch (Exception e){
                            e.printStackTrace();
                            onFilterSuccess("",""+page);
                        }

                    }
                    else{
                        try{
                            if (user.getUser_id().length()==0){
                                getSuffle(page,"");
                                Log.e( "Inside try IF","brand" );

                            }
                            else
                                getSuffle(page, user.getUser_id());

                        }catch (Exception e){
                            e.printStackTrace();
                            getSuffle(page,"");
                        }

                    }


                } else {
                    Log.e( "Inside ELSE","brand" );
                    if (Filter_count>0){
                        try{
                            if (user.getUser_id().equalsIgnoreCase( "" ))
                                onFilterSuccess("",""+page);
                            else
                                onFilterSuccess(user.getUser_id(),""+page);
                        }catch (Exception e){
                            e.printStackTrace();
                            onFilterSuccess("",""+page);
                        }

                    }
                    else{
                        try{
                            if (user.getUser_id().length()==0){
                                getProductList(page,"");
                                Log.e( "Inside try else","brand" );

                            }
                            else
                                getProductList(page, user.getUser_id());

                        }catch (Exception e){
                            e.printStackTrace();
                            getProductList(page,"");
                        }
                    }


                }
            } else {
                iv_noProductAvailable.setVisibility(View.VISIBLE);
            }
        }
    }




    private void removeFavourite(final TinderItem tinderItem) {
        //progressBar.setVisibility(View.VISIBLE);
        RemoveFavouriteService.removeFavourite(
                Productlist.this,
                user.getUser_id(),
                tinderItem.getId(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "RemoveFavRes--->" + response);

                        if (response != null) {
                            try {
                                undoItems.remove(undoItems.size()-1);

                                JSONObject obj = new JSONObject(response);
                                if (obj.getString(Constant.RemoveFavourite.STATUS).equals("S")) {

                                    tinderItemArrayList.add(0, tinderItem);
                                    if (swipeView != null) {

                                        swipeView.removeAllViews();
                                        for (int i=0; i<tinderItemArrayList.size(); i++) {

                                            TinderItem tItem = tinderItemArrayList.get(i);
                                            TinderCard tCard = new TinderCard(Productlist.this, tItem, Productlist.this, Productlist.this);
                                            swipeView.addView(tCard);
                                        }
                                    }
                                    int count = Integer.parseInt(tv_heart.getText().toString());
                                    tv_heart.setText(""+(count-1));

                                    progressBar.setVisibility(View.GONE);
                                }
//                                Toast.makeText(getActivity(), obj.getString(Constant.RemoveFavourite.MESSAGE), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                }
        );
    }
    private void searchResult() {

        try {
            JSONArray dataArray = new JSONArray(intent.getStringExtra("Data"));

            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject object = dataArray.getJSONObject(i);

                TinderItem tinderItem = new TinderItem();

                tinderItem.setId(object.getString("id"));
                tinderItem.setTitle(object.getString("title"));
                tinderItem.setPrice(object.getString("price"));
                tinderItem.setDescription(object.getString("description"));
                tinderItem.setMain_image(object.getString("main_image"));
                tinderItem.setBrandname(object.getString("brands"));
                TinderCard tinderCard = new TinderCard(Productlist.this, tinderItem, Productlist.this, Productlist.this);
                tinderItemArrayList.add(tinderItem);

                if (swipeView != null)
                    swipeView.addView(tinderCard);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDialog(String text){
        AlertDialog.Builder builder=new AlertDialog.Builder( Productlist.this,R.style.Theme_AppCompat_Light_Dialog_Alert );
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

    public void openFilterDialog(){
        dialog=new Dialog( Productlist.this, R.style.AppTheme_NoActionBar );
        dialog.setContentView( R.layout.act_filter );
        dialog.setCancelable( false );

        filterArray = new JSONArray();
        brandHashMap = new HashMap<>();

        setUpViewFilter(dialog);
        user = AppSharedPreferences.getSharePreference(getApplicationContext()).getUser();
        fillBrandListAdapter();
        try{

            if (user.getUser_id().equalsIgnoreCase( "" )){
                getFilterProducts("");
            }else {
                getFilterProducts(user.getUser_id());
            }
        }catch (Exception e){
            e.printStackTrace();
            getFilterProducts("");
        }
        back_click_clothing.setOnClickListener(this);
        rl_btn_applyFilter.setOnClickListener(this);


        range_seekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tv_minimum.setText("$" + String.valueOf(minValue));
                tv_maximum.setText("$" + String.valueOf(maxValue));
            }
        });

        range_seekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                tv_minimum.setText(String.valueOf(minValue));
                tv_maximum.setText(String.valueOf(maxValue));
            }
        });

        back_click_clothing.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
        rl_btn_applyFilter.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page=1;
                try{
                if (user.getUser_id().equalsIgnoreCase( "" ))
                    onFilterSuccess("",""+page);
                else
                    onFilterSuccess(user.getUser_id(),""+page);
            }catch (Exception e){
                e.printStackTrace();
                onFilterSuccess("",""+page);
            }
            }
        } );

        dialog.show();
    }

    public void setUpViewFilter(Dialog dialog) {
        progressBarFilter = (ProgressBar)dialog. findViewById(R.id.progressBar);
        rec_filterList = (RecyclerView)dialog. findViewById(R.id.rec_filterList);
        back_click_clothing = (ImageView) dialog.findViewById(R.id.back_click_clothing);
        rl_btn_applyFilter = (RelativeLayout)dialog. findViewById(R.id.rl_btn_applyFilter);
        range_seekbar = (CrystalRangeSeekbar)dialog. findViewById(R.id.range_seekbar);
        tv_minimum = (TextView) dialog.findViewById(R.id.tv_minimum);
        tv_maximum = (TextView)dialog. findViewById(R.id.tv_maximum);
    }
    private void fillBrandListAdapter() {

        filterArrayList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(Productlist.this, 1, GridLayoutManager.VERTICAL, false);
        rec_filterList.setLayoutManager(manager);
        DividerItemDecoration divider = new DividerItemDecoration(rec_filterList.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable( ContextCompat.getDrawable(Productlist.this, R.drawable.line));
        rec_filterList.addItemDecoration(divider);
        rec_filterList.setHasFixedSize(true);
        //rec_filterList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BrandlistAdapter(filterArrayList, Productlist.this, Productlist.this);
        rec_filterList.setAdapter(adapter);

    }


    private void getFilterProducts(String userId) {


        progressBarFilter.setVisibility(View.VISIBLE);
        ProductFilterService.getProductFilter(
                Productlist.this,
                userId,
                titleresult,
                /*brandID*/"0",
                progressBarFilter,
                new APIService.Success<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "GetProductFilter--->" + response);

                        if (response != null) {

                            Log.e( "response",""+response );
                            try {

                                JSONObject obj = new JSONObject(response);

                                JSONObject priceFilter = obj.getJSONObject(Constant.ProductFilter.PRICE_FILTERS);

                                minrange = priceFilter.getString(Constant.ProductFilter.MIN_PRICE);
                                maxrange = priceFilter.getString(Constant.ProductFilter.MAX_PRICE);

                                tv_minimum.setText("$" + minrange);
                                tv_maximum.setText("$" + maxrange);

                                range_seekbar.setMinValue(Float.parseFloat(minrange));
                                range_seekbar.setMaxValue(Float.parseFloat(maxrange));

                                JSONArray jarrary = obj.getJSONArray(Constant.ProductFilter.BRAND_FILTERS);

                                for (i = 0; i < jarrary.length(); i++) {
                                    JSONObject jsonObject = jarrary.getJSONObject(i);

                                    BrandModel hero = new BrandModel(
                                            jsonObject.getString(Constant.ProductFilter.BRAND_ID),
                                            jsonObject.getString(Constant.ProductFilter.BRAND), false);

                                    filterArrayList.add(hero);
                                }
                                adapter.notifyDataSetChanged();
                                progressBarFilter.setVisibility(View.GONE);

                                if(minrange.equals(maxrange)) {
                                    range_seekbar.setEnabled(false);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBarFilter.setVisibility(View.GONE);
                            } finally {
                                adapter.notifyItemChanged(i);
                                progressBarFilter.setVisibility(View.GONE);
                            }

                        }
                    }
                }
        );
    }


    private void onFilterSuccess(String userId, String paged) {
        getFilterArray();


        if (brandID != null && brandID.equals("0")) {
            Log.e( "IF filterArray.)",""+filterArray.toString());
            params11 = new ArrayList<NameValuePair>();
            params11.add(new BasicNameValuePair("category_id", "0"));
            params11.add(new BasicNameValuePair("user_id", userId));
            params11.add(new BasicNameValuePair("brand_id", "0"));
            params11.add(new BasicNameValuePair("paged", ""+paged));
            params11.add(new BasicNameValuePair("filters", filterArray.toString()));

        } else {
            Log.e( "ELSE filterArray.)",""+filterArray.toString());
            params11 = new ArrayList<NameValuePair>();
            params11.add(new BasicNameValuePair("category_id", titleresult));
            params11.add(new BasicNameValuePair("user_id", userId));
            params11.add(new BasicNameValuePair("brand_id", "0"));
            params11.add(new BasicNameValuePair("paged", ""+paged));
            params11.add(new BasicNameValuePair("filters", filterArray.toString()));

        }

        new ApplyFilter().execute();
    }


    @Override
    public void onBrandCheck(BrandModel brandModel, int pos) {

        if (brandHashMap.containsKey(String.valueOf(pos)))
            brandHashMap.remove(String.valueOf(pos));
        else
            brandHashMap.put(String.valueOf(pos), brandModel.getId());

    }


    public class ApplyFilter extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(Productlist.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected String doInBackground(Void... params) {
            String obj;//new JSONArray();
            try {
                Log.e( "params11",""+params11.toString() );
                obj = getJSONFromUrl( APIs.applayfilter, params11);
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            Filter_count++;
            Log.e("Result", "" + result);

 /*
            try {
                JSONObject json = new JSONObject(result);
                String status = json.getString(Constant.ApplyFilter.STATUS);
                Log.d("Status:", status);

                if (status.equals("S")) {


                    Intent i=new Intent( getApplicationContext(), Productlist.class );
                    i.putExtra("titile_product",""+titile_product);
                    i.putExtra("id", "");
                    i.putExtra("From", "Filter");
                    i.putExtra("Data", json.getString("data"));
                    i.putExtra("ids", brandID);
                    i.putExtra("id", categoryID);
                    i.putExtra("bagCount", json.getString("bag_count"));
                    i.putExtra("totalSteez", json.getString("total_steez"));
                    startActivity( i );
                } else {

                    Toast.makeText(getApplicationContext(), "No record found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
*/
            pDialog.dismiss();

            dialog.dismiss();
            try {

                JSONObject obj = new JSONObject(result);

                fvt = obj.getString(Constant.ProductList.BAG_COUNT);
                cart = obj.getString(Constant.ProductList.TOTAL_STEEZ);

                tv_favourite.setText(fvt);
                tv_heart.setText(cart);

                totalPage = obj.getInt(Constant.ProductList.PAGES);

                Log.e( "Filter","totalpage: "+totalPage );
                if (obj.get(Constant.ProductList.STATUS).equals("S")) {

                    JSONArray dataArray = obj.getJSONArray(Constant.ProductList.DATA);
                    tinderItemArrayList.clear();
                    swipeView.removeAllViews();
                    for (int i = 0; i < dataArray.length(); i++) {

                        JSONObject object = dataArray.getJSONObject(i);

                        TinderItem tinderItem = new TinderItem();

                        tinderItem.setId(object.getString(Constant.ProductList.ID));
                        tinderItem.setTitle(object.getString(Constant.ProductList.TITLE));
                        tinderItem.setPrice(object.getString(Constant.ProductList.PRICE));
                        tinderItem.setDescription(object.getString(Constant.ProductList.DESCRIPTION));
                        tinderItem.setMain_image(object.getString(Constant.ProductList.MAIN_IMAGE));
                        tinderItem.setBrandname(object.getString(Constant.ProductList.BRANDS));

                        TinderCard tinderCard = new TinderCard(Productlist.this, tinderItem, Productlist.this, Productlist.this);
                        tinderItemArrayList.add(tinderItem);

                        if (swipeView != null) {
                            swipeView.addView(tinderCard);
                        }
                    }

                    progressBarFilter.setVisibility(View.GONE);

                } else if (obj.getString(Constant.ProductList.STATUS).equals("F")) {
//                                    Toast.makeText(getActivity(), obj.getString(Constant.ProductList.MESSAGE), Toast.LENGTH_LONG).show();
                    progressBarFilter.setVisibility(View.GONE);
                } else {
                    if (obj.getString(Constant.ProductList.STATUS).equals("w")) {
                        progressBarFilter.setVisibility(View.GONE);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                progressBarFilter.setVisibility(View.GONE);
            }

        }

    }

    public String getJSONFromUrl(String url, List<NameValuePair> params) {
        InputStream is = null;
        String json = "";

        try {
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

    }


    private JSONArray getBrandArray() {
        JSONArray jsonArray = new JSONArray();
        for (String value : brandHashMap.values()) {
            jsonArray.put(value);

        }

        return jsonArray;
    }

    private void getFilterArray() {


        Log.e( TAG+" MIN: ",""+tv_minimum.getText().toString() );
        Log.e( TAG+" MAX: ",""+tv_maximum.getText().toString() );
        String min = String.valueOf(tv_minimum.getText().toString());

        String[] minS = min.split("\\$");
        String part1 = minS[0].trim();
        String part2 = minS[1].trim();

        String max = String.valueOf(tv_maximum.getText().toString());

        String[] maxS = max.split("\\$");
        String part11 = maxS[0].trim();
        String part22 = maxS[1].trim();

        try {
            Log.e( "part2",""+part2 );
            Log.e( "part22",""+part22 );

            JSONObject price = new JSONObject();
            price.put("min", part2);
            price.put("max", part22);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("brand", getBrandArray());
            jsonObject.put("price", price);
            filterArray.put(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
