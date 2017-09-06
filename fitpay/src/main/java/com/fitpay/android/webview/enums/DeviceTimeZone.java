package com.fitpay.android.webview.enums;

import android.support.annotation.IntDef;

/**
 * Device time zone set by
 */

public class DeviceTimeZone {
    public static final int SET_BY_NETWORK = 1;
    public static final int SET_BY_USER = 2;
    public static final int SET_BY_DEVICE_LOCATION = 3;

    @IntDef({SET_BY_NETWORK, SET_BY_USER, SET_BY_DEVICE_LOCATION})
    public @interface SetBy {
    }
}
