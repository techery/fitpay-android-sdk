package com.fitpay.android.rtm.callbacks;

import com.fitpay.android.rtm.models.WebViewSessionData;

/**
 * RTM callback listener
 */
public interface RTMListener {
    void onConnect();

    void onError(String message);

    void onUserLogin(WebViewSessionData sessionData);

    void onSynchronizationRequest();

    void onSynchronizationComplete();
}
