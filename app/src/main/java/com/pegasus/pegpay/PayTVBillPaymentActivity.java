package com.pegasus.pegpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidquery.AQuery;

/**
 * Created by Zed on 5/9/2016.
 */
public class PayTVBillPaymentActivity extends AppCompatActivity {
    AQuery aq;
    String[] items = new String[]{"DSTV", "GoTV", "Startimes", "Azam TV", "Others"};
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
        Intent i = null;
        String title = null;
        String tv = null;

        switch (position){
            case 0:
                title = "DSTV Packages";
                tv = "dstv";
                break;
            case 1:
                title = "GoTV Packages";
                tv = "gotv";
                break;
            case 2:
                title = "Startimes Packages";
                tv = "startimes";
                break;
            case 3:
                title = "Azam TV";
                tv = "azam";
                break;
            case 4:
                startActivity(new Intent(this, OtherPaymentsActivity.class));
                break;
        }

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(tv)){
            i = new Intent(this, TVPackageSelectionActivity.class);
            i.putExtra("title", title);
            i.putExtra("tv", tv);
            startActivity(i);
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
