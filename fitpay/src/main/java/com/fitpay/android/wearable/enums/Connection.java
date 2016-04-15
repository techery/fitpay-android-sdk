package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.wearable.constants.States;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Wearable connection states enum
 */
public final class Connection {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({States.DISCONNECTED, States.CONNECTING, States.CONNECTED, States.DISCONNECTING, States.INITIALIZED})
    public @interface State {
    }

    @State
    private int state;

    public Connection(@State int state) {
        this.state = state;
    }

    @Connection.State
    public int getState() {
        return state;
    }
}
