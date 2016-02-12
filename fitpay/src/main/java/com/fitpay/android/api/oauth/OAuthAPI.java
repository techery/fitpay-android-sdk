package com.fitpay.android.api.oauth;

import com.fitpay.android.api.oauth.objects.OAuthToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Vlad on 12.02.2016.
 */
public interface OAuthAPI {
    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<OAuthToken> getAuthToken(@Field(OAuthConst.PARAM_GRANT_TYPE) String grantType);
}
