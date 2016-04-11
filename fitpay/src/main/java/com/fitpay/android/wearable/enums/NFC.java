package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.wearable.constants.States;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes action with NFC
 */
public class NFC {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({States.ENABLE, States.DISABLE, States.DONT_CHANGE})
    public @interface Action {
    }

    @Action
    private byte action;

    public NFC(@Action byte action) {
        this.action = action;
    }

    @Action
    public byte getAction() {
        return action;
    }
}
