package com.fitpay.android.wearable.ble.callbacks;

/**
 * Descriptor read callback
 */
public interface GattDescriptorReadCallback {
    void call(byte[] data);
}
