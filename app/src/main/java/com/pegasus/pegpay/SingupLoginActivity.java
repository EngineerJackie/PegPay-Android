package com.pegasus.pegpay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zed on 4/16/2016.
 */
public class SingupLoginActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    int tab;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tab = getIntent().getIntExtra("tab", 0);
        isEdit = getIntent().getBooleanExtra("is_edit", false);

        if(isEdit){
            setContentView(R.layout.my_account_activity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("My Account");
        }else{
            setContentView(R.layout.signup_login_activity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            tabLayout = (TabLayout)findViewById(R.id.tab_layout);
            viewPager = (ViewPager)findViewById(R.id.pager);

            viewPager.setAdapter(new HomeTabAdapter(getSupportFragmentManager()));
            tabLayout.setupWithViewPager(viewPager);

            viewPager.setCurrentItem(tab);
        }
    }

    public void switchTab(int tab){
        viewPager.setCurrentItem(tab);
    }

    private class HomeTabAdapter extends FragmentStatePagerAdapter {
        public HomeTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position){
                case 0:
                    fragment = new SignUpFragment();
                    break;
                case 1:
                    fragment = new LoginFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Sign Up";
            switch (position){
                case 0:
                    title = "Sign Up";
                    break;
                case 1:
                    title = "Log In";
                    break;
            }
            return title;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.log("onActivityResult() -> " + requestCode);
        if(requestCode == PhotoSelectionDialog.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Utils.log("Photo saved: " + PhotoSelectionDialog.photoFile.getAbsolutePath());
            SignUpFragment.setImage(PhotoSelectionDialog.photoFile);
        }

        if(requestCode == PhotoSelectionDialog.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            try {
                Bitmap bitmap = Utils.scaleImage(this, uri);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = SignUpFragment.photoSelectionField == 0 ? "avatar_" : "id_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(null);
                File image = new File(storageDir, imageFileName + ".jpg");
                FileOutputStream out = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();

                SignUpFragment.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
