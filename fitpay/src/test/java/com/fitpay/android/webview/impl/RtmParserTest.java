package com.fitpay.android.webview.impl;

import android.app.Activity;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.webview.enums.RtmType;
import com.fitpay.android.webview.events.RtmMessage;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by Vlad on 19.07.2017.
 */

public class RtmParserTest {

    private static WebViewCommunicatorImpl wvci;

    @BeforeClass
    public static void init() {
        Activity context = Mockito.mock(Activity.class);
        wvci = new WebViewCommunicatorImpl(context, -1);
    }

    @Test
    public void testWebAppVersionLower() {
        String rtmMsgStr = "{\"callbackId\":\"0\",\"jsonData\":\"{\\\"version\\\":3}\",\"type\":\"version\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);
        int webAppRtmVersion = 2;

        String errorMsg = null;
        try {
            RtmParserImpl.parse(wvci, webAppRtmVersion, msg);
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        Assert.assertNull(errorMsg);
    }

    @Test
    public void testWebAppVersionHigher() {
        String rtmMsgStr = "{\"callbackId\":\"0\",\"jsonData\":\"{\\\"version\\\":3}\",\"type\":\"version\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);
        int webAppRtmVersion = RtmType.RTM_VERSION + 1;

        String errorMsg = null;
        try {
            RtmParserImpl.parse(wvci, webAppRtmVersion, msg);
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        Assert.assertEquals("WebApp RTM version:" + webAppRtmVersion + " is not supported", errorMsg);
    }

    @Test
    public void testWebAppVersionSame() {
        String rtmMsgStr = "{\"callbackId\":\"0\",\"jsonData\":\"{\\\"version\\\":3}\",\"type\":\"version\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);
        int webAppRtmVersion = RtmType.RTM_VERSION;

        String errorMsg = null;
        try {
            RtmParserImpl.parse(wvci, webAppRtmVersion, msg);
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        Assert.assertNull(errorMsg);
    }

    @Ignore("needs to be rewritten, we don't throw exceptions anymore... needs to listen to RxBus instead for the unrecognized message")
    @Test
    public void testWebAppVersionSameNoMethod() {
        String rtmMsgStr = "{\"callbackId\":\"9\",\"jsonData\":\"{\\\"next\\\":\\\"\\\\/walletAccess\\\",\\\"previous\\\":\\\"\\\\/cards\\\"}\",\"type\":\"navigationStart\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);
        int webAppRtmVersion = 3;

        String errorMsg = "";
        try {
            RtmParserImpl.parse(wvci, webAppRtmVersion, msg);
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        Assert.assertEquals("unsupported action value navigationStart", errorMsg);
    }

    @Test
    public void testWebAppVersionSameWrongData() {
        String rtmMsgStr = "{\"callbackId\":\"0\",\"type\":\"version\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);
        int webAppRtmVersion = RtmType.RTM_VERSION;

        String errorMsg = null;
        try {
            RtmParserImpl.parse(wvci, webAppRtmVersion, msg);
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        Assert.assertEquals("missing required message data", errorMsg);
    }
}
