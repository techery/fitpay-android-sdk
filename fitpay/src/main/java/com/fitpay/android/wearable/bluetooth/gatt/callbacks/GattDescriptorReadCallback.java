package com.fitpay.android.wearable.bluetooth.gatt.callbacks;

public interface GattDescriptorReadCallback {
    void call(byte[] value);
}
