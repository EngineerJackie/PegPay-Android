package com.pegasus.pegpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.androidquery.AQuery;

/**
 * Created by Zed on 5/9/2016.
 */
public class PaymentReceiptActivity extends AppCompatActivity {
    AQuery aq;
    Receipt receipt;
    LinearLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_receipt_activity);
        aq = new AQuery(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        receipt = (Receipt)getIntent().getSerializableExtra("receipt");
        container = (LinearLayout)findViewById(R.id.ln_receipt_container);

        aq.id(R.id.txt_amount).text(receipt.amount);

        LayoutInflater inflater = getLayoutInflater();
        AQuery a;
        for(Info info: receipt.items){
            View view = inflater.inflate(R.layout.receipt_item, null);
            a = new AQuery(view);
            a.id(R.id.txt_label).text(info.label + ":");
            a.id(R.id.txt_value).text(info.value);
            container.addView(view);
        }

        aq.id(R.id.btn_dashboard).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentReceiptActivity.this, DashboardActivity.class));
            }
        });
        aq.id(R.id.btn_transaction).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentReceiptActivity.this, RecentTransactions.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            goBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBack(){
        Intent i = new Intent(this, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
