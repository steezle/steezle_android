package com.steezle.e_com.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.steezle.e_com.R;
import com.steezle.e_com.model.SubcategoryModel;
import com.steezle.e_com.networking.CustomVolleyRequest;
import com.steezle.e_com.view.Productlist;
import com.steezle.e_com.view.TabActivity;

import java.util.List;


public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.HeroViewHolder> {

    private List<SubcategoryModel> heroList;
    private Context context;
    private ImageLoader imageLoader;
    private static int currentPosition = 0;

    public SubcategoryAdapter(List<SubcategoryModel> heroList, Context context) {
        this.heroList = heroList;
        this.context = context;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.act_sub_category_item, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {
        SubcategoryModel subcategoryModel = heroList.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        imageLoader.get(subcategoryModel.getImage(),
                ImageLoader.getImageListener(
                        holder.product_image,
                        R.drawable.empty_menu,
                        R.drawable.empty_menu));


        //String brandName = ProjectUtility.toCamelCaseWord(hero.getName());
        //holder.tv_brandName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.f);
        holder.tv_brandName.setText(subcategoryModel.getName());
        holder.product_image.setImageUrl(subcategoryModel.getImage(), imageLoader);
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }


    class HeroViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_brandName;
        public NetworkImageView product_image;

        HeroViewHolder(View itemView) {
            super(itemView);

            tv_brandName = (TextView) itemView.findViewById(R.id.tv_brandName);
            product_image = (NetworkImageView) itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent( context, Productlist.class );
                    i.putExtra("id", heroList.get(getAdapterPosition()).getId());
                    i.putExtra("From", "Category");
                    context.startActivity( i );

                }
            });

        }

    }

}