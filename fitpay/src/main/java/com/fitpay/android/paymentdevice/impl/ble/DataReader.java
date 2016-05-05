package com.fitpay.android.paymentdevice.impl.ble;

/**
 * Data reader interface
 */
interface DataReader {
    void onRead(byte[] data);
}
