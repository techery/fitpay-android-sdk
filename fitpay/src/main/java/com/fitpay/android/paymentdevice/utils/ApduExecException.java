package com.fitpay.android.paymentdevice.utils;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.utils.StringUtils;

/**
 * Created by Vlad on 15.11.2016.
 */
public class ApduExecException extends Exception {

    @ResponseState.ApduState
    private String responseState;
    private String responseCode;
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

    public ApduExecException(@ResponseState.ApduState String state, String message, String commandId, String responseCode) {
        super(message);
        responseState = state;
        this.commandId = commandId;
        this.responseCode = responseCode;
    }

    @ResponseState.ApduState
    public String getResponseState() {
        return responseState;
    }

    public String getResponseCode() {
        return responseCode;
    }

    @Override
    public String getMessage() {
        if (!StringUtils.isEmpty(commandId)) {
            StringBuilder sb = new StringBuilder();
            sb.append("commandId:").append(commandId).append(" ");
            if (!StringUtils.isEmpty(responseCode)) {
                sb.append("responseCode:").append(responseCode).append(" ");
            }
            sb.append(super.getMessage());
            return sb.toString();
        } else {
            return super.getMessage();
        }
    }
}
