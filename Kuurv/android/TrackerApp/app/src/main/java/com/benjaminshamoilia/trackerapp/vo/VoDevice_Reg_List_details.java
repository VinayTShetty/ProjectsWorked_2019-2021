package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

public class VoDevice_Reg_List_details implements Serializable {


    private int id;
    private int user_id;
    private String ble_address = "";
    private String device_name = "";
    private String correction_status = "";
    private String device_type = "";
    private String device_image = "";
    private String latitude = "";
    private String longitude = "";
    private String tracker_device_ale = "";
    private String marked_lost = "";
    private String is_active = "";
    private String contact_name = "";
    private String contact_email = "";
    private String contact_mobile = "";
    private String status = "";
    private String created_at = "";
    private String updated_at = "";
    private String device_image_path = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public String getDevice_image() {
        return device_image;
    }

    public void setDevice_image(String device_image) {
        this.device_image = device_image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTracker_device_ale() {
        return tracker_device_ale;
    }

    public void setTracker_device_ale(String tracker_device_ale) {
        this.tracker_device_ale = tracker_device_ale;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDevice_image_path() {
        return device_image_path;
    }

    public void setDevice_image_path(String device_image_path) {
        this.device_image_path = device_image_path;
    }
}
