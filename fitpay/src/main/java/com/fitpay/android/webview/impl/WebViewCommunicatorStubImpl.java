package com.fitpay.android.webview.impl;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.fitpay.android.webview.WebViewCommunicator;
import  com.fitpay.android.webview.callback.OnTaskCompleted;

import com.google.gson.Gson;
import android.app.Activity;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Ross Gabay on 4/13/2016.
 * Stubbed out implementation of the WebViewCommunicator interface
 */
public class WebViewCommunicatorStubImpl implements WebViewCommunicator {

    private final String TAG = WebViewCommunicatorStubImpl.class.getSimpleName();

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

    @JavascriptInterface
    public void dispatchMessage(String message) throws JSONException{

        if(message == null) throw new IllegalArgumentException("invalid message");

        Log.d(TAG, "received message: "+ message);
        JSONObject obj = new JSONObject(message);

        String callBackId = obj.getString("callBackId");
        if(callBackId == null) throw new IllegalArgumentException("action is missing in the message from the UI");
        Log.d(TAG, "received callbackId: "+ callBackId);

        String action = obj.getJSONObject("data").getString("action");
        if(action == null) throw new IllegalArgumentException("action is missing in the message from the UI");

        switch(action){

            case "userData" :
                //params are only there for the userData action
                String deviceId = obj.getJSONObject("data").getJSONObject("data").getString("deviceId");
                String token = obj.getJSONObject("data").getJSONObject("data").getString("token");
                String userId = obj.getJSONObject("data").getJSONObject("data").getString("userId");

                if((deviceId==null) || (token ==null) || (userId == null)) throw new IllegalArgumentException("missing required message data");
                sendUserData(callBackId, deviceId, token, userId);
                break;

            case "sync":
                sync(callBackId);
                break;

            default:
                throw new IllegalArgumentException("unsupported action value in message");

        }
    }

    @Override
    @JavascriptInterface
    public String sync(String callBackId) {
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
                        sendMessageToJs(callBackId, "true",  gson.toJson(stubResponse));
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
    public String sendUserData(String callBackId, String deviceId, String token, String userId) {
        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(USER_DATA_STUB_RESPONSE);

        Log.d(TAG, "received data: deviceId: " + deviceId +", token: " + token + ", userId: " + userId);

        Runnable t = new Runnable(){
            @Override
            public void run(){
                try{
                    //this is where the "underlying" SDK's sendUserData() is called
                    Thread.sleep(5000);

                    stubResponse.setStatus(USER_DATA_STUB_RESPONSE);
                    sendMessageToJs(callBackId, "true",  gson.toJson(stubResponse));
                    if(callback!=null) callback.onTaskCompleted(USER_DATA_STUB_RESPONSE);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        return gson.toJson(stubResponse);
    }

    //not used by the first iteration of the webview
    @Override
    @JavascriptInterface
    public String retrieveConfigJson() {
        throw new UnsupportedOperationException("method not supported in this iteration");
    }

    public void sendMessageToJs(String callBackId, String success, String response) {

        String responseMessage = "{ \"callBackId\" :" + callBackId + "," +
                                   "\"success\" :"  + success + "," +
                                   "\"response\" :" + response +" }";

        Log.d(TAG, responseMessage);

        String url = "javascript:window.RtmBridge.resolve('" + responseMessage + "');";
        Log.d(TAG, url);

        WebView w = (WebView)activity.findViewById(wId);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                w.loadUrl(url);
            }
        });
    }
}


