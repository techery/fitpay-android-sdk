package com.fitpay.android.paymentdevice.interfaces;

/**
 * abstract interface for NFC object
 */
public interface ISecureMessage {
    boolean isNfcEnabled();
    byte getNfcErrorCode();
}
