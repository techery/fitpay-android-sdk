package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.wearable.constants.States;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * NFC actions enum
 */
public final class NFC {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({States.ENABLE, States.DISABLE, States.DONT_CHANGE})
    public @interface Action {
    }
}
