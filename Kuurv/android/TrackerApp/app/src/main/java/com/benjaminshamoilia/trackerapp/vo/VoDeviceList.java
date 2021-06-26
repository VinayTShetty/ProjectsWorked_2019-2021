package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;
import java.util.List;

public class VoDeviceList implements Serializable
{
    private String response;
    private String message;
    private List<VoDeviceListData> data = null;

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

    public List<VoDeviceListData> getData() {
        return data;
    }

    public void setData(List<VoDeviceListData> data) {
        this.data = data;
    }
}
