package com.benjaminshamoilia.trackerapp.vo;


import java.io.Serializable;

public class VoMarkAsLost implements Serializable
{
    public MarkedasLost getData() {
        return data;
    }

    public void setData(MarkedasLost data) {
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

    private MarkedasLost data;
    private String response="";
    private String message="";


}


class MarkedasLost implements Serializable
{

}