package com.fitpay.android.webview.impl.parser;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.webview.enums.RtmType;
import com.fitpay.android.webview.events.RtmMessage;
import com.fitpay.android.webview.events.RtmMessageResponse;
import com.fitpay.android.webview.events.a2a.A2AVerificationRequest;
import com.fitpay.android.webview.impl.WebViewCommunicatorImpl;
import com.fitpay.android.webview.models.a2a.A2ASupportsAuth;

/**
 * RtmMessage parser v6
 */
public class RtmParserV6 extends RtmParserV5 {

    public RtmParserV6(WebViewCommunicatorImpl impl) {
        super(impl);
    }

    @Override
    public void parseMessage(RtmMessage msg) {
        switch (msg.getType()) {
            case RtmType.SUPPORTS_ISSUER_APP_AUTH:
                RxBus.getInstance().post(new RtmMessageResponse(msg.getCallbackId(), true, new A2ASupportsAuth(impl.isSupportAppAuth()), RtmType.SUPPORTS_ISSUER_APP_AUTH));
                break;

            case RtmType.APP_TO_APP_VERIFICATION:
                A2AVerificationRequest appToAppVerification = Constants.getGson().fromJson(msg.getData(), A2AVerificationRequest.class);
                appToAppVerification.setCallbackId(msg.getCallbackId());
                RxBus.getInstance().post(appToAppVerification);
                break;

            default:
                super.parseMessage(msg);
        }
    }
}
