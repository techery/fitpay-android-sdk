package com.fitpay.android.webview.enums;

import android.support.annotation.StringDef;

/**
 * Rtm request & response types
 */
public class RtmType {

    public static final int RTM_VERSION = 4;

    public static final String VERSION = "version";
    public static final String SYNC = "sync";
    public static final String USER_DATA = "userData";
    public static final String NO_HISTORY = "noHistory";
    public static final String SCAN_REQUEST = "scanRequest";
    public static final String CARD_SCANNED = "cardScanned";
    public static final String SDK_VERSION = "sdkVersion";
    public static final String SDK_VERSION_REQUEST = "sdkVersionRequest";

    @StringDef({VERSION, SYNC, USER_DATA, NO_HISTORY, SCAN_REQUEST, SDK_VERSION_REQUEST})
    public @interface Request {
    }

    public static final String DEVICE_STATUS = "deviceStatus";
    public static final String LOGOUT = "logout";
    public static final String RESOLVE = "resolve";
    public static final String UNRECOGNIZED = "unrecognized";

    @StringDef({DEVICE_STATUS, LOGOUT, RESOLVE, VERSION, CARD_SCANNED, SDK_VERSION, UNRECOGNIZED})
    public @interface Response {
    }
}
