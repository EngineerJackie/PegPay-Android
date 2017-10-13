package com.pegasus.pegpay;


import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zed on 4/25/2016.
 */
public class RecentTransactions extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener{
    AQuery aq;

    String url = "http://vpointconsultancy.com/pegpay/get_transactions.php";

    TransactionAdapter adapter;
    private ArrayList<Transaction> transactions;

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;

    boolean loaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_transactions);
        aq = new AQuery(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.lst_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        refreshLayout.setColorSchemeColors(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);
        refreshLayout.setOnRefreshListener(this);

        aq.id(R.id.img_retry).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTransactions(true, true);
            }
        });

        getTransactions(false, true);
    }

    private void getTransactions(boolean skipCache, boolean hideList){
        if(hideList){
            aq.id(R.id.prog).visible();
            aq.id(R.id.rlt_error_container).gone();
            aq.id(R.id.lst_transactions).gone();
            aq.id(R.id.rtl_empty_container).gone();
        }

        String cache = CacheManager.getDbCache(url, this);
        if(cache != null && !skipCache){
            try{
                populateTransactions(new JSONArray(cache));
            }catch (JSONException ex){
                Utils.log("Failed to sync transactions -> " + ex.getMessage());
            }
        }
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (!loaded) {
                        populateTransactions(json);
                    }
                    CacheManager.saveDbCache(url, json.toString(), RecentTransactions.this);
                }
                if (json == null && !loaded) {
                    showError();
                }
            }
        });
    }

    private void populateTransactions(JSONArray json) {
        aq.id(R.id.prog).gone();
        aq.id(R.id.rlt_error_container).gone();
        aq.id(R.id.lst_transactions).visible();
        aq.id(R.id.rtl_empty_container).gone();

        try {
            transactions = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                transactions.add(new Transaction(json.getJSONObject(i)));
            }

            if(transactions.size() == 0){
                aq.id(R.id.rtl_empty_container).visible();
                aq.id(R.id.lst_transactions).gone();
            }else{
                adapter = new TransactionAdapter(this, transactions);
                recyclerView.setAdapter(adapter);
            }

            loaded = true;
            refreshLayout.setRefreshing(false);
        } catch (JSONException ex) {
            Utils.log("error loading transactions -> " + ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Transaction> filterTransactionList = filter(transactions, query);
        adapter.animateTo(filterTransactionList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Transaction> filter(List<Transaction> models, String query) {
        query = query.toLowerCase().trim();

        if(query.isEmpty()){
            return transactions;
        }else{
            final List<Transaction> filterTransactionList = new ArrayList<>();
            for (Transaction transaction : models) {
                final String text = transaction.title.toLowerCase();
                if (text.contains(query)) {
                    filterTransactionList.add(transaction);
                }
            }
            return filterTransactionList;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError(){
        refreshLayout.setRefreshing(false);
        loaded = false;
        aq.id(R.id.prog).gone();
        aq.id(R.id.rlt_error_container).visible();
        aq.id(R.id.lst_transactions).gone();
        aq.id(R.id.rtl_empty_container).gone();
    }

    @Override
    public void onRefresh() {
        loaded = false;
        getTransactions(true, false);
    }
}
