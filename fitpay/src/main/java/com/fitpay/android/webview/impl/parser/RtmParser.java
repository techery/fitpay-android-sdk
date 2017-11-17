package com.fitpay.android.webview.impl.parser;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.webview.events.RtmMessage;
import com.fitpay.android.webview.events.UnrecognizedRtmMessage;
import com.fitpay.android.webview.impl.WebViewCommunicatorImpl;

/**
 * RtmMessage parser
 */
public class RtmParser {
    protected WebViewCommunicatorImpl impl;

    public RtmParser(WebViewCommunicatorImpl impl) {
        this.impl = impl;
    }

    public void parseMessage(RtmMessage msg) {
        switch (msg.getType()) {
            default:
                RxBus.getInstance().post(new UnrecognizedRtmMessage(msg));
        }
    }

    protected void throwException(String message) {
        throw new IllegalStateException(message);
    }
}
