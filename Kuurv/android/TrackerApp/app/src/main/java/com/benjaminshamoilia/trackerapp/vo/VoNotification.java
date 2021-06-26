package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VoNotification implements Serializable
{
    String response="";
    String message="";
    private List<NotificationData> data=new ArrayList<>();

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<NotificationData> getData() {
        return data;
    }

    public void setData(List<NotificationData> data) {
        this.data = data;
    }
}
