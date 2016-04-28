package com.fitpay.android.utils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface AuthClient {

    /**
     * Login user and get auth token
     */
    @FormUrlEncoded
    @POST("oauth/authorize")
    //@POST(Constants.BASE_URL + "oauth/authorize")
    Call<OAuthToken> loginUser(@FieldMap Map<String, String> options);

    /**
     * Resource owner password credentials grant
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Call<OAuthToken> authUser(@FieldMap Map<String, String> options);


}