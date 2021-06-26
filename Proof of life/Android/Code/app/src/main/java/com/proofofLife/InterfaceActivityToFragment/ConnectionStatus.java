package com.proofofLife.InterfaceActivityToFragment;

import android.bluetooth.BluetoothDevice;

public interface ConnectionStatus {
    public void deviceStatus(BluetoothDevice bluetoothDevice, boolean status);
}
