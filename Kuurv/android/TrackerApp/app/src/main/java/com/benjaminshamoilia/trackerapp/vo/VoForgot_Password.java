package com.benjaminshamoilia.trackerapp.vo;

import org.json.JSONObject;

public class VoForgot_Password
{

    String response="";
    JSONObject data;
    String message="";


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
