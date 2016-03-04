package com.fitpay.android.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.fitpay.android.api.models.Device;
import com.fitpay.android.rtm.enums.ErrorCodes;
import com.fitpay.android.rtm.models.WebViewSessionData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PresenceChannel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.User;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class RTMSession extends Unit {

    private static final String PUBLIC_KEY = "publicKey";
    private static final String REQUESTER = "requester";
    private static final String ENCRYPTED_DATA = "encryptedData";

    private static final String CLIENT_DEVICE_SYNC = "client-device-sync";
    private static final String CLIENT_DEVICE_DATE_RETRIEVED = "client-device-sync-data-retrieved";
    private static final String CLIENT_DEVICE_SYNC_COMPLETE = "client-device-sync-complete";
    private static final String CLIENT_DEVICE_SYNC_FAILED = "client-device-sync-failed";

    private static final String CLIENT_DEVICE_SYNC_ACK = "client-device-sync-ack";
    private static final String CLIENT_DEVICE_RECONNECTED = "client-device-reconnected";


    private static final String CLIENT_DEVICE_KEY = "client-device-key";
    private static final String CLIENT_WV_KEY = "client-wv-key";
    private static final String CLIENT_DEVICE_KEY_REQUEST = "client-device-key-request";
    private static final String CLIENT_USER_DATA = "client-user-data";
    private static final String CLIENT_USER_DATA_ACK = "client-user-data-ack";
    private static final String CLIENT_USER_DATA_REQUEST = "client-user-data-request";
    private static final String CLIENT_AUTO_LOGOUT = "client-auto-logout";

//    private static final String CLIENT_DEVICE_PING = "client-device-ping";

    private static final String CLIENT_GET_ESE_DATA ="client-get-ese-data";
    private static final String CLIENT_ESE_DATA = "client-ese-data";
    private static final String CLIENT_SPSD_DATA = "client-spsd-data";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            CLIENT_DEVICE_SYNC,
            CLIENT_DEVICE_DATE_RETRIEVED,
            CLIENT_DEVICE_SYNC_COMPLETE,
            CLIENT_DEVICE_SYNC_FAILED,
            CLIENT_DEVICE_KEY,
            CLIENT_DEVICE_KEY_REQUEST,
            CLIENT_USER_DATA,
            CLIENT_DEVICE_SYNC_ACK,
            CLIENT_WV_KEY,
            CLIENT_USER_DATA_ACK,
//            CLIENT_DEVICE_PING,
            CLIENT_AUTO_LOGOUT
    })
    private @ interface ActionType{}

    private RTMListener mListener;

    private Pusher mPusher;
    private PresenceChannel mChannel;

    private JsonParser mParser;

    public RTMSession(@NonNull String authUrl) {

        mParser = new JsonParser();

        HttpAuthorizer authorizer = new HttpAuthorizer(authUrl);
        PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
        mPusher = new Pusher(Constants.PUSHER_KEY, options);
        mPusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                Log.i("PUSHER", connectionStateChange.toString());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Log.i("PUSHER", s);
            }
        });

        try {
            KeysManager.getInstance().createPairForType(KeysManager.KEY_RTM);
        } catch (Exception e) {
            Constants.printError(e.toString());
        }
    }

    public void connectDevice(@NonNull Device device){
        String elementId = device.getSecureElementId();

        if(TextUtils.isEmpty(elementId)){
            elementId = device.getExternalReferenceId();
        }

        //TODO:elementID shouldn't be null
        if(TextUtils.isEmpty(elementId)) {
            elementId = UUID.randomUUID().toString();
        }

        final String channelName = String.format("presence-%s", StringUtils.toSHA1(elementId));

        mChannel = mPusher.subscribePresence(channelName, presenceChannelEventListener);

        mChannel.bind(CLIENT_DEVICE_SYNC, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_DATE_RETRIEVED, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_SYNC_COMPLETE, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_SYNC_FAILED, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_KEY, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_KEY_REQUEST, presenceChannelEventListener);
        mChannel.bind(CLIENT_USER_DATA, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_SYNC_ACK, presenceChannelEventListener);
        mChannel.bind(CLIENT_WV_KEY, presenceChannelEventListener);
        mChannel.bind(CLIENT_USER_DATA_ACK, presenceChannelEventListener);
        mChannel.bind(CLIENT_AUTO_LOGOUT, presenceChannelEventListener);
//        mChannel.bind(CLIENT_DEVICE_PING, presenceChannelEventListener);
    }

    public void openWebView(@NonNull Device device, @NonNull WebView webView){
        String deviceData = String.format("{\"deviceType\":%s, \"manufacturerName\":%s, \"deviceName\":%s, \"secureElement\": {\"secureElementId\":%s}}",
                device.getDeviceType(),
                device.getManufacturerName(),
                device.getDeviceName(),
                device.getSecureElementId());

        String url = String.format("%s/login?deviceDat=%s",
                Constants.BASE_URL,
                StringUtils.base64UrlEncode(deviceData));

        webView.loadUrl(url);
    }

    public void setRTMListener(RTMListener listener) {
        mListener = listener;
    }

    /**
     * Establishes websocket connection, provides URL for webview member;
     * When webview loads URL and establishes websocket connection RTM session is ready to be used by RTM client for exchanging messages;
     * In order to be notified when particular event occurs, callback must be set (onConnect, onParticipantsReady, onUserLogin)
     *
     * @param secureElementId secure element Id (provided by payment device)
     */
    public void connectAndWaitForParticipants(String secureElementId) {

    }

    private PresenceChannelEventListener presenceChannelEventListener  = new PresenceChannelEventListener(){

        @Override
        public void onEvent(String presenceName, String action, String data) {
            Log.i("PUSHER", presenceName + action + data);

            JsonObject jsonObject = null;
            if(!TextUtils.isEmpty(data)) {
                jsonObject = mParser.parse(data).getAsJsonObject();
            }

            //@ActionType String type = action;
            switch (action){
//                case CLIENT_DEVICE_PING:
//                    break;
                case  CLIENT_DEVICE_SYNC:
                    mChannel.trigger("client-device-sync-ack", "{}");
                    mChannel.trigger("client-device-sync-complete", "{}");
                    break;
                case CLIENT_DEVICE_DATE_RETRIEVED:
                    break;
                case CLIENT_DEVICE_SYNC_COMPLETE:
                    break;
                case CLIENT_DEVICE_SYNC_FAILED:
                    break;
                case CLIENT_DEVICE_KEY:
                    if(jsonObject != null){
                        if(jsonObject.has(PUBLIC_KEY)) {
                            ECCKeyPair keyPair = KeysManager.getInstance().getPairForType(KeysManager.KEY_RTM);
                            keyPair.setServerPublicKey(jsonObject.get(PUBLIC_KEY).getAsString());
                        }
                        if(jsonObject.has(REQUESTER)) {
                            mChannel.trigger("client-device-key", "{\"publicKey\":" + KeysManager.getInstance().getPairForType(KeysManager.KEY_RTM).getPublicKey() + ",\"requester\":" + jsonObject.get(REQUESTER).getAsString() + "}");
                        }
                    }
                    break;
                case CLIENT_DEVICE_KEY_REQUEST:
                    mChannel.trigger("client-wv-key-request", "{\"requester\":\"device\"}");

                    if(jsonObject != null && jsonObject.has(REQUESTER)) {
                            mChannel.trigger("client-device-key", "{\"publicKey\":" + KeysManager.getInstance().getPairForType(KeysManager.KEY_RTM).getPublicKey() + ",\"requester\":" + jsonObject.get(REQUESTER).getAsString() + "}");
                    }

//                    mChannel.trigger("client-device-key", "{\"publicKey\":" + KeysManager.getInstance().getKeyId(KeysManager.KEY_RTM) + ",\"requester\":\"wv\"}");
                    break;
                case CLIENT_USER_DATA:
                    if(jsonObject != null && jsonObject.has(ENCRYPTED_DATA)){
                        final String encryptedData = jsonObject.get(ENCRYPTED_DATA).getAsString();
                        final String decryptedData = StringUtils.getDecryptedString(KeysManager.KEY_RTM, encryptedData);
                        JsonObject decJson = mParser.parse(decryptedData).getAsJsonObject();
                    }
                    mChannel.trigger("client-user-data-ack", "{}");
                    break;
                case CLIENT_DEVICE_SYNC_ACK:
                    break;
                case CLIENT_WV_KEY:
                    if(jsonObject != null && jsonObject.has(PUBLIC_KEY)) {
                        ECCKeyPair keyPair = KeysManager.getInstance().getPairForType(KeysManager.KEY_RTM);
                        keyPair.setServerPublicKey(jsonObject.get(PUBLIC_KEY).getAsString());
                    }
                    break;
                case CLIENT_USER_DATA_ACK:
                    break;
                case CLIENT_AUTO_LOGOUT:
                    break;
            }
        }

        @Override
        public void onSubscriptionSucceeded(String s) {
            Log.i("PUSHER", s);
        }

        @Override
        public void onAuthenticationFailure(String s, Exception e) {
            Log.i("PUSHER", s);
        }

        @Override
        public void onUsersInformationReceived(String s, Set<User> set) {
            Log.i("PUSHER", s);
        }

        @Override
        public void userSubscribed(String s, User user) {
            Log.i("PUSHER", s);
        }

        @Override
        public void userUnsubscribed(String s, User user) {
            Log.i("PUSHER", s);
        }
    };


    public interface RTMListener {
        /**
         * Completion handler
         *
         * @param url       Provides url object to be used in WebView, or null if error occurs
         * @param errorCode Provides error object, or null if no error occurs
         */
        void onConnect(String url, int errorCode);

        void onError(int errorCode);

        void onUserLogin(WebViewSessionData sessionData);

        void onSynchronizationRequest();

        void onSynchronizationComplete();
    }
}
