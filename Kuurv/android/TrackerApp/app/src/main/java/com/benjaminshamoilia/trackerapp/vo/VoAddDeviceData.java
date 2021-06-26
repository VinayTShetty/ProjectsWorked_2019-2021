package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoAddDeviceData implements Serializable {
    String response = "";
    String message = "";
    VoAddDevice data;

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

    public VoAddDevice getData() {
        return data;
    }

    public void setData(VoAddDevice data) {
        this.data = data;
    }
}