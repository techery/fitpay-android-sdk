package com.fitpay.android.wearable.ble.callbacks;

public interface GattDescriptorReadCallback {
    void call(byte[] data);
}
