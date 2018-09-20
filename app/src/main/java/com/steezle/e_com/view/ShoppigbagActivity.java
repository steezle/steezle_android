package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;

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

import com.steezle.e_com.adapter.GetCartAdapter;
import com.steezle.e_com.fragments.CategoryFragment;
import com.steezle.e_com.model.GetcartModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.GetCartListService;
import com.steezle.e_com.services.RemoveCartService;
import com.steezle.e_com.utils.Constant;

public class ShoppigbagActivity extends AppCompatActivity implements APIs, GetCartAdapter.OnRemoveFromShopping,
        View.OnClickListener, GetCartAdapter.OnProductDetail {

    public static final String TAG = ShoppigbagActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    private ImageView back_click_clothing;
    private Intent intent;
    private ArrayList<GetcartModel> getCardArrayList;
    private GetCartAdapter adapter;
    private RequestQueue requestQueue;
    private User user;
    private RelativeLayout rl_btn_checkOut;
    private int i;
    private TextView tv_totalAmount, tv_favourite;
    private ImageView iv_empty_favourite;
    private List<NameValuePair> params11;
    private RecyclerView rec_getCartList;
    private LinearLayout ll_emptyShoppingBag, ll_btn_shopNow, ll_checkOut, ll_progress;

    /*public SharedPreferences sharedpreferences;
    public SharedPreferences.Editor editor;
    public static final String mypreference = "mypref";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myshoppingbag);
        requestQueue = Volley.newRequestQueue(this);
        //sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        //Initlization
        setUpView();
        user = AppSharedPreferences.getSharePreference(this).getUser();
        fillGetCartAdapter();
//        getCartList();


        //Register Button Click Event
        back_click_clothing.setOnClickListener(this);
        rl_btn_checkOut.setOnClickListener(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e( "Cart count",""+AppSharedPreferences.getSharePreference(getApplicationContext()).getCartCount() );
        getCartList();
        tv_favourite.setText(AppSharedPreferences.getSharePreference(getApplicationContext()).getCartCount());
    }
    private void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rec_getCartList = (RecyclerView) findViewById(R.id.rec_getCartList);
        back_click_clothing = (ImageView) findViewById(R.id.back_click_clothing);
        rl_btn_checkOut = (RelativeLayout) findViewById(R.id.rl_btn_checkOut);
        tv_totalAmount = (TextView) findViewById(R.id.tv_totalAmount);
        iv_empty_favourite = (ImageView) findViewById(R.id.iv_empty_favourite);
        tv_favourite = (TextView) findViewById(R.id.tv_favourite);

        ll_emptyShoppingBag = (LinearLayout) findViewById(R.id.ll_emptyShoppingBag);
        ll_btn_shopNow = (LinearLayout) findViewById(R.id.ll_btn_shopNow);
        ll_checkOut = (LinearLayout) findViewById(R.id.ll_checkOut);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click_clothing:
                finish();
                break;

            case R.id.rl_btn_checkOut:

                JSONArray array = new JSONArray();
                for (int j = 0; j < getCardArrayList.size(); j++) {

                    GetcartModel getcartModel = getCardArrayList.get(j);

                    try {
                        JSONObject object = new JSONObject();
                        object.put(Constant.CheckOut.PRODUCT_ID, getcartModel.getProduct_id());
                        object.put(Constant.CheckOut.VARIATION_ID, getcartModel.getVariation_id()/*"0"*/);
                        object.put(Constant.CheckOut.QUANTITY, getcartModel.getQty());

                        array.put(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(ShoppigbagActivity.this, BillingaddressActivty.class);
                intent.putExtra("CartProductArray", array.toString());
                startActivity(intent);
                break;
        }

    }


    private void fillGetCartAdapter() {

        getCardArrayList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(ShoppigbagActivity.this, 1, GridLayoutManager.VERTICAL, false);
        rec_getCartList.setLayoutManager(manager);
        rec_getCartList.setHasFixedSize(true);
        DividerItemDecoration divider = new DividerItemDecoration(rec_getCartList.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(ShoppigbagActivity.this, R.drawable.line));
        rec_getCartList.addItemDecoration(divider);

        adapter = new GetCartAdapter(getCardArrayList,
                ShoppigbagActivity.this,
                ShoppigbagActivity.this,
                tv_favourite,
                tv_totalAmount,
                ShoppigbagActivity.this);
        rec_getCartList.setAdapter(adapter);

    }


    private void getCartList() {

        if(getCardArrayList != null )
            getCardArrayList.clear();

        //progressBar.setVisibility(View.VISIBLE);
        GetCartListService.getCartList(
                this,
                user.getUser_id(),
                progressBar,
                new APIService.Success<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {

                        Log.e(TAG, "GetCartListResponse--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                AppSharedPreferences.getSharePreference(ShoppigbagActivity.this).setCartCount(obj.getString("bag_count"));
                                AppSharedPreferences.getSharePreference(ShoppigbagActivity.this).setTotalSteez(obj.getString("total_steez"));

                                /*try {
                                    editor = sharedpreferences.edit();
                                    editor.putString("total_steez", obj.getString( "total_steez" ));
                                    editor.putString("cart", obj.getString( "bag_count" ));
                                    editor.commit();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }*/

                                if (obj.getString(Constant.GetCartList.STATUS).equals("S")) {

                                    ll_checkOut.setVisibility(View.VISIBLE);
                                    JSONObject data = obj.getJSONObject(Constant.GetCartList.DATA);

                                    String totals = data.getString(Constant.GetCartList.CART_SUBTOTAL);

                                    tv_favourite.setText(obj.getString(Constant.GetCartList.BAG_COUNT));

                                    tv_totalAmount.setText("$" + String.format("%.2f", Double.parseDouble(totals)));
                                    JSONArray cartArray = data.getJSONArray(Constant.GetCartList.CART);
                                    List<String> list=new ArrayList<>(  );
                                    JSONObject jsonObject = null;
                                    for (int i = 0; i < cartArray.length(); i++) {
                                        try{
                                            list=new ArrayList<>(  );
                                            jsonObject = cartArray.getJSONObject(i);
                                            JSONArray array=jsonObject.getJSONArray( "variations" );
                                            for (int j=0; j<array.length();j++){
                                                JSONObject object=array.getJSONObject( j );
                                                list.add( object.getString( "value" ) );
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        try{
                                            Log.e( "list size",""+list.size() );
                                            GetcartModel getcartModel = new GetcartModel(list,
                                                    jsonObject.getString(Constant.GetCartList.PRODUCT_ID),
                                                    jsonObject.getString(Constant.GetCartList.PRODUCT_TITLE),
                                                    jsonObject.getString(Constant.GetCartList.BRAND),
                                                    jsonObject.getString(Constant.GetCartList.QUANTITY),
                                                    jsonObject.getString(Constant.GetCartList.PRICE),
                                                    jsonObject.getString(Constant.GetCartList.PRODUCT_IMAGE),
                                                    jsonObject.getString("variation_id"));
                                            getCardArrayList.add(getcartModel);

                                        }catch (Exception e){
                                            e.printStackTrace();
                                            GetcartModel getcartModel = new GetcartModel(list,
                                                    jsonObject.getString(Constant.GetCartList.PRODUCT_ID),
                                                    jsonObject.getString(Constant.GetCartList.PRODUCT_TITLE),
                                                    jsonObject.getString(Constant.GetCartList.BRAND),
                                                    jsonObject.getString(Constant.GetCartList.QUANTITY),
                                                    jsonObject.getString(Constant.GetCartList.PRICE),
                                                    jsonObject.getString(Constant.GetCartList.PRODUCT_IMAGE),
                                                    jsonObject.getString("variation_id"));
                                            getCardArrayList.add(getcartModel);

                                        }


                                    }

                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    if (getCardArrayList.size() > 0) {
                                        ll_emptyShoppingBag.setVisibility(View.GONE);
                                        ll_checkOut.setVisibility(View.VISIBLE);
                                    } else {
                                        ll_emptyShoppingBag.setVisibility(View.VISIBLE);
                                        ll_checkOut.setVisibility(View.GONE);
                                        ll_btn_shopNow.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(ShoppigbagActivity.this, TabActivity.class));
                                            }
                                        });
                                    }

                                } else if (obj.getString(Constant.GetCartList.STATUS).equals("W")) {

                                    Toast.makeText(ShoppigbagActivity.this, obj.getString(Constant.GetCartList.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                } else if (obj.getString(Constant.GetCartList.STATUS).equals("F")) {

                                    ll_emptyShoppingBag.setVisibility(View.VISIBLE);
                                    ll_checkOut.setVisibility(View.GONE);
                                    ll_btn_shopNow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(ShoppigbagActivity.this, TabActivity.class));
                                            /*CategoryFragment categoryFragment = new CategoryFragment();
                                            ((TabActivity) getApplicationContext()).loadFragment(categoryFragment, false, "" );*/
                                        }
                                    });
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
    public void onProductDetailClick(GetcartModel getcartModel, int pos) {


        Intent i = new Intent(ShoppigbagActivity.this, ProductDetailActivity.class);
        i.putExtra(Constant.ProductDetail.PRODUCT_ID, getcartModel.getProduct_id());
        i.putExtra(Constant.GETFVTMODEL, getcartModel);
        startActivity(i);


    }





    private void onbagapi() {
        params11 = new ArrayList<NameValuePair>();
        params11.add(new BasicNameValuePair("card_update", ""));
        params11.add(new BasicNameValuePair("user_id", user.getUser_id()));
        new bagbackground().execute();
    }




    public class bagbackground extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(ShoppigbagActivity.this);

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
                obj = getJSONFromUrl(APIs.checkout, params11);
                return obj;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            Log.e("Result", "" + result);

            try {
                JSONObject json = new JSONObject(result);
                String status = json.getString("status");
                Log.d("Status:", status);

                if (status.equals("S")) {

                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "No record found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            pDialog.dismiss();
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

    }


    @Override
    public void onRemoveClick(final GetcartModel getcartModel, final int pos, final TextView tv_quantity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppigbagActivity.this);
        builder.setMessage("Do you want to remove this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //progressBar.setVisibility(View.VISIBLE);
                        removeCart(getcartModel, pos);

                        String steez = tv_quantity.getText().toString();
                        double productPrice = Double.parseDouble(getcartModel.getPrice());
                        if (steez.equals("1")) {
                            String totalAmount = tv_totalAmount.getText().toString();
                            String[] tA = totalAmount.split("\\$");
                            String part11 = tA[0].trim();
                            String part22 = tA[1].trim();

                            double finalP = Double.parseDouble(part22) - productPrice;
                            tv_totalAmount.setText("$" + String.valueOf(finalP));
                        } else {
                            double totalSteezAmount = Double.parseDouble(steez) * productPrice;
                            String totalAmount = tv_totalAmount.getText().toString();
                            String[] tA = totalAmount.split("\\$");
                            String part11 = tA[0].trim();
                            String part22 = tA[1].trim();

                            double finalAll;
                            if (totalSteezAmount < Double.parseDouble(part22)) {
                                finalAll = Double.parseDouble(part22) - totalSteezAmount;
                            } else {
                                finalAll = totalSteezAmount - Double.parseDouble(part22);
                            }
                            tv_totalAmount.setText("$" + String.valueOf(finalAll));
                        }

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


    private void removeCart(final GetcartModel getcartModel, final int pos) {

        RemoveCartService.removeCart(
                this,
                user.getUser_id(),
                getcartModel.getProduct_id(),
                getcartModel.getVariation_id(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "RemoveCartRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                AppSharedPreferences.getSharePreference(ShoppigbagActivity.this).setCartCount(obj.getString("bag_count"));
                                AppSharedPreferences.getSharePreference(ShoppigbagActivity.this).setTotalSteez(obj.getString("total_steez"));

                                /*try {
                                    editor = sharedpreferences.edit();
                                    editor.putString("total_steez", obj.getString( "total_steez" ));
                                    editor.putString("cart", obj.getString( "bag_count" ));
                                    editor.commit();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }*/
                                if (obj.getString(Constant.RemoveCart.STATUS).equals("S")) {

                                    tv_favourite.setText(obj.getString(Constant.GetFavourite.BAG_COUNT));

                                    getCardArrayList.remove(pos);
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                    if (getCardArrayList.size() > 0) {
                                        ll_emptyShoppingBag.setVisibility(View.GONE);
                                        ll_checkOut.setVisibility(View.VISIBLE);
                                    } else {
                                        ll_emptyShoppingBag.setVisibility(View.VISIBLE);
                                        ll_checkOut.setVisibility(View.GONE);
                                        ll_btn_shopNow.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(ShoppigbagActivity.this, TabActivity.class));
                                            }
                                        });
                                    }

                                } else {

                                    Toast.makeText(ShoppigbagActivity.this, obj.getString(Constant.RemoveCart.MESSAGE), Toast.LENGTH_LONG).show();
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

}


    /*private void removecart(final GetcartModel getcartModel, final int pos) {

        //showpDialog();
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, APIs.remove_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("S")) {
                                getCardArrayList.remove(pos);
                                rec_getCartList.setAdapter(adapter);
                            } else {

                                Toast.makeText(ShoppigbagActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            //Notify com.steezle.e_com.adapter about data changes
                            adapter.notifyItemChanged(i);
                        }
                        //hidepDialog();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ShoppigbagActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        //hidepDialog();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                user = AppSharedPreferences.getSharePreference(ShoppigbagActivity.this).getUser();
                params.put("user_id", user.getUser_id());
                params.put("product_id", getcartModel.getProduct_id());
                params.put("variation_id", getcartModel.getVariation_id());
                return params;
            }

        };

        VolleySingleton.getInstance(this);
        requestQueue.add(stringRequest1);

    }*/


     /*private void loadHeroes() {

        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString(Constant.GetCartList.STATUS).equals("S")) {


                                JSONObject obj1 = obj.getJSONObject(Constant.GetCartList.DATA);

                                String totals = obj1.getString(Constant.GetCartList.CART_SUBTOTAL);

                                tv_favourite.setText(obj.getString(Constant.GetCartList.BAG_COUNT));

                                tv_totalAmount.setText(totals);
                                JSONArray jarrary = obj1.getJSONArray(Constant.GetCartList.CART);


                                for (int i = 0; i < jarrary.length(); i++) {

                                    JSONObject jsonObject = jarrary.getJSONObject(i);

                                    GetcartModel hero = new GetcartModel(
                                            jsonObject.getString(Constant.GetCartList.PRODUCT_ID),
                                            jsonObject.getString(Constant.GetCartList.PRODUCT_TITLE),
                                            jsonObject.getString(Constant.GetCartList.BRAND),
                                            jsonObject.getString(Constant.GetCartList.QUANTITY),
                                            jsonObject.getString(Constant.GetCartList.PRICE),
                                            jsonObject.getString(Constant.GetCartList.PRODUCT_IMAGE));

                                    getCardArrayList.add(hero);
                                }
                                adapter = new GetCartAdapter(getCardArrayList, ShoppigbagActivity.this, ShoppigbagActivity.this);
                                rec_getCartList.setAdapter(adapter);
                                GridLayoutManager manager = new GridLayoutManager(ShoppigbagActivity.this, 1, GridLayoutManager.VERTICAL, false);
                                rec_getCartList.setLayoutManager(manager);

                                if (getCardArrayList.size() > 0)
                                    iv_empty_favourite.setVisibility(View.GONE);
                                else
                                    iv_empty_favourite.setVisibility(View.VISIBLE);


                            } else if (obj.getString(Constant.GetCartList.STATUS).equals("W")) {

                                Toast.makeText(ShoppigbagActivity.this, obj.getString(Constant.GetCartList.MESSAGE), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ShoppigbagActivity.this, obj.getString(Constant.GetCartList.MESSAGE), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(ShoppigbagActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        hidepDialog();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                user = AppSharedPreferences.getSharePreference(ShoppigbagActivity.this).getUser();
                params.put("user_id", user.getUser_id());

                return params;
            }

        };

        VolleySingleton.getInstance(this);
        requestQueue.add(stringRequest);

    }*/




