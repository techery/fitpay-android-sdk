package com.fitpay.android.rtm.constants;

public class Const {
    public static final int SUCCESS = 0;
    public static final int ER_UNKNOWN = SUCCESS + 1;
    public static final int ER_NO_USER_DATA_AVAILABLE = ER_UNKNOWN + 1;
    public static final int ER_UNAUTHORIZED_TOKEN_REJECTED = ER_NO_USER_DATA_AVAILABLE + 1;
    public static final int ER_COMMITS_COULD_NOT_BE_APPLIED_TO_DEVICE = ER_UNAUTHORIZED_TOKEN_REJECTED + 1;
}
