package com.steezle.e_com.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.SaveCardAdapter;
import com.steezle.e_com.adapter.SettingPagerAdapter;
import com.steezle.e_com.model.SaveCardModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.DeleteSaveCardService;
import com.steezle.e_com.services.GetSaveCardService;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.view.SaveCardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PaymentFragment extends SettingPagerAdapter implements SaveCardAdapter.OnRemoveCard,
        View.OnClickListener {

    private static final String TAG = PaymentFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private View mainFragmentView;
    private RecyclerView rec_saveCardList;
    private ArrayList<SaveCardModel> getSaveCardArrayList;
    private ImageView iv_empty_favourite;
    private SaveCardAdapter adapter;
    private User users;
    private int i;
    private RequestQueue requestQueue;
    private LinearLayout ll_btn_addCards;
    private Fragment activeFragment;
    private Stack<Fragment> fragmentStack;
    private LinearLayout ll_emptyCardView;
    private LinearLayout ll_btn_addCard, ll_btn_moreAddCard;

    @Override
    protected View initView() {
        return null;
    }

    @Override
    protected View initView(User user) {

        mainFragmentView = View.inflate(mContext, R.layout.act_payment_settings, null);
        requestQueue = Volley.newRequestQueue(getActivity());

        //Initlization
        setUpView(mainFragmentView);
        users = AppSharedPreferences.getSharePreference(getActivity()).getUser();
        fillSaveCardAdapter();

        //Register Button Click Event
        ll_btn_addCard.setOnClickListener(this);
        ll_btn_moreAddCard.setOnClickListener(this);

        return mainFragmentView;
    }


    private void setUpView(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        rec_saveCardList = (RecyclerView) view.findViewById(R.id.rec_saveCardList);
        ll_btn_addCard = (LinearLayout) view.findViewById(R.id.ll_btn_addCard);
        ll_btn_moreAddCard = (LinearLayout) view.findViewById(R.id.ll_btn_moreAddCard);
        iv_empty_favourite = (ImageView) view.findViewById(R.id.iv_empty_favourite);
        ll_emptyCardView = (LinearLayout) view.findViewById(R.id.ll_emptyCardView);
    }


    @Override
    public void onResume() {
        super.onResume();
        getSaveCardList();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_btn_addCard:
                Intent i = new Intent(getActivity(), SaveCardActivity.class);
                startActivity(i);
                break;


            case R.id.ll_btn_moreAddCard:
                Intent i1 = new Intent(getActivity(), SaveCardActivity.class);
                startActivity(i1);
                break;


        }
    }


    private void fillSaveCardAdapter() {

        getSaveCardArrayList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rec_saveCardList.setLayoutManager(manager);
        adapter = new SaveCardAdapter(getSaveCardArrayList, getActivity(), PaymentFragment.this);
        rec_saveCardList.setAdapter(adapter);


    }


    private void  getSaveCardList() {

        if (getSaveCardArrayList != null)
            getSaveCardArrayList.clear();

        //progressBar.setVisibility(View.VISIBLE);
        GetSaveCardService.getSaveCard(
                getActivity(),
                users.getUser_id(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "GetSaveCardRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString(Constant.GetSaveCard.STATUS).equals("S")) {

                                    JSONArray jarrary = obj.getJSONArray(Constant.GetSaveCard.DATA);

                                    for (int i = 0; i < jarrary.length(); i++) {
                                        //gets each JSON object within the JSON array
                                        JSONObject jsonObject = jarrary.getJSONObject(i);

                                        SaveCardModel saveCardModel = new SaveCardModel(
                                                jsonObject.getString(Constant.GetSaveCard.TOKEN),
                                                jsonObject.getString(Constant.GetSaveCard.CARD_STRING));

                                        getSaveCardArrayList.add(saveCardModel);
                                    }

                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                    if (getSaveCardArrayList.size() > 0) {
                                        ll_btn_moreAddCard.setVisibility( View.VISIBLE );
                                        ll_emptyCardView.setVisibility(View.GONE);
                                    } else {
                                        ll_btn_moreAddCard.setVisibility( View.GONE );
                                        ll_emptyCardView.setVisibility(View.VISIBLE);
                                    }

                                } else if (obj.getString(Constant.GetSaveCard.STATUS).equals("W")) {

                                    Toast.makeText(getApplicationContext(), obj.getString(Constant.GetSaveCard.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                } else if (obj.getString(Constant.GetSaveCard.STATUS).equals("F")) {

                                    ll_emptyCardView.setVisibility(View.VISIBLE);
                                    //Toast.makeText(getApplicationContext(), obj.getString(Constant.GetSaveCard.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            } finally {
                                adapter.notifyItemChanged(i);
                            }

                        } else if (response.equals("null")) {

                            Toast.makeText(getActivity(), "No record Found", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }

        );
    }


    @Override
    protected void initData() {
        super.initData();

    }


    @Override
    public void onRemoveCard(final SaveCardModel saveCardModel, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(getString(R.string.creditcardremove));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                deleteSaveCard(saveCardModel, pos);
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void deleteSaveCard(final SaveCardModel saveCardModel, final int pos) {

        //progressBar.setVisibility(View.VISIBLE);
        DeleteSaveCardService.deleteSaveCard(
                getActivity(),
                users.getUser_id(),
                saveCardModel.getToken(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "DeleteSaveCardRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject object = new JSONObject(response);

                                if (object.getString(Constant.DeleteSaveCard.STATUS).equals("S")) {

                                    getSaveCardArrayList.remove(pos);
                                    adapter.notifyDataSetChanged();
//                                    Toast.makeText(getActivity(), object.getString(Constant.DeleteSaveCard.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);

                                } else {

                                    Toast.makeText(getActivity(), object.getString(Constant.DeleteSaveCard.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                                if (getSaveCardArrayList.size() > 0) {
                                    ll_btn_moreAddCard.setVisibility( View.VISIBLE );
                                    ll_emptyCardView.setVisibility(View.GONE);
                                } else {
                                    ll_btn_moreAddCard.setVisibility( View.GONE );
                                    ll_emptyCardView.setVisibility(View.VISIBLE);
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


/*private void loadHeroes() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.getcard,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("null")) {

                            Toast.makeText(getActivity(), "No record Found", Toast.LENGTH_SHORT).show();

                        } else {
                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString("status").equals("S")) {

                                    JSONArray jarrary = obj.getJSONArray("data");

                                    for (int i = 0; i < jarrary.length(); i++) {
                                        //gets each JSON object within the JSON array
                                        JSONObject jsonObject = jarrary.getJSONObject(i);

                                        SaveCardModel hero = new SaveCardModel(
                                                jsonObject.getString("token"),
                                                jsonObject.getString("card_string"));

                                        getSaveCardArrayList.add(hero);
                                    }
                                    adapter = new SaveCardAdapter(getSaveCardArrayList, getActivity(), PaymentFragment.this);
                                    rec_saveCardList.setAdapter(adapter);
                                    GridLayoutManager manager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                    rec_saveCardList.setLayoutManager(manager);


                                    if (getSaveCardArrayList.size() > 0)
                                        iv_empty_favourite.setVisibility(View.GONE);
                                    else
                                        iv_empty_favourite.setVisibility(View.VISIBLE);


                                } else if (obj.getString("status").equals("W")) {

                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                }


                                hidepDialog();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                //Notify com.steezle.e_com.adapter about data changes
                                adapter.notifyItemChanged(i);
                            }


                        }
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
        VolleySingleton.getInstance(getActivity());
        requestQueue.add(stringRequest);
    }*/

    /*private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/




    /*private void removeSaveCard(final SaveCardModel saveCardModel, final int pos) {

        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.deletecard,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            String msg = obj.getString("message");
                            String status = obj.getString("status");


                            if (status.equals("S")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                builder.setTitle(getResources().getString(R.string.app_name));
                                builder.setMessage(getString(R.string.creditcardremove));

                                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {

                                        getSaveCardArrayList.remove(pos);
                                        adapter.notifyDataSetChanged();

                                    }
                                });

                                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                params.put("token", saveCardModel.getToken());
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



