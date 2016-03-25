package com.fitpay.android.wearable.bluetooth.gatt;

import android.bluetooth.BluetoothGattCharacteristic;

public interface CharacteristicChangeListener {
    void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic);
}
