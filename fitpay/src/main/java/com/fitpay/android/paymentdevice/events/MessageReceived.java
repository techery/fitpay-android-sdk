package com.fitpay.android.paymentdevice.events;

import java.util.Map;

/**
 * Created by Vlad on 19.08.2016.
 */
public class MessageReceived {
    private Map<String, String> data;

    public MessageReceived(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }
}
