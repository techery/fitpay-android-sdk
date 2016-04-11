package com.fitpay.android.wearable.callbacks;

import com.fitpay.android.utils.Listener;
import com.fitpay.android.wearable.enums.Connection;

/**
 * Created by Vlad on 05.04.2016.
 */
public abstract class ConnectionListener extends Listener implements IConnectionListener {
    public ConnectionListener() {
        super();
        mCommands.put(Connection.class, data -> {
            Connection event = (Connection) data;
            onDeviceStateChanged(event.getState());
        });
    }
}
