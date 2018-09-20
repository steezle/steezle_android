package com.steezle.e_com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.plus.Plus;
import com.steezle.e_com.R;
import com.steezle.e_com.model.GetcartModel;
import com.steezle.e_com.model.GetfvtModel;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.networking.CustomVolleyRequest;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.CheckOutService;
import com.steezle.e_com.utils.Constant;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class GetCartAdapter extends RecyclerView.Adapter<GetCartAdapter.HeroViewHolder> {

    public static final String TAG = GetCartAdapter.class.getSimpleName();
    public static final String mypreference = "mypref";

    private User user;
    private List<GetcartModel> getCartArrayList;
    private Context context;
    private ImageLoader imageLoader;
    private OnRemoveFromShopping onRemoveFromShopping;
    private List<NameValuePair> params11;
    //private String productid, variationid, total/*, updatecard*/;
    private TextView total_steez;
    private TextView tv_totalAmount;
    private String PLUS = "Plus";
    private String MINUS = "Minus";
    private OnProductDetail onProductDetail;
    SpannableStringBuilder mSSBuilder;
    String mText="Yung2";

    public GetCartAdapter(List<GetcartModel> getCartArrayList,
                          Context context,
                          GetCartAdapter.OnRemoveFromShopping onRemoveFromShopping,
                          TextView total_steez,
                          TextView tv_totalAmount,
                          OnProductDetail onProductDetail) {
        this.getCartArrayList = getCartArrayList;
        this.context = context;
        this.imageLoader = imageLoader;
        this.onRemoveFromShopping = onRemoveFromShopping;
        this.total_steez = total_steez;
        this.tv_totalAmount = tv_totalAmount;
        this.onProductDetail = onProductDetail;
    }

    @Override
    public GetCartAdapter.HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fvt_bag, parent, false);
        return new GetCartAdapter.HeroViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {

        GetcartModel getcartModel = getCartArrayList.get(position);


        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        imageLoader.get(getcartModel.getProduct_image(),
                ImageLoader.getImageListener(
                        holder.product_image,
                        R.drawable.empty_menu,
                        R.drawable.empty_menu));

        holder.tv_productName.setText(getcartModel.getProduct_title());
        holder.tv_brandName.setText(getcartModel.getBrand());

        if (getcartModel.getBrand().equalsIgnoreCase( mText )) {
            // Initialize a new SpannableStringBuilder instance
            mSSBuilder = new SpannableStringBuilder( mText );
            // Initialize a new SuperscriptSpan instance
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();

            // Apply the SuperscriptSpan
            mSSBuilder.setSpan(
                    superscriptSpan,
                    mText.indexOf( "2" ),
                    mText.indexOf( "2" ) + String.valueOf( "2" ).length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            showSmallSizeText( "2" );
            holder.tv_brandName.setText( mSSBuilder );
//            holder.tv_brandName.setText(  "Yung"+ Html.fromHtml(context.getResources().getString(R.string.super_2)),);
        }
        else
            holder.tv_brandName.setText(getcartModel.getBrand());
        holder.tv_quantity.setText(getcartModel.getQty());
        holder.tv_price.setText("$" + getcartModel.getPrice());

        try{
            String temp="";
            for (int i=0;i<getcartModel.getVaiations_list().size();i++)
            {
                if (i==getcartModel.getVaiations_list().size()-1)
                    temp=temp+""+getcartModel.getVaiations_list().get( i );
                else
                    temp=temp+""+getcartModel.getVaiations_list().get( i )+", ";
            }
            Log.e( "temp",""+temp );
            holder.tv_size.setText(temp);

        }catch (Exception e){
            e.printStackTrace();
            holder.tv_size.setVisibility( View.GONE );
        }


        holder.product_image.setImageUrl(getcartModel.getProduct_image(), imageLoader);

        //productid = getcartModel.getProduct_id();
        //variationid = getcartModel.getVariation_id();

        holder.setcartModel(getcartModel, position);
    }

    protected void showSmallSizeText(String textToSmall){
        // Initialize a new RelativeSizeSpan instance
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(.5f);

        // Apply the RelativeSizeSpan to display small text
        mSSBuilder.setSpan(
                relativeSizeSpan,
                mText.indexOf(textToSmall),
                mText.indexOf(textToSmall) + String.valueOf(textToSmall).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

    }
    @Override
    public int getItemCount() {
        return getCartArrayList.size();
    }

    public class HeroViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_main;
        private TextView tv_size;
        private TextView tv_productName, tv_brandName, tv_price, tv_quantity, tv_btn_plus, tv_btn_minus;
        public NetworkImageView product_image;
        private GetcartModel getcartModel;
        private int position;
        private ImageView iv_removeProduct;
        //private int quantity = 1;
        private ProgressBar progressBar;

        HeroViewHolder(View itemView) {
            super(itemView);

            tv_productName = (TextView) itemView.findViewById(R.id.tv_productName);
            tv_brandName = (TextView) itemView.findViewById(R.id.tv_brandName);
            tv_quantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            product_image = (NetworkImageView) itemView.findViewById(R.id.product_image);
            iv_removeProduct = (ImageView) itemView.findViewById(R.id.iv_removeProduct);
            tv_btn_plus = (TextView) itemView.findViewById(R.id.tv_btn_plus);
            tv_btn_minus = (TextView) itemView.findViewById(R.id.tv_btn_minus);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductDetail != null)
                        onProductDetail.onProductDetailClick(getcartModel, position);
                }
            });

            iv_removeProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRemoveFromShopping != null)
                        onRemoveFromShopping.onRemoveClick(getcartModel, position, tv_quantity);
                }
            });


            tv_btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!tv_quantity.getText().equals("0")) {

                        //progressBar.setVisibility(View.VISIBLE);
                        //quantity++;

                        int quantity = Integer.parseInt(tv_quantity.getText().toString());
                        quantity++;
                        JSONArray array1 = new JSONArray();
                        JSONObject object3 = new JSONObject();

                        try {
                            object3.put("product_id", getcartModel.getProduct_id());
                            object3.put("qty", ""+quantity);
                            object3.put("variation_id", getcartModel.getVariation_id());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //updatecard = array1.put(object3).toString();


                        checkOut(tv_quantity,
                                tv_btn_minus,
                                quantity,
                                array1.put(object3).toString(),
                                Double.parseDouble(getcartModel.getPrice()),
                                PLUS,
                                getcartModel,
                                progressBar);


                    } else {
                        tv_btn_plus.setEnabled(false);
                    }


                }
            });


            tv_btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Integer.parseInt(tv_quantity.getText().toString()) > 1) {

                        //progressBar.setVisibility(View.VISIBLE);

                        //quantity--;

                        int quantity = Integer.parseInt(tv_quantity.getText().toString());
                        quantity--;

                        JSONArray array1 = new JSONArray();
                        JSONObject object3 = new JSONObject();

                        try {
                            object3.put("product_id", getcartModel.getProduct_id());
                            object3.put("qty", ""+quantity);
                            object3.put("variation_id", getcartModel.getVariation_id());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //updatecard = array1.put(object3).toString();

                        checkOut(tv_quantity,
                                tv_btn_minus,
                                quantity,
                                array1.put(object3).toString(),
                                Double.parseDouble(getcartModel.getPrice()),
                                MINUS,
                                getcartModel,
                                progressBar);

                    } else {
                        tv_btn_minus.setEnabled(false);
                    }
                }
            });

        }

        private void setcartModel(GetcartModel cartModel, int pos) {
            getcartModel = cartModel;
            position = pos;
            //quantity = Integer.parseInt(cartModel.getQty());
        }
    }


    @SuppressLint("SetTextI18n")
    private void updatePrice(double price, String from) {

        String priceString = tv_totalAmount.getText().toString();

        String[] parts = priceString.split("\\$");
        String part1 = parts[0].trim();
        String part2 = parts[1].trim();

        double priceInt = Double.parseDouble(part2);

        if (from.equals(PLUS)) {

            double finalPrice = priceInt + price;
            tv_totalAmount.setText("$" + String.format("%.2f", finalPrice));

        } else if (from.equals(MINUS)) {

            double finalPrice = priceInt - price;
            tv_totalAmount.setText("$" + String.format("%.2f", finalPrice));

        }
    }


    public interface OnRemoveFromShopping {
        public void onRemoveClick(GetcartModel getcartModel, int pos, TextView tv_quantity);
    }


    private void checkOut(final TextView tv_quantity,
                          final TextView tv_btn_minus,
                          final int quantity,
                          final String updateBagObject,
                          final double price,
                          final String from,
                          final GetcartModel getcartModel,
                          final ProgressBar progressBar) {

        user = AppSharedPreferences.getSharePreference(context).getUser();
        CheckOutService.checkOut(
                context,
                user.getUser_id(),
                updateBagObject,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "CheckOut Response--->" + response);

                        if (response != null) {
                            try {
                                JSONObject json = new JSONObject(response);
                                String status = json.getString(Constant.CheckOut.STATUS);

                                if (status.equals("S")) {

                                    tv_quantity.setText(String.valueOf(quantity));
                                    total_steez.setText(json.getString(Constant.CheckOut.BAG_COUNT));
                                    AppSharedPreferences.getSharePreference(context).setCartCount(json.getString(Constant.CheckOut.BAG_COUNT));

                                    updatePrice(price, from);
                                    getcartModel.setQty(String.valueOf(quantity));
                                    //Toast.makeText(context, json.getString(Constant.CheckOut.MESSAGE), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                    if(from.equals(PLUS)) {
                                        if(Integer.parseInt(tv_quantity.getText().toString()) > 1) {
                                            tv_btn_minus.setEnabled(true);
                                        }
                                    }

                                } else {

                                    Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
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


    public interface OnProductDetail {
        public void onProductDetailClick(GetcartModel getcartModel, int pos);
    }


}


/*private void onbagapi() {

        user = AppSharedPreferences.getSharePreference(context).getUser();
        params11 = new ArrayList<NameValuePair>();
        params11.add(new BasicNameValuePair("updated_cart", updatecard));
        params11.add(new BasicNameValuePair("user_id", user.getUser_id()));
        new bagbackground().execute();
    }

    public class bagbackground extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(context);

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


                    Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
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

    }*/
