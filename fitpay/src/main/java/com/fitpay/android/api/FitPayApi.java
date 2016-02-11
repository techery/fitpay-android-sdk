package com.fitpay.android.api;


import com.fitpay.android.models.AuthenticatedUser;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FitPayApi {

    @FormUrlEncoded
    @POST("users/login")
    Call<AuthenticatedUser> loginUser(@FieldMap Map<String, String> options);

}