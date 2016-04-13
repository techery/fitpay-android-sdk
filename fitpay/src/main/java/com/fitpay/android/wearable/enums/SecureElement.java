package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.wearable.constants.States;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes action with SecureElement
 */
public final class SecureElement {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({States.POWER_OFF, States.POWER_ON, States.RESET})
    public @interface Action {
    }
}
