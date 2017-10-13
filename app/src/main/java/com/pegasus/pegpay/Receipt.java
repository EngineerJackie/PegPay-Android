package com.pegasus.pegpay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zed on 5/9/2016.
 */
public class Receipt implements Serializable {
    private static final long serialVersionUID = 1L;

    public String amount;
    public ArrayList<Info> items;

    public Receipt(){}

    public Receipt(JSONObject json){
        try{
            this.amount = json.getString("amount");
            items = new ArrayList<>();
            JSONArray lines = json.getJSONArray("items");
            Info info;
            for (int i = 0; i < lines.length(); i++){
                info = new Info();
                info.label = lines.getJSONObject(i).getString("label");
                info.value = lines.getJSONObject(i).getString("value");
                items.add(info);
            }
        }catch (Exception ex){
            Utils.log("Error initializing reciept: " + ex.getMessage());
        }
    }
}
