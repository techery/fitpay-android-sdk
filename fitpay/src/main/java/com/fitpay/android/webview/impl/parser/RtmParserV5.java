package com.fitpay.android.webview.impl.parser;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.webview.enums.RtmType;
import com.fitpay.android.webview.events.IdVerificationRequest;
import com.fitpay.android.webview.events.RtmMessage;
import com.fitpay.android.webview.impl.WebViewCommunicatorImpl;

/**
 * RtmMessage parser v3
 */
public class RtmParserV5 extends RtmParserV4 {

    public RtmParserV5(WebViewCommunicatorImpl impl) {
        super(impl);
    }

    @Override
    public void parseMessage(RtmMessage msg) {
        switch (msg.getType()) {
            case RtmType.ID_VERIFICATION_REQUEST:
                RxBus.getInstance().post(new IdVerificationRequest(msg.getCallbackId()));
                break;

            default:
                super.parseMessage(msg);
        }
    }
}
