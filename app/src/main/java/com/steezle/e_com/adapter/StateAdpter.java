package com.steezle.e_com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.steezle.e_com.R;
import com.steezle.e_com.model.StateModel;

import java.util.ArrayList;


public class StateAdpter extends RecyclerView.Adapter<StateAdpter.HeroViewHolder> {

    private ArrayList<StateModel> stateArrayList;
    private Context context;
    private static int currentPosition = 0;
    private OnStateSelected onStateSelected;
    EditText editText;

    public StateAdpter(EditText editText,ArrayList<StateModel> stateArrayList, Context context, OnStateSelected onStateSelected) {
        this.editText=editText;
        this.stateArrayList = stateArrayList;
        this.context = context;
        this.onStateSelected = onStateSelected;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_state, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {
        StateModel stateModel = stateArrayList.get(position);

        holder.tv_stateName.setText(stateModel.getName());
        holder.setState(stateModel);

    }

    @Override
    public int getItemCount() {
        return stateArrayList.size();
    }

    public class HeroViewHolder extends RecyclerView.ViewHolder {

        TextView tv_stateName;
        StateModel stateModel;

        HeroViewHolder(View itemView) {
            super(itemView);
            tv_stateName = (TextView) itemView.findViewById(R.id.tv_stateName);

            tv_stateName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onStateSelected != null)
                        onStateSelected.onStateSelected(editText,stateModel);
                }
            });

        }

        public void setState(StateModel state) {
            this.stateModel = state;
        }
    }

    public interface OnStateSelected {
        public void onStateSelected(EditText editText,StateModel stateModel);
    }

}