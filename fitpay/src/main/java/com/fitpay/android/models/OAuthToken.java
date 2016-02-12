package com.fitpay.android.models;

import com.fitpay.android.api.oauth.OAuthConst;
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
        final String authorizationHeader = String.format("%s %s", OAuthConst.AUTHORIZATION_BEARER, accessToken);
        return authorizationHeader;
    }
}
