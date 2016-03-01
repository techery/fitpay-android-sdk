package com.fitpay.android.paymentDevice.enums;

import android.support.annotation.StringDef;

public class PaymentDeviceAlert {

    public static final String TRANSACTION = "Transaction";
    public static final String SECURITY = "Security";
    public static final String CONNECTION = "Connection";

    @StringDef({TRANSACTION, SECURITY, CONNECTION})
    public @interface Type {
    }
}
