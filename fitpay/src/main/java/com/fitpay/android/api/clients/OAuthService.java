package com.fitpay.android.api.clients;

import com.fitpay.android.api.models.OAuthToken;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 */
public interface OAuthService {
    @GET("oauth/token?grant_type=client_credentials")
    Call<OAuthToken> getAuthToken();
}
