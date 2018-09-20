package com.steezle.e_com.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.steezle.e_com.adapter.CategoryAdapter;
import com.steezle.e_com.model.CategoryModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.CategoryService;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.view.LoginActivity;
import com.steezle.e_com.view.ShoppigbagActivity;
import com.steezle.e_com.view.SpacesItemDecoration;

public class CategoryFragment extends Fragment implements APIs, View.OnClickListener {

    public static final String TAG = CategoryFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView rec_categoryList;
    private ArrayList<CategoryModel> feedsList;
    private int i;
    private TextView tv_favourite;
    private ImageView iv_favourite;
    private Context context;
    private User user;
    private CategoryAdapter adapter;
    private RequestQueue requestQueue;
    //public static final String mypreference = "mypref";
    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        Log.e( TAG,"Inside onResume" );
        /*try {
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
            tv_favourite.setText(sharedpreferences.getString( "cart", "" ) );
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        tv_favourite.setText(AppSharedPreferences.getSharePreference(getActivity()).getCartCount());

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_category, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());

        //Initlization
        setUpView(v);
        user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
        fillCategoryAdapter();
        try{
            if (user.getUser_id().length()==0){
                getCategories("");
            }
            else
                getCategories(user.getUser_id());

        }catch (Exception e){
            e.printStackTrace();
            getCategories("");
        }


        //Register Button Click Event
        iv_favourite.setOnClickListener(this);


        return v;

    }


    private void setUpView(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        rec_categoryList = (RecyclerView) view.findViewById(R.id.rec_categoryList);
        tv_favourite = (TextView) view.findViewById(R.id.tv_favourite);
        iv_favourite = (ImageView) view.findViewById(R.id.iv_favourite);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_favourite:
                try{
                    if (user.getUser_id().equalsIgnoreCase( "" )){
                            openDialog();
                    }
                    else{
                        Intent i = new Intent(getActivity(), ShoppigbagActivity.class);
                        startActivity(i);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    openDialog();
                }
                break;
        }

    }


    private void fillCategoryAdapter() {

        feedsList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        rec_categoryList.setLayoutManager(manager);
        rec_categoryList.setHasFixedSize(true);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._1sdp);
        rec_categoryList.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new CategoryAdapter(getActivity(), feedsList);
        rec_categoryList.setAdapter(adapter);
    }


    private void getCategories(String userId) {

        //progressBar.setVisibility(View.VISIBLE);
        CategoryService.getCategory(
                getActivity(),
                userId,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {

                        Log.e(TAG, "CategoryResponse--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString(Constant.Categoty.STATUS).equals("S")) {

                                    tv_favourite.setText(obj.getString(Constant.Categoty.BAG_COUNT));

                                    JSONArray jarrary = obj.getJSONArray(Constant.Categoty.DATA);

                                    for (int i = 0; i < jarrary.length(); i++) {
                                        //gets each JSON object within the JSON array
                                        JSONObject jsonObject = jarrary.getJSONObject(i);

                                        CategoryModel hero = new CategoryModel(
                                                jsonObject.getString(Constant.Categoty.NAME),
                                                jsonObject.getString(Constant.Categoty.ID),
                                                jsonObject.getString(Constant.Categoty.IMAGE));

                                        feedsList.add(hero);
                                    }

                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(getActivity(), obj.getString(Constant.Categoty.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            } finally {
                                adapter.notifyItemChanged(i);
                            }

                        }
                    }
                }
        );
    }
    public void openDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder( getActivity(),R.style.Theme_AppCompat_Light_Dialog_Alert );
        builder.setTitle( "Steezle" );
        builder.setMessage( "Sign in or Sign Up to add products to Bag" );
        builder.setCancelable( false );
        builder.setPositiveButton( "Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Call Login screen
                Intent i=new Intent( getActivity(), LoginActivity.class );
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




