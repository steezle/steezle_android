package com.steezle.e_com.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.steezle.e_com.R;
import com.steezle.e_com.adapter.RecentSearchAdapter;
import com.steezle.e_com.database.DatabaseHandler;
import com.steezle.e_com.model.SearchItem;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.networking.CommonValidation;
import com.steezle.e_com.services.APIService;
import com.steezle.e_com.services.SearchService;
import com.steezle.e_com.utils.Constant;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Search extends AppCompatActivity implements RecentSearchAdapter.SearchListener, View.OnClickListener {

    public static final String TAG = Search.class.getSimpleName();

    private ProgressBar progressBar;
    private EditText edt_search;
    private User user;
    private DatabaseHandler databaseHandler;
    private RecentSearchAdapter recentSearchAdapter;
    private ArrayList<SearchItem> searchItems;
    private RecyclerView rec_recentSearchList;
    private List<NameValuePair> params11;
    private TextView tv_clear, tv_back_search, tv_noProdctFound;
    RelativeLayout relative_background;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.act_search );
        user = AppSharedPreferences.getSharePreference(getApplicationContext()).getUser();
        databaseHandler = new DatabaseHandler(Search.this);
        //Initlization
        setUpView();
        fillRecentSearchAdapter();


        //Register Button Click Event
        tv_back_search.setOnClickListener(this);


        edt_search.requestFocus();
        CommonValidation.showKeyBoard(Search.this);
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    CommonValidation.closeKeybroad(Search.this);
                    try {
                        // Check if no view has focus:
                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

//                    insertHistory();
                    try{
                        if (user.getUser_id().length()==0)
                        {
                            searchResult(edt_search.getText().toString(),"");
                        }
                        else
                            searchResult( edt_search.getText().toString(),user.getUser_id());

                    }catch (Exception e){
                        e.printStackTrace();
                        searchResult(edt_search.getText().toString(),"");
                    }

                }
                return false;
            }
        });
    }


    private void setUpView() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        edt_search = (EditText)findViewById(R.id.edt_search);
        rec_recentSearchList = (RecyclerView)findViewById(R.id.rec_recentSearchList);
        tv_back_search = (TextView) findViewById(R.id.tv_back_search);
        tv_noProdctFound = (TextView)findViewById(R.id.tv_noProdctFound);
        tv_clear = (TextView)findViewById(R.id.tv_clear);
        relative_background=(RelativeLayout)findViewById( R.id.relative_background );
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_back_search:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();

                break;
        }
    }


    private void fillRecentSearchAdapter() {

        searchItems = databaseHandler.getHealthRecordByPatientId();

        recentSearchAdapter = new RecentSearchAdapter(Search.this,
                searchItems, this);

        GridLayoutManager manager = new GridLayoutManager(Search.this, 1, GridLayoutManager.VERTICAL, false);
        rec_recentSearchList.setLayoutManager(manager);
        rec_recentSearchList.setAdapter(recentSearchAdapter);

        if(searchItems.size() > 0) {
            relative_background.setVisibility(View.GONE);
        } else {
            relative_background.setVisibility(View.VISIBLE);
        }

    }


    private void searchResult(final String searchText, String userID) {

        SearchService.getSearch(
                Search.this,
                userID,
                searchText/*sv_search.getQuery().toString()*//*edt_search.getText().toString()*/,
                progressBar,
                new APIService.Success<String>() {
                    @Override
                    public void onSuccess(String response) {

                        Log.e(TAG, "SearchRes--->" + response);

                        if (response != null) {

                            try {

                                JSONObject json = new JSONObject(response);

                                if (json.getString(Constant.SearchList.STATUS).equals("S")) {

                                    insertHistory();

                                    Intent i=new Intent( getApplicationContext(), Productlist.class );
                                    i.putExtra("id", "");
                                    i.putExtra("From", "Search");
                                    i.putExtra("Data", json.getString("data"));
                                    i.putExtra("KeyWord", edt_search.getText().toString());
                                    i.putExtra("Key", searchText);
                                    startActivity( i );

                                    finish();
                                    progressBar.setVisibility(View.GONE);

                                } else {

                                    Toast.makeText(getApplicationContext(), "No record found", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                }
        );

    }


    private void insertHistory() {

        searchItems = databaseHandler.getHealthRecordByPatientId();
        int Count=0;
        for (int i=0; i<searchItems.size();i++){
            if (edt_search.getText().toString().equalsIgnoreCase( searchItems.get( i ).getSearchText() )){
                Count=1;
            }
        }
        if (Count==0){
            SearchItem searchItem = new SearchItem();
            searchItem.setSearchText(edt_search.getText().toString());
            databaseHandler.searchInsertUpdateData(searchItem);
        }

        if(searchItems != null)
            searchItems.clear();

        searchItems.addAll(databaseHandler.getHealthRecordByPatientId());
        recentSearchAdapter.notifyDataSetChanged();

    }


    @Override
    public void onSearch(SearchItem searchItem) {
        CommonValidation.closeKeybroad(Search.this);
        try{
            if (user.getUser_id().length()==0)
            {
                searchResult(searchItem.getSearchText(),"");
            }
            else
                searchResult( searchItem.getSearchText(),user.getUser_id());

        }catch (Exception e){
            e.printStackTrace();
            searchResult(searchItem.getSearchText(),"");
        }
    }
}