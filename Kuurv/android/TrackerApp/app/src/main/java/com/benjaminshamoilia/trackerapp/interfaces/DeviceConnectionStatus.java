package com.benjaminshamoilia.trackerapp.interfaces;

import com.clj.fastble.data.BleDevice;

public interface DeviceConnectionStatus
{
    public void ConnectionStatus(String BleAddress, String ConnectionStatus, BleDevice bleDevice);
    public  void infoReceievedFrombleDevice(String info);
    public  void deleteinfoReceievedfromBleDevice(int ackreceieved,BleDevice deletedDevice);
    public boolean bluetoothturningOff(boolean bleoff);
    public void readbatterystatusFromDevice(int batterystatus);
}
