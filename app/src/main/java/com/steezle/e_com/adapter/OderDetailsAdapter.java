package com.steezle.e_com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.steezle.e_com.R;

import java.util.List;

import com.steezle.e_com.model.OrderhistoryModel;
import com.steezle.e_com.networking.CustomVolleyRequest;


public class OderDetailsAdapter extends RecyclerView.Adapter<OderDetailsAdapter.HeroViewHolder> {

    private List<OrderhistoryModel> heroList;
    private Context context;
    private ImageLoader imageLoader;
    private static int currentPosition = 0;

    public OderDetailsAdapter(List<OrderhistoryModel> heroList, Context context) {
        this.heroList = heroList;
        this.context = context;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderhistory, parent, false);
        return new HeroViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {

        OrderhistoryModel orderhistoryModel = heroList.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(orderhistoryModel.getProduct_thumbnail_url(),
                ImageLoader.getImageListener(
                        holder.product_image,
                        R.drawable.empty_menu,
                        R.drawable.empty_menu));

       /* Glide.with(context)
                .load(orderhistoryModel.getProduct_thumbnail_url())
                .thumbnail(0.1f)
                .apply(new RequestOptions()
                .placeholder(R.drawable.empty_menu)
                .error(R.drawable.empty_menu))
                .into(holder.product_image);*/

        holder.product_image.setImageUrl(orderhistoryModel.getProduct_thumbnail_url(), imageLoader);
        holder.tv_productName.setText(orderhistoryModel.getName());

        if(orderhistoryModel.getBrand().equals("Young2"))
            holder.tv_brandName.setText(Html.fromHtml("Yung<sup><small>2</small></sup>"));
        else
            holder.tv_brandName.setText(orderhistoryModel.getBrand());
        holder.tv_order_price.setText("$" + orderhistoryModel.getPrice());
        holder.tv_quantity.setText("Qty." + orderhistoryModel.getQuantity());

//        holder.tv_size.setText(orderhistoryModel.getAttribute_pa_size());

        try{
            String temp="";
            for (int i=0;i<orderhistoryModel.getVaiations_list().size();i++)
            {
                if (i==orderhistoryModel.getVaiations_list().size()-1)
                    temp=temp+""+orderhistoryModel.getVaiations_list().get( i );
                else
                    temp=temp+""+orderhistoryModel.getVaiations_list().get( i )+", ";
            }
            Log.e( "temp",""+temp );
            holder.tv_size.setText(temp);

        }catch (Exception e){
            e.printStackTrace();
            holder.tv_size.setVisibility( View.GONE );
        }

    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    class HeroViewHolder extends RecyclerView.ViewHolder {
        TextView tv_productName, tv_brandName, tv_order_price, tv_quantity, tv_size;
        public NetworkImageView product_image;

        HeroViewHolder(View itemView) {
            super(itemView);
            tv_productName = (TextView) itemView.findViewById(R.id.tv_productName);
            tv_brandName = (TextView) itemView.findViewById(R.id.tv_brandName);
            tv_order_price = (TextView) itemView.findViewById(R.id.tv_order_price);
            tv_quantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            product_image = (NetworkImageView) itemView.findViewById(R.id.product_image);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);

        }
    }

}
