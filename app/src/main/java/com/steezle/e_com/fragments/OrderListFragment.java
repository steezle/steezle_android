package com.steezle.e_com.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.steezle.e_com.adapter.OderListAdapter;
import com.steezle.e_com.model.GetcartModel;
import com.steezle.e_com.model.Order_Model;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.CancelOrderService;
import com.steezle.e_com.services.GetOrderListService;
import com.steezle.e_com.utils.Constant;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OrderListFragment extends Fragment implements APIs, OderListAdapter.CancelOrder {

    public static final String TAG = OrderListFragment.class.getSimpleName();

    private RecyclerView rec_orderList;
    private ArrayList<Order_Model> feedsList;
    private User user;
    private int i;
    private Context context;
    private OderListAdapter adapter;
    private ImageView iv_empty_order;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;

    public static OrderListFragment newInstance() {
        OrderListFragment fragment = new OrderListFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.act_order, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());

        //Initlization
        setUpView(v);
        user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
        fillOrderAdapter();
        getOrderList();

        return v;
    }


    private void setUpView(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        rec_orderList = (RecyclerView) view.findViewById(R.id.rec_orderList);
        iv_empty_order = (ImageView) view.findViewById(R.id.iv_empty_order);
    }





    private void fillOrderAdapter() {

        feedsList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rec_orderList.setLayoutManager(manager);
        DividerItemDecoration divider = new DividerItemDecoration(rec_orderList.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line));
        rec_orderList.addItemDecoration(divider);
        adapter = new OderListAdapter(feedsList, getActivity(), OrderListFragment.this);
        rec_orderList.setAdapter(adapter);

    }


    private void getOrderList() {

        //progressBar.setVisibility(View.VISIBLE);
        GetOrderListService.getOrderList(
                getActivity(),
                user.getUser_id(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "GetOrderListResponse--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);
                                if (obj.getString(Constant.GetOrderList.STATUS).equals("S")) {

                                    JSONArray jsonArray = obj.optJSONArray(Constant.GetOrderList.DATA);

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        Order_Model hero = new Order_Model(
                                                jsonObject.getString(Constant.GetOrderList.ORDER_ID),
                                                jsonObject.getString(Constant.GetOrderList.ORDER_NUMBER),
                                                jsonObject.getString(Constant.GetOrderList.CREATED_AT),
                                                jsonObject.getString(Constant.GetOrderList.UPDATED_AT),
                                                jsonObject.getString(Constant.GetOrderList.COMPLETED_AT),
                                                jsonObject.getString(Constant.GetOrderList.ORDER_STATUS),
                                                jsonObject.getString("currency"),
                                                jsonObject.getString(Constant.GetOrderList.TOTAL),
                                                jsonObject.getString(Constant.GetOrderList.SUBTOTAL),
                                                jsonObject.getString(Constant.GetOrderList.TOTAL_LINE_ITEM_QUANTITY),
                                                jsonObject.getString("total_tax"),
                                                jsonObject.getString("total_shipping"),
                                                jsonObject.getString("cart_tax"),
                                                jsonObject.getString("shipping_tax"),
                                                jsonObject.getString("total_discount"),
                                                jsonObject.getString("used_coupons"),
                                                jsonObject.getString("order_key"),
                                                jsonObject.getString("billing_address"),
                                                jsonObject.getString("shipping_address")
                                        );

                                        feedsList.add(hero);
                                    }

                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                } else if (obj.getString(Constant.GetOrderList.STATUS).equals("W")) {

                                    iv_empty_order.setVisibility(View.VISIBLE);
//                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.GetOrderList.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                } else {

//                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.GetOrderList.MESSAGE), Toast.LENGTH_SHORT).show();
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
    public void onOrderCancel(final Order_Model order_model, final ImageView dot, final TextView tv_order_status,
                              final TextView tv_cancel_order) {

        if (tv_order_status.getText().toString().trim().equals("Completed")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to cancel this order?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            cancelOrder(order_model.getId(), dot, tv_order_status, order_model,tv_cancel_order);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } else {

            Toast.makeText(getActivity(), "Order already cancelled!", Toast.LENGTH_LONG).show();
        }

    }


    private void cancelOrder(final String orderID, final ImageView iv_dot,
                             final TextView tv_order_status, final Order_Model orderModel,
                             final TextView tv_cancel_order) {

        //progressBar.setVisibility(View.VISIBLE);
        CancelOrderService.cancelOrder(
                getActivity(),
                user.getUser_id(),
                orderID,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "CancelOrderResponse--->" + response);

                        if (response != null) {

                            try {
                                JSONObject obj = new JSONObject(response);

                                if (obj.getString(Constant.CancelOrder.STATUS).equals("S")) {

                                    Toast.makeText(getActivity(), obj.getString(Constant.CancelOrder.MESSAGE), Toast.LENGTH_LONG).show();
                                    tv_order_status.setText("Cancelled");
                                    iv_dot.setBackgroundResource(R.drawable.round_dot_yellow);
                                    tv_order_status.setBackgroundResource(R.drawable.round_cancel_yellow);
                                    orderModel.setStatus("Cancelled");
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    tv_cancel_order.setVisibility(View.INVISIBLE);

                                } else {

                                    Toast.makeText(getActivity(), obj.getString(Constant.CancelOrder.MESSAGE), Toast.LENGTH_LONG).show();
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

}




  /* private void loadHeroes() {

        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, orderlist,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("S")) {

                                JSONArray jsonArray = obj.optJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //gets each JSON object within the JSON array
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Order_Model hero = new Order_Model(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("order_number"),
                                            jsonObject.getString("created_at"),
                                            jsonObject.getString("status"),
                                            jsonObject.getString("total"),
                                            jsonObject.getString("total_line_items_quantity"));

                                    feedsList.add(hero);
                                }
                                adapter = new OderListAdapter(feedsList, getActivity(), OrderListFragment.this);
                                rec_orderList.setAdapter(adapter);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                rec_orderList.setLayoutManager(manager);

                                if (feedsList.size() > 0)
                                    iv_empty_favourite.setVisibility(View.GONE);
                                else
                                    iv_empty_favourite.setVisibility(View.VISIBLE);


                            } else if (obj.getString("status").equals("W")) {

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

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
        int socketTimeout = 30000;//30 seconds - change to what you want
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



 /*private void cancelOrders(final String orderID) {

        //showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Ordercancel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.optJSONArray("data");

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
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        //hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                user = AppSharedPreferences.getSharePreference(getActivity()).getUser();
                params.put("user_id", user.getUser_id());
                params.put("order_id", orderID);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(getActivity());
        requestQueue.add(stringRequest);
    }*/