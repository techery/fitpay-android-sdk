package com.fitpay.android.wearable.listeners;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.wearable.enums.States;
import com.fitpay.android.wearable.model.ApduPair;

/**
 * Created by Vlad on 05.04.2016.
 */
public interface SyncListener {
    void onDeviceStateChanged(@States.Wearable int state);

    void onDeviceInfoReceived(Device device);

    void onNFCStateReceived(boolean isEnabled);

    void onTransactionReceived(byte[] data);

    void onApduPackageResultReceived(ApduPair pair);

    void onApplicationControlReceived(byte[] data);
}
