package com.fitpay.android.wearable.ble;

/**
 * Data reader interface
 */
interface DataReader {
    void onRead(byte[] data);
}
