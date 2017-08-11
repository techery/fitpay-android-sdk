package com.fitpay.android.webview.impl.parser;

import com.fitpay.android.webview.enums.RtmType;
import com.fitpay.android.webview.events.RtmMessage;
import com.fitpay.android.webview.impl.WebViewCommunicatorImpl;

/**
 * RtmMessage parser v3
 */
public class RtmParserV4 extends RtmParserV2 {

    public RtmParserV4(WebViewCommunicatorImpl impl) {
        super(impl);
    }

    @Override
    public void parseMessage(RtmMessage msg) {
        switch (msg.getType()) {
            case RtmType.SCAN_REQUEST:
                impl.startScan();
                break;

            default:
                super.parseMessage(msg);
        }
    }
}
