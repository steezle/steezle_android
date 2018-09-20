package com.steezle.e_com.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.steezle.e_com.R;


import java.util.ArrayList;
import java.util.List;

import com.steezle.e_com.model.GetfvtModel;
import com.steezle.e_com.networking.CustomVolleyRequest;
import com.steezle.e_com.view.ShoppigbagActivity;

import org.json.JSONException;

public class MysteezleAdapter extends RecyclerView.Adapter<MysteezleAdapter.HeroViewHolder> {

    private List<GetfvtModel> getFavArrayList;
    private Context context;
    private ImageLoader imageLoader;
    private static int currentPosition = 0;
    private OnRemoveFromFavorite onRemoveFromFavorite;
    private OnAddToBag onAddToBag;
    private OnProductDetails onProductDetails;
    SpannableStringBuilder mSSBuilder;
    String mText="Yung2";

    public MysteezleAdapter(List<GetfvtModel> getFavArrayList,
                            Context context,
                            OnRemoveFromFavorite onRemoveFromFavorite,
                            OnAddToBag onAddToBag,
                            OnProductDetails onProductDetails) {
        this.getFavArrayList = getFavArrayList;
        this.context = context;
        this.onRemoveFromFavorite = onRemoveFromFavorite;
        this.onAddToBag = onAddToBag;
        this.onProductDetails = onProductDetails;

    }


    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_bag, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {

        GetfvtModel getfvtModel = getFavArrayList.get(position);
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        imageLoader.get(getfvtModel.getProduct_image(),
                ImageLoader.getImageListener(
                        holder.product_image,
                        R.drawable.empty_menu,
                        R.drawable.empty_menu));

        holder.product_image.setImageUrl(getfvtModel.getProduct_image(), imageLoader);
        holder.tv_productName.setText(getfvtModel.getProduct_name());

        if (getfvtModel.getBrand().equalsIgnoreCase( mText )) {
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
        } else
        holder.tv_brandName.setText(getfvtModel.getBrand());
        holder.tv_price.setText("$ " + getfvtModel.getProduct_price());

        holder.setFavModel(getfvtModel, position);
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
        return getFavArrayList.size();
    }

    class HeroViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rl_main;
        private TextView tv_productName, tv_brandName, tv_price, txtclose;
        private Button btn_addToBag;
        private NetworkImageView product_image;
        private ImageView iv_removeProduct;
        private GetfvtModel getfvtModel;
        private int position;

        HeroViewHolder(View itemView) {
            super(itemView);

            btn_addToBag = (Button) itemView.findViewById(R.id.btn_addToBag);
            tv_productName = (TextView) itemView.findViewById(R.id.tv_productName);
            tv_brandName = (TextView) itemView.findViewById(R.id.tv_brandName);
            iv_removeProduct = (ImageView) itemView.findViewById(R.id.iv_removeProduct);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            product_image = (NetworkImageView) itemView.findViewById(R.id.product_image);
            rl_main = (RelativeLayout) itemView.findViewById(R.id.rl_main);

            iv_removeProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRemoveFromFavorite != null)
                        onRemoveFromFavorite.onRemoveClick(getfvtModel, position);
                }
            });

            btn_addToBag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onAddToBag != null) {
                        try {
                            onAddToBag.onAddToBagClick(getfvtModel, position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductDetails != null)
                        onProductDetails.onProductDetailClick(getfvtModel, position);
                }
            });

        }

        public void setFavModel(GetfvtModel favModel, int pos) {
            getfvtModel = favModel;
            position = pos;
        }

    }

    public interface OnRemoveFromFavorite {
        public void onRemoveClick(GetfvtModel getfvtModel, int pos);
    }


    public interface OnAddToBag {
        public void onAddToBagClick(GetfvtModel getfvtModel, int pos) throws JSONException;
    }

    public interface OnProductDetails {
        public void onProductDetailClick(GetfvtModel getfvtModel, int pos);
    }

}