package com.proofofLife.CustomObjects;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.Nullable;

import java.io.Serializable;
public class CustBluetootDevice implements Serializable {
    private String bleAddress;
    private String deviceName;
    private BluetoothDevice bluetoothDevice;
    private boolean isConnected;
    private String dataObtained;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CustBluetootDevice(String bleAddress, String deviceName, BluetoothDevice bluetoothDevice, boolean isConnected, String id) {
        this.bleAddress = bleAddress;
        this.deviceName = deviceName;
        this.bluetoothDevice = bluetoothDevice;
        this.isConnected = isConnected;
        this.id=id;
    }

    public CustBluetootDevice(String bleAddress, String deviceName, BluetoothDevice bluetoothDevice, boolean isConnected) {
        this.bleAddress = bleAddress;
        this.deviceName = deviceName;
        this.bluetoothDevice = bluetoothDevice;
        this.isConnected = isConnected;
    }

    public CustBluetootDevice() {
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getDataObtained() {
        return dataObtained;
    }

    public void setDataObtained(String dataObtained) {
        this.dataObtained = dataObtained;
    }

    /**
     *
     * equals method is used to make the Unique when the Device is added to the Arraylist.
     * By overriding the equals method.
     *
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof CustBluetootDevice && (this.bleAddress.equalsIgnoreCase(((CustBluetootDevice) obj).bleAddress));
    }

}
