package com.fitpay.android.paymentdevice.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.paymentdevice.constants.States;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Data sync states enum
 */
public final class Sync {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({States.STARTED, States.IN_PROGRESS, States.COMPLETED, States.FAILED, States.INC_PROGRESS, States.COMMIT_COMPLETED, States.COMPLETED_NO_UPDATES})
    public @interface State {
    }

    @State
    private int state;
    private int value;
    private String message;

    public Sync(@State int state) {
        this.state = state;
    }

    public Sync(@State int state, int value) {
        this.state = state;
        this.value = value;
    }

    public Sync(@State int state, String message) {
        this.state = state;
        this.message = message;
    }

    @Sync.State
    public int getState() {
        return state;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return new StringBuilder()
                .append("Sync(")
                .append("state=")
                .append(state)
                .append(", value=")
                .append(value)
                .append(")")
                .toString();
    }
}
