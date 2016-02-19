package com.fitpay.android.api.enums;

import com.google.gson.annotations.SerializedName;

//TODO: replace with @IntDef

public enum ErrorCode {

    @SerializedName("200")
    OK,

    @SerializedName("400")
    BADREQUEST,

    @SerializedName("401")
    UNAUTHORIZED,

    @SerializedName("402")
    REQUEST_FAILED,

    @SerializedName("404")
    NOT_FOUND,

    @SerializedName("500")
    SERVER_ERROR_0,

    @SerializedName("502")
    SERVER_ERROR_1,

    @SerializedName("503")
    SERVER_ERROR_2,

    @SerializedName("504")
    SERVER_ERROR_3;
}