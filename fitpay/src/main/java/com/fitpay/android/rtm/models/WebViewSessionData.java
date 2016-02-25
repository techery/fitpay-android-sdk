package com.fitpay.android.rtm.models;

/**
 * This data can then be used to set or verify a user device relationship, retrieve commit changes for the device, etc...
 */
public class WebViewSessionData {

    private String userId;
    private String deviceId;
    private String token;

    public WebViewSessionData(String userId, String deviceId, String token) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}