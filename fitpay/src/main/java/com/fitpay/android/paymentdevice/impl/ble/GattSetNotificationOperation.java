package com.fitpay.android.paymentdevice.impl.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Subscribe to notification operation
 */
class GattSetNotificationOperation extends GattOperation {

    public GattSetNotificationOperation(UUID serviceUuid, UUID characteristicUuid, UUID descriptorUuid) {
        mService = serviceUuid;
        mCharacteristic = characteristicUuid;
        mDescriptor = descriptorUuid;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        BluetoothGattCharacteristic characteristic = gatt.getService(mService).getCharacteristic(mCharacteristic);
        gatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(mDescriptor);
        descriptor.setValue(getConfigurationValue());
        gatt.writeDescriptor(descriptor);
    }

    protected byte[] getConfigurationValue() {
        return BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    }
}
