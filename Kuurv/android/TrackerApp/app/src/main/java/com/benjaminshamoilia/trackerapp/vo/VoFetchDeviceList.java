package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoFetchDeviceList implements Serializable
{

    String response="";
    VoFetchDeviceData data=null;
    String message="";

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public VoFetchDeviceData getData() {
        return data;
    }

    public void setData(VoFetchDeviceData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
