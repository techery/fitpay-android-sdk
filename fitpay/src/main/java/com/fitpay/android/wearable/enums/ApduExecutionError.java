package com.fitpay.android.wearable.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Vlad on 13.04.2016.
 */
public class ApduExecutionError {

    public static final int CONTINUATION_ERROR = 0;
    public static final int ON_TIMEOUT = 1;
    public static final int WRONG_CHECKSUM = 2;
    public static final int WRONG_SEQUENCE = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONTINUATION_ERROR, ON_TIMEOUT, WRONG_CHECKSUM, WRONG_SEQUENCE})
    public @interface Reason {
    }

    private @Reason int reason;

    public ApduExecutionError(@Reason int reason) {
        this.reason = reason;
    }

    public @ApduExecutionError.Reason int getReason(){
        return reason;
    }
}
