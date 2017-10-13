package com.pegasus.pegpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Zed on 5/9/2016.
 */
public class TVPackageSelectionActivity extends AppCompatActivity {
    AQuery aq;
    ArrayList<TVPackage> mPackages;
    ArrayAdapter<String> adapter;
    ListView listView;

    String url = "http://vpointconsultancy.com/pegpay/api.php?action=tv_packages";
    String tv;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_package_activity);
        aq = new AQuery(this);

        Intent intent = getIntent();
        tv = intent.getStringExtra("tv");
        title = intent.getStringExtra("title");

        url = url + "&tv=" + tv;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        actionBar.setTitle(title);

        listView = (ListView)findViewById(R.id.lst_packages);

        getPackages();
    }

    private void getPackages(){
        aq.id(R.id.prog).visible();
        aq.id(R.id.rtl_empty_container).gone();
        aq.id(R.id.rlt_error_container).gone();
        aq.id(R.id.lst_packages).gone();

        Utils.log("Getting packages: " + url);
        aq.ajax(url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                Utils.log("Response: " + response);
                if(response == null){
                    aq.id(R.id.rlt_error_container).visible();
                }else{
                    loadPackages(response);
                }
            }
        });
    }

    private void loadPackages(String response){
        mPackages = new ArrayList<>();
        try{
            TVPackage tvPackage;
            JSONArray packages = new JSONArray(response);
            String[] labels = new String[packages.length()];

            for(int i = 0; i < packages.length(); i++){
                tvPackage = new TVPackage(packages.getJSONObject(i));
                mPackages.add(tvPackage);
                labels[i] = tvPackage.name;
            }

            Utils.log("Packages: " + packages.length());

            adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txt_label, labels){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    TVPackage p = mPackages.get(position);

                    if(view == null){
                        LayoutInflater inflater = getLayoutInflater();
                        view = inflater.inflate(R.layout.list_item, null);
                    }

                    TextView textView = (TextView)view.findViewById(R.id.txt_label);
                    textView.setText(p.name);

                    return view;
                }
            };

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TVPackage pack = mPackages.get(position);
                    Intent intent = new Intent(TVPackageSelectionActivity.this, TVPackagePaymentActivity.class);
                    intent.putExtra("title", pack.name);
                    intent.putExtra("recipient", title);
                    intent.putExtra("amount", Integer.toString(pack.amount));
                    startActivity(intent);
                }
            });

            aq.id(R.id.prog).gone();
            aq.id(R.id.rtl_empty_container).gone();
            aq.id(R.id.rlt_error_container).gone();
            aq.id(R.id.lst_packages).visible();
        }catch (Exception ex){
            Utils.log("Error loading packages: -> " + ex.getMessage());
        }
    }
}
