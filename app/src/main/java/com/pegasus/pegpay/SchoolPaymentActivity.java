package com.pegasus.pegpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidquery.AQuery;

/**
 * Created by Zed on 5/9/2016.
 */
public class SchoolPaymentActivity extends AppCompatActivity {
    AQuery aq;
    String[] items = new String[]{"Universities", "Other Schools", "Others"};
    ListView listView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_bill_activity);
        aq = new AQuery(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        listView = (ListView)findViewById(R.id.lst_bill_categories);
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.txt_label, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewItemClick(position);
            }
        });
    }

    private void listViewItemClick(int position){
        switch (position){
            case 0:
                startActivity(new Intent(this, UniversityListActivity.class));
                break;
            case 1:
            case 2:
                startActivity(new Intent(this, OtherPaymentsActivity.class));
                break;
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
}
