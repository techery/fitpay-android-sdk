package com.fitpay.android.wearable.interfaces;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Commit;
import com.fitpay.android.wearable.enums.States;

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
    void setNFCState(@States.NFC byte state);
    void sendApduPackage(ApduPackage apduPackage);
    void sendTransactionData(byte[] data);
    void setSecureElementState(@States.SecureElement byte state);
    @States.Wearable
    int getState();
    void setState(@States.Wearable int state);
}
