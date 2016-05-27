package com.fitpay.android.paymentdevice.interfaces;

import android.content.Context;

import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.paymentdevice.CommitHandler;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.enums.NFC;
import com.fitpay.android.paymentdevice.enums.SecureElement;

import java.util.Properties;

/**
 * abstract interface of wearable payment device
 */
public interface IPaymentDeviceConnector extends CommitHandler {

    void setContext(Context contexxt);
    void init(Properties props);

    void reset();
    void connect();
    void disconnect();
    void reconnect();
    void close();

    String getMacAddress();

    void readDeviceInfo();
    void readNFCState();
    void setNFCState(@NFC.Action byte state);
    void executeApduPackage(ApduPackage apduPackage);
    void sendNotification(byte[] data);
    void setSecureElementState(@SecureElement.Action byte state);

    void syncInit();
    void syncComplete();

    void addCommitHandler(String commitType, CommitHandler handler);
    void removeCommitHandler(String commitType);

    //TODO review - should this have a getState method?
    @Connection.State int getState();
    void setState(@Connection.State int state);
}
