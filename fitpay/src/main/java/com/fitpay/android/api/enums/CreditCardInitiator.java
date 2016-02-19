package com.fitpay.android.api.enums;

import com.google.gson.annotations.SerializedName;

//TODO: replace with @StringDef

public enum CreditCardInitiator {

    @SerializedName("${CARDHOLDER}")
    CARDHOLDER("${CARDHOLDER}"),

    @SerializedName("${ISSUER}")
    ISSUER("${ISSUER}");

    String initiator;

    CreditCardInitiator(String initiator) {
        this.initiator = initiator;
    }
}