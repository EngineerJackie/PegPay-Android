package com.pegasus.pegpay;

import org.json.JSONObject;

/**
 * Created by Zed on 4/25/2016.
 */
public class Transaction {
    public String title;
    public TransactionType type;
    public int amount;
    public String date;
    public int icon;
    String subtitle;
    String amountPrefix;

    public Transaction(JSONObject json){
        try{
            this.title = json.getString("title");
            this.type = TransactionType.valueOf(json.getString("type"));
            this.amount = json.getInt("amount");
            this.date = json.getString("date");
            if(this.type == TransactionType.TOPUP){
                amountPrefix = "+";
            }else{
                amountPrefix = "-";
            }
            setSubtitle();
        }catch (Exception ex){
            Utils.log("Error getting transaction: " + ex.getMessage());
        }
    }

    private void setSubtitle(){
        switch (this.type){
            case TOPUP:
                this.subtitle = "PegPay Topup";
                this.icon = R.drawable.ic_list_topup;
                break;
            case AIRTIME:
                this.subtitle = "Airtime";
                this.icon = R.drawable.ic_list_airtime;
                break;
            case MERCHANT:
                this.subtitle = "Merchant Payment";
                this.icon = R.drawable.ic_merchant_payment;
                break;
            case BILL:
                this.subtitle = "Bill Payment";
                this.icon = R.drawable.ic_list_wallet;
                break;
            case SENT:
                this.subtitle = "Money Sent";
                this.icon = R.drawable.ic_list_send_money;
                break;

        }
    }
}
