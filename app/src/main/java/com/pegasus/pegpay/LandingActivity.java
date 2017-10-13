package com.pegasus.pegpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidquery.AQuery;

public class LandingActivity extends AppCompatActivity {
    AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);
        aq = new AQuery(this);

        aq.id(R.id.btn_signup).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this, SingupLoginActivity.class);
                i.putExtra("tab", 0);
                startActivity(i);
            }
        });

        aq.id(R.id.btn_login).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this, SingupLoginActivity.class);
                i.putExtra("tab", 1);
                startActivity(i);
            }
        });
    }
}
