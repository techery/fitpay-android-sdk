package com.fitpay.android.webview.impl.parser;

import com.fitpay.android.webview.events.RtmMessage;
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
                throwException("unsupported action value " + msg.getType());
        }
    }

    protected void throwException(String message) {
        throw new IllegalStateException(message);
    }
}
