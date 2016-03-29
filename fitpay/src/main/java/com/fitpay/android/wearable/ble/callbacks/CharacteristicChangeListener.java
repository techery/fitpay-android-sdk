package com.fitpay.android.wearable.ble.callbacks;

import android.bluetooth.BluetoothGattCharacteristic;

public interface CharacteristicChangeListener {
    void onCharacteristicChanged(BluetoothGattCharacteristic characteristic);
}
