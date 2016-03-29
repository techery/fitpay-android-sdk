package com.fitpay.android.wearable.ble.constants;

import java.util.UUID;

public class CurrentTimeConstants {
    public static final UUID SERVICE_UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_CURRENT_TIME = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_LOCAL_TIME_INFORMATION = UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_REFERENCE_TIME_INFORMATION = UUID.fromString("00002a14-0000-1000-8000-00805f9b34fb");
}
