package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.enums.States;

/**
 * Created by Vlad on 05.04.2016.
 */
public interface WearableListener extends ApduListener{
    void onDeviceStateChanged(@States.Wearable int state);
    void onDeviceInfoReceived(Device device);
    void onNFCStateReceived(boolean isEnabled);
    void onTransactionReceived(byte[] data);
    void onApplicationControlReceived(byte[] data);
}
