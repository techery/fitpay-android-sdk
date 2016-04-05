package com.fitpay.android.wearable.listeners;

import com.fitpay.android.wearable.enums.States;

/**
 * Created by Vlad on 05.04.2016.
 */
public interface ConnectionStateListener {
    void onStateChanged(@States.Wearable int state);
}
