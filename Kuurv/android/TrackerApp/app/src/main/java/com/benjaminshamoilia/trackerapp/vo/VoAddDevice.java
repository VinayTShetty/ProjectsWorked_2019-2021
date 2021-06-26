package com.benjaminshamoilia.trackerapp.vo;

import java.io.Serializable;

/*This is used getting the data from the from the Add_Device API response...*/

public class VoAddDevice  implements Serializable {

    String id = "";
    String user_id = "";
    String ble_address = "";
    String device_name = "";
    String correction_status = "";
    String device_type = "";
    String latitude = "";
    String longitude = "";
    String device_image = "";
    String tracker_device_alert = "";
    String marked_lost = "";
    String is_active = "";
    String contact_name = "";
    String contact_email = "";
    String contact_mobile = "";
    String status = "";
    String updated_at = "";
    String created_at = "";
    String device_image_path="";

    public String getDevice_image_path() {
        return device_image_path;
    }

    public void setDevice_image_path(String device_image_path) {
        this.device_image_path = device_image_path;
    }




    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
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

    public String getDevice_image() {
        return device_image;
    }

    public void setDevice_image(String device_image) {
        this.device_image = device_image;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
