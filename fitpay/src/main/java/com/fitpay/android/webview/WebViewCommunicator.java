package com.fitpay.android.webview;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.fitpay.android.paymentdevice.DeviceService;

import org.json.JSONException;


public interface WebViewCommunicator {

    /**
     * this method is called by the WV to initiate sync() or sendUserData() indirectly
     */
    void dispatchMessage(String message) throws JSONException;

    /**
     * this method is called by the WV to initiate the sync sequence in the SDK
     */
    @JavascriptInterface
    void sync(String callBackId);

    /**
     * this method is called by the WV to provide 'session data' (deviceID, userID, OAuth token) to the SDK
     */
    @JavascriptInterface
    void sendUserData(String callbackId, String deviceId, String token, String userId);


    /**
     * this method is called by the WV onLoad() to retrieve JSON object with host device and wearable data
     */
    @JavascriptInterface
    String retrieveConfigJson();

    /**
     * Provide a configured DeviceService to the communicator to support operations that require interaction with the payment device
     * One example is sync.
     *
     * @param deviceService
     */
    void setDeviceService(DeviceService deviceService);

    /**
     * send logout message to JS
     */
    void logout();

    /**
     * this method should be called manually in {@link Activity#onDestroy()}
     */
    void close();
}
