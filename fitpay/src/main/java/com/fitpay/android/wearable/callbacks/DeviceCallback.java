package com.fitpay.android.wearable.callbacks;

/**
 * Created by Vlad on 23.03.2016.
 */
public interface DeviceCallback {
    void onConnect();
    void onDisconnect();
    void onDataSent();
    void onDataReceived();
}
