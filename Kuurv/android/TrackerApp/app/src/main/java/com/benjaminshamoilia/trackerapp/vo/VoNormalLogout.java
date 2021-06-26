package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoNormalLogout implements Serializable
{
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public NormalLogoutData getData() {
        return data;
    }

    public void setData(NormalLogoutData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String response="";
    private NormalLogoutData data;
    private String message="";
}


class NormalLogoutData implements Serializable
{

}