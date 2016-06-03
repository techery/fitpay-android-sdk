package com.fitpay.android.api.models.security;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.StringUtils;
import com.google.gson.annotations.SerializedName;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import net.minidev.json.JSONObject;

import java.text.ParseException;

/**
 * OAuth token.
 */
final public class OAuthToken {

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private long expiresIn;

    private String userId = null;

    public OAuthToken() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getUserId() {
        if (StringUtils.isEmpty(userId) && !StringUtils.isEmpty(accessToken)) {
            JWT jwt = null;
            try {
                jwt = JWTParser.parse(accessToken);
                JSONObject jsonObject = jwt.getJWTClaimsSet().toJSONObject();
                userId = (String)jsonObject.get("user_id");
            } catch (ParseException e) {
                Constants.printError(e.toString());
            }
        }

        return userId;
    }

    public static class Builder {

        private String tokenType;
        private String accessToken;
        private long expiresIn;
        private String userId = null;

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public OAuthToken build() {
            OAuthToken token = new OAuthToken();
            token.accessToken = this.accessToken;
            token.tokenType = this.tokenType;
            token.expiresIn = this.expiresIn;
            token.userId = this.userId;
            return token;
        }
    }
}
