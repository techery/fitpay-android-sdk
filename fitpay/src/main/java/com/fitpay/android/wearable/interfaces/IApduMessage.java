package com.fitpay.android.wearable.interfaces;

/**
 * Created by Vlad on 29.03.2016.
 */
public interface IApduMessage extends IMessage{
    byte getResult();
    int getSequenceId();
    byte[] getData();
}
