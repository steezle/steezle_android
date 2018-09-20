package com.steezle.e_com.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.steezle.e_com.adapter.MysteezleAdapter;

import com.steezle.e_com.model.GetfvtModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.AddToCartService;
import com.steezle.e_com.services.GetFavouriteService;
import com.steezle.e_com.services.RemoveFavouriteService;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.view.ProductDetailActivity;
import com.steezle.e_com.view.ShoppigbagActivity;
import com.steezle.e_com.view.TabActivity;

public class FavouriteFragment extends Fragment implements APIs, MysteezleAdapter.OnRemoveFromFavorite,
        MysteezleAdapter.OnAddToBag,  View.OnClickListener, MysteezleAdapter.OnProductDetails {

    public static final String TAG = FavouriteFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView rec_emptyList;
    private ArrayList<GetfvtModel> feedsList;
    private ImageView back_click_clothing;
    private int i;
    private Context context;
    private MysteezleAdapter adapter;
    private User user;
    private RequestQueue requestQueue;
    private ImageView iv_empty_favourite;
    private ImageView iv_favourite;
    private ImageView iv_userIcon;
    private TextView tv_favourite;
    private int bagCount = 0;

    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.act_mysteez, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());

        //Initlization
        setUpview(v);
        user = AppSharedPreferences.getSharePreference(getActivity()).getUser();

        fillMySteezleAdapter();
        //fillMySteezleAdapter();
        //getMySteez();


        //Register Button Click Event
        iv_userIcon.setOnClickListener(this);
        iv_favourite.setOnClickListener(this);
        back_click_clothing.setOnClickListener(this);


        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        //fillMySteezleAdapter();
        Log.e( "Cart count",""+AppSharedPreferences.getSharePreference(getActivity()).getCartCount() );
        tv_favourite.setText(AppSharedPreferences.getSharePreference(getActivity()).getCartCount());
        user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
        try{
            if (user.getUser_id().length()==0)
            {
                getMySteez("");
            }
            else
                getMySteez( user.getUser_id());

        }catch (Exception e){
            e.printStackTrace();
            getMySteez("");
        }
    }

    private void setUpview(View view) {

        back_click_clothing = (ImageView) view.findViewById(R.id.back_click_clothing);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        rec_emptyList = (RecyclerView) view.findViewById(R.id.rec_emptyList);
        tv_favourite = (TextView) view.findViewById(R.id.tv_favourite);
        iv_favourite = (ImageView) view.findViewById(R.id.iv_favourite);
        iv_userIcon = (ImageView) view.findViewById(R.id.iv_userIcon);
        iv_empty_favourite = (ImageView) view.findViewById(R.id.iv_empty_favourite);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click_clothing:
                CategoryFragment categoryFragment = new CategoryFragment();
                ((TabActivity) getActivity()).loadFragment(categoryFragment, false, "");
                break;

            case R.id.iv_favourite:
                Intent i = new Intent(getActivity(), ShoppigbagActivity.class);
                startActivity(i);
                break;

            case R.id.iv_userIcon:
                Intent ii = new Intent(getActivity(), TabActivity.class);
                ii.putExtra("From", "user");
                startActivity(ii);
                break;
        }
    }


    private void fillMySteezleAdapter() {

        feedsList = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rec_emptyList.setLayoutManager(manager);
        DividerItemDecoration divider = new DividerItemDecoration(rec_emptyList.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line));
        rec_emptyList.addItemDecoration(divider);

        adapter = new MysteezleAdapter(
                feedsList,
                getActivity(),
                FavouriteFragment.this,
                FavouriteFragment.this,
                FavouriteFragment.this);
        rec_emptyList.setAdapter(adapter);

    }


    private void getMySteez(String userId) {

        if(feedsList != null)
            feedsList.clear();

        //progressBar.setVisibility(View.VISIBLE);
        GetFavouriteService.getFavList(
                getActivity(),
               userId,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "GetFavResponse--->" + response);

                        if (response != null) {

                            try {

                                JSONObject object = new JSONObject(response);

                                tv_favourite.setText(object.getString(Constant.GetFavourite.BAG_COUNT));

                                if (object.getString(Constant.GetFavourite.STATUS).equals("S")) {

                                    //bagCount = object.getInt(Constant.GetFavourite.BAG_COUNT);

                                    JSONArray jarrary = object.getJSONArray(Constant.GetFavourite.DATA);

                                    for (int i = 0; i < jarrary.length(); i++) {

                                        JSONObject jsonObject = jarrary.getJSONObject(i);

                                        GetfvtModel hero = new GetfvtModel(
                                                jsonObject.getString(Constant.GetFavourite.PRODUCT_ID),
                                                jsonObject.getString(Constant.GetFavourite.PRODUCT_IMAGE),
                                                jsonObject.getString(Constant.GetFavourite.PRODUCT_NAME),
                                                jsonObject.getString(Constant.GetFavourite.BRAND),
                                                jsonObject.getString(Constant.GetFavourite.PRODUCT_TYPE),
                                                jsonObject.getString(Constant.GetFavourite.PRODUCT_PRICE));

                                        feedsList.add(hero);
                                    }

                                    if (feedsList.size() > 0) {
                                        iv_empty_favourite.setVisibility(View.GONE);
                                    } else {
                                        iv_empty_favourite.setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                } else if (object.getString(Constant.GetFavourite.STATUS).equals("W")) {

                                    progressBar.setVisibility(View.GONE);
                                    iv_empty_favourite.setVisibility(View.VISIBLE);

                                } else {

                                    Toast.makeText(getActivity(), object.getString(Constant.GetFavourite.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                }

        );

    }


    @Override
    public void onRemoveClick(final GetfvtModel getfvtModel, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to remove this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeFavourite(getfvtModel, pos);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void removeFavourite(final GetfvtModel getfvtModel, final int pos) {

        //progressBar.setVisibility(View.VISIBLE);
        RemoveFavouriteService.removeFavourite(
                getActivity(),
                user.getUser_id(),
                getfvtModel.getProduct_id(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "RemoveFavResponse--->" + response);

                        if (response != null) {

                            try {
                                JSONObject obj = new JSONObject(response);
                                tv_favourite.setText(obj.getString(Constant.GetFavourite.BAG_COUNT));

                                if (obj.getString(Constant.RemoveFavourite.STATUS).equals("S")) {

                                    feedsList.remove(pos);

                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                } else {

                                    Toast.makeText(getActivity(), obj.getString(Constant.RemoveFavourite.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                                if (feedsList.size() > 0) {
                                    iv_empty_favourite.setVisibility(View.GONE);
                                } else {
                                    iv_empty_favourite.setVisibility(View.VISIBLE);
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
    public void onAddToBagClick(GetfvtModel getfvtModel, int pos) throws JSONException {

        JSONArray array1 = new JSONArray();
        JSONObject object3 = new JSONObject();
        object3.put("product_id", getfvtModel.getProduct_id());
        object3.put("qty", "1");

        array1.put(object3).toString();

        if (getfvtModel.getProduct_type().equals("simple")) {

            addToCart(array1.toString(), pos);

        } else {

            Intent i = new Intent(getActivity(), ProductDetailActivity.class);
            i.putExtra(Constant.ProductDetail.PRODUCT_ID, getfvtModel.getProduct_id());
            i.putExtra(Constant.GETFVTMODEL, getfvtModel);
            startActivity(i);
        }

    }


    private void addToCart(final String variationId, final int pos) {

        //progressBar.setVisibility(View.VISIBLE);
        AddToCartService.addToCart(
                getActivity(),
                user.getUser_id(),
                variationId,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "AddToCartResponse--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                String data = obj.getString(Constant.AddToCart.DATA);

                                tv_favourite.setText(obj.getString(Constant.AddToCart.BAG_COUNT));

                                if (obj.getString(Constant.AddToCart.STATUS).equals("S")) {

                                    feedsList.remove(pos);
                                    adapter.notifyDataSetChanged();

                                    final Dialog alertDialog = new Dialog(getActivity());
                                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    alertDialog.setContentView(R.layout.view_dialog_addbag);
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    alertDialog.show();

                                    new Handler(  ).postDelayed( new Runnable() {
                                        @Override
                                        public void run() {

                                            alertDialog.dismiss();
                                        }
                                    },1000 );
//                                    Toast.makeText(getActivity(), obj.getString(Constant.AddToCart.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(getActivity(), obj.getString(Constant.AddToCart.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                                if (feedsList.size() > 0) {
                                    iv_empty_favourite.setVisibility(View.GONE);
                                } else {
                                    iv_empty_favourite.setVisibility(View.VISIBLE);
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
    public void onProductDetailClick(GetfvtModel getfvtModel, int pos) {

        Intent i = new Intent(getActivity(), ProductDetailActivity.class);
        i.putExtra(Constant.ProductDetail.PRODUCT_ID, getfvtModel.getProduct_id());
        i.putExtra(Constant.GETFVTMODEL, getfvtModel);
        startActivity(i);
    }
}




 /* private void removeFavorite(final GetfvtModel getfvtModel, final int pos) {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.remove_fvt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("S")) {
                                feedsList.remove(pos);
                                rec_emptyList.setAdapter(adapter);
                            }

                            Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            //Notify com.steezle.e_com.adapter about data changes
                            adapter.notifyItemChanged(i);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
                params.put("product_id", getfvtModel.getProduct_id());
                params.put("user_id", user.getUser_id());

                return params;
            }

        };

        VolleySingleton.getInstance(getActivity());
        requestQueue.add(stringRequest);

    }*/


   /* private void addTocart(final String variationId) {


        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            String data = obj.getString("data");

                            if (obj.getString("status").equals("S")) {

                                Dialog alertDialog = new Dialog(getActivity());
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                alertDialog.setContentView(R.layout.view_dialog_addbag);
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertDialog.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }

                })

        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
                JSONArray array = new JSONArray();
                params.put("user_id", user.getUser_id());
                params.put("cart_products", variationId);

                return params;
            }

        };

        stringRequest.setShouldCache(false);
        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(getActivity());
        requestQueue.add(stringRequest);

    }*/



 /* private void loadHeroes() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_fvt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            tv_favourite.setText(obj.getString("bag_count"));
                            if (obj.getString("status").equals("S")) {
                                JSONArray jarrary = obj.getJSONArray("data");


                                for (int i = 0; i < jarrary.length(); i++) {
                                    //gets each JSON object within the JSON array
                                    JSONObject jsonObject = jarrary.getJSONObject(i);
                                    GetfvtModel hero = new GetfvtModel(
                                            jsonObject.getString("product_id"),
                                            jsonObject.getString("product_image"),
                                            jsonObject.getString("product_name"),
                                            jsonObject.getString("brand"),
                                            jsonObject.getString("product_type"),
                                            jsonObject.getString("product_price"));
                                    feedsList.add(hero);
                                }


                                adapter = new MysteezleAdapter(
                                        feedsList,
                                        getActivity(),
                                        FavouriteFragment.this, FavouriteFragment.this);
                                rec_emptyList.setAdapter(adapter);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                rec_emptyList.setLayoutManager(manager);

                                if (feedsList.size() > 0)
                                    iv_empty_favourite.setVisibility(View.GONE);
                                else
                                    iv_empty_favourite.setVisibility(View.VISIBLE);

                            } else if (obj.getString("status").equals("W")) {

                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            //Notify com.steezle.e_com.adapter about data changes
                            adapter.notifyItemChanged(i);
                        }
                        hidepDialog();
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
                user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
                params.put("user_id", user.getUser_id());
                return params;
            }

        };

        stringRequest.setShouldCache(false);
        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(getActivity());
        requestQueue.add(stringRequest);

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/
