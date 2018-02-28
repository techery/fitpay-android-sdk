package com.fitpay.android.webview.models.a2a;

/**
 * RTM response data for {@value com.fitpay.android.webview.enums.RtmType#SUPPORTS_ISSUER_APP_AUTH}
 */
public class A2ASupportsAuth {

    private boolean supportsIssuerAppAuth;

    public A2ASupportsAuth(boolean supportsIssuerAppAuth) {
        this.supportsIssuerAppAuth = supportsIssuerAppAuth;
    }
}
