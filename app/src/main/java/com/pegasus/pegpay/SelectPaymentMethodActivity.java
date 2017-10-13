package com.pegasus.pegpay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Zed on 5/9/2016.
 */
public class SelectPaymentMethodActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    ProgressDialog progressDialog;

    AQuery aq;
    RadioGroup radioGroup;
    HashMap<String, Object> params;
    String url = "http://vpointconsultancy.com/pegpay/api.php?action=pay";
    boolean hidePegPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_payment_method_activity);
        aq = new AQuery(this);

        params = (HashMap<String, Object>)getIntent().getSerializableExtra("params");
        hidePegPay = getIntent().getBooleanExtra("hide_peg_pay", false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        radioGroup = (RadioGroup)findViewById(R.id.payment_methods);
        radioGroup.setOnCheckedChangeListener(this);

        if(hidePegPay){
            aq.id(R.id.rd_pegpay_account).enabled(false);
        }

        aq.id(R.id.btn_submit).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rd_mtn_mobile_money:
                hideAllFields();
                aq.id(R.id.ln_mtn_mobile_money).visible();
                break;
            case R.id.rd_airtel_money:
                hideAllFields();
                aq.id(R.id.ln_airtel_money).visible();
                break;
            case R.id.rd_pegpay_account:
                hideAllFields();
                break;
            case R.id.rd_visa_master_card:
                hideAllFields();
                aq.id(R.id.ln_visa_mastercard).visible();
                break;
        }
    }

    private void submit(){
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if(checkedId == -1){
            showErrorToast("Please select a payment method");
            return;
        }

        Utils.hideKeyboard(this);

        String paymentMethod = null;

        String mtnNumber = aq.id(R.id.txt_mtn_number).getText().toString().trim();
        String airtelNumber = aq.id(R.id.txt_airtel_number).getText().toString().trim();
        String cardNumber = aq.id(R.id.txt_card_number).getText().toString().trim();
        String cvc = aq.id(R.id.txt_security_code).getText().toString().trim();
        String expiryMonth = aq.id(R.id.txt_expiry_month).getText().toString().trim();
        String expiryYear = aq.id(R.id.txt_expiry_year).getText().toString().trim();

        switch (checkedId){
            case R.id.rd_mtn_mobile_money:
                if(mtnNumber.length() < 10){
                    showErrorToast("Please enter a valid MTN number");
                    return;
                }
                paymentMethod = "MTN Mobile Money";
                break;
            case R.id.rd_airtel_money:
                if(airtelNumber.length() < 10){
                    showErrorToast("Please enter a valid Airtel number");
                    return;
                }
                paymentMethod = "Airtel Money";
                break;
            case R.id.rd_pegpay_account:
                paymentMethod = "PegPay";
                break;
            case R.id.rd_visa_master_card:
                if(cardNumber.isEmpty()){
                    showErrorToast("Please enter your card number");
                    return;
                }
                if(expiryMonth.isEmpty() || expiryYear.isEmpty()){
                    showErrorToast("Please enter your card's expiry month and year");
                    return;
                }
                if(cvc.isEmpty()){
                    showErrorToast("Please enter your card's security code");
                    return;
                }
                paymentMethod = "VISA/Master Card";
                break;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        params.put("payment_method", paymentMethod);

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject response, AjaxStatus status) {
                Utils.log(response.toString());
                progressDialog.dismiss();
                if(response != null){
                    showReceipt(response);
                }else{
                    showErrorToast("Sorry, an error occured while processing your payment.");
                }
            }
        });
    }

    private void showReceipt(JSONObject response){
        Receipt receipt = new Receipt(response);
        Intent intent = new Intent(this, PaymentReceiptActivity.class);
        intent.putExtra("receipt", receipt);
        startActivity(intent);
    }

    private void hideAllFields(){
        aq.id(R.id.ln_airtel_money).gone();
        aq.id(R.id.ln_mtn_mobile_money).gone();
        aq.id(R.id.ln_visa_mastercard).gone();
    }

    private void showErrorToast(String error){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
