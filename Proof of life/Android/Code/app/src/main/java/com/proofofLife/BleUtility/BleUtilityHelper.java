package com.proofofLife.BleUtility;

import android.bluetooth.BluetoothAdapter;

public class BleUtilityHelper {
    public static boolean ble_on_off(){
        boolean ble_on_off=false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            ble_on_off=false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            ble_on_off=false;
        } else {
            // Bluetooth is enabled
            ble_on_off=true;
        }
        return ble_on_off;
    }


}
