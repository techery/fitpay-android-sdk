package com.fitpay.android.webview.models.a2a;

import com.fitpay.android.webview.events.a2a.A2AVerificationRequest;

/**
 * Issuer response data for {@link A2AVerificationRequest}
 */
public class A2AIssuerResponse {
    public final static String AUTHENTICATION_CODE_RESPONSE = "STEP_UP_AUTH_CODE";
    public final static String STEPUP_RESULT_RESPONSE = "STEP_UP_RESPONSE";

    private String response;
    private String authCode;

    public A2AIssuerResponse(String response, String authCode) {
        this.response = response;
        this.authCode = authCode;
    }
}
