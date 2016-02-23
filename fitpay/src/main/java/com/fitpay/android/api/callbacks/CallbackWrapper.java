package com.fitpay.android.api.callbacks;

import com.fitpay.android.api.enums.ResultCode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vlad on 22.02.2016.
 */
public class CallbackWrapper<T> implements Callback<T> {

    private ApiCallback<T> mCallback;

    public CallbackWrapper(ApiCallback<T> callback){
        mCallback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(mCallback != null) {
            if (response.isSuccess() && response.errorBody() == null){
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
        if(mCallback != null){
            mCallback.onFailure(ResultCode.REQUEST_FAILED, t.getMessage());
        }
    }
}
