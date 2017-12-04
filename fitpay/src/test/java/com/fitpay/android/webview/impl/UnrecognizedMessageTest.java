package com.fitpay.android.webview.impl;

import android.util.Log;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.webview.events.RtmMessage;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Vlad on 04.12.2017.
 */

public class UnrecognizedMessageTest {

    private RtmMessage message;

    @Test
    public void testUnrecognizedRtmMessage() {
        final CountDownLatch latch = new CountDownLatch(1);
        UnrecognizedRtmMessageListener listener = new UnrecognizedRtmMessageListener(latch);
        NotificationManager.getInstance().addListenerToCurrentThread(listener);

        String rtmMsgStr = "{\"callbackId\":\"10\",\"data\":\"{\\\"resource\\\":\\\"The Truth Is Out There\\\"}\",\"type\":\"somethingUnknown\"}";
        RtmMessage msg = Constants.getGson().fromJson(rtmMsgStr, RtmMessage.class);

        RxBus.getInstance().post(msg);

        try {
            latch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NotificationManager.getInstance().removeListener(listener);

        assertNotNull("unrecognized message shouldn't be null", message);
        UnrecognizedRtmData data = Constants.getGson().fromJson(message.getJsonData(), UnrecognizedRtmData.class);
        assertEquals("unrecognized message data should be equal", "The Truth Is Out There", data.resource);
    }

    private class UnrecognizedRtmMessageListener extends Listener {
        public UnrecognizedRtmMessageListener(final CountDownLatch latch) {
            super();
            mCommands.put(RtmMessage.class, data -> {
                Log.d("UnrecognizedMessageTest", "data received");
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
