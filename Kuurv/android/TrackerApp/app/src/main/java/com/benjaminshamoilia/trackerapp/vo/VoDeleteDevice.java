package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoDeleteDevice implements Serializable
{
    private String response;
    private VoDeleteDeviceResponse data;
    private String message;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public VoDeleteDeviceResponse getData() {
        return data;
    }

    public void setData(VoDeleteDeviceResponse data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
