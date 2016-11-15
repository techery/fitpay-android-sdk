package com.fitpay.android.paymentdevice.utils;

import com.fitpay.android.api.enums.ResponseState;

/**
 * Created by Vlad on 15.11.2016.
 */
public class ApduExecException extends Exception {

    @ResponseState.ApduState
    private String responseState;

    public ApduExecException(@ResponseState.ApduState String state, String message) {
        super(message);
        responseState = state;
    }

    @ResponseState.ApduState
    public String getResponseState() {
        return responseState;
    }
}
