package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.enums.States;
import com.fitpay.android.wearable.utils.ApduPair;

/**
 * Created by Vlad on 05.04.2016.
 */
public interface WearableListener {
    void onDeviceStateChanged(@States.Wearable int state);
    void onDeviceInfoReceived(Device device);
    void onNFCStateReceived(boolean isEnabled);
    void onTransactionReceived(byte[] data);
    void onApduPackageResultReceived(ApduPair pair);
    void onApplicationControlReceived(byte[] data);
}
