package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoLoginData implements Serializable
{
    String response = "";
    String message = "";
    String auth_token ="";
    VoUserData data;

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

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public VoUserData getData() {
        return data;
    }

    public void setData(VoUserData data) {
        this.data = data;
    }


}
