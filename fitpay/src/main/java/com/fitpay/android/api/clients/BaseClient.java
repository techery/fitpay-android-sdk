package com.fitpay.android.api.clients;

import com.fitpay.android.utils.C;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vlad on 12.02.2016.
 */
public abstract class BaseClient<T> {

    protected static final String BASE_URL = "https://demo.pagare.me/";
    protected static final String API_URL = BASE_URL + "api/";

    protected static final String HEADER_AUTHORIZATION = "Authorization";
    protected static final String AUTHORIZATION_BASIC = "Basic";
    protected static final String AUTHORIZATION_BEARER = "Bearer";

    protected T mAPIService;

    public GsonConverterFactory getDefaultGsonConverter() {
        return GsonConverterFactory.create(C.getDefaultGson());
    }

    public T getService() {
        return mAPIService;
    }
}
