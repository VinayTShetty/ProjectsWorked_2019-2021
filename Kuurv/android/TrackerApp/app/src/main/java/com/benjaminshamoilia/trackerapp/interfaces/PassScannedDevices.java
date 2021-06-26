package com.benjaminshamoilia.trackerapp.interfaces;

import com.polidea.rxandroidble2.RxBleDevice;

public interface PassScannedDevices {
    public void sendingResultstoFragment(RxBleDevice  bleDevice);
}
