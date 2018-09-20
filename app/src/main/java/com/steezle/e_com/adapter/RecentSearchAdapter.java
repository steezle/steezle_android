package com.steezle.e_com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.steezle.e_com.R;
import com.steezle.e_com.model.SearchItem;

import java.util.ArrayList;
import java.util.List;


public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.HeroViewHolder> {

    private List<SearchItem> heroList;
    private Context context;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;
    private static int currentPosition = 0;
    private SearchListener searchListener;

    public RecentSearchAdapter(
            Context context,
            ArrayList<SearchItem> heroList,
            SearchListener searchListener) {
        this.heroList = heroList;
        this.context = context;
        this.searchListener = searchListener;
        this.progressBar = progressBar;
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_search_row, parent, false);
        return new HeroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroViewHolder holder, int position) {

        SearchItem searchItem = heroList.get(position);

        holder.tv_search.setText(searchItem.getSearchText());

        holder.setOrderModel(searchItem);
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    class HeroViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_search;
        private SearchItem searchItem;

        HeroViewHolder(View itemView) {
            super(itemView);

            tv_search = (TextView) itemView.findViewById(R.id.tv_search);

            tv_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    searchListener.onSearch(searchItem);
                }
            });
        }

        public void setOrderModel(SearchItem searchItem) {
            this.searchItem = searchItem;
        }
    }


    public interface SearchListener {
        public void onSearch(SearchItem searchItem);
    }
}