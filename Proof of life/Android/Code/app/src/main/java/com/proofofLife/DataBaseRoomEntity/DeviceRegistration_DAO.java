package com.proofofLife.DataBaseRoomEntity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.proofofLife.DataBaseRoomDAO.DeviceRegistation_Room;

import java.util.List;

@Dao
public interface DeviceRegistration_DAO {
    @Query("Select * from registration_fingerPrint")
    List<DeviceRegistation_Room> getDeviceInfo();
    @Insert
    void insert_ActionInfo(DeviceRegistation_Room deviceRegistation_room);
    @Update
    void update_ActionInfo(DeviceRegistation_Room deviceRegistation_room);
    @Delete
    void delete_ActionInfo(DeviceRegistation_Room deviceRegistation_room);
    @Query("DELETE FROM registration_fingerPrint WHERE ble_address = :bleaddressParams")
    void deleteByBleAddress(String bleaddressParams);
    @Query("UPDATE registration_fingerPrint SET otp_validity = :otpValidity, otp_display = :otpDisplayTime,pol = :poolValue,fingerprint_match = :fingerPrintMatch WHERE ble_address =:bleAddress")
    void updateSetting(String  otpValidity,String  otpDisplayTime,String  poolValue,String  fingerPrintMatch,String bleAddress);
    @Query("UPDATE registration_fingerPrint SET secret_key = :secretKey WHERE ble_address =:bleAddress")
    void setSecretKey(String  secretKey,String bleAddress);
    @Query("select * from registration_fingerPrint where ble_address=:bleAddressInput")
    public boolean isRecordAvaliableForBleAddress(String bleAddressInput);
    @Query("UPDATE registration_fingerPrint SET finger_print_data = :fingerPrintData WHERE ble_address =:bleAddress")
    void setfingerPrintData(String  fingerPrintData,String bleAddress);
    @Query("select secret_key from registration_fingerPrint where ble_address=:bleAddress_loc")
    String getSecretKeyFromDataBase(String bleAddress_loc);

}
