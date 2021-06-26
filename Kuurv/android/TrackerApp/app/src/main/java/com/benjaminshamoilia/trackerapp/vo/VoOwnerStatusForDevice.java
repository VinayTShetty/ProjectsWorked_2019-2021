package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoOwnerStatusForDevice implements Serializable {
  private String bleAddress="";
  private boolean ownerstatus=false;

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public boolean isOwnerstatus() {
        return ownerstatus;
    }

    public void setOwnerstatus(boolean ownerstatus) {
        this.ownerstatus = ownerstatus;
    }
}
