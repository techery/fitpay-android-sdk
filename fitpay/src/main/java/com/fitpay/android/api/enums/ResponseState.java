package com.fitpay.android.api.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Vlad on 04.04.2016.
 */
public final class ResponseState {
    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String FAILED = "FAILED";
    public static final String EXPIRED = "EXPIRED";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({SUCCESSFUL, FAILED, EXPIRED})
    public @interface ApduState {}
}
