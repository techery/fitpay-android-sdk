package com.fitpay.android.api.models.apdu;

/**
 * Created by Vlad on 01.04.2016.
 */
public final class ApduResponse {
    private String commandId;
    private String responseCode;
    private String responseData;

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}
