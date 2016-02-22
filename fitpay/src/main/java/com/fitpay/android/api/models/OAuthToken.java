package com.fitpay.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * OAuth token.
 */
public class OAuthToken {
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private long expiresIn;
    @SerializedName("jti")
    private String userId;

    public OAuthToken() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getUserId(){
        return userId;
    }
}
