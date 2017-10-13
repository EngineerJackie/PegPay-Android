package com.pegasus.pegpay;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Zed on 5/19/2016.
 */
public class Message implements Serializable {
    public String subject;
    public String content;
    public String date;
    public boolean read = false;

    public Message(){}
    public Message(JSONObject json){
        try{
            this.subject = json.getString("subject");
            this.content = json.getString("content");
            this.date = json.getString("date");
            this.read = json.getBoolean("read");
        }catch (Exception ex){
            Utils.log("Error populating message: " + ex.getMessage());
        }
    }
}
