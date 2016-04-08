package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Vlad on 06.04.2016.
 */
public final class SyncEvent {

    public static final int STARTED = 0;
    public static final int IN_PROGRESS = 1;
    public static final int COMPLETED = 2;
    public static final int FAILED = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STARTED, IN_PROGRESS, COMPLETED, FAILED})
    public @interface State {
    }

    @State
    private int state;

    public SyncEvent(@State int state) {
        this.state = state;
    }

    @SyncEvent.State
    public int getState() {
        return state;
    }
}
