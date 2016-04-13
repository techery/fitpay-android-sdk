package com.fitpay.android.wearable.ble.interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Characteristic read implementation
 */
public interface CharacteristicReader {
    void onRead(BluetoothGattCharacteristic characteristic);
}
