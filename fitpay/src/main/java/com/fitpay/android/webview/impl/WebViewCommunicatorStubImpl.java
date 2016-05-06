package com.fitpay.android.webview.impl;

import android.webkit.JavascriptInterface;

import com.fitpay.android.webview.WebViewCommunicator;
import  com.fitpay.android.webview.callback.OnTaskCompleted;

import com.google.gson.Gson;
import android.app.Activity;


/**
 * Created by Ross Gabay on 4/13/2016.
 * Stubbed out implementation of the WebViewCommunicator interface
 */
public class WebViewCommunicatorStubImpl implements WebViewCommunicator {

    private final Activity activity;
    private OnTaskCompleted callback;

    public WebViewCommunicatorStubImpl(Activity ctx, OnTaskCompleted callback) {
        this.activity = ctx;
        this.callback = callback;
    }

    private static String USER_DATA_STUB_RESPONSE = "OK";
    private static String SYNC_STUB_RESPONSE = "OK";

    final Gson gson = new Gson();

    @Override
    @JavascriptInterface
    public String sync() {
        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(USER_DATA_STUB_RESPONSE);

        callback.onTaskCompleted(SYNC_STUB_RESPONSE);
        return gson.toJson(stubResponse);
    }

    @Override
    @JavascriptInterface
    public String sendUserData(String data) {
        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(SYNC_STUB_RESPONSE);

        return gson.toJson(stubResponse);
    }

    //not used by the first iteration of the webview
    @Override
    @JavascriptInterface
    public String retrieveConfigJson() {
        throw new UnsupportedOperationException("method not supported in this iteration");
    }
}


