package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.wearable.enums.States;

/**
 * Created by Vlad on 05.04.2016.
 */
public interface ConnectionListener {
    void onConnectionStateChanged(@States.Wearable int state);
}
