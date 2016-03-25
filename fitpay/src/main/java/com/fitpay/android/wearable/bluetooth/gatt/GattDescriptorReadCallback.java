package com.fitpay.android.wearable.bluetooth.gatt;

public interface GattDescriptorReadCallback {
    void call(byte[] value);
}
