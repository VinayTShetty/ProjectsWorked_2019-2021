/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.benjaminshamoilia.trackerapp.helper;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String TRACKER_SERVICE_UUID = "00b04fb3-f9e5-0800-0008-430100ab0000";
    public static String DEVICE_INFO_SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_SERVICE_UUID = "0000180F-0000-1000-8000-00805f9b34fb";


    public static String TRACKER_CHAR_UUID = "0000ab01-0143-0800-0008-e5f9b34fb000";
    public static String MANUFACTURE_CHAR_UUID = "00002a29-0000-1000-8000-00805f9b34fb";


    public static UUID UUID_TRACKER_SERVICE =
            UUID.fromString(SampleGattAttributes.TRACKER_SERVICE_UUID);
    public static UUID UUID_TRACKER_CHARACHTERISTICS =
            UUID.fromString(SampleGattAttributes.TRACKER_CHAR_UUID);
    static {
        // Sample Services.
        attributes.put(TRACKER_SERVICE_UUID, "Tracker Service");
        attributes.put(DEVICE_INFO_SERVICE_UUID, "Device Information Service");
        attributes.put(BATTERY_SERVICE_UUID, "Battery Service");
        // Sample Characteristics.
        attributes.put(TRACKER_CHAR_UUID, "Tracker Characteristic");
        attributes.put(MANUFACTURE_CHAR_UUID, "Manufacturer");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
