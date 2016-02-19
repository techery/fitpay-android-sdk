package com.fitpay.android.api.enums;

import com.google.gson.annotations.SerializedName;

//TODO: replace with @StringDef

public enum CommitType {

    @SerializedName("${CREDITCARD_CREATED}")
    CREDITCARD_CREATED("${CREDITCARD_CREATED}"),

    @SerializedName("${CREDITCARD_DEACTIVATED}")
    CREDITCARD_DEACTIVATED("${CREDITCARD_DEACTIVATED}"),

    @SerializedName("${CREDITCARD_ACTIVATED}")
    CREDITCARD_ACTIVATED("${CREDITCARD_ACTIVATED}"),

    @SerializedName("${CREDITCARD_DELETED}")
    CREDITCARD_DELETED("${CREDITCARD_DELETED}"),

    @SerializedName("${RESET_DEFAULT_CREDITCARD}")
    RESET_DEFAULT_CREDITCARD("${RESET_DEFAULT_CREDITCARD}"),

    @SerializedName("${SET_DEFAULT_CREDITCARD}")
    SET_DEFAULT_CREDITCARD("${SET_DEFAULT_CREDITCARD}");

    String type;

    CommitType(String type) {
        this.type = type;
    }
}