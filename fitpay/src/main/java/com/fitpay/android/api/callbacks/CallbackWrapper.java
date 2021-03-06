package com.fitpay.android.api.callbacks;

import android.support.annotation.NonNull;

import com.fitpay.android.api.enums.ResultCode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Wrapper around Retrofit callback
 */
final public class CallbackWrapper<T> implements Callback<T> {

    private ApiCallback<T> mCallback;

    public CallbackWrapper(@NonNull ApiCallback<T> callback){
        mCallback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(mCallback != null) {
            if (response.isSuccessful() && response.errorBody() == null){
                mCallback.onSuccess(response.body());
            } else {
                @ResultCode.Code int errorCode = response.code();

                if(response.errorBody() != null){
                    try {
                        mCallback.onFailure(errorCode, response.errorBody().string());
                    } catch (IOException e) {
                    }
                } else {
                    mCallback.onFailure(errorCode, response.message());
                }
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();   //TODO remove a little later - helpful in development
        if(mCallback != null){
            mCallback.onFailure(ResultCode.REQUEST_FAILED, t.getMessage());
        }
    }
}
