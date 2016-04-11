package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.api.models.device.Device;

/**
 * Created by Vlad on 11.04.2016.
 */
interface IWearableListener extends IApduListener, IConnectionListener {
    void onDeviceInfoReceived(Device device);
    void onNFCStateReceived(boolean isEnabled, byte errorCode);
    void onTransactionReceived(byte[] data);
    void onApplicationControlReceived(byte[] data);
}
