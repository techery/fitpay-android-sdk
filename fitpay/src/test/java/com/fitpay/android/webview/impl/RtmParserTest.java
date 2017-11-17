package com.fitpay.android.webview.impl;

import android.app.Activity;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.webview.enums.RtmType;
import com.fitpay.android.webview.events.RtmMessage;
import com.fitpay.android.webview.events.UnrecognizedRtmMessage;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Vlad on 19.07.2017.
 */

public class RtmParserTest {

    private WebViewCommunicatorImpl wvci;

    private NotificationManager manager;
    private Listener listener;

    private UnrecognizedRtmMessage unrecognizedRtmMessage;

    @Before
    public void init() {
        Activity context = Mockito.mock(Activity.class);
        wvci = new WebViewCommunicatorImpl(context, -1);

        manager = NotificationManager.getInstance();
    }

    @After
    public void terminate() {
        wvci = null;

        if (null != listener) {
            manager.removeListener(listener);
        }
    }

    @Test
    public void testWebAppVersionLower() {
        String rtmMsgStr = "{\"callbackId\":\"0\",\"data\":\"{\\\"version\\\":3}\",\"type\":\"version\"}";
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
        String rtmMsgStr = "{\"callbackId\":\"0\",\"data\":\"{\\\"version\\\":3}\",\"type\":\"version\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);
        int webAppRtmVersion = RtmType.RTM_VERSION + 1;

        String errorMsg = null;
        try {
            RtmParserImpl.parse(wvci, webAppRtmVersion, msg);
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        assertEquals("WebApp RTM version:" + webAppRtmVersion + " is not supported", errorMsg);
    }

    @Test
    public void testWebAppVersionSame() {
        String rtmMsgStr = "{\"callbackId\":\"0\",\"data\":\"{\\\"version\\\":3}\",\"type\":\"version\"}";
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
        String rtmMsgStr = "{\"callbackId\":\"9\",\"data\":\"{\\\"next\\\":\\\"\\\\/walletAccess\\\",\\\"previous\\\":\\\"\\\\/cards\\\"}\",\"type\":\"navigationStart\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);
        int webAppRtmVersion = 3;

        String errorMsg = "";
        try {
            RtmParserImpl.parse(wvci, webAppRtmVersion, msg);
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        }

        assertEquals("unsupported action value navigationStart", errorMsg);
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

        assertEquals("missing required message data", errorMsg);
    }

    @Test
    public void testUnrecognizedRtmMessage() {
        final CountDownLatch latch = new CountDownLatch(1);

        listener = new UnrecognizedRtmMessageListener(latch);
        manager.addListenerToCurrentThread(listener);

        String rtmMsgStr = "{\"callbackId\":\"10\",\"data\":\"{\\\"resource\\\":\\\"The Truth Is Out There\\\"}\",\"type\":\"somethingUnknown\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);

        int webAppRtmVersion = 5;
        RtmParserImpl.parse(wvci, webAppRtmVersion, msg);

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull("unrecognized message shouldn't be null", unrecognizedRtmMessage);
        UnrecognizedRtmData data = Constants.getGson().fromJson(unrecognizedRtmMessage.getJsonData(), UnrecognizedRtmData.class);
        assertEquals("unrecognized message data should be equal", "The Truth Is Out There", data.resource);
    }

    private class UnrecognizedRtmMessageListener extends Listener {
        public UnrecognizedRtmMessageListener(final CountDownLatch latch) {
            super();
            mCommands.put(UnrecognizedRtmMessage.class, data -> {
                unrecognizedRtmMessage = (UnrecognizedRtmMessage) data;
                latch.countDown();
            });
        }
    }

    private class UnrecognizedRtmData {
        String resource;
    }
}
