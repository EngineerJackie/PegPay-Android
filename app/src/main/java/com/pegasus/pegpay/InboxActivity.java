package com.pegasus.pegpay;

import android.content.Intent;
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
 * Created by Zed on 5/19/2016.
 */
public class InboxActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener{
    AQuery aq;

    String url = "http://vpointconsultancy.com/pegpay/get_messages.php";

    MessageAdapter adapter;
    private ArrayList<Message> messages;

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
                getMessages(true, true);
            }
        });

        getMessages(false, true);
    }

    private void getMessages(boolean skipCache, boolean hideList){
        if(hideList){
            aq.id(R.id.prog).visible();
            aq.id(R.id.rlt_error_container).gone();
            aq.id(R.id.lst_transactions).gone();
            aq.id(R.id.rtl_empty_container).gone();
        }

        String cache = CacheManager.getDbCache(url, this);
        if(cache != null && !skipCache){
            try{
                populateMessages(new JSONArray(cache));
            }catch (JSONException ex){
                Utils.log("Failed to sync messages -> " + ex.getMessage());
            }
        }
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (!loaded) {
                        populateMessages(json);
                    }
                    CacheManager.saveDbCache(url, json.toString(), InboxActivity.this);
                }
                if (json == null && !loaded) {
                    showError();
                }
            }
        });
    }

    private void populateMessages(JSONArray json) {
        aq.id(R.id.prog).gone();
        aq.id(R.id.rlt_error_container).gone();
        aq.id(R.id.lst_transactions).visible();
        aq.id(R.id.rtl_empty_container).gone();

        try {
            messages = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                messages.add(new Message(json.getJSONObject(i)));
            }

            if(messages.size() == 0){
                aq.id(R.id.rtl_empty_container).visible();
                aq.id(R.id.lst_transactions).gone();
            }else{
                adapter = new MessageAdapter(this, messages);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Message m = messages.get(position);
                        Intent i = new Intent(InboxActivity.this, MessageDetailActivity.class);
                        i.putExtra("message", m);
                        startActivity(i);
                    }
                });
            }

            loaded = true;
            refreshLayout.setRefreshing(false);
        } catch (JSONException ex) {
            Utils.log("error loading messages -> " + ex.getMessage());
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
        final List<Message> filtermessageList = filter(messages, query);
        adapter.animateTo(filtermessageList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Message> filter(List<Message> models, String query) {
        query = query.toLowerCase().trim();

        if(query.isEmpty()){
            return messages;
        }else{
            final List<Message> filterMessageList = new ArrayList<>();
            for (Message message : models) {
                final String text = message.subject.toLowerCase();
                if (text.contains(query)) {
                    filterMessageList.add(message);
                }
            }
            return filterMessageList;
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
        getMessages(true, false);
    }
}
