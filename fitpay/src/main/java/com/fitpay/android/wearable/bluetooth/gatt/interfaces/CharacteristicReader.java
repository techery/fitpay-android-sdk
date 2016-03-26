package com.fitpay.android.wearable.bluetooth.gatt.interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by Vlad on 25.03.2016.
 */
public interface CharacteristicReader {
    void onRead(BluetoothGattCharacteristic characteristic);
}
