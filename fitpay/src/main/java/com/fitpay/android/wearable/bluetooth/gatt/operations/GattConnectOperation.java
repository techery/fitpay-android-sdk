package com.fitpay.android.wearable.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class GattConnectOperation extends GattOperation {

    public GattConnectOperation(BluetoothDevice device) {
        super(device);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
    }
}
