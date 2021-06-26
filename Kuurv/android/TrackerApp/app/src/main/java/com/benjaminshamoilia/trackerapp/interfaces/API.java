package com.benjaminshamoilia.trackerapp.interfaces;

import com.benjaminshamoilia.trackerapp.vo.*;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface API
{
    @FormUrlEncoded
    @POST("login")
    Call<VoLoginData> userLoginAPI(@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("sigup")
    Call<VoRegisterData> userRegisterAPI(@FieldMap Map<String, String> mHashMap);

    /**
     *           Used for Adding the Device without image  ---->Add_Device_API_without_image
     *           Used for Adding the Device with image  ---->Add_Device_API_with_image
     * @param mHashMap
     * @return
     */
    @FormUrlEncoded
    @POST("adddevice")
    Call<VoAddDeviceData> Add_Device_API_without_image(@FieldMap Map<String, String> mHashMap);


    @Multipart
    @POST("adddevice")
    Call<VoAddDeviceData> Add_Device_API_with_image (
            @Part("user_id") RequestBody userId,
            @Part ("ble_address") RequestBody ble_address,
            @Part("device_name") RequestBody device_name,
            @Part("correction_status") RequestBody correction_status ,
            @Part("device_type") RequestBody device_type ,
            @Part("longitude") RequestBody longitude,
            @Part("latitude") RequestBody latitude,
            @Part("tracker_device_alert") RequestBody tracker_device_alert,
            @Part("marked_lost") RequestBody marked_lost ,
            @Part("is_active") RequestBody is_active,
            @Part("contact_name") RequestBody contact_name,
            @Part("contact_email") RequestBody contact_email,
            @Part("contact_mobile") RequestBody contact_mobile,
            @Part MultipartBody.Part device_image
    );


  /*  @FormUrlEncoded
    @POST("devicelist")
    Call<String> Device_list (@FieldMap Map<String, String> mHashMap);*/


   /* @FormUrlEncoded
    @POST("deletedevice")
    Call<String> Delete_device(@FieldMap Map<String, String> mHashMap);*/

    @FormUrlEncoded
    @POST("deletedevice")
    Call<VoDeleteDevice> Delete_device(@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("notificationlist")
    Call<VoNotification> Notification_List (@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("forgotpassword")
    Call<VoForgot_Password> forgot_password(@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("changepassword")
    Call<VoForgot_Password> ChangePassword(@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("markaslost")
    Call<VoMarkAsLost> markedAsLost(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("crash")
    Call<VoCrushLogOut> crushLogOut(@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("logout")
    Call<VoNormalLogout> NormalLogout(@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("devicelist")
    Call<VoDeviceList> Device_listAPI (@FieldMap Map<String, String> mHashMap);


    @FormUrlEncoded
    @POST("fetchdevice")
    Call<VoFetchDeviceList> fetchDeviceList (@FieldMap Map<String, String> mHashMap);

}
