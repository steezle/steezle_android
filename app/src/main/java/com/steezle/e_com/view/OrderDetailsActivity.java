package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.steezle.e_com.adapter.OderDetailsAdapter;
import com.steezle.e_com.model.Order_Model;
import com.steezle.e_com.model.OrderhistoryModel;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.OrderHistoryService;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.utils.ProjectUtility;


public class OrderDetailsActivity extends AppCompatActivity implements APIs, View.OnClickListener {

    public static final String TAG = OrderDetailsActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView rec_shoppingBag;
    private List<OrderhistoryModel> feedsList;
    private ImageView back_click_clothing;
    private int i;
    private String result;
    private Context context;
    private OderDetailsAdapter adapter;
    private RequestQueue requestQueue;
    private TextView tv_order, tv_placed, tv_address, tv_payment_type, tv_marchandise, tv_shippingPrice,
            tv_taxPrice, tv_totalPrice, tv_status, tv_couponCodeName, tv_discountedPrice;
    private String sorder, sMerchandise, sShapping, sTax, sTotalPayment;
    private Intent intent;
    private String orderid;
    private Order_Model order_model;
    private RelativeLayout rl_discountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        context = this;
        setContentView( R.layout.act_order_details );
        requestQueue = Volley.newRequestQueue( this );

        //Initlization
        setUpView();
        intent = getIntent();
        orderid = intent.getStringExtra( "id" );
        order_model = (Order_Model) intent.getSerializableExtra( "Order_Model" );
        fillOrderHistoryAdapter();
        getOrderHistory();
        //Register Button Click Event
        back_click_clothing.setOnClickListener( this );
    }


    private void setUpView() {
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        rec_shoppingBag = (RecyclerView) findViewById( R.id.rec_shoppingBag );
        tv_order = (TextView) findViewById( R.id.tv_order );
        tv_placed = (TextView) findViewById( R.id.tv_placed );
        tv_address = (TextView) findViewById( R.id.tv_address );
        tv_payment_type = (TextView) findViewById( R.id.tv_payment_type );
        tv_marchandise = (TextView) findViewById( R.id.tv_marchandise );
        tv_shippingPrice = (TextView) findViewById( R.id.tv_shippingPrice );
        tv_taxPrice = (TextView) findViewById( R.id.tv_taxPrice );
        tv_status = (TextView) findViewById( R.id.tv_status );
        back_click_clothing = (ImageView) findViewById( R.id.back_click_clothing );
        tv_totalPrice = (TextView) findViewById( R.id.tv_totalPrice );

        rl_discountView = (RelativeLayout) findViewById( R.id.rl_discountView );
        tv_couponCodeName = (TextView) findViewById( R.id.tv_couponCodeName );
        tv_discountedPrice = (TextView) findViewById( R.id.tv_discountedPrice );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click_clothing:
                finish();
                break;
        }
    }

    private void fillOrderHistoryAdapter() {
        feedsList = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager( getApplicationContext(), 1, GridLayoutManager.VERTICAL, false );
        rec_shoppingBag.setLayoutManager( manager );
        rec_shoppingBag.setHasFixedSize( true );
        DividerItemDecoration divider = new DividerItemDecoration( rec_shoppingBag.getContext(), DividerItemDecoration.VERTICAL );
        divider.setDrawable( ContextCompat.getDrawable( getApplicationContext(), R.drawable.line ) );
        rec_shoppingBag.addItemDecoration( divider );
        adapter = new OderDetailsAdapter( feedsList, getApplicationContext() );
        rec_shoppingBag.setAdapter( adapter );
    }

    private void getOrderHistory() {
        //progressBar.setVisibility(View.VISIBLE);
        OrderHistoryService.getOrderHistory(
                this,
                orderid,
                progressBar,
                new APIService.Success<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        Log.e( TAG, "OrderHistoryRes--->" + response );

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject( response );

                                if (obj.getString( Constant.GetOrderList.STATUS ).equals( "S" )) {

                                    //Data Object
                                    JSONObject data = obj.getJSONObject( "data" );

                                    sorder = data.getString( "order_number" );
                                    tv_order.setText( "#" + sorder );

                                    //tv_placed.setText(data.getString("created_at"));
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sourceFormat = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
                                    sourceFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

                                    Date parsed = null; // => Date is in UTC now
                                    try {
                                        parsed = sourceFormat.parse( data.getString( "created_at" ) );
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    //TimeZone tz = TimeZone.getTimeZone("America/Chicago");
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat destFormat = new SimpleDateFormat( "hh:mm a - MMM d, yyyy" );
                                    destFormat.setTimeZone( TimeZone.getDefault() );

                                    String result = destFormat.format( parsed );
                                    String[] timeDate = result.split( "\\-" );
                                    String time = timeDate[0];
                                    String date = timeDate[1];
                                    tv_placed.setText( time + "on" + date );

                                    tv_address.setText( data.getString( "billing_address" ) );

                                    //payment_details Object
                                    JSONObject payment_details = data.getJSONObject( "payment_details" );

                                    tv_payment_type.setText( payment_details.getString( "method_title" ) );

                                    sMerchandise = data.getString( "subtotal" );
                                    tv_marchandise.setText( "$" + sMerchandise );

                                    sShapping = data.getString( "Shipping" );
                                    tv_shippingPrice.setText( "$" + sShapping );

                                    if (data.getString( "total_discount" ).equals( "0.00" )) {
                                        rl_discountView.setVisibility( View.GONE );
                                    } else {
                                        rl_discountView.setVisibility( View.VISIBLE );
                                        tv_couponCodeName.setText( "(" + data.getString( "used_coupons" ) + ")" );
                                        tv_discountedPrice.setText( "- $" + data.getString( "total_discount" ) );
                                    }

                                    sTax = data.getString( "Tax" );
                                    tv_taxPrice.setText( "$" + sTax );

                                    sTotalPayment = data.getString( "total" );
                                    tv_totalPrice.setText( "$" + sTotalPayment );

                                    if (data.getString( "status" ).equals( "cancelled" )) {
                                        tv_status.setBackgroundResource( R.drawable.processing );
                                        String statusCancelled = ProjectUtility.toCamelCaseWord( data.getString( "status" ) );
                                        tv_status.setText( statusCancelled );
                                    } else {
                                        tv_status.setBackgroundResource( R.drawable.processing_green );
                                        String statusCompleted = ProjectUtility.toCamelCaseWord( data.getString( "status" ) );
                                        tv_status.setText( statusCompleted );
                                    }

                                    JSONArray line_itemsArray = data.optJSONArray( "line_items" );

                                    //STARTS FROM HERE
                                    List<String> list = new ArrayList<>();
                                    for (int n = 0; n < line_itemsArray.length(); n++) {
                                        JSONObject obb = line_itemsArray.getJSONObject( n );
                                        JSONArray array = obb.getJSONArray( "variation" );

                                        list = new ArrayList<>();
                                        for (int i = 0; i < array.length(); i++) {
                                            try {
                                                JSONObject object = array.getJSONObject( i );
                                                list.add( object.getString( "value" ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        try {
                                            OrderhistoryModel hero = new OrderhistoryModel( list,
                                                    obb.getString( "price" ),
                                                    obb.getString( "name" ),
                                                    obb.getString( "product_thumbnail_url" ),
                                                    obb.getString( "brand" ),
                                                    obb.getString( "quantity" ), "" );

                                            feedsList.add( hero );

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility( View.GONE );

                                } else {
                                    Toast.makeText( getApplicationContext(), obj.getString( Constant.GetOrderList.MESSAGE ), Toast.LENGTH_LONG ).show();
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

}

/*private void loadHeroes() {

        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, orderlisthistory,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            JSONObject data = obj.getJSONObject("data");
                            sorder = data.getString("order_number");
                            sPlaced = data.getString("created_at");
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date parsed = null; // => Date is in UTC now
                            try {
                                parsed = sourceFormat.parse(sPlaced);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            TimeZone tz = TimeZone.getTimeZone("America/Chicago");
                            SimpleDateFormat destFormat = new SimpleDateFormat("HH:mm MMM d, yyyy");
                            destFormat.setTimeZone(tz);
                            result = destFormat.format(parsed);
                            sAddress = data.getString("billing_address");
                            JSONObject obj1 = data.getJSONObject("payment_details");
                            sPayment = obj1.getString("method_title");
                            sMerchandise = data.getString("subtotal");
                            sShapping = data.getString("Shipping");
                            sText = data.getString("Tax");
                            sstatus = data.getString("status");
                            storder = data.getString("total");
                            sdiscount = data.getString("total_discount");
                            scouponcode = data.getString("used_coupons");

                            if (sstatus.equals("Completed")) {
                                status.setBackgroundColor(Color.parseColor("#fff05a28"));
                            } else {
                                status.setBackgroundColor(Color.parseColor("#006633"));
                            }

                            JSONArray jsonArray = data.optJSONArray("line_items");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                //gets each JSON object within the JSON array
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                OrderhistoryModel hero = new OrderhistoryModel(
                                        jsonObject.getString("price"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("product_thumbnail_url"),
                                        jsonObject.getString("brand"),
                                        jsonObject.getString("quantity"));


                                feedsList.add(hero);
                            }
                            adapter = new OderDetailsAdapter(feedsList, getApplicationContext());
                            rec_shoppingBag.setAdapter(adapter);
                            GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
                            rec_shoppingBag.setLayoutManager(manager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            //Notify com.steezle.e_com.adapter about data changes
                            adapter.notifyItemChanged(i);
                            tv_order.setText("#" + sorder);
                            tv_placed.setText(result);
                            tv_address.setText(sAddress);
                            tv_payment_type.setText(sPayment);
                            tv_marchandise.setText("$" + sMerchandise);
                            tv_shippingPrice.setText("$" + sShapping);
                            tv_taxPrice.setText("$" + sText);
                            status.setText(sstatus);
                            tv_totalPrice.setText("$" + storder);
                            tv_discountPrice.setText("$" + sdiscount);
                            tv_coupon.setText(scouponcode);
                        }
                        hidepDialog();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        hidepDialog();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", orderid);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
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




