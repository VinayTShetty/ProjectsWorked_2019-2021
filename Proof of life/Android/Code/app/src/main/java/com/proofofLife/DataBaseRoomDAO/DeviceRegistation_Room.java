package com.proofofLife.DataBaseRoomDAO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "registration_fingerPrint")
public class DeviceRegistation_Room implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "ble_address")
    private String bleAddress="";
    @ColumnInfo(name = "finger_print_data")
    private String fingerPrintData="";
    /**
     * User Setting.
     */
    @ColumnInfo(name = "secret_key")
    private String secretkey="";
    @ColumnInfo(name = "thresh_hold_matching")
    private String threshHoldMatching="";
    @ColumnInfo(name = "otp_validity")
    private String otpValidity="";
    @ColumnInfo(name = "time_sync")
    private String timeSync="";
    @ColumnInfo(name = "otp_display")
    private String otpDisplay="";
    @ColumnInfo(name = "skew")
    private String skewValue="";
    @ColumnInfo(name = "pol")
    private String polValue="";
    @ColumnInfo(name = "branding_info")
    private String brandingInfo="";
    @ColumnInfo(name = "fingerprint_match")
    private String fingerprintMatch="";
    @ColumnInfo(name = "ble_Data")
    private String bleData="";

    public DeviceRegistation_Room() {

    }

    public DeviceRegistation_Room(String bleAddress, String fingerPrintData,String bleData_loc) {
        this.bleAddress = bleAddress;
        this.fingerPrintData = fingerPrintData;
        this.bleData=bleData_loc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getFingerPrintData() {
        return fingerPrintData;
    }

    public void setFingerPrintData(String fingerPrintData) {
        this.fingerPrintData = fingerPrintData;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getThreshHoldMatching() {
        return threshHoldMatching;
    }

    public void setThreshHoldMatching(String threshHoldMatching) {
        this.threshHoldMatching = threshHoldMatching;
    }

    public String getOtpValidity() {
        return otpValidity;
    }

    public void setOtpValidity(String otpValidity) {
        this.otpValidity = otpValidity;
    }

    public String getTimeSync() {
        return timeSync;
    }

    public void setTimeSync(String timeSync) {
        this.timeSync = timeSync;
    }

    public String getOtpDisplay() {
        return otpDisplay;
    }

    public void setOtpDisplay(String otpDisplay) {
        this.otpDisplay = otpDisplay;
    }

    public String getSkewValue() {
        return skewValue;
    }

    public void setSkewValue(String skewValue) {
        this.skewValue = skewValue;
    }

    public String getPolValue() {
        return polValue;
    }

    public void setPolValue(String polValue) {
        this.polValue = polValue;
    }

    public String getFingerprintMatch() {
        return fingerprintMatch;
    }

    public void setFingerprintMatch(String fingerprintMatch) {
        this.fingerprintMatch = fingerprintMatch;
    }

    public String getBrandingInfo() {
        return brandingInfo;
    }

    public void setBrandingInfo(String brandingInfo) {
        this.brandingInfo = brandingInfo;
    }

    public String getBleData() {
        return bleData;
    }

    public void setBleData(String bleData) {
        this.bleData = bleData;
    }
}
