package com.fitpay.android.wearable.bluetooth.gatt;

public interface GattCharacteristicReadCallback {
    void call(byte[] characteristic);
}
