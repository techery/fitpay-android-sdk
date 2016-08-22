package com.fitpay.android.paymentdevice.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * App Message (GCM)
 */
public final class AppMessage {

    public static final String PROVISION = "Provision";
    public static final String ACTIVATE = "Activate";
    public static final String SUSPEND = "Suspend";
    public static final String PAYMENT_NOTIFICATION = "Payment Notification";
    public static final String SYNC = "Sync";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PROVISION, ACTIVATE, SUSPEND, PAYMENT_NOTIFICATION, SYNC})
    public @interface Type {
    }

    @Type
    private String type;

    public AppMessage(@Type String type) {
        this.type = type;
    }

    @Type
    public String getType() {
        return type;
    }
}
