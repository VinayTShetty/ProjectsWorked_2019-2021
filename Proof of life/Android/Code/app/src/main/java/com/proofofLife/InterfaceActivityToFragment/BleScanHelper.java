package com.proofofLife.InterfaceActivityToFragment;

import com.proofofLife.CustomObjects.CustBluetootDevice;

public interface BleScanHelper {
    void scanDevices(CustBluetootDevice custBluetootDevice, String SCAN_TAG);
    void scanStarted(String SCAN_TAG);
    void scanStoped(String SCAN_TAG);
}
