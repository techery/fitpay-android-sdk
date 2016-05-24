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
import com.fitpay.android.paymentdevice.DeviceService;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.webview.WebViewCommunicator;
import com.fitpay.android.webview.callback.OnTaskCompleted;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.http.HEAD;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;


/**
 * Created by Ross Gabay on 4/13/2016.
 * Stubbed out implementation of the WebViewCommunicator interface
 */
public class WebViewCommunicatorStubImpl implements WebViewCommunicator {

    private final String TAG = WebViewCommunicatorStubImpl.class.getSimpleName();

    private static String USER_DATA_STUB_RESPONSE = "0";
    private static String SYNC_STUB_RESPONSE = "0";
    private static final String RESPONSE_FAILURE = "1";


    private final Activity activity;
    private OnTaskCompleted callback;
    private int wId;
    private DeviceService deviceService;

    private User user;
    private Device device;

    private SyncCompleteListener syncListener;

    public WebViewCommunicatorStubImpl(Activity ctx, int wId, OnTaskCompleted callback) {
        this(ctx, wId);
        this.callback = callback;
    }

    public WebViewCommunicatorStubImpl(Activity ctx, int wId) {
        this.activity = ctx;
        this.wId = wId;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

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
    public String sync(String callbackId) {

        if (null == device) {
            SyncResponseModel response = new SyncResponseModel.Builder()
                    .status(RESPONSE_FAILURE)
                    .reason("No device specified for sync operation")
                    .build();

            Log.d(TAG, "sync can not be done.  No device has been specified.   response: " + response);

            if (null != callbackId) {
                sendMessageToJs(callbackId, "true", gson.toJson(response));
            }
            if (null != callback){
                callback.onTaskCompleted(RESPONSE_FAILURE);
            }

            return gson.toJson(response);

        }

        if (null == deviceService) {
            SyncResponseModel response = new SyncResponseModel.Builder()
                    .status(RESPONSE_FAILURE)
                    .reason("No DeviceService has not been configured for sync operation")
                    .build();

            Log.d(TAG, "sync can not be done.  No device service configured.   response: " + response);

            if (null != callbackId) {
                sendMessageToJs(callbackId, "true", gson.toJson(response));
            }
            if (null != callback){
                callback.onTaskCompleted(RESPONSE_FAILURE);
            }

            return gson.toJson(response);
        }

        if (null != syncListener) {
            NotificationManager.getInstance().removeListener(syncListener);
        }
        syncListener = new SyncCompleteListener(callbackId);
        NotificationManager.getInstance().addListener(syncListener);

        try {
            deviceService.syncData(device);
        } catch (IllegalArgumentException ex) {
            SyncResponseModel response = new SyncResponseModel.Builder()
                    .status(RESPONSE_FAILURE)
                    .reason(ex.getMessage())
                    .build();

            Log.d(TAG, "sync can not be done.  Reason: " + ex.getMessage() + ",  response: " + response);

            if (null != callbackId) {
                sendMessageToJs(callbackId, "true", gson.toJson(response));
            }
            if (null != callback){
                callback.onTaskCompleted(RESPONSE_FAILURE);
            }

        }

        AckResponseModel stubResponse = new AckResponseModel.Builder()
                .status(USER_DATA_STUB_RESPONSE)
                .build();

        Log.d(TAG, "sync providing synchronous ack response: " + stubResponse);
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

        ApiManager.getInstance().setAuthToken(oAuthToken);

        // Get user and device asynchronously

        Log.d(TAG, "sendUserData initiating asynchronous retrieval of user and device");
        getUserAndDevice(deviceId, callbackId);

        // provide synchronous ack

        AckResponseModel stubResponse = new AckResponseModel.Builder()
                .status(USER_DATA_STUB_RESPONSE)
                .build();

        Log.d(TAG, "sendUserData providing synchronous ack response: " + stubResponse);
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

        Log.d(TAG, "sending message to webview: " + responseMessage);

        String url = "javascript:window.RtmBridge.resolve('" + responseMessage + "');";
        Log.d(TAG, "message url: " + url);

        WebView w = (WebView)activity.findViewById(wId);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                w.loadUrl(url);
            }
        });
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
                        AckResponseModel stubResponse = new AckResponseModel.Builder()
                            .status(USER_DATA_STUB_RESPONSE)
                            .build();
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


    private Observable<Boolean> getSyncObservable(final DeviceService deviceService, final Device device, final String callbackId) {

        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                Log.d(TAG, "initiate sync");
                deviceService.syncData(device);
                return aBoolean;
            }
        });
    }

    private Observer<Boolean> getSyncObserver(final String callbackId) {

        return new Observer<Boolean>() {

            @Override
            public void onCompleted() {
                Log.d(TAG, "sync observer on completed");
                //TODO ?  either here of in onNext process response to callback
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "sync observer error: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean bool) {
                Log.d(TAG, "sync observer onNext: " + bool);
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
                AckResponseModel stubResponse = new AckResponseModel.Builder()
                    .status(USER_DATA_STUB_RESPONSE)
                        .build();
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


    private class SyncCompleteListener extends Listener {

        private String callbackId;

        private SyncCompleteListener(final String callbackId) {
            this.callbackId = callbackId;
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
        }

        //        @Override
        public void onSyncStateChanged(Sync syncEvent) {
            Log.d(TAG, "received on sync state changed event: " + syncEvent);
            switch (syncEvent.getState()) {
                case States.COMPLETED: {
                    AckResponseModel stubResponse = new AckResponseModel.Builder()
                            .status(USER_DATA_STUB_RESPONSE)
                            .build();
                    if (null != callbackId) {
                        sendMessageToJs(callbackId, "true", gson.toJson(stubResponse));
                    }
                    if (null != callback) {
                        callback.onTaskCompleted(USER_DATA_STUB_RESPONSE);
                    }
                    break;
                }
                case States.FAILED: {
                    SyncResponseModel response = new SyncResponseModel.Builder()
                            .status(RESPONSE_FAILURE)
                            .reason("sync failure")
                            .build();
                    if (null != callbackId) {
                        sendMessageToJs(callbackId, "true", gson.toJson(response));
                    }
                    if (null != callback) {
                        callback.onTaskCompleted(RESPONSE_FAILURE);
                    }
                    break;
                }
                default: {
                    Log.d(TAG, "skipping sync changed event: " + syncEvent);
                    break;
                }
            }
        }
    }



}


