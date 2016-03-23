package com.fitpay.android.wearable.services;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by ssteveli on 1/22/16.
 */
public abstract class ServiceHandler {

    protected final Context context;

    public ServiceHandler(Context context) {
        this.context = context;
    }

    public void close() {

    }

    public void reset() {

    }

    public UUID getServiceUUID() {
        return null;
    }

    public List<String> getCharacteristicsToSubscribe() {
        return Collections.emptyList();
    }

    public boolean canHandleCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        return false;
    }

    public boolean canHandleCharacteristicWrite(BluetoothGattCharacteristic characteristic) {
        return false;
    }

    public void handleCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        // do nothing
    }

    public byte[] handleCharacteristicWrite(BluetoothGattCharacteristic characteristic, int offset, byte[] value) {
        return null;
    }

    public BluetoothGattService buildService() {
        return null;
    }

    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return null;
    }

    public String getCharacteristicName(BluetoothGattCharacteristic characteristic) {
        return null;
    }

    public boolean isAdvertised() {
        return false;
    }
}
