package com.fitpay.android.wearable.bluetooth.gatt.interfaces;

import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by Vlad on 25.03.2016.
 */
public interface DescriptionReader {
    void onRead(BluetoothGattDescriptor descriptor);
}
