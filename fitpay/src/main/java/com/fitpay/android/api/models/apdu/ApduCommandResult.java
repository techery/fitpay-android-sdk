package com.fitpay.android.api.models.apdu;

import com.fitpay.android.wearable.ble.utils.Hex;
import com.fitpay.android.wearable.interfaces.IApduMessage;

import java.util.Arrays;

/**
 * Created by Vlad on 01.04.2016.
 */
public final class ApduCommandResult {

    public static String SUCCESS_RESULT = "9000";

    private String commandId;
    private String responseCode;
    private String responseData;

    public ApduCommandResult(String commandId, IApduMessage message) {
        byte[] data = message.getData();

        this.commandId = commandId;
        responseCode = Hex.bytesToHexString(Arrays.copyOfRange(data, data.length - 2, data.length));
        responseData = Hex.bytesToHexString(data);
    }

    public String getCommandId() {
        return commandId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseData() {
        return responseData;
    }
}
