package com.fitpay.android.wearable.bluetooth.gatt.callbacks;

public interface GattCharacteristicReadCallback {
    void call(byte[] characteristic);
}
