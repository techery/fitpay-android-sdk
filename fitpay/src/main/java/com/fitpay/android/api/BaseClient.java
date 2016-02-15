package com.fitpay.android.api;

import com.fitpay.android.utils.C;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vlad on 12.02.2016.
 */
public abstract class BaseClient<T> {

    protected T mAPIService;

    public GsonConverterFactory getDefaultGsonConverter() {
        return GsonConverterFactory.create(C.getDefaultGson());
    }

    public T getService() {
        return mAPIService;
    }
}
