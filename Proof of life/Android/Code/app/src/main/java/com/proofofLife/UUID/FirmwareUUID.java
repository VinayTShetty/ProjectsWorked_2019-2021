package com.proofofLife.UUID;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.UUID;

public class FirmwareUUID {
    public static final UUID PROOF_OF_LIFE_SERVICE_UUID= UUID.fromString("0000ab00-2987-4433-2208-abf9b34fb000");
    public static final UUID PROOF_OF_LIFE_CHARCTERSTICS_UUID=UUID.fromString("0000ab01-2987-4433-2208-abf9b34fb000");
    /**
     * client chanrcterstic UUID,for enabling the Charcterstic notification.
     */
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
}
