package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoCrushLogOut implements Serializable
{

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public CrusLogoutData getData() {
        return data;
    }

    public void setData(CrusLogoutData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String response="";

    private CrusLogoutData data;

    private String message="";

}


class CrusLogoutData implements Serializable
{

}
