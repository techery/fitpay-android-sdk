package com.fitpay.android.wearable.interfaces;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.wearable.enums.States;

/**
 * Created by Vlad on 29.03.2016.
 */
public interface IWearable {
    void connect();

    void disconnect();

    void reconnect();

    void close();

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
