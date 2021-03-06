package com.fitpay.android.paymentdevice.callbacks;

import com.fitpay.android.utils.Listener;
import com.fitpay.android.paymentdevice.enums.Connection;

/**
 * Connection callback
 */
public abstract class ConnectionListener extends Listener implements IListeners.ConnectionListener {
    public ConnectionListener() {
        super();
        mCommands.put(Connection.class, data -> {
            Connection event = (Connection) data;
            onDeviceStateChanged(event.getState());
        });
    }
}
