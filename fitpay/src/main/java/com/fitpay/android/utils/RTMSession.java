package com.fitpay.android.utils;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.fitpay.android.api.models.device.PaymentDevice;
import com.fitpay.android.rtm.callbacks.RTMListener;
import com.fitpay.android.rtm.enums.ErrorCodes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PresenceChannel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.User;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import java.text.ParseException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class RTMSession {

    private static final String WV = "wv";
    private static final String FPCTRL = "fpctrl";
    private static final String DEVICE = "device";

    private static final String DESTINATION = "destination";
    private static final String SENDER = "sender";

    private static final String PUBLIC_KEY = "publicKey";
    private static final String REQUESTER = "requester";
    private static final String ENCRYPTED_DATA = "encryptedData";

    private static final String CLIENT_DEVICE_SYNC = "client-device-sync";
    private static final String CLIENT_DEVICE_SYNC_ACK = "client-device-sync-ack";
    private static final String CLIENT_DEVICE_SYNC_COMPLETE = "client-device-sync-complete";
    private static final String CLIENT_DEVICE_SYNC_FAILED = "client-device-sync-failed";

    private static final String CLIENT_DEVICE_KEY = "client-device-key";
    private static final String CLIENT_WV_KEY = "client-wv-key";
    private static final String CLIENT_FPCTRL_KEY = "client-fpctrl-key";

    private static final String CLIENT_DEVICE_KEY_REQUEST = "client-device-key-request";
    private static final String CLIENT_WV_KEY_REQUEST = "client-wv-key-request";
    private static final String CLIENT_FPCTRL_KEY_REQUEST = "client-fpctrl-key-request";
    private static final String CLIENT_USER_DATA_REQUEST = "client-user-data-request";

    private static final String CLIENT_USER_DATA = "client-user-data";
    private static final String CLIENT_USER_DATA_ACK = "client-user-data-ack";
    private static final String CLIENT_DEVICE_DATA_RETRIEVED = "client-device-sync-data-retrieved";
    private static final String CLIENT_USER_DATA_FAILED = "client-user-data-failed";

    private static final String CLIENT_DEVICE_RECONNECTED = "client-device-reconnected";
    private static final String CLIENT_AUTO_LOGOUT = "client-auto-logout";

    private static final String CLIENT_GET_ESE_DATA ="client-get-ese-data";
    private static final String CLIENT_ESE_DATA = "client-ese-data";
    private static final String CLIENT_SPSD_DATA = "client-spsd-data";

    private RTMListener mListener;

    private Pusher mPusher;
    private PresenceChannel mChannel;

    private JsonParser mParser;

    public RTMSession(@NonNull String authUrl) {
        this(authUrl, null);
    }

    public RTMSession(@NonNull String authUrl, RTMListener listener) {
        mListener = listener;

        mParser = new JsonParser();

        HttpAuthorizer authorizer = new HttpAuthorizer(authUrl);
        PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
        mPusher = new Pusher(Constants.PUSHER_KEY, options);
        mPusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                if(mListener != null){
                    mListener.onError(e.toString());
                }
            }
        });
    }

    /**
     * Establishes websocket connection, provides URL for webview member;
     * When webview loads URL and establishes websocket connection RTM session is ready to be used by RTM client for exchanging messages;
     * In order to be notified when particular event occurs, callback must be set (onConnect, onParticipantsReady, onUserLogin)
     *
     * @param device payment device
     */
    public void connectDevice(@NonNull PaymentDevice device) {

        disconnectDevice();

        final String channelName = getChannelName(device);
        mChannel = mPusher.subscribePresence(channelName, presenceChannelEventListener);

        mChannel.bind(CLIENT_WV_KEY, presenceChannelEventListener);
        mChannel.bind(CLIENT_FPCTRL_KEY, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_KEY_REQUEST, presenceChannelEventListener);
        mChannel.bind(CLIENT_USER_DATA, presenceChannelEventListener);
        mChannel.bind(CLIENT_DEVICE_SYNC, presenceChannelEventListener);
        mChannel.bind(CLIENT_GET_ESE_DATA, presenceChannelEventListener);
        mChannel.bind(CLIENT_AUTO_LOGOUT, presenceChannelEventListener);
    }

    /**
     * Disconnect payment device
     */
    public void disconnectDevice() {
        if (mChannel != null) {
            mPusher.unsubscribe(mChannel.getName());
            mChannel = null;
        }
    }

    /**
     * Open FitPay site and connect to device
     *
     * @param device  payment device
     * @param webView webView
     */
    public void openWebView(@NonNull PaymentDevice device, @NonNull WebView webView) {

        connectDevice(device);

        String deviceJson = new Gson().toJson(device, PaymentDevice.class);

        String url = new StringBuilder()
       //         .append(Constants.BASE_URL)
                .append("/login?deviceData=")
                .append(StringUtils.base64UrlEncode(deviceJson))
                .toString();

        webView.loadUrl(url);
    }

    public void setRTMListener(RTMListener listener) {
        mListener = listener;
    }

    private String getChannelName(PaymentDevice deviceInfo){
        final String elementId = deviceInfo.getSecureElementId();
        return "presence-" + StringUtils.toSHA1(elementId);
    }

    private @KeysManager.KeyType Integer getKeyTypeForUser(User user){
        JsonObject json = mParser.parse(user.getInfo()).getAsJsonObject();
        String userName = json.get("user").getAsString();

        return stringToKeyType(userName);
    }

    private @KeysManager.KeyType Integer stringToKeyType(String value){
        switch (value){
            case WV:
                return KeysManager.KEY_WV;

            case FPCTRL:
                return KeysManager.KEY_FPCTRL;
        }

        return null;
    }

    private void generateKeyForUser(User user){

        @KeysManager.KeyType Integer type = getKeyTypeForUser(user);

        if(type != null) {
            try {
                KeysManager.getInstance().createPairForType(type);
            } catch (Exception e) {
                Constants.printError(e);
            }
        }
    }

    private void removeKeyForUser(User user){
        @KeysManager.KeyType Integer type = getKeyTypeForUser(user);

        if(type != null){
            KeysManager.getInstance().removePairForType(type);
        }
    }

    private PresenceChannelEventListener presenceChannelEventListener = new PresenceChannelEventListener() {

        @Override
        public void onEvent(String presenceName, String action, String data) {
            if (mChannel == null) {
                return;
            }

            boolean isSuccessful= false;

            JsonObject jsonObject = null;
            if (!StringUtils.isEmpty(data)) {
                jsonObject = mParser.parse(data).getAsJsonObject();
            }

            switch (action) {
                case CLIENT_DEVICE_KEY_REQUEST:
                    if (jsonObject != null && jsonObject.has(REQUESTER)) {

                        boolean requesterCorrect = false;

                        String requester = jsonObject.get(REQUESTER).getAsString();
                        switch (requester){
                            case WV:
                                requesterCorrect = true;
                                mChannel.trigger(CLIENT_WV_KEY_REQUEST, "{\"requester\":\"" + DEVICE + "\"}");
                                break;

                            case FPCTRL:
                                requesterCorrect = true;
                                mChannel.trigger(CLIENT_FPCTRL_KEY_REQUEST, "{\"requester\":\"" + DEVICE + "\"}");
                                break;
                        }

                        if(requesterCorrect) {
                            final String str = new StringBuilder()
                                    .append("{\"publicKey\":\"")
                                    .append(KeysManager.getInstance().getPairForType(KeysManager.KEY_WV).getPublicKey())
                                    .append("\",\"requester\":\"")
                                    .append(requester)
                                    .append("\"}")
                                    .toString();

                            mChannel.trigger(CLIENT_DEVICE_KEY, str);
                        }
                    }
                    break;

                case CLIENT_WV_KEY:
                    if (jsonObject != null && jsonObject.has(PUBLIC_KEY)) {
                        ECCKeyPair keyPair = KeysManager.getInstance().getPairForType(KeysManager.KEY_WV);
                        keyPair.setServerPublicKey(jsonObject.get(PUBLIC_KEY).getAsString());
                    }
                    break;

                case CLIENT_FPCTRL_KEY:
                    if (jsonObject != null && jsonObject.has(PUBLIC_KEY)) {
                        ECCKeyPair keyPair = KeysManager.getInstance().getPairForType(KeysManager.KEY_FPCTRL);
                        keyPair.setServerPublicKey(jsonObject.get(PUBLIC_KEY).getAsString());
                    }
                    break;

                case CLIENT_DEVICE_SYNC:
                    mChannel.trigger(CLIENT_DEVICE_SYNC_ACK, "{}");
                    //TODO: retrieve the new commit
                    //...some code here
                    mChannel.trigger(CLIENT_DEVICE_DATA_RETRIEVED, "{}");
                    //TODO: commit applied to the device
                    //...some code here
                    mChannel.trigger(CLIENT_DEVICE_SYNC_COMPLETE, "{}");
                    break;

                case CLIENT_USER_DATA:
                    @ErrorCodes.UserData int udCode = ErrorCodes.UDEC_UNKNOWN;

                    if (jsonObject != null && jsonObject.has(ENCRYPTED_DATA)) {
                        final String encryptedData = jsonObject.get(ENCRYPTED_DATA).getAsString();

                        try {
                            JWEObject jweObject;
                            jweObject = JWEObject.parse(encryptedData);
                            JWEHeader jweHeader = jweObject.getHeader();
                            Map<String, Object> params = jweHeader.getCustomParams();
                            if(params.containsKey(DESTINATION)
                                    && params.get(DESTINATION).equals(DEVICE)
                                    && params.containsKey(SENDER)){

                                @KeysManager.KeyType Integer keyType = stringToKeyType(params.get(SENDER).toString());

                                if(keyType != null) {
                                    final String decryptedData = StringUtils.getDecryptedString(keyType, encryptedData);
                                    //TODO: do something with decrypted data
                                    JsonObject decJson = mParser.parse(decryptedData).getAsJsonObject();
                                    isSuccessful = true;
                                }
                            }

                        } catch (ParseException e) {
                            Constants.printError(e);
                        }
                    }

                    if(isSuccessful) {
                        mChannel.trigger(CLIENT_USER_DATA_ACK, "{}");
                    } else {
                        String error = new StringBuilder()
                                .append("{\"error\":")
                                .append(udCode)
                                .append("}")
                                .toString();
                        mChannel.trigger(CLIENT_USER_DATA_FAILED, error);
                    }
                    break;

                case CLIENT_GET_ESE_DATA:
                    break;

                case CLIENT_AUTO_LOGOUT:
                    break;
            }
        }

        @Override
        public void onSubscriptionSucceeded(String s) {
            if(mListener != null){
                mListener.onConnect();
            }
        }

        @Override
        public void onAuthenticationFailure(String s, Exception e) {
            if(mListener != null){
                mListener.onError(e.toString());
            }
        }

        @Override
        public void onUsersInformationReceived(String s, Set<User> set) {
            for(User user : set){
                generateKeyForUser(user);
            }
        }

        @Override
        public void userSubscribed(String s, User user) {
            generateKeyForUser(user);
        }

        @Override
        public void userUnsubscribed(String s, User user) {
            removeKeyForUser(user);
        }
    };

}
