package com.steezle.e_com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.steezle.e_com.R;
import com.steezle.e_com.model.CountryModel;

import java.util.ArrayList;


public class CountryAdpter extends RecyclerView.Adapter<CountryAdpter.HeroViewHolder> {

    private ArrayList<CountryModel> countryArrayList;
    private Context context;
    private static int currentPosition = 0;
    private OnCountrySelected onCountrySelected;
    EditText editText, editTextS;

    public CountryAdpter(EditText editText, EditText editTextS, ArrayList<CountryModel> countryArrayList, Context context, OnCountrySelected onCountrySelected) {
        this.editText=editText;
        this.editTextS=editTextS;
        this.countryArrayList = countryArrayList;
        this.context = context;
        this.onCountrySelected = onCountrySelected;
    }


    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {
        CountryModel countryModel = countryArrayList.get(position);

        holder.tv_country.setText(countryModel.getName());
        holder.setState(countryModel, position);
    }

    @Override
    public int getItemCount() {
        return countryArrayList.size();
    }

    public class HeroViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_country;
        private int pos;
        private CountryModel countrymodel;

        HeroViewHolder(View itemView) {
            super(itemView);

            tv_country = (TextView) itemView.findViewById(R.id.tv_country);

            tv_country.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onCountrySelected != null)
                        onCountrySelected.onCountrySelect(editText,editTextS,countrymodel, pos);
                }
            });
        }

        public void setState(CountryModel countrymodel, int position) {
            this.countrymodel = countrymodel;
            pos = position;
        }
    }

    public interface OnCountrySelected {
        public void onCountrySelect(EditText editText,EditText editTextS,CountryModel countrymodel, int pos);
    }

}