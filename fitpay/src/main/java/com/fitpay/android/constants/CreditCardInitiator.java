package com.fitpay.android.constants;

import com.google.gson.annotations.SerializedName;

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