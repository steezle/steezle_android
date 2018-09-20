package com.steezle.e_com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.steezle.e_com.R;
import com.steezle.e_com.model.SaveCardModel;
import com.steezle.e_com.networking.CustomVolleyRequest;

import java.util.List;


public class SaveCardpaymnetAdapter extends RecyclerView.Adapter<SaveCardpaymnetAdapter.HeroViewHolder> {

    private List<SaveCardModel> heroList;
    private Context context;
    private ImageLoader imageLoader;
    private static int currentPosition = 0;
    private OnRemoveCard onRemoveCard;
    private int lastCheckedPosition = -1;


    public SaveCardpaymnetAdapter(List<SaveCardModel> heroList, Context context, SaveCardpaymnetAdapter.OnRemoveCard onRemoveCard) {
        this.heroList = heroList;
        this.context = context;
        this.onRemoveCard = onRemoveCard;
    }


    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_save_swipe, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {
        SaveCardModel hero = heroList.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        holder.tv_saveCard.setText(hero.getCard_string());
        holder.setSavCard(hero, position);

        if(lastCheckedPosition == position) {
            holder.radio_card.setChecked(true);
        } else {
            holder.radio_card.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    public class HeroViewHolder extends RecyclerView.ViewHolder {

        //private RadioGroup radioGroup_card;
        private RadioButton radio_card;
        private TextView tv_saveCard;
        private int pos;
        private SaveCardModel saveCardModel;


        HeroViewHolder(View itemView) {
            super(itemView);

            tv_saveCard = (TextView) itemView.findViewById(R.id.tv_saveCard);
            //radioGroup_card = (RadioGroup) itemView.findViewById(R.id.radioGroup_card);
            radio_card = (RadioButton) itemView.findViewById(R.id.radio_card);

            radio_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    radio_card.setChecked(true);
                    if (onRemoveCard != null)
                        onRemoveCard.onRemoveCard(saveCardModel, pos);

                    lastCheckedPosition = pos;

                    notifyDataSetChanged();
                }
            });
           /* radioGroup_card.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup arg0, int id) {

                    boolean isChecked = radio_card.isChecked();

                    if (isChecked) {

                        radio_card.setChecked(true);

                        if (onRemoveCard != null)
                            onRemoveCard.onRemoveCard(saveCardModel, pos);
                    } else
                        radio_card.setChecked(false);
                }
            });*/
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