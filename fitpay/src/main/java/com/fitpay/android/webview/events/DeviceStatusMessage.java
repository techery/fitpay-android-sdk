package com.fitpay.android.webview.events;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Responses from other systems
 */
public class DeviceStatusMessage {

    public static final int ERROR = 0;
    public static final int SUCCESS = 1;
    public static final int PROGRESS = 2;
    public static final int PENDING = 3;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({ ERROR, SUCCESS, PROGRESS, PENDING})
    public @interface Code{}

    private String message;
    @Code
    private int type;

    public DeviceStatusMessage(String status, @Code int type) {
        this.message = status;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
