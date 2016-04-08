package com.fitpay.android.api.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Vlad on 04.04.2016.
 */
public final class ResponseState {

    public static final String PROCESSED = "PROCESSED"; // all apdu commands were processed
    public static final String FAILED = "FAILED"; // at least one apdu command was not successful
    public static final String ERROR = "ERROR"; // processing was stopped due to a condition other than apdu failure (no response to apdu, etc.)
    public static final String EXPIRED = "EXPIRED"; // adpu package expired prior to being executed

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PROCESSED, FAILED, ERROR, EXPIRED})
    public @interface ApduState {
    }
}
