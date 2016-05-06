package com.fitpay.android.webview;

import android.webkit.JavascriptInterface;

import java.util.Map;


public interface WebViewCommunicator {

    /**
     *  this method is called by the WV to initiate the sync sequence in the SDK
     */
    @JavascriptInterface
    String sync();

    /**
     *  this method is called by the WV to provide 'session data' (deviceID, userID, OAuth token) to the SDK
     */
    @JavascriptInterface
    String sendUserData(String data);

    //
    /**
     *  this method is called by the WV onLoad() to retrieve JSON object with host device and wearable data
     */
    @JavascriptInterface
    String retrieveConfigJson();

}
