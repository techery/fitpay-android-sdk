package com.fitpay.android.wearable.ble.callbacks;

/**
 * Created by Vlad on 23.03.2016.
 */
public interface DeviceSearchListener {
    void onNewDevice();
    void onSearchBegin();
    void onSearchEnd();
}
