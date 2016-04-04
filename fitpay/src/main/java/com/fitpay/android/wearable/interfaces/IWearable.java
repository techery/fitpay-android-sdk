package com.fitpay.android.wearable.interfaces;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.wearable.enums.States;

/**
 * Created by Vlad on 29.03.2016.
 */
public interface IWearable {
    boolean isInitialized();
    void connect();
    void disconnect();
    void close();
    void getDeviceInfo();
    void getNFCState();
    void setNFCState(@States.NFC byte state);
    void sendApduPackage(ApduPackage apduPackage);
    void sendTransactionData(byte[] data);
    void setSecureElementState(@States.SecureElement byte state);
}
