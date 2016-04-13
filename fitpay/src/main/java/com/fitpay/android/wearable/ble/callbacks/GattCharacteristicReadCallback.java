package com.fitpay.android.wearable.ble.callbacks;

/**
 * Characteristic read callback
 */
public interface GattCharacteristicReadCallback {
    void call(byte[] data);
}
