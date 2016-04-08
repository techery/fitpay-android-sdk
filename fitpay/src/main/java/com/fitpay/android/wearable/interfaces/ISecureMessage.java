package com.fitpay.android.wearable.interfaces;

/**
 * abstract interface for NFC object
 */
public interface ISecureMessage {
    boolean isNfcEnabled();
    byte getNfcErrorCode();
}
