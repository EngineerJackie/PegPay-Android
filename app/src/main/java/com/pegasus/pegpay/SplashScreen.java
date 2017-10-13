package com.pegasus.pegpay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zed on 4/15/2016.
 */
public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        startSplashTimeout(SPLASH_TIME_OUT);
    }

    private void startSplashTimeout(int timeout){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, AccountManager.isLoggedIn(SplashScreen.this) ? DashboardActivity.class : LandingActivity.class);
                startActivity(i);
                finish();
            }
        }, timeout);
    }
}
