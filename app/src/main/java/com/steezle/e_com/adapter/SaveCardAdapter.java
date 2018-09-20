package com.steezle.e_com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.steezle.e_com.R;

import com.steezle.e_com.model.SaveCardModel;
import com.steezle.e_com.networking.CustomVolleyRequest;

import java.util.List;


public class SaveCardAdapter extends RecyclerView.Adapter<SaveCardAdapter.HeroViewHolder> {

    private List<SaveCardModel> heroList;
    private Context context;
    private ImageLoader imageLoader;
    private static int currentPosition = 0;
    private OnRemoveCard onRemoveCard;

    public SaveCardAdapter(List<SaveCardModel> heroList, Context context, OnRemoveCard onRemoveCard) {
        this.heroList = heroList;
        this.context = context;
        this.onRemoveCard = onRemoveCard;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_save_item, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {
        SaveCardModel hero = heroList.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        holder.tv_saveCard.setText(hero.getCard_string());
        holder.setSavCard(hero, position);

    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    public class HeroViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_saveCard, tv_removeCard;
        private int pos;
        private SaveCardModel saveCardModel;

        HeroViewHolder(View itemView) {
            super(itemView);

            tv_saveCard = (TextView) itemView.findViewById(R.id.tv_saveCard);
            tv_removeCard = (TextView) itemView.findViewById(R.id.tv_removeCard);

            tv_removeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRemoveCard != null)
                        onRemoveCard.onRemoveCard(saveCardModel, pos);
                }
            });
        }

        public void setSavCard(SaveCardModel saveCardModel, int position) {
            this.saveCardModel = saveCardModel;
            pos = position;
        }
    }

    public interface OnRemoveCard {

        public void onRemoveCard(SaveCardModel saveCardModel, int pos);
    }
}