package com.fitpay.android.api.enums;

import android.support.annotation.IntDef;

public class ErrorCode{
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int REQUEST_FAILED = 402;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR_0 = 500;
    public static final int SERVER_ERROR_1 = 502;
    public static final int SERVER_ERROR_2 = 503;
    public static final int SERVER_ERROR_3 = 504;

    @IntDef({OK, BAD_REQUEST, UNAUTHORIZED, REQUEST_FAILED, NOT_FOUND, SERVER_ERROR_0, SERVER_ERROR_1, SERVER_ERROR_2, SERVER_ERROR_3})
    public @interface Code{}
}