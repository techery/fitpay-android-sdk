package com.fitpay.android.webview.impl;

import android.webkit.JavascriptInterface;

import com.fitpay.android.R;
import com.fitpay.android.webview.WebViewCommunicator;
import  com.fitpay.android.webview.callback.OnTaskCompleted;

import com.google.gson.Gson;
import android.app.Activity;
import android.webkit.WebView;


/**
 * Created by Ross Gabay on 4/13/2016.
 * Stubbed out implementation of the WebViewCommunicator interface
 */
public class WebViewCommunicatorStubImpl implements WebViewCommunicator {

    private final Activity activity;
    private OnTaskCompleted callback;
    private  int wId;

    public WebViewCommunicatorStubImpl(Activity ctx, int wId, OnTaskCompleted callback) {
        this.activity = ctx;
        this.wId = wId;
        this.callback = callback;
    }

    public WebViewCommunicatorStubImpl(Activity ctx, int wId) {
        this.activity = ctx;
        this.wId = wId;
    }

    private static String USER_DATA_STUB_RESPONSE = "OK";
    private static String SYNC_STUB_RESPONSE = "0";

    final Gson gson = new Gson();

    @Override
    @JavascriptInterface
    public String sync() {
        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(USER_DATA_STUB_RESPONSE);

        SyncResponseModel syncResponse = new SyncResponseModel();

           Runnable t = new Runnable(){
                @Override
                public void run(){
                    try{
                        //this is where the "underlying" SDK's sync is called
                        Thread.sleep(5000);

                        syncResponse.setStatus(SYNC_STUB_RESPONSE);
                        sendMessageToJs(gson.toJson(syncResponse));
                        if(callback!=null) callback.onTaskCompleted(SYNC_STUB_RESPONSE);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

        Thread thread = new Thread(t);
        thread.start();


        return gson.toJson(stubResponse);
    }

    @Override
    @JavascriptInterface
    public String sendUserData(String data) {
        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(SYNC_STUB_RESPONSE);

        System.out.println(data);
        return gson.toJson(stubResponse);
    }

    //not used by the first iteration of the webview
    @Override
    @JavascriptInterface
    public String retrieveConfigJson() {
        throw new UnsupportedOperationException("method not supported in this iteration");
    }

    public void sendMessageToJs(String msg) {
        final String str = msg;
        WebView w = (WebView)activity.findViewById(wId);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                w.loadUrl("javascript:droid_callback('" + msg+ "');");
            }
        });
    }
}


