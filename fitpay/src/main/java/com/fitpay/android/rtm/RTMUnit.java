package com.fitpay.android.rtm;

import android.support.annotation.NonNull;

import com.fitpay.android.rtm.enums.SyncErrorCode;
import com.fitpay.android.rtm.models.WebViewSessionData;
import com.fitpay.android.utils.Unit;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class RTMUnit extends Unit {

    private final String authUrl;
    private RTMListener mListener;

    public RTMUnit(@NonNull String authUrl) {
        this.authUrl = authUrl;
    }

    public void setRTMListener(RTMListener listener) {
        mListener = listener;
    }

    /**
     * Establishes websocket connection, provides URL for webview member;
     * When webview loads URL and establishes websocket connection RTM session is ready to be used by RTM client for exchanging messages;
     * In order to be notified when particular event occurs, callback must be set (onConnect, onParticipantsReady, onUserLogin)
     *
     * @param secureElementId secure element Id (provided by payment device)
     */
    public void connectAndWaitForParticipants(String secureElementId) {

    }

    public interface RTMListener {
        /**
         * Completion handler
         *
         * @param url       Provides url object to be used in WebView, or null if error occurs
         * @param errorCode Provides error object, or null if no error occurs
         */
        void onConnect(String url, @SyncErrorCode int errorCode);

        void onError(@SyncErrorCode int errorCode);

        void onUserLogin(WebViewSessionData sessionData);

        void onSynchronizationRequest();

        void onSynchronizationComplete();
    }
}
