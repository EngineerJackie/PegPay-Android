package com.pegasus.pegpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;

public class LoginFragment extends Fragment {
    View view;
    AQuery aq;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_fragment, container, false);

        aq = new AQuery(view);

        aq.id(R.id.btn_submit).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();
            }
        });

        return view;
    }

    private void processLogin(){
        String username = aq.id(R.id.txt_username).getText().toString().trim();
        String password = aq.id(R.id.txt_password).getText().toString().trim();

        if(username.equalsIgnoreCase(AccountManager.getEmail(getActivity())) && !password.isEmpty()){
            AccountManager.saveLoggedIn(getActivity(), true);
            startActivity(new Intent(getActivity(), DashboardActivity.class));
        }else{
            Toast.makeText(getActivity(), "Your login details are incorrect", Toast.LENGTH_SHORT).show();
        }
    }
}
