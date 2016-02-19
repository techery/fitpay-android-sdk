package com.fitpay.android.rtm.enums;

import android.support.annotation.IntDef;

import com.fitpay.android.rtm.constants.Const;

@IntDef({
        Const.SUCCESS,
        Const.ER_UNKNOWN,
        Const.ER_NO_USER_DATA_AVAILABLE,
        Const.ER_UNAUTHORIZED_TOKEN_REJECTED,
        Const.ER_COMMITS_COULD_NOT_BE_APPLIED_TO_DEVICE})
public @interface SyncErrorCode {}

