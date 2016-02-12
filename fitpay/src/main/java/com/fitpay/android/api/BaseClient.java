package com.fitpay.android.api;

/**
 * Created by Vlad on 12.02.2016.
 */
public abstract class BaseClient <T> {

    protected T mAPIService;

    public T getService(){
        return mAPIService;
    }
}
