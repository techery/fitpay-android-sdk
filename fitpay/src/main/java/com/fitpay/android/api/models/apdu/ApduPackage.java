package com.fitpay.android.api.models.apdu;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;

/**
 * Created by Vlad on 01.04.2016.
 */
public class ApduPackage extends ApduPackageModel {
    private static final String APDU_RESPONSE = "apduResponse";

    /**
     * Endpoint to allow for returning responses to APDU execution.
     *
     * @param apduExecutionResult package confirmation data:(packageId, state, executedTs, executedDuration, apduResponses:(commandId, commandId, responseData))
     * @param callback   result callback
     */
    public void confirm(@NonNull ApduExecutionResult apduExecutionResult, @NonNull ApiCallback<Void> callback) {
        makePostCall(APDU_RESPONSE, apduExecutionResult, Void.class, callback);
    }

}
