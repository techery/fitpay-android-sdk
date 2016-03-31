package com.fitpay.android.wearable.ble.callbacks;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Vlad on 23.03.2016.
 */
public interface DeviceSearchListener {
    void onNewDevice(BluetoothDevice device);
    void onSearchBegin();
    void onSearchEnd();
}
