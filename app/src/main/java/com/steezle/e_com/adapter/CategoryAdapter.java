package com.steezle.e_com.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.steezle.e_com.R;

import com.steezle.e_com.model.CategoryModel;
import com.steezle.e_com.networking.CustomVolleyRequest;

import com.steezle.e_com.view.Productlist;
import com.steezle.e_com.view.SubCategory;
import com.steezle.e_com.view.TabActivity;

import java.util.ArrayList;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.HeroViewHolder> {

    private List<CategoryModel> categoryArrayList;
    private Context context;
    private ImageLoader imageLoader;
    private static int currentPosition = 0;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> feedsList) {
        this.categoryArrayList = feedsList;
        this.context = context;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.act_category_item, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {
        CategoryModel hero = categoryArrayList.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        imageLoader.get(hero.getImage(),
                ImageLoader.getImageListener(
                        holder.product_image,
                        R.drawable.empty_menu,
                        R.drawable.empty_menu));

        //String catName = ProjectUtility.toCamelCaseWord(hero.getName());
        holder.tv_categoryName.setText(hero.getName());
        holder.product_image.setImageUrl(hero.getImage(), imageLoader);
    }


    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }


    public class HeroViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_categoryName;
        private NetworkImageView product_image;

        HeroViewHolder(View itemView) {
            super(itemView);

            tv_categoryName = (TextView) itemView.findViewById(R.id.tv_categoryName);
            product_image = (NetworkImageView) itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (categoryArrayList.get(getAdapterPosition()).getId().equals("0")) {

                        Log.e( "CategoryAdapter","0" );
                        Intent i=new Intent( context, Productlist.class );
                        i.putExtra("categoryid", categoryArrayList.get(getAdapterPosition()).getId());
                        i.putExtra("categoryname", categoryArrayList.get(getAdapterPosition()).getName());
                        i.putExtra("productname", categoryArrayList.get(getAdapterPosition()).getName());
                        i.putExtra("ids", categoryArrayList.get(getAdapterPosition()).getId());
                        i.putExtra("id", "0");
                        context.startActivity( i );

                    } else {
                        Log.e( "CategoryAdapter","1" );
                        Intent i=new Intent( context, SubCategory.class );
                        i.putExtra("categoryid", categoryArrayList.get(getAdapterPosition()).getId());
                        i.putExtra("categoryname", categoryArrayList.get(getAdapterPosition()).getName());
                        context.startActivity( i );


                    }

                }
            });


        }
    }
}

