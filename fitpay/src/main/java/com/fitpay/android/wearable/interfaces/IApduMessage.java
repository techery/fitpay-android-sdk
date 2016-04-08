package com.fitpay.android.wearable.interfaces;

/**
 * abstract interface for APDU object
 */
public interface IApduMessage {
    byte getResult();
    int getSequenceId();
    byte[] getData();
}
