package com.fitpay.android.wearable.ble.callbacks;

import android.bluetooth.BluetoothDevice;

/**
 * Device search callback
 */
public interface DeviceSearchListener {
    void onNewDevice(BluetoothDevice device);
    void onSearchBegin();
    void onSearchEnd();
}
