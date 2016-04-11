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
    @IntDef({States.STARTED, States.IN_PROGRESS, States.COMPLETED, States.FAILED})
    public @interface State {
    }

    @State
    private int state;

    public Sync(@State int state) {
        this.state = state;
    }

    @Sync.State
    public int getState() {
        return state;
    }
}
