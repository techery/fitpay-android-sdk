package com.fitpay.android.webview.events.a2a;

/**
 * Error event if {@link A2AVerificationRequest} failed
 */
public class A2AVerificationFailed {
    private String reason;

    public A2AVerificationFailed(String reason) {
        this.reason = reason;
    }
}
