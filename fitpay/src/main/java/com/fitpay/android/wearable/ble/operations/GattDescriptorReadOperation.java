package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.fitpay.android.wearable.ble.callbacks.GattDescriptorReadCallback;
import com.fitpay.android.wearable.ble.interfaces.DescriptionReader;
import com.orhanobut.logger.Logger;

import java.util.UUID;

public class GattDescriptorReadOperation extends GattOperation implements DescriptionReader {

    private final GattDescriptorReadCallback mCallback;

    public GattDescriptorReadOperation(UUID service, UUID characteristic, UUID descriptor, GattDescriptorReadCallback callback) {
        mService = service;
        mCharacteristic = characteristic;
        mDescriptor = descriptor;
        mCallback = callback;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Logger.d("Reading from " + mDescriptor);
        BluetoothGattDescriptor descriptor = gatt.getService(mService).getCharacteristic(mCharacteristic).getDescriptor(mDescriptor);
        gatt.readDescriptor(descriptor);
    }

    @Override
    public void onRead(BluetoothGattDescriptor descriptor) {
        mCallback.call(descriptor.getValue());
    }
}
