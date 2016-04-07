package com.fitpay.android.webview;

import android.webkit.JavascriptInterface;

import java.util.Map;

/**
 * Created by Ross Gabay on 3/31/2016.
 */
public interface WebViewCommunicator {
    @JavascriptInterface
    Map<String, Object> loginEventHandler(oauthData oauthData); //oauthData contains OAuth2 credentials of the End User

    @JavascriptInterface
    Map<String, Object> syncEventHandler();

    @JavascriptInterface
    Map<String, Object> pingEventHandler();

    @JavascriptInterface
    Map<String, Object> updateSeEventHandler();

    public class oauthData {
        protected String bearerToken;
    }
}
