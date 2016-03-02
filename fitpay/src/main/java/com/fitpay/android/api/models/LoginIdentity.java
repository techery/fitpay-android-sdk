package com.fitpay.android.api.models;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Login required data
 */
public final class LoginIdentity {

    Map<String, String> data;

    private LoginIdentity(){
        data = new HashMap<>();
    }

    public Map<String, String> getData(){
        return data;
    }

    public static class Builder {
        private String username = null;
        private String password = null;
        private String clientId = null;
        private String redirectUri = null;

        /**
         * Creates a Builder instance that can be used to build {@link Map<String,String>} with various configuration
         * settings. Builder follows the builder pattern, and it is typically used by first
         * invoking various configuration methods to set desired options, and finally calling
         * {@link #create()}.
         */
        public Builder(){
        }

        /**
         * Creates a {@link LoginIdentity} instance based on the current configuration. This method is free of
         * side-effects to this {@code Builder} instance and hence can be called multiple times.
         *
         * @return an instance of {@link LoginIdentity} configured with the options currently set in this builder
         */
        public LoginIdentity create() throws IllegalArgumentException{
            LoginIdentity loginIdentity = new LoginIdentity();

            if(TextUtils.isEmpty(username)){
                throw new NullPointerException("Username can't be null");
            }

            if(TextUtils.isEmpty(password)){
                throw new NullPointerException("Password can't be null");
            }

            if(TextUtils.isEmpty(clientId)){
                throw new NullPointerException("ClientId can't be null");
            }

            if(TextUtils.isEmpty(redirectUri)){
                throw new NullPointerException("RedirectUri can't be null");
            }

            String data = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            loginIdentity.data.put("credentials", data);
            loginIdentity.data.put("response_type", "token");
            loginIdentity.data.put("client_id", clientId);
            loginIdentity.data.put("redirect_uri", redirectUri);

            return loginIdentity;
        }

        /**
         * Set username
         * @param username username
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setUsername(@NonNull String username) {
            this.username = username;
            return this;
        }

        /**
         * Set password
         * @param password password
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setPassword(@NonNull String password) {
            this.password = password;
            return this;
        }

        /**
         * Set clientId
         * @param clientId client id
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setClientId(@NonNull String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Set redirect uri
         * @param redirectUri redirect uri
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setRedirectUri(@NonNull String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }
    }
}
