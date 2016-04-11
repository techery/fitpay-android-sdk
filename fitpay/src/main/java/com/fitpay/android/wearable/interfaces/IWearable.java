package com.fitpay.android.wearable.interfaces;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.wearable.enums.Connection;
import com.fitpay.android.wearable.enums.NFC;
import com.fitpay.android.wearable.enums.SecureElement;

/**
 * abstract interface of wearable payment device
 */
public interface IWearable {
    void connect();
    void disconnect();
    void reconnect();
    void close();
    String getMacAddress();
    void getDeviceInfo();
    void getNFCState();
    void setNFCState(@NFC.Action byte state);
    void sendApduPackage(ApduPackage apduPackage);
    void sendTransactionData(byte[] data);
    void setSecureElementState(@SecureElement.Action byte state);
    @Connection.State int getState();
    void setState(@Connection.State int state);
}
