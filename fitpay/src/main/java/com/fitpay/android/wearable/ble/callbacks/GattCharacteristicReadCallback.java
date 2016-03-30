package com.fitpay.android.wearable.ble.callbacks;

public interface GattCharacteristicReadCallback {
    void call(byte[] data);
}
