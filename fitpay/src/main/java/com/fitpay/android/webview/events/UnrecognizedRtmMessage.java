package com.fitpay.android.webview.events;

/**
 * Unrecognized {@link RtmMessage}
 */

public class UnrecognizedRtmMessage extends RtmMessage {
    public UnrecognizedRtmMessage(RtmMessage msg) {
        super(msg.getCallbackId(), msg.getJsonData(), msg.getType());
    }
}
