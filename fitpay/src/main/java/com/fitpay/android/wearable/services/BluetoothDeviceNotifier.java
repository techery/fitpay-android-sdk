package com.fitpay.android.wearable.services;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by ssteveli on 1/25/16.
 */
public interface BluetoothDeviceNotifier {
    void sendNotificationsToDevices(BluetoothGattCharacteristic characteristic);
}
