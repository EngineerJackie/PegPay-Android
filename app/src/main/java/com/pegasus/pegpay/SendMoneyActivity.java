package com.pegasus.pegpay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zed on 5/10/2016.
 */
public class SendMoneyActivity extends AppCompatActivity {
    private final int PICK_CONTACT = 2;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    AQuery aq;
    String url = "http://vpointconsultancy.com/pegpay/api.php?action=confirm";

    HashMap<String, Object> params;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_airtime_activity);
        aq = new AQuery(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        EditText amountText = aq.id(R.id.txt_amount).getEditText();
        amountText.addTextChangedListener(new NumberTextWatcherForThousand(amountText));

        aq.id(R.id.btn_phone_book).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoneNumber();
            }
        });

        aq.id(R.id.btn_submit).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void pickPhoneNumber(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_CONTACT:
                    Cursor cursor = null;
                    String phoneNumber = "";
                    List<String> allNumbers = new ArrayList<>();
                    int phoneIdx = 0;
                    try {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
                        phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        if (cursor.moveToFirst()) {
                            while (cursor.isAfterLast() == false) {
                                phoneNumber = cursor.getString(phoneIdx);
                                allNumbers.add(phoneNumber);
                                cursor.moveToNext();
                            }
                        } else {
                            Toast.makeText(this, "Error picking phone number 1", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Utils.log("Error: -> " + e.getMessage());
                        Toast.makeText(this, "Error picking phone number 2", Toast.LENGTH_SHORT).show();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }

                        final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Choose a number");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                String selectedNumber = items[item].toString();
                                selectedNumber = selectedNumber.replace("-", "").replace(" ", "");
                                aq.id(R.id.txt_phone_number).text(selectedNumber);
                            }
                        });

                        AlertDialog alert = builder.create();
                        if(allNumbers.size() > 1) {
                            alert.show();
                        } else {
                            String selectedNumber = phoneNumber.toString();
                            selectedNumber = selectedNumber.replace("-", "").replace(" ", "");
                            aq.id(R.id.txt_phone_number).text(selectedNumber);
                        }

                        if (phoneNumber.length() == 0) {
                            Toast.makeText(this, "Error picking phone number 3", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        } else {
            Toast.makeText(this, "Error picking phone number 4", Toast.LENGTH_SHORT).show();
        }
    }

    private void submit(){
        Utils.hideKeyboard(this);

        String phoneNumber = aq.id(R.id.txt_phone_number).getText().toString().trim();
        String amount = NumberTextWatcherForThousand.trimCommaOfString(aq.id(R.id.txt_amount).getText().toString().trim());

        if(phoneNumber.length() < 10){
            showErrorToast("Please enter a valid phone number");
            return;
        }
        if(amount.isEmpty()){
            showErrorToast("Please enter the amount");
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        params = new HashMap<>();
        params.put("phone_number", phoneNumber);
        params.put("amount", amount);
        params.put("recipient", "Payment: " + phoneNumber);
        params.put("ref", "Payment for " + phoneNumber);

        aq.ajax(url, params, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                progressDialog.dismiss();
                showConfirmation(response);
            }
        });
    }

    private void showConfirmation(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage(message);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                Intent i = new Intent(SendMoneyActivity.this, SelectPaymentMethodActivity.class);
                params.put("confirmed", true);
                i.putExtra("params", params);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Cancel", null);

        alertDialog = builder.create();
        alertDialog.show();
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
