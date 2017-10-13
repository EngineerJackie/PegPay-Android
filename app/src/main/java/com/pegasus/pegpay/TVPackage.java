package com.pegasus.pegpay;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Zed on 5/9/2016.
 */
public class TVPackage implements Serializable {
    String name;
    int amount;

    public TVPackage(){}

    public TVPackage(JSONObject json){
        try{
            this.name = json.getString("name");
            this.amount = json.getInt("amount");
        }catch (Exception ex){
            Utils.log("Error initializing package -> " + ex.getMessage());
        }
    }
}
