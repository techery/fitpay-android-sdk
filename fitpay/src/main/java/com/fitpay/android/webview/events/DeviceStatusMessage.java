package com.fitpay.android.webview.events;

/**
 * Responses from other systems
 */
public class DeviceStatusMessage {

    private String message;
    private int type;

    public DeviceStatusMessage(String status, int type) {
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
