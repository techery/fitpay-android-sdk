package com.fitpay.android.api.models;

import com.fitpay.android.api.clients.BaseClient;
import com.google.gson.annotations.SerializedName;

/**
 * OAuth token.
 */
public class OAuthToken {
    @SerializedName("token_type")
    private final String tokenType;
    @SerializedName("access_token")
    private final String accessToken;

    public OAuthToken(String tokenType, String accessToken) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public String getAuthHeader() {
        final String authorizationHeader = String.format("%s %s", BaseClient.AUTHORIZATION_BEARER, accessToken);
        return authorizationHeader;
    }
}
