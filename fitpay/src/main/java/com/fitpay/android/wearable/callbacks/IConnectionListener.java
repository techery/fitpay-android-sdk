package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.wearable.enums.Connection;

/**
 * Created by Vlad on 11.04.2016.
 */
interface IConnectionListener {
    void onDeviceStateChanged(@Connection.State int state);
}
