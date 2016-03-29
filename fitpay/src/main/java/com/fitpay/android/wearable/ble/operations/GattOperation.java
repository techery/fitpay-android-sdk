package com.fitpay.android.wearable.ble.operations;

import android.bluetooth.BluetoothGatt;

import com.fitpay.android.wearable.ble.utils.OperationQueue;

import java.util.UUID;

public abstract class GattOperation {

    private static final int DEFAULT_TIMEOUT_IN_MILLIS = 30000;

    protected UUID mService;
    protected UUID mCharacteristic;
    protected UUID mDescriptor;
    protected OperationQueue mNestedQueue;

    public abstract void execute(BluetoothGatt bluetoothGatt);

    public GattOperation() {
    }

    public int getTimeoutMs() {
        return DEFAULT_TIMEOUT_IN_MILLIS;
    }

    public void setNestedQueue(OperationQueue queue) {
        mNestedQueue = queue;
    }

    public OperationQueue getNestedQueue() {
        return mNestedQueue;
    }

    public boolean hasNested() {
        return mNestedQueue != null && mNestedQueue.size() > 0;
    }
}
