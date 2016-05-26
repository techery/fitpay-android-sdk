package com.fitpay.android.paymentdevice;

/**
 * Created by tgs on 5/26/16.
 */
public class DeviceOperationException extends Exception {

    private int errorCode;

    public DeviceOperationException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DeviceOperationException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
