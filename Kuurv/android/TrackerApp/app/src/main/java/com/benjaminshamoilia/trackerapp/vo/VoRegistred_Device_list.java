package com.benjaminshamoilia.trackerapp.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for Fetching device_list of the users...
 */
public class VoRegistred_Device_list  implements Serializable {
    String response = "";
    String message = "";
    /* VoDeviceList_details data[];*/
    JSONObject  data;

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

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
}

