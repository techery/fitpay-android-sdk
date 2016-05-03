package com.fitpay.android.webview.impl;

import com.fitpay.android.webview.WebViewCommunicator;

import com.google.gson.Gson;

/**
 * Stubbed out implementation of the WebViewCommunicator interface
 */
public class WebViewCommunicatorStubImpl implements WebViewCommunicator{

    private static String USER_DATA_STUB_RESPONSE = "OK";
    private static String SYNC_STUB_RESPONSE = "OK";

    final Gson gson = new Gson();

    @Override
    public String sync() {
        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(USER_DATA_STUB_RESPONSE);

        return gson.toJson(stubResponse);
    }

    @Override
    public String sendUserData(String data) {
        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(SYNC_STUB_RESPONSE);

        return gson.toJson(stubResponse);
    }

    //not used by the first iteration of the webview
    @Override
    public String retrieveConfigJson() {
        throw new UnsupportedOperationException("method not supported in this iteration");
    }
}
