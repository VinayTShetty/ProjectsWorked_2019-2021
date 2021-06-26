package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoDuration implements Serializable {

    String duration = "";
    boolean isSelected = false;

    public VoDuration(String duration, boolean isSelected) {
        this.duration = duration;
        this.isSelected = isSelected;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}


