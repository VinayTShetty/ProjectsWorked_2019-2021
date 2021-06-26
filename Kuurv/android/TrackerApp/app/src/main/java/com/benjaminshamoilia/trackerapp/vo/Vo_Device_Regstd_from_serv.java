package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class Vo_Device_Regstd_from_serv implements Serializable {
    private String ble_address = "";
    private String device_name = "";
    private String latitude = "";
    private String longitude = "";


    private String photo_serverURL = "";

    private String photo_localURL = "";
    private String created_time = "";
    private String updated_time = "";

    /*Added By JD*/
    private int id;
    private String server_id = "";
    private int is_silent_mode = 1;
    private int buzzer_volume = 0;
    private int seperate_alert = 0;
    private int repeat_alert = 0;
//    private int selected_sound = 0;
//    private int duration_alert = 0;
    private String correction_status = "";
    private String device_type = "";
    private String tracker_device_alert = "";
    private String marked_lost = "";
    private String is_active = "";
    private String contact_name = "";
    private String contact_email = "";
    private String contact_mobile = "";
    private int battery_status = 100;
    private String tracker_Address = "";




    public String getTracker_Address() {
        return tracker_Address;
    }

    public void setTracker_Address(String tracker_Address) {
        this.tracker_Address = tracker_Address;
    }

    public String getPhoto_serverURL() {
        return photo_serverURL;
    }

    public void setPhoto_serverURL(String photo_serverURL) {
        this.photo_serverURL = photo_serverURL;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBle_address() {
        return ble_address;
    }

    public void setBle_address(String ble_address) {
        this.ble_address = ble_address;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPhoto_localURL() {
        return photo_localURL;
    }

    public void setPhoto_localURL(String photo_localURL) {
        this.photo_localURL = photo_localURL;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public int getIs_silent_mode() {
        return is_silent_mode;
    }

    public void setIs_silent_mode(int is_silent_mode) {
        this.is_silent_mode = is_silent_mode;
    }

    public int getBuzzer_volume() {
        return buzzer_volume;
    }

    public void setBuzzer_volume(int buzzer_volume) {
        this.buzzer_volume = buzzer_volume;
    }

    public int getSeperate_alert() {
        return seperate_alert;
    }

    public void setSeperate_alert(int seperate_alert) {
        this.seperate_alert = seperate_alert;
    }

    public int getRepeat_alert() {
        return repeat_alert;
    }

    public void setRepeat_alert(int repeat_alert) {
        this.repeat_alert = repeat_alert;
    }

//    public int getSelected_sound() {
//        return selected_sound;
//    }
//
//    public void setSelected_sound(int selected_sound) {
//        this.selected_sound = selected_sound;
//    }
//
//    public int getDuration_alert() {
//        return duration_alert;
//    }
//
//    public void setDuration_alert(int duration_alert) {
//        this.duration_alert = duration_alert;
//    }

    public String getCorrection_status() {
        return correction_status;
    }

    public void setCorrection_status(String correction_status) {
        this.correction_status = correction_status;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getTracker_device_alert() {
        return tracker_device_alert;
    }

    public void setTracker_device_alert(String tracker_device_alert) {
        this.tracker_device_alert = tracker_device_alert;
    }

    public String getMarked_lost() {
        return marked_lost;
    }

    public void setMarked_lost(String marked_lost) {
        this.marked_lost = marked_lost;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getContact_mobile() {
        return contact_mobile;
    }

    public void setContact_mobile(String contact_mobile) {
        this.contact_mobile = contact_mobile;
    }

    public int getBattery_status() {
        return battery_status;
    }

    public void setBattery_status(int battery_status) {
        this.battery_status = battery_status;
    }

}
