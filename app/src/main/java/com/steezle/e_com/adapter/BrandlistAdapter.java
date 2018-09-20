package com.steezle.e_com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.steezle.e_com.R;
import com.steezle.e_com.model.BrandModel;

import java.util.List;


/**
 * Created by Belal on 7/15/2017.
 */
public class BrandlistAdapter extends RecyclerView.Adapter<BrandlistAdapter.HeroViewHolder> {

    private List<BrandModel> brandArrayList;
    private Context context;
    private ImageLoader imageLoader;
    private static int currentPosition = 0;
    private OnBrandCheck onBrandCheck;

    public BrandlistAdapter(List<BrandModel> brandArrayList, Context context, OnBrandCheck onBrandCheck) {
        this.brandArrayList = brandArrayList;
        this.context = context;
        this.onBrandCheck = onBrandCheck;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.act_brandlist_item, parent, false);
        return new HeroViewHolder(v);
    }
    SpannableStringBuilder mSSBuilder;
    String mText="Yung2";
    @Override
    public void onBindViewHolder(HeroViewHolder holder, final int position) {

        final BrandModel brandModel = brandArrayList.get(position);


        if (brandModel.getBrand().equalsIgnoreCase( mText ))
        {
            // Initialize a new SpannableStringBuilder instance
            mSSBuilder = new SpannableStringBuilder(mText);
            // Initialize a new SuperscriptSpan instance
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();

// Apply the SuperscriptSpan
            mSSBuilder.setSpan(
                    superscriptSpan,
                    mText.indexOf("2"),
                    mText.indexOf("2") + String.valueOf("2").length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            showSmallSizeText("2");
            holder.tv_brandName.setText(mSSBuilder);
//            holder.tv_brandName.setText(  "Yung"+ Html.fromHtml(context.getResources().getString(R.string.super_2)),);
        }
        else
        holder.tv_brandName.setText(brandModel.getBrand());


        //in some cases, it will prevent unwanted situations
        holder.cb_brand.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.cb_brand.setChecked(brandModel.isStatus());

        holder.cb_brand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                brandModel.setStatus(isChecked);
                onBrandCheck.onBrandCheck(brandModel, position);
            }
        });

        holder.setBrandModel(brandModel, position);

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
        return brandArrayList.size();
    }


    public class HeroViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_checkBox;
        private TextView tv_brandName;
        public NetworkImageView imageView;
        private CheckBox cb_brand;
        private BrandModel brandModel;
        private int pos;

        HeroViewHolder(View itemView) {
            super(itemView);
            tv_brandName = (TextView) itemView.findViewById(R.id.tv_brandName);
            cb_brand = (CheckBox) itemView.findViewById(R.id.cb_brand);
            iv_checkBox = (ImageView) itemView.findViewById(R.id.iv_checkBox);

        }

        public void setBrandModel(BrandModel brandModel, int pos) {
            this.brandModel = brandModel;
            this.pos = pos;
        }
    }

    public interface OnBrandCheck {
        public void onBrandCheck(BrandModel brandModel, int pos);
    }

    // Custom method to make text size small

}