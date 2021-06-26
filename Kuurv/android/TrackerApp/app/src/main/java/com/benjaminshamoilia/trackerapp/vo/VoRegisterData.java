package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoRegisterData implements Serializable
{

    String response = "";
    String message = "";
    VoUserData data;
    String auth_token="";

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
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

    public VoUserData getData() {
        return data;
    }

    public void setData(VoUserData data) {
        this.data = data;
    }

}
