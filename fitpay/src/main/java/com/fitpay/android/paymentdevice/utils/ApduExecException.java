package com.fitpay.android.paymentdevice.utils;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.utils.StringUtils;

/**
 * Created by Vlad on 15.11.2016.
 */
public class ApduExecException extends Exception {

    @ResponseState.ApduState
    private String responseState;

    private String commandId;

    public ApduExecException(@ResponseState.ApduState String state, String message) {
        super(message);
        responseState = state;
    }

    public ApduExecException(@ResponseState.ApduState String state, String message, String commandId) {
        super(message);
        responseState = state;
        this.commandId = commandId;
    }

    @ResponseState.ApduState
    public String getResponseState() {
        return responseState;
    }

    @Override
    public String getMessage() {
        if (!StringUtils.isEmpty(commandId)) {
            return "commandId:" + commandId + " " + super.getMessage();
        } else {
            return super.getMessage();
        }
    }
}
