package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.BrandlistAdapter;
import com.steezle.e_com.model.BrandModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.ProductFilterService;
import com.steezle.e_com.utils.Constant;

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


public class FilterList extends AppCompatActivity implements BrandlistAdapter.OnBrandCheck,
        View.OnClickListener {

    public static final String TAG = FilterList.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView rec_filterList;
    private ImageView back_click_clothing;
    private ArrayList<BrandModel> filterArrayList;
    private User user;
    private String brandID;
    private String categoryID, minrange, maxrange;
    private RelativeLayout rl_btn_applyFilter;
    private CrystalRangeSeekbar range_seekbar;
    private TextView tv_minimum, tv_maximum;
    private List<NameValuePair> params11;
    private BrandlistAdapter adapter;
    private Intent intent;

    private HashMap<String, String> brandHashMap;
    private JSONArray filterArray;

    private int i;
    String titile_product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.act_filter );

        filterArray = new JSONArray();
        brandHashMap = new HashMap<>();

        intent=getIntent();
         
        titile_product= intent.getStringExtra("titile_product");
        brandID = intent.getStringExtra(Constant.ProductFilter.BRAND_ID);
        categoryID = intent.getStringExtra(Constant.ProductFilter.CATEGORY_ID);
        setUpView();
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


        //Register Button Click Event
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
            @SuppressLint("SetTextI18n")
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                tv_minimum.setText(String.valueOf(minValue));
                tv_maximum.setText(String.valueOf(maxValue));
            }
        });
    }
    public void setUpView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rec_filterList = (RecyclerView) findViewById(R.id.rec_filterList);
        back_click_clothing = (ImageView) findViewById(R.id.back_click_clothing);
        rl_btn_applyFilter = (RelativeLayout) findViewById(R.id.rl_btn_applyFilter);
        range_seekbar = (CrystalRangeSeekbar) findViewById(R.id.range_seekbar);
        tv_minimum = (TextView) findViewById(R.id.tv_minimum);
        tv_maximum = (TextView) findViewById(R.id.tv_maximum);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click_clothing:
                onBackPressed();
                break;

            case R.id.rl_btn_applyFilter:
                try{
                    if (user.getUser_id().equalsIgnoreCase( "" ))
                    onFilterSuccess("");
                    else
                        onFilterSuccess(user.getUser_id());
                }catch (Exception e){
                    e.printStackTrace();
                    onFilterSuccess("");
                }

                break;
        }

    }


    private void fillBrandListAdapter() {

        filterArrayList = new ArrayList<>();

        GridLayoutManager manager = new GridLayoutManager(FilterList.this, 1, GridLayoutManager.VERTICAL, false);
        rec_filterList.setLayoutManager(manager);
        DividerItemDecoration divider = new DividerItemDecoration(rec_filterList.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(FilterList.this, R.drawable.line));
        rec_filterList.addItemDecoration(divider);
        rec_filterList.setHasFixedSize(true);
        //rec_filterList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BrandlistAdapter(filterArrayList, FilterList.this, FilterList.this);
        rec_filterList.setAdapter(adapter);

    }


    private void getFilterProducts(String userId) {

        //progressBar.setVisibility(View.VISIBLE);
        ProductFilterService.getProductFilter(
                FilterList.this,
                userId,
                categoryID,
                /*brandID*/"0",
                progressBar,
                new APIService.Success<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "GetProductFilter--->" + response);

                        if (response != null) {

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
                                progressBar.setVisibility(View.GONE);

                                if(minrange.equals(maxrange)) {
                                    range_seekbar.setEnabled(false);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            } finally {
                                adapter.notifyItemChanged(i);
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    }
                }
        );
    }


    private void onFilterSuccess(String userId) {
        getFilterArray();


        if (brandID != null && brandID.equals("0")) {
            Log.e( "IF filterArray.)",""+filterArray.toString());
            params11 = new ArrayList<NameValuePair>();
            params11.add(new BasicNameValuePair("category_id", "0"));
            params11.add(new BasicNameValuePair("user_id", userId));
            params11.add(new BasicNameValuePair("brand_id", "0"));
            params11.add(new BasicNameValuePair("filters", filterArray.toString()));

        } else {
            Log.e( "ELSE filterArray.)",""+filterArray.toString());
            params11 = new ArrayList<NameValuePair>();
            params11.add(new BasicNameValuePair("category_id", categoryID));
            params11.add(new BasicNameValuePair("user_id", userId));
            params11.add(new BasicNameValuePair("brand_id", "0"));
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
        ProgressDialog pDialog = new ProgressDialog(FilterList.this);

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
                obj = getJSONFromUrl(APIs.applayfilter, params11);
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            Log.e("Result", "" + result);

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
            pDialog.dismiss();
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
