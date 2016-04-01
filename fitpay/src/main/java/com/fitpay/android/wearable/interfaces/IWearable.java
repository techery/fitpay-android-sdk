package com.fitpay.android.wearable.interfaces;

import com.fitpay.android.api.models.apdu.ApduPackage;

/**
 * Created by Vlad on 29.03.2016.
 */
public interface IWearable {
    boolean isInitialized();
    void connect();
    void disconnect();
    void close();
    void getDeviceInfo();
    void getSecurityState();
    void setSecurityState(boolean enabled);
    void sendApduPackage(ApduPackage apduPackage);
    void sendTransactionData(byte[] data);
    void resetDevice();
}
