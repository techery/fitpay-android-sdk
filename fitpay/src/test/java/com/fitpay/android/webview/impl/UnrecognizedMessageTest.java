package com.fitpay.android.webview.impl;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.webview.events.RtmMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Vlad on 04.12.2017.
 */

public class UnrecognizedMessageTest {

    private CountDownLatch latch = new CountDownLatch(1);
    private UnrecognizedRtmMessageListener listener;

    private RtmMessage message;

    @Before
    public void testActionsSetup() throws Exception {
        this.listener = new UnrecognizedRtmMessageListener();
        NotificationManager.getInstance().addListenerToCurrentThread(listener);
    }

    @After
    public void cleanup() {
        NotificationManager.getInstance().removeListener(listener);
        this.listener = null;
    }

    @Test
    public void testUnrecognizedRtmMessage() {
        String rtmMsgStr = "{\"callbackId\":\"10\",\"data\":\"{\\\"resource\\\":\\\"The Truth Is Out There\\\"}\",\"type\":\"somethingUnknown\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);

        RxBus.getInstance().post(msg);

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull("unrecognized message shouldn't be null", message);
        UnrecognizedRtmData data = Constants.getGson().fromJson(message.getJsonData(), UnrecognizedRtmData.class);
        assertEquals("unrecognized message data should be equal", "The Truth Is Out There", data.resource);
    }

    private class UnrecognizedRtmMessageListener extends Listener {
        public UnrecognizedRtmMessageListener() {
            super();
            mCommands.put(RtmMessage.class, data -> {
                message = (RtmMessage) data;
                if ("somethingUnknown".equals(message.getType())) {
                    latch.countDown();
                }
            });
        }
    }

    private class UnrecognizedRtmData {
        String resource;
    }
}
