package com.fitpay.android.paymentdevice.interfaces;

/**
 * abstract interface for APDU object
 */
public interface IApduMessage {
    byte getResult();
    int getSequenceId();
    byte[] getData();
}
