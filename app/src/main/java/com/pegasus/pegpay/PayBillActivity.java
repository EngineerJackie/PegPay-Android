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
public class PayBillActivity extends AppCompatActivity {
    AQuery aq;
    String[] categories = new String[]{"Utilities", "Pay TV", "Tax", "School Fees", "Others"};
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
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.txt_label, categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewItemClick(position);
            }
        });
    }

    private void listViewItemClick(int position){
        Intent intent = null;
        switch (position){
            case 0:
                intent = new Intent(this, UtilitiesBillPaymentActivity.class);
                break;
            case 1:
                intent = new Intent(this, PayTVBillPaymentActivity.class);
                break;
            case 2:
                intent = new Intent(this, TaxBillPaymentActivity.class);
                break;
            case 3:
                intent = new Intent(this, SchoolPaymentActivity.class);
                break;
            case 4:
                intent = new Intent(this, OtherPaymentsActivity.class);
                break;
        }

        if(intent != null){
            startActivity(intent);
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
