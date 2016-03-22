package com.fitpay.android.wearable.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PaymentDeviceAlert {

    public static final String TRANSACTION = "Transaction";
    public static final String SECURITY = "Security";
    public static final String CONNECTION = "Connection";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TRANSACTION, SECURITY, CONNECTION})
    public @interface Type {
    }
}
