package com.fitpay.android.wearable.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.fitpay.android.wearable.utils.OperationQueue;

public abstract class GattOperation{

    private static final int DEFAULT_TIMEOUT_IN_MILLIS = 30000;

    protected BluetoothDevice mDevice;
    protected OperationQueue mNestedQueue;

    public abstract void execute(BluetoothGatt bluetoothGatt);

    public GattOperation(BluetoothDevice device) {
        mDevice = device;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getTimeoutMs() {
        return DEFAULT_TIMEOUT_IN_MILLIS;
    }

    public void setNestedQueue(OperationQueue queue){
        mNestedQueue = queue;
    }

    public OperationQueue getNestedQueue(){
        return mNestedQueue;
    }

    public boolean hasNested(){
        return mNestedQueue != null && mNestedQueue.size() > 0;
    }
}
