package com.fitpay.android.webview.events;

/**
 * Request for {@link com.fitpay.android.webview.models.IdVerification} data
 */
public class IdVerificationRequest {
    private String callbackId;

    public IdVerificationRequest(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getCallbackId() {
        return callbackId;
    }
}
