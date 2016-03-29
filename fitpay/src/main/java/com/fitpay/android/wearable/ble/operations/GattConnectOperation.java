package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.fitpay.android.wearable.ble.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.ble.interfaces.CharacteristicReader;
import com.orhanobut.logger.Logger;

import java.util.UUID;

public class GattConnectOperation extends GattOperation{

    public GattConnectOperation() {
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        //do nothing here
    }
}
