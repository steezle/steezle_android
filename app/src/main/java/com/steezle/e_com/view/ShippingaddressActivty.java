package com.steezle.e_com.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.common.StringUtils;
import com.steezle.e_com.R;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import info.hoang8f.android.segmented.SegmentedGroup;

import com.steezle.e_com.adapter.CountryAdpter;
import com.steezle.e_com.adapter.StateAdpter;
import com.steezle.e_com.model.CountryModel;
import com.steezle.e_com.model.StateModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.networking.CommonValidation;
import com.steezle.e_com.networking.VolleySingleton;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.CountryListService;
import com.steezle.e_com.services.SetAddressService;
import com.steezle.e_com.services.StateListService;
import com.steezle.e_com.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShippingaddressActivty extends AppCompatActivity implements APIs,
        View.OnClickListener,
        CountryAdpter.OnCountrySelected,
        StateAdpter.OnStateSelected {

    public static final String TAG = ShippingaddressActivty.class.getSimpleName();

    private RecyclerView recyclerViewfile;
    private ArrayList<CountryModel> countryArrayList;
    private ArrayList<StateModel> stateArrayList, stateArrayListS;
    private ImageView back_click;
    private int i;
    private Switch switch_sameAddress;
    private Button btn_saveAddress;
    private LinearLayout linaddress;
    private Context context;
    private User user;
    private CountryAdpter adapter;
    private StateAdpter stateAdapter, stateAdpterS;
    private RequestQueue requestQueue;
    private AlertDialog dialog, stateDialog;
    private EditText edt_billing_fullName, edt_billing_address1, edt_billing_address2, edt_billing_phone,
            edt_billing_country, edt_billing_state, edt_billing_city, edt_billing_zipcode;
    private EditText edt_shipping_fullName, edt_shipping_address1, edt_shipping_address2, edt_shipping_phone,
            edt_shipping_country, edt_shipping_state, edt_shipping_city, edt_shipping_zipcode;
    private TextView edt_address;
    private JSONObject obj1;
    private ProgressBar progressBar;
    private String billing, shipping;
//    String zip_valid="[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_billingadress);
        requestQueue = Volley.newRequestQueue(ShippingaddressActivty.this);

        //Initlization
        setUpview();
        user = AppSharedPreferences.getSharePreference(this).getUser();
        countryArrayList = new ArrayList<CountryModel>();
        stateArrayList = new ArrayList<>();
        stateArrayListS=new ArrayList<>(  );

        getCountry();

        new getAddress().execute();


        //Register Button Click Event
        back_click.setOnClickListener(this);
        edt_address.setOnClickListener(this);
        edt_billing_country.setOnClickListener(this);
        edt_billing_state.setOnClickListener(this);
        edt_shipping_country.setOnClickListener(this);
        edt_shipping_state.setOnClickListener(this);
        btn_saveAddress.setOnClickListener(this);


        switch_sameAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    edt_shipping_fullName.setText(edt_billing_fullName.getText().toString());
                    edt_shipping_address1.setText(edt_billing_address1.getText().toString());
                    edt_shipping_address2.setText(edt_billing_address2.getText().toString());
                    //edt_shipping_phone.setText(edt_billing_phone.getText().toString());
                    edt_shipping_country.setText(edt_billing_country.getText().toString());
                    edt_shipping_state.setText(edt_billing_state.getText().toString());
                    edt_shipping_city.setText(edt_billing_city.getText().toString());
                    edt_shipping_zipcode.setText(edt_billing_zipcode.getText().toString());

                } else {

                    edt_shipping_fullName.setText("");
                    edt_shipping_address1.setText("");
                    edt_shipping_address2.setText("");
                    //edt_shipping_phone.setText("");
                    edt_shipping_country.setText("");
                    edt_shipping_state.setText("");
                    edt_shipping_city.setText("");
                    edt_shipping_zipcode.setText("");
                }
            }
        });

    }


    private void setUpview() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        back_click = (ImageView) findViewById(R.id.back_click);
        edt_address = (TextView) findViewById(R.id.edt_address);
        edt_billing_fullName = (EditText) findViewById(R.id.edt_billing_fullName);
        edt_billing_address1 = (EditText) findViewById(R.id.edt_billing_address1);
        edt_billing_address2 = (EditText) findViewById(R.id.edt_billing_address2);
        edt_billing_phone = (EditText) findViewById(R.id.edt_billing_phone);
        edt_billing_country = (EditText) findViewById(R.id.edt_billing_country);
        edt_billing_city = (EditText) findViewById(R.id.edt_billing_city);
        edt_billing_state = (EditText) findViewById(R.id.edt_billing_state);
        edt_billing_zipcode = (EditText) findViewById(R.id.edt_billing_zipcode);
        linaddress = (LinearLayout) findViewById(R.id.lin_sameaddress);
        btn_saveAddress = (Button) findViewById(R.id.btn_saveAddress);
        switch_sameAddress = (Switch) findViewById(R.id.switch_sameAddress);

        edt_shipping_fullName = (EditText) findViewById(R.id.edt_shipping_fullName);
        edt_shipping_address1 = (EditText) findViewById(R.id.edt_shipping_address1);
        edt_shipping_address2 = (EditText) findViewById(R.id.edt_shipping_address2);
        edt_shipping_phone = (EditText) findViewById(R.id.edt_shipping_phone);
        edt_shipping_country = (EditText) findViewById(R.id.edt_shipping_country);
        edt_shipping_state = (EditText) findViewById(R.id.edt_shipping_state);
        edt_shipping_city = (EditText) findViewById(R.id.edt_shipping_city);
        edt_shipping_zipcode = (EditText) findViewById(R.id.edt_shipping_zipcode);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_click:
                finish();
                break;

            case R.id.edt_address:
//                linaddress.setVisibility(View.GONE);
                edt_billing_fullName.setText("");
                edt_billing_address1.setText("");
                edt_billing_address2.setText("");
                edt_billing_country.setText("");
                edt_billing_city.setText("");
                edt_billing_state.setText("");
                edt_billing_zipcode.setText("");
                edt_billing_phone.setText("");
                edt_shipping_fullName.setText("");
                edt_shipping_address1.setText("");
                edt_shipping_address2.setText("");
                edt_shipping_country.setText("");
                edt_shipping_city.setText("");
                edt_shipping_state.setText("");
                edt_shipping_zipcode.setText("");
                break;

            case R.id.btn_saveAddress:
                if (checkBillingValidation() && checkShippingValidation()) {
                    //progressBar.setVisibility(View.VISIBLE);
                    setAddress();
                }
                break;

            case R.id.edt_billing_country:
                alertScrollView(edt_billing_country, edt_billing_state);
                break;

            case R.id.edt_billing_state:
                alertState(edt_billing_state);
                break;

            case R.id.edt_shipping_country:
                alertScrollView(edt_shipping_country, edt_shipping_state);
                break;

            case R.id.edt_shipping_state:
                alertState(edt_shipping_state);
                break;

        }
    }

    public void alertScrollView(EditText editText, EditText editTextS) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.popup_country, null, false);
        recyclerViewfile = (RecyclerView) myScrollView.findViewById(R.id.recyclerViewlist);

        AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
        builder.setView(myScrollView)
                .setTitle("Country");
               /* .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
*/
        dialog = builder.create();
        dialog.show();

            adapter = new CountryAdpter(editText,editTextS,countryArrayList,
                    ShippingaddressActivty.this,
                    ShippingaddressActivty.this);

        recyclerViewfile.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(ShippingaddressActivty.this, 1, GridLayoutManager.VERTICAL, false);
        recyclerViewfile.setLayoutManager(manager);


    }

    public void alertState(EditText editText) {


            LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View myScrollView = inflater.inflate( R.layout.popup_state, null, false );
            recyclerViewfile = (RecyclerView) myScrollView.findViewById( R.id.recyclerViewlist );

            final AlertDialog.Builder builder = new AlertDialog.Builder( ShippingaddressActivty.this );
            builder.setView( myScrollView )
                    .setTitle( "State" );
                    /*.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    } );
*/
            stateDialog = builder.create();

        if (editText==edt_billing_state) {
            stateAdapter = new StateAdpter( editText, stateArrayList, ShippingaddressActivty.this, ShippingaddressActivty.this );
            recyclerViewfile.setAdapter( stateAdapter );
            GridLayoutManager manager = new GridLayoutManager( ShippingaddressActivty.this, 1, GridLayoutManager.VERTICAL, false );
            recyclerViewfile.setLayoutManager( manager );


            if (stateArrayList.size() > 0) {
                stateDialog.show();
            } else {
                final AlertDialog.Builder builder1 = new AlertDialog.Builder( ShippingaddressActivty.this );
                builder1
//                    .setView(myScrollView)
                        .setMessage( "Please Select Country" )
                        .setTitle( "Steezle" )
                        .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        } );
                builder1.show();
            }
        }
        else{
            stateAdpterS = new StateAdpter(editText,stateArrayListS, ShippingaddressActivty.this, ShippingaddressActivty.this);

            recyclerViewfile.setAdapter(stateAdpterS);
            GridLayoutManager manager = new GridLayoutManager(ShippingaddressActivty.this, 1, GridLayoutManager.VERTICAL, false);
            recyclerViewfile.setLayoutManager(manager);

            if (stateArrayListS.size()>0) {
                stateDialog.show();
            }
            else
            {
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(ShippingaddressActivty.this);
                builder1
//                    .setView(myScrollView)
                        .setMessage("Please Select Country")
                        .setTitle("Steezle")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });
                builder1.show();
            }
        }
    }


    private void getCountry() {

        //progressBar.setVisibility(View.VISIBLE);
        CountryListService.getCountry(
                this,
                user.getUser_id(),
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "GetCountryRes--->" + response);

                        try {

                            JSONObject obj = new JSONObject(response);

                            JSONArray jarrary = obj.getJSONArray(Constant.CountryList.DATA);

                            for (int i = 0; i < jarrary.length(); i++) {
                                //gets each JSON object within the JSON array
                                JSONObject jsonObject = jarrary.getJSONObject(i);

                                if (jsonObject.getString(Constant.CountryList.NAME).equals("Canada") ||
                                        jsonObject.getString(Constant.CountryList.NAME).equals("United States (US)")) {

                                    CountryModel hero = new CountryModel(
                                            jsonObject.getString(Constant.CountryList.NAME),
                                            jsonObject.getString(Constant.CountryList.CODE));

                                    countryArrayList.add(hero);
                                }
                                //countryAdpter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }

        );
    }

    private void getState(String countryCode, final EditText editTextS) {
        if (editTextS==edt_shipping_state) {
            stateArrayListS.clear();
        }
        else{
            stateArrayList.clear();
        }
        StateListService.getState(
                this,
                user.getUser_id(),
                countryCode,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "GetStateListRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                JSONArray jarrary = obj.getJSONArray(Constant.StateList.DATA);

                                for (int i = 0; i < jarrary.length(); i++) {
                                    //gets each JSON object within the JSON array
                                    JSONObject jsonObject = jarrary.getJSONObject(i);

                                    StateModel hero = new StateModel(
                                            jsonObject.getString(Constant.StateList.NAME),
                                            jsonObject.getString(Constant.StateList.CODE));

//                                    stateArrayList.add(hero);
                                    if (editTextS==edt_shipping_state)
                                        stateArrayListS.add( hero );
                                    else
                                        stateArrayList.add(hero);

                                }

                                if (editTextS==edt_shipping_state){
                                    if (stateAdpterS!=null)
                                        stateAdpterS.notifyDataSetChanged();
                                }

                                else {
                                    if (stateAdapter != null)
                                        stateAdapter.notifyDataSetChanged();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }

        );
    }



    @SuppressLint("ResourceType")
    private boolean checkBillingValidation() {

        if (CommonValidation.isEdittextEmpty(edt_billing_fullName)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter your name!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_billing_address1)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter address!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_billing_address2)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter address!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_billing_phone)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter your phone number!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isValidMobile(edt_billing_phone)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter your 10 digit phone number!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_billing_country)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please select country!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_billing_state)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please select state!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
//            return true;
            return false;

        } else if (CommonValidation.isEdittextEmpty(edt_billing_city)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter your city!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;

        } else if (CommonValidation.isEdittextEmpty(edt_billing_zipcode)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter zip or postal code!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;

        } /*else if (!CommonValidation.isAValidZipCode(edt_billing_zipcode.getText().toString())) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter valid zipcode!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }*/ else {

            return true;
        }

    }


    private boolean checkShippingValidation() {

       /* if (switch_sameAddress.isChecked()) {

            return true;

        } else*/

        if (CommonValidation.isEdittextEmpty(edt_shipping_fullName)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter your name!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_shipping_address1)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter address!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_shipping_address2)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter address!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_shipping_country)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please select country!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_shipping_state)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please select state!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_shipping_city)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter your city!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else if (CommonValidation.isEdittextEmpty(edt_shipping_zipcode)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter zip or postal code!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        } /*else if (!CommonValidation.isAValidZipCode(edt_shipping_zipcode.getText().toString())) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ShippingaddressActivty.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage("Please enter valid zipcode!");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }*/ else {

            return true;

        }
    }



    public class getAddress extends AsyncTask<String, String, String> {

        private getAddress() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(final String... strings) {

            InputStream inputStream = null;
            String result = "";

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(APIs.getaddress);

                JSONObject jsonObject = new JSONObject();
                user = AppSharedPreferences.getSharePreference(ShippingaddressActivty.this).getUser();
                jsonObject.accumulate("user_id", user.getUser_id());

                String json = "";
                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);

                httpPost.setHeader("Content-type", "application/json");
//                httpPost.setHeader("application/json", "Accept ");

                HttpResponse httpResponse = httpclient.execute(httpPost);

                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }

                    result = total.toString();


                } else
                    result = "Did not work!";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Log.e(TAG, "AddressRes--->" + response);

            if (response != null) {

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (obj.getString("status").equals("S")) {

                        obj1 = obj.getJSONObject("data");

                        JSONObject obj2 = obj1.getJSONObject("billing");

                        edt_billing_fullName.setText(obj2.getString("name"));
                        edt_billing_address1.setText(obj2.getString("address_1"));
                        edt_billing_address2.setText(obj2.getString("address_2"));
                        edt_billing_phone.setText(obj2.getString("phone"));
                        edt_billing_country.setText(obj2.getString("country"));
                        edt_billing_city.setText(obj2.getString("city"));
                        edt_billing_state.setText(obj2.getString("state"));
                        edt_billing_zipcode.setText(obj2.getString("postcode"));

                        JSONObject obj3 = obj1.getJSONObject("shipping");

                        edt_shipping_fullName.setText(obj3.getString("name"));
                        edt_shipping_address1.setText(obj3.getString("address_1"));
                        edt_shipping_address2.setText(obj3.getString("address_2"));
                        edt_shipping_city.setText(obj3.getString("city"));
                        edt_shipping_zipcode.setText(obj3.getString("postcode"));
                        edt_shipping_country.setText(obj3.getString("country"));
                        edt_shipping_state.setText(obj3.getString("state"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }

            }
        }

    }



    private void setAddress() {

        try {

            JSONArray billArray = new JSONArray();
            JSONObject billObj = new JSONObject();

            billObj.put(Constant.SetAddress.NAME, edt_billing_fullName.getText().toString());
            billObj.put(Constant.SetAddress.ADDRESS_1, edt_billing_address1.getText().toString());
            billObj.put(Constant.SetAddress.ADDRESS_2, edt_billing_address2.getText().toString());
            billObj.put(Constant.SetAddress.PHONE, edt_billing_phone.getText().toString());
            billObj.put(Constant.SetAddress.COUNTRY, edt_billing_country.getText().toString());
            billObj.put(Constant.SetAddress.STATE, edt_billing_state.getText().toString());
            billObj.put(Constant.SetAddress.CITY, edt_billing_city.getText().toString());
            billObj.put(Constant.SetAddress.POST_CODE, edt_billing_zipcode.getText().toString());
            billObj.put(Constant.SetAddress.EMAIL, user.getUser_email());

            JSONArray shipArray = new JSONArray();
            JSONObject shipObj = new JSONObject();

            shipObj.put(Constant.SetAddress.NAME, edt_shipping_fullName.getText().toString());
            shipObj.put(Constant.SetAddress.ADDRESS_1, edt_shipping_address1.getText().toString());
            shipObj.put(Constant.SetAddress.ADDRESS_2, edt_shipping_address2.getText().toString());
            shipObj.put(Constant.SetAddress.COUNTRY, edt_shipping_country.getText().toString());
            shipObj.put(Constant.SetAddress.STATE, edt_shipping_state.getText().toString());
            shipObj.put(Constant.SetAddress.CITY, edt_shipping_city.getText().toString());
            shipObj.put(Constant.SetAddress.POST_CODE, edt_shipping_zipcode.getText().toString());

            String bill = billArray.put(billObj).toString();
            String shipp = shipArray.put(shipObj).toString();

            billing = bill.replaceAll("\\[", "").replaceAll("\\]", "");
            shipping = shipp.replaceAll("\\[", "").replaceAll("\\]", "");


        } catch (Exception e) {
            e.printStackTrace();
        }

        user = AppSharedPreferences.getSharePreference(ShippingaddressActivty.this).getUser();
        SetAddressService.setAddress(
                this,
                user.getUser_id(),
                billing,
                shipping,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "SetAddressRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject object = new JSONObject(response);

                                if (object.getString(Constant.SetAddress.STATUS).equals("S")) {


//                                   Toast.makeText(getApplicationContext(), object.getString(Constant.SetAddress.MESSAGE), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShippingaddressActivty.this);
                                    alertDialog.setMessage( ""+object.getString(Constant.SetAddress.MESSAGE));
                                    alertDialog.setCancelable( false );
                                    alertDialog.setTitle( "Steezle" );
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();

                                            finish();

                                        }
                                    });
                                    alertDialog.show();

                                    /*Intent i = new Intent(ShippingaddressActivty.this, PaymentActivity.class);
                                    i.putExtra("CartProductArray", cartProductArray);
                                    startActivity(i);*/

                                } else {

                                    Toast.makeText(getApplicationContext(), object.getString(Constant.SetAddress.MESSAGE), Toast.LENGTH_LONG).show();
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
    public void onCountrySelect(EditText editText,EditText editTextS,CountryModel countrymodel, int pos) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            editText.setText(countrymodel.getName());
//            edt_shipping_country.setText(countrymodel.getName());
            editTextS.setText( "" );
//            edt_billing_state.setText( "" );
//            Toast.makeText(getApplicationContext(), countrymodel.getCode(), Toast.LENGTH_LONG).show();
            getState(countrymodel.getCode() , editTextS);
        }
    }

    @Override
    public void onStateSelected(EditText editText,StateModel stateModel) {
        if (stateDialog != null && stateDialog.isShowing()) {
            stateDialog.dismiss();
            editText.setText(stateModel.getName());
//            edt_shipping_state.setText(stateModel.getName());
//            Toast.makeText(getApplicationContext(), stateModel.getCode(), Toast.LENGTH_LONG).show();
        }
    }



}


    /*@SuppressLint("StaticFieldLeak")
    private class shippsetAddress extends AsyncTask<String, String, String> {


        private shippsetAddress() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(final String... strings) {


            InputStream inputStream = null;
            String result = "";

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(APIs.setaddress);
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();

                object.put("name", edt_billing_fullName.getText().toString());
                object.put("address1", edt_shipping_address1.getText().toString());
                object.put("address2", edt_shipping_address2.getText().toString());
                object.put("phone", edt_billing_phone.getText().toString());
                object.put("country", edt_billing_country.getText().toString());
                object.put("state", edt_billing_state.getText().toString());
                object.put("city", edt_billing_city.getText().toString());
                object.put("postcode", edt_billing_zip.getText().toString());
                object.put("email", user.getUser_email());

                JSONArray array1 = new JSONArray();

                JSONObject object1 = new JSONObject();

                object1.put("name", edt_billing_fullName.getText().toString());
                object1.put("address1", edt_billing_address1.getText().toString());
                object1.put("address2", edt_billing_address2.getText().toString());
                object.put("phone", edt_billing_phone.getText().toString());
                object1.put("country", edt_billing_country.getText().toString());
                object1.put("state", edt_billing_state.getText().toString());
                object1.put("city", edt_billing_city.getText().toString());
                object1.put("postcode", edt_billing_zip.getText().toString());
                object.put("email", user.getUser_email());

                bill = array.put(object).toString();
                shipp = array1.put(object1).toString();

                String str1 = bill.replaceAll("\\[", "").replaceAll("\\]", "");
                String str2 = shipp.replaceAll("\\[", "").replaceAll("\\]", "");

                user = AppSharedPreferences.getSharePreference(ShippingaddressActivty.this).getUser();
                jsonObject.accumulate("user_id", user.getUser_id());
                jsonObject.accumulate("billing", str1);
                jsonObject.accumulate("shipping", str2);

                String json = "";
                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);

                httpPost.setEntity(se);

                httpPost.setHeader("Content-type", "application/json");

                httpPost.setHeader("application/json", "Accept ");
                HttpResponse httpResponse = httpclient.execute(httpPost);

                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }

                    result = total.toString();


                    Toast.makeText(ShippingaddressActivty.this, result, Toast.LENGTH_LONG).show();
                } else
                    result = "Did not work!";


            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Log.e(TAG, "SetAddress--->" + response);

            if (response != null) {

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (obj.getString("status").equals("S")) {

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (obj.getString("status").equals("F")) {

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }*/





    /*class FetchingStudentAsync extends AsyncTask<String, String, String> {


        public FetchingStudentAsync() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(final String... strings) {


            InputStream inputStream = null;
            String result = "";

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(APIs.getaddress);

                JSONObject jsonObject = new JSONObject();
                user = AppSharedPreferences.getSharePreference(ShippingaddressActivty.this).getUser();
                jsonObject.accumulate("user_id", user.getUser_id());

                String json = "";
                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);

                httpPost.setEntity(se);

                httpPost.setHeader("Content-type", "application/json");

                httpPost.setHeader("application/json", "Accept ");

                HttpResponse httpResponse = httpclient.execute(httpPost);

                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }
                    result = total.toString();
                    Toast.makeText(ShippingaddressActivty.this, result, Toast.LENGTH_LONG).show();

                } else
                    result = "Did not work!";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Log.e(TAG, "GetAddress--->" + response);

            if (response != null) {

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (obj.getString("status").equals("S")) {
                        obj1 = obj.getJSONObject("data");
                        JSONObject obj2 = obj1.getJSONObject("billing");
                        edt_billing_fullName.setText(obj2.getString("name"));
                        edt_billing_address1.setText(obj2.getString("address_1"));
                        edt_billing_address2.setText(obj2.getString("address_2"));
                        edt_billing_phone.setText(obj2.getString("phone"));
                        edt_billing_country.setText(obj2.getString("country"));
                        edt_billing_city.setText(obj2.getString("city"));
                        edt_billing_state.setText(obj2.getString("state"));
                        edt_billing_zip.setText(obj2.getString("postcode"));

                        JSONObject obj3 = obj1.getJSONObject("shipping");
                        edt_shipping_fullName.setText(obj3.getString("name"));
                        edt_shipping_address1.setText(obj3.getString("address_1"));
                        edt_shipping_address2.setText(obj3.getString("address_2"));
                        edt_shipping_city.setText(obj3.getString("city"));
                        edt_shipping_zipcode.setText(obj3.getString("postcode"));
                        edt_shipping_country.setText(obj3.getString("country"));
                        edt_shipping_state.setText(obj3.getString("state"));

                        *//*switch_sameAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    JSONObject obj3 = null;
                                    try {
                                        obj3 = obj1.getJSONObject("shipping");
                                        edt_shipping_fullName.setText(obj3.getString("name"));
                                        edt_shipping_address1.setText(obj3.getString("address_1"));
                                        edt_shipping_address2.setText(obj3.getString("address_2"));
                                        edt_shipping_city.setText(obj3.getString("city"));
                                        edt_shipping_zipcode.setText(obj3.getString("postcode"));
                                        edt_shipping_country.setText(obj3.getString("country"));
                                        edt_shipping_state.setText(obj3.getString("state"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    edt_shipping_fullName.setText("");
                                    edt_shipping_address1.setText("");
                                    edt_shipping_address2.setText("");
                                    edt_shipping_country.setText("");
                                    edt_shipping_city.setText("");
                                    edt_shipping_state.setText("");
                                    edt_shipping_zipcode.setText("");
                                }
                            }
                        });*//*


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }*/






  /*private void getCountryList() {

        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, countries,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            JSONArray jarrary = obj.getJSONArray("data");

                            for (int i = 0; i < jarrary.length(); i++) {
                                //gets each JSON object within the JSON array
                                JSONObject jsonObject = jarrary.getJSONObject(i);

                                if (jsonObject.getString("name").equals("Canada") ||
                                        jsonObject.getString("name").equals("United States (US)")) {


                                    CountryModel hero = new CountryModel(
                                            jsonObject.getString("name"),
                                            jsonObject.getString("code"));


                                    countryArrayList.add(hero);
                                }
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
                        Toast.makeText(ShippingaddressActivty.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "13");

                return params;
            }
        };

        VolleySingleton.getInstance(ShippingaddressActivty.this);
        requestQueue.add(stringRequest);
    }*/



   /*private void getStateList(final String code) {

        if (stateArrayList != null)
            stateArrayList.clear();

        showpDialog();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, APIs.state,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            JSONArray jarrary = obj.getJSONArray("data");

                            for (int i = 0; i < jarrary.length(); i++) {
                                //gets each JSON object within the JSON array
                                JSONObject jsonObject = jarrary.getJSONObject(i);

                                StateModel hero = new StateModel(
                                        jsonObject.getString("name"),
                                        jsonObject.getString("code"));


                                stateArrayList.add(hero);
                            }

                            if (stateAdapter != null)
                                stateAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ShippingaddressActivty.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        hidepDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                params.put("country_code", code);


                return params;
            }
        };

        VolleySingleton.getInstance(ShippingaddressActivty.this);
        requestQueue.add(stringRequest);
    }*/



    /*private void setAddress() {

        try {

            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();

            object.put(Constant.SetAddress.NAME, edt_billing_firstName.getText().toString());
            object.put(Constant.SetAddress.ADDRESS_1, edt_shipping_address1.getText().toString());
            object.put(Constant.SetAddress.ADDRESS_2, edt_shipping_address2.getText().toString());
            object.put(Constant.SetAddress.PHONE, edt_billing_phone.getText().toString());
            object.put(Constant.SetAddress.COUNTRY, "a");
            object.put(Constant.SetAddress.STATE, "a");
            object.put(Constant.SetAddress.CITY, edt_billing_city.getText().toString());
            object.put(Constant.SetAddress.POST_CODE, edt_billing_zip.getText().toString());
            object.put(Constant.SetAddress.EMAIL, "dhaduk.juli22@gmail.com");

            JSONArray array1 = new JSONArray();

            JSONObject object1 = new JSONObject();

            object1.put(Constant.SetAddress.NAME, edt_billing_firstName.getText().toString());
            object1.put(Constant.SetAddress.ADDRESS_1, edt_shipping_address1.getText().toString());
            object1.put(Constant.SetAddress.ADDRESS_2, edt_shipping_address2.getText().toString());
            object1.put(Constant.SetAddress.PHONE, edt_billing_phone.getText().toString());
            object1.put(Constant.SetAddress.COUNTRY, "a");
            object1.put(Constant.SetAddress.STATE, "a");
            object1.put(Constant.SetAddress.CITY, edt_billing_city.getText().toString());
            object1.put(Constant.SetAddress.POST_CODE, edt_billing_zip.getText().toString());
            object1.put(Constant.SetAddress.EMAIL, "dhaduk.juli22@gmail.com");

            String bill = array.put(object).toString();
            String shipp = array1.put(object1).toString();

            billing = bill.replaceAll("\\[", "").replaceAll("\\]", "");
            shipping = shipp.replaceAll("\\[", "").replaceAll("\\]", "");


        } catch (Exception e) {
            e.printStackTrace();
        }

        user = AppSharedPreferences.getSharePreference(ShippingaddressActivty.this).getUser();
        SetAddressService.setAddress(
                this,
                user.getUser_id(),
                billing,
                shipping,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "SetAddressRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject object = new JSONObject(response);

                                if (object.getString(Constant.SetAddress.STATUS).equals("S")) {

                                    progressBar.setVisibility(View.GONE);
                                    Intent i = new Intent(ShippingaddressActivty.this, PaymentActivity.class);
                                    i.putExtra("CartProductArray", cartProductArray);
                                    startActivity(i);

                                } else {

                                    Toast.makeText(getApplicationContext(), object.getString(Constant.SetAddress.MESSAGE), Toast.LENGTH_LONG).show();
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
    }*/