package com.pegasus.pegpay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.androidquery.AQuery;

/**
 * Created by Zed on 5/19/2016.
 */
public class MessageDetailActivity extends AppCompatActivity {
    Message message;
    AQuery aq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_activity);
        aq = new AQuery(this);

        message = (Message) getIntent().getSerializableExtra("message");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        aq.id(R.id.txt_subject).text(message.subject);
        aq.id(R.id.txt_content).text(message.content);
    }
}
