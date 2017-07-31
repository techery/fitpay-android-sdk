package com.fitpay.android.webview.impl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.fitpay.android.R;
import com.fitpay.android.api.ApiManager;
import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.security.OAuthToken;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.cardscanner.IFitPayCardScanner;
import com.fitpay.android.cardscanner.ScannedCardInfo;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Sync;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceConnector;
import com.fitpay.android.paymentdevice.models.SyncRequest;
import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.EventCallback;
import com.fitpay.android.utils.FPLog;
import com.fitpay.android.utils.Listener;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.android.utils.RxBus;
import com.fitpay.android.utils.StringUtils;
import com.fitpay.android.webview.WebViewCommunicator;
import com.fitpay.android.webview.enums.RtmType;
import com.fitpay.android.webview.events.DeviceStatusMessage;
import com.fitpay.android.webview.events.RtmMessage;
import com.fitpay.android.webview.events.RtmMessageResponse;
import com.fitpay.android.webview.events.UserReceived;
import com.fitpay.android.webview.models.RtmVersion;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.fitpay.android.utils.Constants.WV_DATA;


/**
 * Created by Ross Gabay on 4/13/2016.
 * Implementation of the WebViewCommunicator interface
 */
public class WebViewCommunicatorImpl implements WebViewCommunicator {

    private final String TAG = WebViewCommunicatorImpl.class.getSimpleName();

    private static final int RESPONSE_OK = 0;
    private static final int RESPONSE_FAILURE = 1;
    private static final int RESPONSE_IN_PROGRESS = 2;

    private final Activity activity;
    private final IPaymentDeviceConnector deviceConnector;

    private User user;
    private Device device;
    private String deviceId = null;

    private DeviceStatusListener deviceStatusListener;
    private DeviceSyncListener listenerForAppCallbacks;

    private RtmMessageListener rtmMessageListener;

    private WebView webView;

    private RtmVersion webAppRtmVersion = new RtmVersion(RtmType.RTM_VERSION);

    private final Gson gson = new Gson();

    private IFitPayCardScanner cardScanner;

    public WebViewCommunicatorImpl(Activity ctx, IPaymentDeviceConnector deviceConnector, int wId) {
        this.activity = ctx;
        this.deviceConnector = deviceConnector;

        deviceStatusListener = new DeviceStatusListener(deviceConnector.id());
        rtmMessageListener = new RtmMessageListener();

        NotificationManager.getInstance().addListener(deviceStatusListener);
        NotificationManager.getInstance().addListener(rtmMessageListener);

        webView = (WebView) activity.findViewById(wId);
    }

//    @Override
//    public void setPaymentConnector(IPaymentDeviceConnector deviceConnector) {
//        this.deviceConnector = deviceConnector;
//    }

    /**
     * set custom card scanner instead of Jumio
     *
     * @param cardScanner custom card scanner
     */
    public void setCardScanner(IFitPayCardScanner cardScanner) {
        this.cardScanner = cardScanner;
    }

    /**
     * this method should be called manually in {@link Activity#onDestroy()}
     */
    public void close() {
        NotificationManager.getInstance().removeListener(deviceStatusListener);
        NotificationManager.getInstance().removeListener(rtmMessageListener);
        NotificationManager.getInstance().removeListener(listenerForAppCallbacks);
    }

    /**
     * send logout message to JS
     */
    public void logout() {
        RxBus.getInstance().post(new RtmMessageResponse("logout"));
        RxBus.getInstance().post(new DeviceStatusMessage(activity.getString(R.string.connecting), deviceId, DeviceStatusMessage.PENDING));
    }

    /**
     * call this function in {@link Activity#onBackPressed()}
     */
    public void onBack() {
        RxBus.getInstance().post(new RtmMessageResponse("back"));
    }

    /**
     * response for a {@link #onBack()} function.
     */
    public void onNoHistory() {
        activity.finish();
    }

    /**
     * call this function asap to retrieve webapp version of RTM
     */
    public void sendRtmVersion() {
        RxBus.getInstance().post(new RtmMessageResponse(new RtmVersion(RtmType.RTM_VERSION), RtmType.VERSION));
    }

    public void sendCardData(@NonNull ScannedCardInfo cardInfo) {
        RxBus.getInstance().post(new RtmMessageResponse(cardInfo, RtmType.CARD_SCANNED));
    }

    @Override
    @JavascriptInterface
    public void dispatchMessage(String message) throws JSONException {
        if (message == null) {
            FPLog.w(WV_DATA, "\\Received\\: invalid message");
            throw new IllegalArgumentException("invalid message");
        }

        JSONObject obj = new JSONObject(message);

        String callBackId = obj.getString("callBackId");
        if (callBackId == null) {
            FPLog.w(WV_DATA, "\\Received\\: callBackId is missing in the message");
            throw new IllegalArgumentException("callBackId is missing in the message");
        }

        String type = obj.getString("type");
        if (type == null) {
            FPLog.w(WV_DATA, "\\Received\\: type is missing in the message");
            throw new IllegalArgumentException("type is missing in the message");
        }

        FPLog.i(WV_DATA, String.format(Locale.getDefault(), "\\Received\\: callbackId:%s type:%s", callBackId, type));

        String dataStr = obj.has("data") ? obj.getString("data") : null;

        RxBus.getInstance().post(new RtmMessage(callBackId, dataStr, type));
    }

    @Override
    @JavascriptInterface
    public void sync(String callbackId) {
        FPLog.d(TAG, "sync received");

//        RxBus.getInstance().post(new DeviceStatusMessage(activity.getString(R.string.sync_started), DeviceStatusMessage.PROGRESS));

        if (null == user) {
            onTaskError(EventCallback.SYNC_COMPLETED, callbackId, "No user specified for sync operation");
            return;
        }

        if (null == device) {
            onTaskError(EventCallback.SYNC_COMPLETED, callbackId, "No device specified for sync operation");
            return;
        }

        if (null == deviceConnector) {
            onTaskError(EventCallback.SYNC_COMPLETED, callbackId, "No PaymentConnector has not been configured for sync operation");
            return;
        }

        NotificationManager.getInstance().removeListener(listenerForAppCallbacks);

        listenerForAppCallbacks = new DeviceSyncListener(deviceConnector.id(), callbackId);
        NotificationManager.getInstance().addListener(listenerForAppCallbacks);

        RxBus.getInstance().post(deviceConnector.id(), new SyncRequest.Builder()
                .setUser(user)
                .setDevice(device)
                .setConnector(deviceConnector)
                .build());
    }

    @Override
    @JavascriptInterface
    public void sendUserData(String callbackId, String deviceId, String token, String userId) {
        this.deviceId = deviceId;

        FPLog.d(TAG, "sendUserData received data: deviceId: " + deviceId + ", token: " + token + ", userId: " + userId);

        OAuthToken oAuthToken = new OAuthToken.Builder()
                .accessToken(token)
                .userId(userId)
                .build();

        ApiManager.getInstance().setAuthToken(oAuthToken);

        getUserAndDevice(deviceId, callbackId);
    }

    //not used by the first iteration of the webview
    @Override
    @JavascriptInterface
    public String retrieveConfigJson() {
        throw new UnsupportedOperationException("method not supported in this iteration");
    }

    private void sendMessageToJs(String callBackId, boolean success, Object response) {
        RxBus.getInstance().post(new RtmMessageResponse(callBackId, success, response, RtmType.RESOLVE));
    }

    private void sendDeviceStatusToJs(DeviceStatusMessage event) {
        RxBus.getInstance().post(new RtmMessageResponse(event, RtmType.DEVICE_STATUS));
    }

    private void getUserAndDevice(final String deviceId, final String callbackId) {
        ApiManager.getInstance().getUser(new ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                if (result == null) {
                    onTaskError(EventCallback.USER_CREATED, callbackId, "getUser failed: result is null");
                    return;
                }

                WebViewCommunicatorImpl.this.user = result;

                RxBus.getInstance().post(new UserReceived(user.getId(), user.getUsername()));

                EventCallback eventCallback = new EventCallback.Builder()
                        .setCommand(EventCallback.USER_CREATED)
                        .setStatus(EventCallback.STATUS_OK)
                        .build();
                eventCallback.send();

                result.getDevice(deviceId, new ApiCallback<Device>() {
                    @Override
                    public void onSuccess(Device result) {
                        WebViewCommunicatorImpl.this.device = result;

                        String token = ApiManager.getPushToken();
                        String deviceToken = device.getNotificationToken();

                        final Runnable onSuccess = () -> onTaskSuccess(EventCallback.GET_USER_AND_DEVICE, callbackId);

                        if (deviceToken == null || !deviceToken.equals(token)) {
                            Device updatedDevice = new Device.Builder().setNotificationToken(token).build();
                            device.updateToken(updatedDevice, deviceToken == null, new ApiCallback<Device>() {
                                @Override
                                public void onSuccess(Device result) {
                                    WebViewCommunicatorImpl.this.device = result;
                                    onSuccess.run();
                                }

                                @Override
                                public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                                    onTaskError(EventCallback.GET_USER_AND_DEVICE, callbackId, "update device failed:" + errorMessage);
                                }
                            });
                        } else {
                            onSuccess.run();
                        }
                    }

                    @Override
                    public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                        onTaskError(EventCallback.GET_USER_AND_DEVICE, callbackId, "getDevice failed " + errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                onTaskError(EventCallback.USER_CREATED, callbackId, "getUser failed " + errorMessage);
            }
        });
    }

    private void onTaskSuccess(@EventCallback.Command String command, String callbackId) {
        onTaskSuccess(command, callbackId, RESPONSE_OK);
    }

    private void onTaskSuccess(@EventCallback.Command String command, String callbackId, int response) {
        AppResponseModel stubResponse = new AppResponseModel.Builder()
                .status(response)
                .build();

        if (null != callbackId) {
            sendMessageToJs(callbackId, true, stubResponse);
        }

        EventCallback eventCallback = new EventCallback.Builder()
                .setCommand(command)
                .setStatus(EventCallback.STATUS_OK)
                .build();
        eventCallback.send();
    }

    private void onTaskError(@EventCallback.Command String command, String callbackId, String errorMessage) {
        AppResponseModel failedResponse = new AppResponseModel.Builder()
                .status(RESPONSE_FAILURE)
                .reason(errorMessage)
                .build();

        FPLog.w(TAG, errorMessage);

        if (null != callbackId) {
            sendMessageToJs(callbackId, false, gson.toJson(failedResponse));
        }

        RxBus.getInstance().post(new DeviceStatusMessage(activity.getString(R.string.sync_failed, errorMessage), deviceId, DeviceStatusMessage.ERROR));

        EventCallback eventCallback = new EventCallback.Builder()
                .setCommand(command)
                .setStatus(EventCallback.STATUS_FAILED)
                .setReason(errorMessage)
                .build();
        eventCallback.send();
    }


    private class DeviceStatusListener extends Listener {
        private DeviceStatusListener(String connectorId) {
            super(connectorId);
            mCommands.put(DeviceStatusMessage.class, data -> {
                if (deviceId == null || deviceId.equals(((DeviceStatusMessage) data).getDeviceId())) {
                    sendDeviceStatusToJs((DeviceStatusMessage) data);
                }
            });
        }
    }

    /**
     * Listen to sync status
     */
    private class DeviceSyncListener extends Listener {

        private String callbackId;

        private DeviceSyncListener(String connectorId, String callbackId) {
            super(connectorId);
            this.callbackId = callbackId;
            mCommands.put(Sync.class, data -> onSyncStateChanged((Sync) data));
        }

        private void onSyncStateChanged(Sync syncEvent) {
            FPLog.d(TAG, "received on sync state changed event: " + syncEvent);
            switch (syncEvent.getState()) {
                case States.COMPLETED:
                case States.COMPLETED_NO_UPDATES: {
                    onTaskSuccess(EventCallback.SYNC_COMPLETED, callbackId);
                    RxBus.getInstance().post(new DeviceStatusMessage(activity.getString(R.string.sync_finished), deviceId, DeviceStatusMessage.SUCCESS));
                    NotificationManager.getInstance().removeListener(listenerForAppCallbacks);
                    break;
                }
                case States.FAILED: {
                    onTaskError(EventCallback.SYNC_COMPLETED, callbackId, !StringUtils.isEmpty(syncEvent.getMessage()) ? syncEvent.getMessage() : "sync failure");
                    NotificationManager.getInstance().removeListener(listenerForAppCallbacks);
                    break;
                }
                default: {
                    FPLog.d(TAG, "skipping sync changed event: " + syncEvent);
                    break;
                }
            }
        }
    }

    /**
     * Listen to RTM messages
     */
    private class RtmMessageListener extends Listener {
        private RtmMessageListener() {
            mCommands.put(RtmMessage.class, data -> {
                RtmMessage msg = (RtmMessage) data;
                switch (webAppRtmVersion.getVersion()) {
                    default:
                        useDefaultParser(msg);
                        break;
                }
            });
            mCommands.put(RtmMessageResponse.class, data -> {
                String str = data.toString();
                FPLog.i(WV_DATA, "\\Response\\: " + str);
                final String url = "javascript:window.RtmBridge.resolve('" + str + "');";
                activity.runOnUiThread(() -> webView.loadUrl(url));
            });
        }

        private void useDefaultParser(RtmMessage msg) {
            String callbackId = msg.getCallbackId();

            switch (msg.getType()) {
                case RtmType.USER_DATA:
                    //params are only there for the userData action
                    String deviceId = null;
                    String token = null;
                    String userId = null;

                    try {
                        JSONObject obj = new JSONObject(msg.getJsonData());
                        deviceId = obj.getString("deviceId");
                        token = obj.getString("token");
                        userId = obj.getString("userId");
                    } catch (Exception e) {
                        FPLog.e(WV_DATA, e);
                        throw new IllegalArgumentException("missing required message data");
                    }

                    sendUserData(callbackId, deviceId, token, userId);
                    break;

                case RtmType.SYNC:
                    sync(callbackId);
                    break;

                case RtmType.VERSION:
                    try {
                        webAppRtmVersion = Constants.getGson().fromJson(msg.getJsonData(), RtmVersion.class);
                    } catch (Exception e) {
                        FPLog.e(WV_DATA, e);
                        throw new IllegalArgumentException("missing required message data");
                    }
                    break;

                case RtmType.NO_HISTORY:
                    onNoHistory();
                    break;

                case RtmType.CARD_SCANNED:
                    if (cardScanner != null) {
                        cardScanner.startScan();
                    }
                    break;

                default:
                    Log.i(TAG, "unsupported action value " + msg.getType() + " in message with callbackId:" + callbackId);
            }
        }
    }
}


