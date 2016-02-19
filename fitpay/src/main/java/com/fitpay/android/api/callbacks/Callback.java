package com.fitpay.android.api.callbacks;

import com.fitpay.android.api.enums.ResultCode;

/**
 * Communicates responses from a server
 *
 * @param <T> expected response type
 */
public interface Callback<T> {
    /**
     * Successful HTTP response.
     */
    void onResponse(T result);

    /**
     * Invoked when a network or unexpected exception occurred during the HTTP request.
     *
     * @param errorCode    error code
     * @param errorMessage readable message of an error
     */
    void onFailure(@ResultCode.Code int errorCode, String errorMessage);
}
