package com.fitpay.android.wearable.interfaces;

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
    void sendAdpuPackage(byte[] pkgData);
    void resetDevice();
}
