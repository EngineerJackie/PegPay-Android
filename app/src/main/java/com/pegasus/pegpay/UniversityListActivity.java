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
public class UniversityListActivity extends AppCompatActivity {
    AQuery aq;
    String[] items = new String[]{"Makerere University",
            "Makerere University Business School",
            "Mbarara University of Science and Technology",
            "Nkozi University",
            "Nkumba University",
            "Kampala International University",
            "Mukono University",
            "Others"};

    ListView listView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.university_list_activity);
        aq = new AQuery(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        listView = (ListView)findViewById(R.id.lst_universities);
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
        String title = items[position];
        if(title.equalsIgnoreCase("Others")){
            startActivity(new Intent(this, OtherPaymentsActivity.class));
        }else{
            Intent i = new Intent(this, UniversityPaymentActivity.class);
            i.putExtra("title", title);
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
