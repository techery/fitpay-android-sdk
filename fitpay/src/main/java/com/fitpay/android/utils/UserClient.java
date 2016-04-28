package com.fitpay.android.utils;

import com.fitpay.android.api.models.user.User;
import com.fitpay.android.api.models.user.UserCreateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface UserClient {

    /**
     * Login user and get auth token
     */
    @POST("users")
    Call<User> createUser(@Body UserCreateRequest user);

}