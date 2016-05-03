package com.fitpay.android.paymentdevice.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.orhanobut.logger.Logger;

import java.util.UUID;

/**
 * Read Gatt descriptor operation
 */
class GattDescriptorReadOperation extends GattBaseReadOperation {

    public GattDescriptorReadOperation(UUID service, UUID characteristic, UUID descriptor, OnReadCallback callback) {
        super(service, characteristic, callback);
        mDescriptor = descriptor;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Logger.d("Reading from " + mDescriptor);
        BluetoothGattDescriptor descriptor = gatt.getService(mService).getCharacteristic(mCharacteristic).getDescriptor(mDescriptor);
        gatt.readDescriptor(descriptor);
    }

    @Override
    public void onRead(byte[] data) {
        if(mCallback != null) {
            mCallback.call(data);
        }
    }
}
