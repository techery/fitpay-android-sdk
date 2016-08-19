package com.fitpay.android.utils;

import com.fitpay.android.paymentdevice.events.MessageReceived;

import java.util.Map;

/**
 * Created by Vlad on 19.08.2016.
 */
public class MessagesManager {
    private String token;
    private static MessagesManager sInstance;

    public static MessagesManager getInstance() {
        if (sInstance == null) {
            sInstance = new MessagesManager();
        }

        return sInstance;
    }

    private MessagesManager() {
        NotificationManager.getInstance().addListener(new MessagesListener());
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    private class MessagesListener extends Listener {
        public MessagesListener() {
            super();
            mCommands.put(MessageReceived.class, event -> {
                Map<String, String> data = ((MessageReceived) event).getData();
            });
        }
    }
}
