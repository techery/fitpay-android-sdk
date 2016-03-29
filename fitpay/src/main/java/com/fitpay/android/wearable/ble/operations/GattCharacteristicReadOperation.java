package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.fitpay.android.wearable.ble.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.ble.interfaces.CharacteristicReader;
import com.orhanobut.logger.Logger;

import java.util.UUID;

public class GattCharacteristicReadOperation extends GattOperation implements CharacteristicReader {

    private final GattCharacteristicReadCallback mCallback;

    public GattCharacteristicReadOperation(UUID service, UUID characteristic, GattCharacteristicReadCallback callback) {
        mService = service;
        mCharacteristic = characteristic;
        mCallback = callback;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Logger.d("writing to " + mCharacteristic);
        BluetoothGattCharacteristic characteristic = gatt.getService(mService).getCharacteristic(mCharacteristic);
        gatt.readCharacteristic(characteristic);
    }

    @Override
    public void onRead(BluetoothGattCharacteristic characteristic) {
        mCallback.call(characteristic.getValue());
    }
}
