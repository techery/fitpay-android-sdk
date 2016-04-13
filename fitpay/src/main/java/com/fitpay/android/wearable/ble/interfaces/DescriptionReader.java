package com.fitpay.android.wearable.ble.interfaces;

import android.bluetooth.BluetoothGattDescriptor;

/**
 * Description read implementation
 */
public interface DescriptionReader {
    void onRead(BluetoothGattDescriptor descriptor);
}
