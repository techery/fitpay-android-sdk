package com.fitpay.android.api.models;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import net.minidev.json.JSONObject;

import java.text.ParseException;

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
        if (TextUtils.isEmpty(userId) && !TextUtils.isEmpty(accessToken)) {
            JWT jwt = null;
            try {
                jwt = JWTParser.parse(accessToken);
                JSONObject jsonObject = jwt.getJWTClaimsSet().toJSONObject();
                userId = (String)jsonObject.get("user_id");
            } catch (ParseException e) {
                Log.e("ERROR", e.toString());
            }
        }

        return userId;
    }
}
