package com.fitpay.android.wearable.services.deviceinfo;

import java.util.UUID;

/**
 * Created by ssteveli on 1/23/16.
 */
public class DeviceInformationConstants {

    public static final UUID SERVICE_UUID  = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");

    public static final String CHARACTERISTIC_MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb";

    public static final String CHARACTERISTIC_FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";

}
