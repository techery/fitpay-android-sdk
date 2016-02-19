package com.fitpay.android.paymentDevice.enums;

import com.google.gson.annotations.SerializedName;

//TODO: change with @StringDef
public enum PaymentDeviceAlert {

    @SerializedName("${TransactionAlert}")
    TRANSACTIONALERT("${TransactionAlert}"),

    @SerializedName("${SecurityAlert}")
    SecurityAlert("${SecurityAlert}"),

    @SerializedName("${ConnectionAlert}")
    CONNECTIONALERT("${ConnectionAlert}");

    String alert;

    PaymentDeviceAlert(String alert) {
        this.alert = alert;
    }
}
