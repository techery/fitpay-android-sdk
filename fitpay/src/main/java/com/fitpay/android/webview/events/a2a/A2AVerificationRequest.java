package com.fitpay.android.webview.events.a2a;

/**
 * App-to-app verification request.
 */
public class A2AVerificationRequest {
    private String cardType;
    private String returnLocation;
    private ATAContext context;
    private String callbackId; //internal usage

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }


    public String getCardType() {
        return cardType;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public ATAContext getContext() {
        return context;
    }

    /**
     * App-to-App context data for {@link android.content.Intent}
     */
    private static class ATAContext {
        private String applicationId;
        private String action;
        private String payload;

        public String getApplicationId() {
            return applicationId;
        }

        public String getAction() {
            return action;
        }

        public String getPayload() {
            return payload;
        }
    }
}
