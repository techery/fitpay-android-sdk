package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.wearable.constants.States;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes data sync state
 */
public final class Sync {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({States.STARTED, States.IN_PROGRESS, States.COMPLETED, States.FAILED, States.INC_PROGRESS})
    public @interface State {
    }

    private @State int state;
    private int value;

    public Sync(@State int state) {
        this.state = state;
    }

    public Sync(@State int state, int value) {
        this.state = state;
        this.value = value;
    }

    @Sync.State
    public int getState() {
        return state;
    }

    public int getValue(){
        return value;
    }
}
