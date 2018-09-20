package com.steezle.e_com.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.SubCategoryExpandableListAdapter;
import com.steezle.e_com.model.SubCategoryItem;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.SubCategoryService;
import com.steezle.e_com.utils.EndlessScrollListener;
import com.steezle.e_com.utils.ProjectUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubCategory extends AppCompatActivity implements APIs, View.OnClickListener {

    public static final String TAG = SubCategory.class.getSimpleName();

    private ProgressBar progressBar;
    private TextView tv_favourite;
    private TextView tv_title;
    private ImageView iv_userPic;
    private ImageView iv_subcat_back;
    private ImageView iv_favourite;
    private User user;
    private SubCategoryExpandableListAdapter subCategoryExpandableListAdapter;
    private ExpandableListView lv_subCategory;
    private List<SubCategoryItem> listDataHeader;
    private HashMap<String, List<SubCategoryItem>> listDataChild;
    private int page = 1, totalPage;
    private String catName;
    private String catId;

    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.act_sub_category );
        //Initlization
        setUpView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        lv_subCategory.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));

        user = AppSharedPreferences.getSharePreference(getApplicationContext()).getUser();
        intent=getIntent();
        catName=intent.getStringExtra( "categoryname");
        String cName = ProjectUtility.toCamelCaseWord(catName);
        tv_title.setText(cName);
        catId = intent.getStringExtra("categoryid");
        fillSubCategoryAdapter();
        try{
            if (user.getUser_id().length()==0){
                getSubCategories(page,"");
            }
            else
                getSubCategories(page, user.getUser_id());

        }catch (Exception e){
            e.printStackTrace();
            getSubCategories(page,"");
        }
        //Register Button Click Event
        iv_userPic.setOnClickListener(this);
        iv_subcat_back.setOnClickListener(this);
        iv_favourite.setOnClickListener(this);

    }

    private void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        iv_userPic = (ImageView) findViewById(R.id.iv_userPic);
        iv_subcat_back = (ImageView) findViewById(R.id.iv_subcat_back);
        lv_subCategory = (ExpandableListView) findViewById(R.id.lv_subCategory);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_favourite = (ImageView) findViewById(R.id.iv_favourite);
        tv_favourite = (TextView) findViewById(R.id.tv_favourite);

    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_subcat_back:
                finish();
                break;

            case R.id.iv_userPic:
                try{
                    if (user.getUser_id().equalsIgnoreCase( "" )){
                        openDialog("go to Profile Page");
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                        intent.putExtra("From", "user");
                        startActivity(intent);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    openDialog("go to Profile Page");
                }
                break;

            case R.id.iv_favourite:
                try{
                    if (user.getUser_id().equalsIgnoreCase( "" )){
                        openDialog("add products to Bag");
                    }
                    else {
                        Intent intent1 = new Intent(getApplicationContext(), ShoppigbagActivity.class);
                        startActivity(intent1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    openDialog("add products to Bag");
                }
                break;
        }

    }
    private void fillSubCategoryAdapter() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        subCategoryExpandableListAdapter = new SubCategoryExpandableListAdapter(
                SubCategory.this,
                listDataHeader,
                listDataChild);

        lv_subCategory.setAdapter(subCategoryExpandableListAdapter);
        lv_subCategory.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (page <= totalPage){
                    try{
                        if (user.getUser_id().length()==0){
                            getSubCategories(page,"");
                        }
                        else
                            getSubCategories(page, user.getUser_id());

                    }catch (Exception e){
                        e.printStackTrace();
                        getSubCategories(page,"");
                    }
                }
                return true;
            }
        });
    }
    private void getSubCategories(int pages,String userId) {
        //progressBar.setVisibility(View.VISIBLE);
        SubCategoryService.getSubCategory(
                SubCategory.this,
                catId,
                userId,
                String.valueOf(pages),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "SubCategoriesRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                tv_favourite.setText(obj.getString("bag_count"));

                                JSONArray jarrary = obj.getJSONArray("data");

                                totalPage = obj.getInt("pages");

                                for (int i = 0; i < jarrary.length(); i++) {

                                    List<SubCategoryItem> subCategoryItems = new ArrayList<>();

                                    SubCategoryItem subCategoryHeaderItem = new SubCategoryItem();

                                    JSONObject jsonObject = jarrary.getJSONObject(i);
                                    subCategoryHeaderItem.setId(jsonObject.getString("id"));
                                    subCategoryHeaderItem.setImage(jsonObject.getString("image"));
                                    subCategoryHeaderItem.setName(jsonObject.getString("name"));
                                    JSONArray subArray = jsonObject.getJSONArray("sub_categories");

                                    listDataHeader.add(subCategoryHeaderItem);

                                    for (int j = 0; j < subArray.length(); j++) {

                                        JSONObject subObject = subArray.getJSONObject(j);

                                        SubCategoryItem subCategoryItem = new SubCategoryItem();

                                        subCategoryItem.setSubId(subObject.getString("id"));
                                        subCategoryItem.setSubName(subObject.getString("name"));
                                        subCategoryItem.setSubImage(subObject.getString("image"));
                                        subCategoryItems.add(subCategoryItem);
                                    }

                                    listDataChild.put(subCategoryHeaderItem.getId(), subCategoryItems);
                                }

                                subCategoryExpandableListAdapter.notifyDataSetChanged();
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
    @Override
    public void onResume() {
        super.onResume();
        Log.e( TAG,"Inside onResume" );
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        tv_favourite.setText(AppSharedPreferences.getSharePreference(getApplicationContext()).getCartCount());
    }

    public void openDialog(String text){
        AlertDialog.Builder builder=new AlertDialog.Builder( SubCategory.this,R.style.Theme_AppCompat_Light_Dialog_Alert );
        builder.setTitle( "Steezle" );
        builder.setMessage( "Sign in or Sign Up to "+text );
        builder.setCancelable( false );
        builder.setPositiveButton( "Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Call Login screen
                Intent i=new Intent( SubCategory.this, LoginActivity.class );
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


