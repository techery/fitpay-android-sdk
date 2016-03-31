package com.fitpay.android.wearable.interfaces;

/**
 * Created by Vlad on 29.03.2016.
 */
public interface ISecureMessage extends IMessage {
    boolean isNfcEnabled();
    byte getNfcErrorCode();
}
