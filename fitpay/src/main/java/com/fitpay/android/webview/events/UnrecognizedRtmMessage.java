package com.fitpay.android.webview.events;

/**
 * Unrecognized {@link RtmMessage}
 */

public class UnrecognizedRtmMessage {
    private final String type;
    private final String data;
    private final String callbackId;

    public UnrecognizedRtmMessage(RtmMessage msg) {
        this.type = msg.getType();
        this.data = msg.getData();
        this.callbackId = msg.getCallbackId();
    }

    public String getType(){
        return this.type;
    }

    public String getData(){
        return this.data;
    }

    public String getCallbackId(){
        return this.callbackId;
    }
}
