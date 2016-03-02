package com.fitpay.android.api.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Vlad on 01.03.2016.
 */
public class CommitTypes {
    public static final String CREDITCARD_CREATED = "CREDITCARD_CREATED";
    public static final String CREDITCARD_DEACTIVATED = "CREDITCARD_DEACTIVATED";
    public static final String CREDITCARD_ACTIVATED = "CREDITCARD_ACTIVATED";
    public static final String CREDITCARD_DELETED = "CREDITCARD_DELETED";
    public static final String RESET_DEFAULT_CREDITCARD = "RESET_DEFAULT_CREDITCARD";
    public static final String SET_DEFAULT_CREDITCARD = "SET_DEFAULT_CREDITCARD";
    public static final String APDU_PACKAGE = "APDU_PACKAGE ";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            CREDITCARD_CREATED,
            CREDITCARD_ACTIVATED,
            CREDITCARD_DEACTIVATED,
            CREDITCARD_DELETED,
            RESET_DEFAULT_CREDITCARD,
            SET_DEFAULT_CREDITCARD,
            APDU_PACKAGE
    })
    public @interface Type{}
}
