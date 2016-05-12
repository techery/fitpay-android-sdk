package com.fitpay.android.api.models.apdu;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.paymentdevice.constants.ApduConstants;
import com.fitpay.android.utils.Hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Apdu package execution result
 */
public final class ApduExecutionResult {

    private String packageId;
    @ResponseState.ApduState
    private String state;
    private long executedTsEpoch;
    private int executedDuration;
    private List<ApduCommandResult> apduResponses;
    private String errorReason;

    public ApduExecutionResult(String packageId){
        this.packageId = packageId;
        apduResponses = new ArrayList<>();
    }

    public String getPackageId() {
        return packageId;
    }

    @ResponseState.ApduState
    public String getState() {
        return state;
    }

    public void setState(@ResponseState.ApduState String state) {
        this.state = state;
    }

    public long getExecutedTsEpoch() {
        return executedTsEpoch;
    }

    public void setExecutedTsEpoch(long executedTsEpoch) {
        this.executedTsEpoch = executedTsEpoch;
    }

    public int getExecutedDuration() {
        return executedDuration;
    }

    public void setExecutedDuration(int executedDuration) {
        this.executedDuration = executedDuration;
    }

    public List<ApduCommandResult> getResponses() {
        return apduResponses;
    }

    public void addResponse(ApduCommandResult response){
        apduResponses.add(response);
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public void deriveState() {
        if (getResponses().size() == 0) {
            state = ResponseState.ERROR;
        } else {
            state = ResponseState.PROCESSED;

            resultsLoop:
            for (ApduCommandResult response : getResponses()) {
                int size = ApduConstants.SUCCESS_RESULTS.length;

                for (int i = 0; i < size; i++) {
                    if (!Arrays.equals(ApduConstants.SUCCESS_RESULTS[i], Hex.hexStringToBytes(response.getResponseCode()))) {
                        state = ResponseState.FAILED;
                        break resultsLoop;
                    }
                }
            }
        }

    }
}
