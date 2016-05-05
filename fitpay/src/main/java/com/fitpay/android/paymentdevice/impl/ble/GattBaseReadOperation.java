package com.fitpay.android.paymentdevice.impl.ble;

import java.util.UUID;

/**
 * Gatt read base operation
 */
abstract class GattBaseReadOperation extends GattOperation implements DataReader{

    protected OnReadCallback mCallback;

    public GattBaseReadOperation(UUID service, UUID characteristic, OnReadCallback callback) {
        mService = service;
        mCharacteristic = characteristic;
        mCallback = callback;
    }

    /**
     * On data read callback
     */
    public interface OnReadCallback {
        void call(byte[] data);
    }
}
