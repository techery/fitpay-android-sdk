package com.fitpay.android.webview.impl;

import com.fitpay.android.webview.events.RtmMessage;
import com.fitpay.android.webview.impl.parser.RtmParser;
import com.fitpay.android.webview.impl.parser.RtmParserV2;
import com.fitpay.android.webview.impl.parser.RtmParserV3;
import com.fitpay.android.webview.impl.parser.RtmParserV4;
import com.fitpay.android.webview.impl.parser.RtmParserV5;

/**
 * implementation of {@link RtmMessage} parser
 */
public class RtmParserImpl {
    public static void parse(WebViewCommunicatorImpl wvComImpl, int rtmVersion, RtmMessage msg) {
        RtmParser parser;
        switch (rtmVersion) {
            case 2:
                parser = new RtmParserV2(wvComImpl);
                break;
            case 3:
                parser = new RtmParserV3(wvComImpl);
                break;
            case 4:
                parser = new RtmParserV4(wvComImpl);
                break;
            case 5:
                parser = new RtmParserV5(wvComImpl);
                break;
            default:
                throw new IllegalStateException("WebApp RTM version:" + rtmVersion + " is not supported");
        }

        parser.parseMessage(msg);
    }
}
