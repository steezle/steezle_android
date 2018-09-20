package com.steezle.e_com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import com.steezle.e_com.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.steezle.e_com.model.Order_Model;
import com.steezle.e_com.view.OrderDetailsActivity;
import com.steezle.e_com.utils.ProjectUtility;


public class OderListAdapter extends RecyclerView.Adapter<OderListAdapter.HeroViewHolder> {

    private List<Order_Model> heroList;
    private Context context;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;
    private static int currentPosition = 0;
    private CancelOrder cancelOrder;
    private TextView tvstatus;

    public OderListAdapter(ArrayList<Order_Model> heroList, Context context, CancelOrder cancelOrder) {
        this.heroList = heroList;
        this.context = context;
        this.progressBar = progressBar;
        this.cancelOrder = cancelOrder;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new HeroViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {

        Order_Model order_model = heroList.get(position);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date parsed = null; // => Date is in UTC now
        try {
            parsed = sourceFormat.parse(order_model.getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //TimeZone tz = TimeZone.getTimeZone("America/Chicago");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat destFormat = new SimpleDateFormat("hh:mm a - MMM d, yyyy");
        destFormat.setTimeZone(TimeZone.getDefault());

        String result = destFormat.format(parsed);
        String[] timeDate = result.split("\\-");
        String time = timeDate[0];
        String date = timeDate[1];


        holder.tv_order.setText("#" + order_model.getOrder_number());
        holder.tv_order_date.setText(time + "on" + date);
        holder.tv_order_price.setText("$" + order_model.getTotal());

        String status = ProjectUtility.toCamelCaseWord(order_model.getStatus());
        holder.tv_order_status.setText(status);

        if (order_model.getStatus().equals("completed")) {
            holder.iv_dot.setBackgroundResource(R.drawable.round_dot_green);
            holder.tv_order_status.setBackgroundResource(R.drawable.round_complete_green);
            holder.tv_cancel_order.setVisibility(View.VISIBLE);
        } else {
            holder.iv_dot.setBackgroundResource(R.drawable.round_dot_yellow);
            holder.tv_order_status.setBackgroundResource(R.drawable.round_cancel_yellow);
            holder.tv_cancel_order.setVisibility(View.INVISIBLE);
        }

        String item = " item";
        if (order_model.getTotal_line_items_quantity().equalsIgnoreCase( "1" )) {
            holder.tv_order_item.setText(order_model.getTotal_line_items_quantity() + item);
        } else {
            holder.tv_order_item.setText(order_model.getTotal_line_items_quantity() + " items");
        }

        holder.setOrderModel(order_model);
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    class HeroViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_order, tv_order_date, tv_order_price, tv_order_item, tv_cancel_order, tv_order_status;
        private Order_Model order_model;
        private ImageView iv_dot;

        HeroViewHolder(View itemView) {
            super(itemView);
            tv_order = (TextView) itemView.findViewById(R.id.tv_order);
            tv_order_date = (TextView) itemView.findViewById(R.id.tv_order_date);
            tv_order_price = (TextView) itemView.findViewById(R.id.tv_order_price);
            iv_dot = (ImageView) itemView.findViewById(R.id.iv_dot);
            tv_order_item = (TextView) itemView.findViewById(R.id.tv_order_item);
            tv_cancel_order = (TextView) itemView.findViewById(R.id.tv_cancel_order);
            tv_order_status = (TextView) itemView.findViewById(R.id.tv_order_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), OrderDetailsActivity.class);
                    i.putExtra("id", heroList.get(getAdapterPosition()).getId());
                    i.putExtra("Order_Model", order_model);
                    v.getContext().startActivity(i);
                }
            });

            tv_cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cancelOrder != null)
                        cancelOrder.onOrderCancel(order_model, iv_dot, tv_order_status,tv_cancel_order);
                }
            });
        }

        public void setOrderModel(Order_Model order_model) {
            this.order_model = order_model;
        }
    }

    public interface CancelOrder {
        public void onOrderCancel(Order_Model order_model, ImageView dot, TextView tv_order_status, TextView tv_cancel_order);
    }
}