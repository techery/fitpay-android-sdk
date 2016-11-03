package com.fitpay.android.webview.events;

/**
 * Created by Vlad on 02.11.2016.
 */

public class RtmMessageResponse extends RtmMessage {

    public RtmMessageResponse(String callbackId, Object data, String type) {
        super(callbackId, data, type);
    }
}
