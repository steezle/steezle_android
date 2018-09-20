package com.steezle.e_com.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.text.Line;
import com.steezle.e_com.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import com.steezle.e_com.adapter.MysteezlefAdapter;
import com.steezle.e_com.model.GetfvtModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.AddToCartService;
import com.steezle.e_com.services.GetFavouriteService;
import com.steezle.e_com.services.RemoveFavouriteService;
import com.steezle.e_com.utils.Constant;

public class MysteezActivity extends AppCompatActivity implements APIs,
        MysteezlefAdapter.OnRemoveFromFavorite,
        MysteezlefAdapter.OnAddToBag, View.OnClickListener {

    public static final String TAG = MysteezActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private ImageView back_click_clothing;
    private RecyclerView rec_viewList;
    private ArrayList<GetfvtModel> feedsList;
    private User user;
    private int i;
    private ImageView iv_favourite;
    private Context context;
    private ProgressDialog pDialog;
    private MysteezlefAdapter adapter;
    private RequestQueue requestQueue;
    private String variationId;
    private TextView tv_favourite, txtcart;
    private ImageView iv_empty_favourite;
    private ImageView iv_userPic;
    LinearLayout linearLayout2;

    //public SharedPreferences sharedpreferences;
    //public SharedPreferences.Editor editor;
    //public static final String mypreference = "mypref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mysteez_swipe);
        requestQueue = Volley.newRequestQueue(this);
        //sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        //Initlization
        setUpView();
        user = AppSharedPreferences.getSharePreference(this).getUser();
        fillMySteezAdapter();
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



        //Register Button Click Event
        tv_favourite.setOnClickListener(this);
        linearLayout2.setOnClickListener(this);
        iv_userPic.setOnClickListener(this);
        back_click_clothing.setOnClickListener(this);

    }


    private void setUpView()
    {
        Log.e( "Inside",""+TAG );
        linearLayout2=(LinearLayout)findViewById( R.id.linearLayout2 );
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        back_click_clothing = (ImageView) findViewById(R.id.back_click_clothing);
        rec_viewList = (RecyclerView) findViewById(R.id.rec_viewList);
        iv_empty_favourite = (ImageView) findViewById(R.id.iv_empty_favourite);
        tv_favourite = (TextView) findViewById(R.id.tv_favourite);
        iv_favourite = (ImageView) findViewById(R.id.iv_favourite);
        iv_userPic = (ImageView) findViewById(R.id.iv_userPic);
        back_click_clothing.setVisibility( View.VISIBLE );
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click_clothing:
                finish();
                break;

            case R.id.linearLayout2:
                Log.e( "clicked","linearlayout2" );
                Intent i = new Intent(MysteezActivity.this, ShoppigbagActivity.class);
                startActivity(i);
                break;

            case R.id.iv_userPic:
                Intent intent = new Intent(MysteezActivity.this, TabActivity.class);
                intent.putExtra("From", "user");
                startActivity(intent);
                break;



        }
    }


    private void fillMySteezAdapter() {

        feedsList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(MysteezActivity.this, 1, GridLayoutManager.VERTICAL, false);
        rec_viewList.setLayoutManager(manager);
        rec_viewList.setHasFixedSize(true);
        DividerItemDecoration divider = new DividerItemDecoration(rec_viewList.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(MysteezActivity.this, R.drawable.line));
        rec_viewList.addItemDecoration(divider);
        adapter = new MysteezlefAdapter(
                feedsList,
                MysteezActivity.this,
                MysteezActivity.this, MysteezActivity.this);
        rec_viewList.setAdapter(adapter);

    }


    private void getMySteez(String userId) {

        user = AppSharedPreferences.getSharePreference(this).getUser();
        Log.e( "USER ID,MysteezActivity","" + user.getUser_id() );
        //progressBar.setVisibility(View.VISIBLE);
        GetFavouriteService.getFavList(
                this,
                userId,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "MySteezRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString(Constant.MySteezSwipe.STATUS).equals("S")) {

                                    tv_favourite.setText(obj.getString(Constant.MySteezSwipe.BAG_COUNT));

                                    //AppSharedPreferences.getSharePreference(MysteezActivity.this).setCartCount(obj.getString( "bag_count" ));
                                    //AppSharedPreferences.getSharePreference(MysteezActivity.this).setTotalSteez(obj.getString( "total_steez"));
                                   /* try {
                                        editor = sharedpreferences.edit();
                                        editor.putString("total_steez", obj.getString( "total_steez" ));
                                        editor.putString("cart", obj.getString( "bag_count" ));
                                        editor.commit();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }*/
                                    feedsList.clear();

                                    JSONArray jarrary = obj.getJSONArray(Constant.MySteezSwipe.DATA);

                                    for (int i = 0; i < jarrary.length(); i++) {
                                        //gets each JSON object within the JSON array
                                        JSONObject jsonObject = jarrary.getJSONObject(i);

                                        GetfvtModel hero = new GetfvtModel(

                                                jsonObject.getString(Constant.MySteezSwipe.PRODUCT_ID),
                                                jsonObject.getString(Constant.MySteezSwipe.PRODUCT_IMAGE),
                                                jsonObject.getString(Constant.MySteezSwipe.PRODUCT_NAME),
                                                jsonObject.getString(Constant.MySteezSwipe.BRAND),
                                                jsonObject.getString(Constant.MySteezSwipe.PRODUCT_TYPE),
                                                jsonObject.getString(Constant.MySteezSwipe.PRODUCT_PRICE));

                                        feedsList.add(hero);

                                    }

                                    if (feedsList.size() > 0) {
                                        iv_empty_favourite.setVisibility(View.GONE);
                                    } else {
                                        iv_empty_favourite.setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                } else if (obj.getString(Constant.MySteezSwipe.STATUS).equals("W")) {

                                    progressBar.setVisibility(View.GONE);
                                    iv_empty_favourite.setVisibility(View.VISIBLE);

                                } else {

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.e( "Cart count",""+AppSharedPreferences.getSharePreference(getApplicationContext()).getCartCount() );
        tv_favourite.setText(AppSharedPreferences.getSharePreference(getApplicationContext()).getCartCount());

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

   /* private void loadHeroes() {

        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_fvt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            tv_favourite.setText(obj.getString("total_cart"));

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
                            adapter = new MysteezlefAdapter(
                                    feedsList,
                                    MysteezActivity.this,
                                    MysteezActivity.this, MysteezActivity.this);
                            rec_viewList.setAdapter(adapter);
                            GridLayoutManager manager = new GridLayoutManager(MysteezActivity.this, 1, GridLayoutManager.VERTICAL, false);
                            rec_viewList.setLayoutManager(manager);

                            if (feedsList.size() > 0)
                                iv_empty_favourite.setVisibility(View.GONE);
                            else
                                iv_empty_favourite.setVisibility(View.VISIBLE);

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
                        Toast.makeText(MysteezActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        hidepDialog();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                user = AppSharedPreferences.getSharePreference(MysteezActivity.this).getUser();
                params.put("user_id", user.getUser_id());

                return params;
            }

        };

        VolleySingleton.getInstance(this);
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



    @Override
    public void onRemoveClick(final GetfvtModel getfvtModel, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MysteezActivity.this);
        builder.setMessage("Do you want to remove this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeFavourites(getfvtModel, pos);
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



    private void removeFavourites(final GetfvtModel getfvtModel, final int pos) {

        //progressBar.setVisibility(View.VISIBLE);
        RemoveFavouriteService.removeFavourite(
                this,
                user.getUser_id(),
                getfvtModel.getProduct_id(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "RemoveFavRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);


                                if (obj.getString("status").equals("S")) {

                                    tv_favourite.setText(obj.getString(Constant.GetFavourite.BAG_COUNT));
                                    feedsList.remove(pos);
                                    AppSharedPreferences.getSharePreference(MysteezActivity.this).setCartCount(obj.getString("bag_count"));
                                    AppSharedPreferences.getSharePreference(MysteezActivity.this).setTotalSteez(obj.getString("total_steez"));

                                    /*try {
                                        editor = sharedpreferences.edit();
                                        editor.putString("total_steez", obj.getString( "total_steez" ));
                                        editor.putString("cart", obj.getString( "bag_count" ));
                                        editor.commit();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }*/
                                    adapter.notifyDataSetChanged();
//                                    progressBar.setVisibility(View.GONE);
                                }

//                                Toast.makeText(MysteezActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                if (feedsList.size() > 0) {
                                    iv_empty_favourite.setVisibility(View.GONE);
                                } else {
                                    iv_empty_favourite.setVisibility(View.VISIBLE);
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



    /*private void removeFavorite(final GetfvtModel getfvtModel, final int pos) {

        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.remove_fvt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("S")) {
                                feedsList.remove(pos);
                                rec_viewList.setAdapter(adapter);
                            }

                            Toast.makeText(MysteezActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();

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
                        Toast.makeText(MysteezActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        hidepDialog();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                user = AppSharedPreferences.getSharePreference(MysteezActivity.this).getUser();
                params.put("product_id", getfvtModel.getProduct_id());
                params.put("user_id", user.getUser_id());

                return params;
            }

        };

        VolleySingleton.getInstance(MysteezActivity.this);
        requestQueue.add(stringRequest);

    }*/


    @Override
    public void onAddToBagClick(GetfvtModel getfvtModel, int pos) throws JSONException {


        JSONArray array1 = new JSONArray();
        JSONObject object3 = new JSONObject();
        object3.put("product_id", getfvtModel.getProduct_id());
        object3.put("qty", "1");


        array1.put(object3).toString();

        if (getfvtModel.getProduct_type().equals("simple")) {

            addCart(array1.toString(), pos);

        } else {

            Intent i = new Intent(MysteezActivity.this, ProductDetailActivity.class);
            i.putExtra(Constant.ProductDetail.PRODUCT_ID, getfvtModel.getProduct_id());
            startActivity(i);
        }

    }



    private void addCart(final String variationId, final int pos) {

        //progressBar.setVisibility(View.VISIBLE);
        AddToCartService.addToCart(
                this,
                user.getUser_id(),
                variationId,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "AddCartRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                String data = obj.getString("data");

                                if (obj.getString("status").equals("S")) {

                                    tv_favourite.setText(obj.getString(Constant.AddToCart.BAG_COUNT));
                                    progressBar.setVisibility(View.GONE);

                                    try{
                                        AppSharedPreferences.getSharePreference(MysteezActivity.this).setCartCount(obj.getString("bag_count"));
                                        AppSharedPreferences.getSharePreference(MysteezActivity.this).setTotalSteez(obj.getString("total_steez"));

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    feedsList.remove(pos);
                                    adapter.notifyDataSetChanged();

                                    final Dialog alertDialog = new Dialog(MysteezActivity.this);
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

                                } else {

                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                                if (feedsList.size() > 0) {
                                    iv_empty_favourite.setVisibility(View.GONE);
                                } else {
                                    iv_empty_favourite.setVisibility(View.VISIBLE);
                                }

                            } catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }
                    }
                }

        );
    }



    /*private void addTocart(final String variationId) {


        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            String data = obj.getString("data");

                            if (obj.getString("status").equals("S")) {

                                Dialog alertDialog = new Dialog(MysteezActivity.this);
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                alertDialog.setContentView(R.layout.view_dialog_addbag);
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertDialog.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MysteezActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        hidepDialog();
                    }

                })

        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                user = AppSharedPreferences.getSharePreference(MysteezActivity.this).getUser();
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
        VolleySingleton.getInstance(MysteezActivity.this);
        requestQueue.add(stringRequest);

    }*/


}
