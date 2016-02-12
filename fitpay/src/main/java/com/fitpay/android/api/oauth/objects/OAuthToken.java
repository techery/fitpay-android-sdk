package com.fitpay.android.api.oauth.objects;

import com.fitpay.android.api.oauth.OAuthConst;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2.0 token.
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

    public Map<String, String> getAuthHeaders(OAuthConfig authConfig, String method,
                                              String url, Map<String, String> postParams) {
        final Map<String, String> headers = new HashMap<>();
        final String authorizationHeader = String.format("%s %s", OAuthConst.AUTHORIZATION_BEARER, accessToken);
        headers.put(OAuthConst.HEADER_AUTHORIZATION, authorizationHeader);
        return headers;
    }
}
