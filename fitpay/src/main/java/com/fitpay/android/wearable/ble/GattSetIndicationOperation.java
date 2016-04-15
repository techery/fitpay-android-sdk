package com.fitpay.android.wearable.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Subscribe to indication operation
 */
class GattSetIndicationOperation extends GattSetNotificationOperation {

    public GattSetIndicationOperation(UUID serviceUuid, UUID characteristicUuid, UUID descriptorUuid) {
        super(serviceUuid, characteristicUuid, descriptorUuid);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        super.execute(gatt);
    }

    @Override
    protected byte[] getConfigurationValue() {
        return BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
    }
}
