package com.fitpay.android.paymentdevice.impl.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.fitpay.android.utils.FPLog;

import java.util.UUID;

/**
 * Write Gatt descriptor operation
 */
class GattDescriptorWriteOperation extends GattOperation {

    public GattDescriptorWriteOperation(UUID service, UUID characteristic, UUID descriptor) {
        mService = service;
        mCharacteristic = characteristic;
        mDescriptor = descriptor;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        FPLog.d("Writing to " + mDescriptor);
        BluetoothGattDescriptor descriptor = gatt.getService(mService).getCharacteristic(mCharacteristic).getDescriptor(mDescriptor);
        gatt.writeDescriptor(descriptor);
    }
}
