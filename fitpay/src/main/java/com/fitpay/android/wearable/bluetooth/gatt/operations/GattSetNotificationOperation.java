package com.fitpay.android.wearable.bluetooth.gatt.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

public class GattSetNotificationOperation extends GattOperation {

    public GattSetNotificationOperation(UUID serviceUuid, UUID characteristicUuid, UUID descriptorUuid) {
        mService = serviceUuid;
        mCharacteristic = characteristicUuid;
        mDescriptor = descriptorUuid;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        BluetoothGattCharacteristic characteristic = gatt.getService(mService).getCharacteristic(mCharacteristic);
        boolean enable = true;
        gatt.setCharacteristicNotification(characteristic, enable);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(mDescriptor);
        descriptor.setValue(getConfigurationValue());
        gatt.writeDescriptor(descriptor);
    }

    protected byte[] getConfigurationValue() {
        return BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    }
}
