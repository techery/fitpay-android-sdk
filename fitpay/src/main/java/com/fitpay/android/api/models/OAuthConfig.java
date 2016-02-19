package com.fitpay.android.api.models;

import android.util.Base64;

/**
 * OAuth client_id and client_secret
 */
public class OAuthConfig {
    private final String consumerKey;
    private final String consumerSecret;

    /**
     * @param consumerKey    The consumer key.
     * @param consumerSecret The consumer secret.
     * @throws {@link java.lang.IllegalArgumentException} if consumer key or consumer secret is null.
     */
    public OAuthConfig(String consumerKey, String consumerSecret) {
        if (consumerKey == null || consumerSecret == null) {
            throw new IllegalArgumentException(
                    "AuthConfig must not be created with null consumer key or secret.");
        }
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    /**
     * concatenate username and password with colon for authentication
     * create Base64 encoded string
     *
     * @return String
     */
    public String getEncodedString() {
        String credentials = consumerKey + ":" + consumerSecret;
        return Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }
}
