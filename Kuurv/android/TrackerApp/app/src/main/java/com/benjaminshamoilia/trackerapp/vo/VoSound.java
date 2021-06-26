package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoSound implements Serializable {

    String soundName = "";
    boolean isSelected = false;
    boolean isPlay = false;

    public VoSound(String sound, boolean isSelected) {
        this.soundName = sound;
        this.isSelected = isSelected;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String sound) {
        this.soundName = sound;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}


