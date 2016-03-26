package com.fitpay.android.wearable.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.orhanobut.logger.Logger;

import java.util.UUID;

public class GattDescriptorWriteOperation extends GattOperation {

    private final UUID mService;
    private final UUID mCharacteristic;
    private final UUID mDescriptor;

    public GattDescriptorWriteOperation(BluetoothDevice device, UUID service, UUID characteristic, UUID descriptor) {
        super(device);
        mService = service;
        mCharacteristic = characteristic;
        mDescriptor = descriptor;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Logger.d("Writing to " + mDescriptor);
        BluetoothGattDescriptor descriptor = gatt.getService(mService).getCharacteristic(mCharacteristic).getDescriptor(mDescriptor);
        gatt.writeDescriptor(descriptor);
    }
}
