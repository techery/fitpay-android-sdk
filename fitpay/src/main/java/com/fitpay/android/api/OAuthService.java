package com.fitpay.android.api;

import com.fitpay.android.api.oauth.OAuthConst;
import com.fitpay.android.models.OAuthToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 */
public interface OAuthService {
    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<OAuthToken> getAuthToken(@Field(OAuthConst.PARAM_GRANT_TYPE) String grantType);
}
