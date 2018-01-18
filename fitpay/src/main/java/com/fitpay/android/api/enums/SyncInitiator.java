package com.fitpay.android.api.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Sync initiators enum
 */
public final class SyncInitiator {

    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String PLATFORM = "PLATFORM";
    public static final String WEB_HOOK = "WEB_HOOK";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PLATFORM, NOTIFICATION, WEB_HOOK})
    public @interface Initiator {
    }
}
