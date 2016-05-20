package com.fitpay.android.webview.impl;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.fitpay.android.api.ApiManager;
import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.security.OAuthToken;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.webview.WebViewCommunicator;
import com.fitpay.android.webview.callback.OnTaskCompleted;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Ross Gabay on 4/13/2016.
 * Stubbed out implementation of the WebViewCommunicator interface
 */
public class WebViewCommunicatorStubImpl implements WebViewCommunicator {

    private final String TAG = WebViewCommunicatorStubImpl.class.getSimpleName();

    private final Activity activity;
    private OnTaskCompleted callback;
    private int wId;

    private User user;
    private Device device;

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

    //TODO update config mechanism - this SUCKS  - the SDK gets configured it does not provide config
    protected Map<String, String> getConfig(){
        Map<String, String> config = new HashMap<>();
        config.put(ApiManager.PROPERTY_API_BASE_URL, "https://api.qa.fitpay.ninja");
        config.put(ApiManager.PROPERTY_AUTH_BASE_URL, "https://auth.qa.fitpay.ninja");
        config.put(ApiManager.PROPERTY_CLIENT_ID, "pagare");
        config.put(ApiManager.PROPERTY_REDIRECT_URI, "https://auth.qa.fitpay.ninja");
        config.put("paymentDeviceType", "MockDevice");
        return config;
    }

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
        stubResponse.setStatus(SYNC_STUB_RESPONSE);

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
    public String sendUserData(String callbackId, String deviceId, String token, String userId) {

        Log.d(TAG, "sendUserData received data: deviceId: " + deviceId +", token: " + token + ", userId: " + userId);

        OAuthToken oAuthToken = new OAuthToken.Builder()
                .accessToken(token)
                .userId(userId)
                .build();

        ApiManager.init(getConfig());
        ApiManager.getInstance().setAuthToken(oAuthToken);

        // Get user and device asynchronously

        Log.d(TAG, "doing asynchornous retrieval of user and device");
        getUserAndDevice(deviceId, callbackId);

       /* Subscription userSubscription = getUserObservable(deviceId, callbackId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(getUserObserver(deviceId, callbackId));*/

        // provide synchronous ack

        AckResponseModel stubResponse = new AckResponseModel();
        stubResponse.setStatus(USER_DATA_STUB_RESPONSE);

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


    private Observable<Boolean> getUserObservable(final String deviceId, final String callbackId) {

        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                Log.d(TAG, "get user");
                ApiManager.getInstance().getUser(new ApiCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        WebViewCommunicatorStubImpl.this.user = result;
                    }

                    @Override
                    public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                        Log.d(TAG, "Hello Tim"); //TODO add handling here
                    }
                });
                return aBoolean;
            }
        });
    }

    private Observer<Boolean> getUserObserver(final String deviceId, final String callbackId) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Subscription deviceSubscription = getDeviceObservable(deviceId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe(getDeviceObserver(callbackId));
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "connection observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "connection observer onNext: " + bool);
            }
        };
    }

    private Observable<Boolean> getDeviceObservable(final String deviceId) {

        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                Log.d(TAG, "get device");
                user.getDevice(deviceId, new ApiCallback<Device>() {
                    @Override
                    public void onSuccess(Device result) {
                        WebViewCommunicatorStubImpl.this.device = result;
                    }

                    @Override
                    public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                        //TODO handle failure
                    }
                });
                return aBoolean;
            }
        });
    }

    private Observer<Boolean> getDeviceObserver(final String callBackId) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "get device completed");
                AckResponseModel stubResponse = new AckResponseModel();
                stubResponse.setStatus(USER_DATA_STUB_RESPONSE);
                if (null != callBackId) {
                    sendMessageToJs(callBackId, "true", gson.toJson(stubResponse));
                }
                if (null != callback){
                    callback.onTaskCompleted(USER_DATA_STUB_RESPONSE);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "connection observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "connection observer onNext: " + bool);
            }
        };
    }


    private void getUserAndDevice(String deviceId, String callbackId) {
        ApiManager.getInstance().getUser(new ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                WebViewCommunicatorStubImpl.this.user = result;
                result.getDevice(deviceId, new ApiCallback<Device>() {
                    @Override
                    public void onSuccess(Device result) {
                        WebViewCommunicatorStubImpl.this.device = result;
                        AckResponseModel stubResponse = new AckResponseModel();
                        stubResponse.setStatus(USER_DATA_STUB_RESPONSE);
                        if (null != callbackId) {
                            sendMessageToJs(callbackId, "true", gson.toJson(stubResponse));
                        }
                        if (null != callback){
                            callback.onTaskCompleted(USER_DATA_STUB_RESPONSE);
                        }
                    }

                    @Override
                    public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                        Log.d(TAG, "getDevice failed");
                        //TODO handle failure and report back to WVC
                    }
                });

            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Log.d(TAG, "getDevice failed");
                //TODO handle failure and report back to WVC
            }
        });

    }

}


