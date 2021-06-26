package com.benjaminshamoilia.trackerapp.interfaces;

import com.clj.fastble.data.BleDevice;

public interface DeviceDisconnectionCallback {
    public void callbackWhenDeviceIsActuallyDisconnected(String BleAddress, String ConnectionStatus, BleDevice bleDevice);
}
