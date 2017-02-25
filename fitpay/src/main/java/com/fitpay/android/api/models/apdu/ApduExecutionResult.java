package com.fitpay.android.api.models.apdu;

import com.fitpay.android.api.enums.ResponseState;
import com.fitpay.android.paymentdevice.constants.ApduConstants;
import com.fitpay.android.utils.Hex;

import java.util.ArrayList;
import java.util.List;

/**
 * Apdu package execution result
 */
public final class ApduExecutionResult {

    private String packageId;
    @ResponseState.ApduState
    private String state;
    private long executedTsEpoch;
    private int executedDuration; //in seconds
    private List<ApduCommandResult> apduResponses;
    private String errorReason;
    private String errorCode;

    public ApduExecutionResult(String packageId) {
        this.packageId = packageId;
        this.setExecutedTsEpoch(System.currentTimeMillis());
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

    public void setExecutedDurationTilNow() {
        this.executedDuration = (int) ((System.currentTimeMillis() - getExecutedTsEpoch()) / 1000);
    }

    public List<ApduCommandResult> getResponses() {
        return apduResponses;
    }

    public void addResponse(ApduCommandResult response) {
        apduResponses.add(response);
        if (null == state || ResponseState.PROCESSED.equals(state)) {
            if (isSuccessResponseCode(response)) {
                state = ResponseState.PROCESSED;
            } else {
                state = ResponseState.FAILED;
            }
        }
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void deriveState() {
        if (getResponses().size() == 0) {
            state = ResponseState.ERROR;
        } else {
            state = ResponseState.PROCESSED;

            for (ApduCommandResult response : getResponses()) {
                if (!isSuccessResponseCode(response)) {
                    state = ResponseState.FAILED;
                    break;
                }
            }
        }
    }

    protected boolean isSuccessResponseCode(ApduCommandResult commandResult) {
        byte[] code = Hex.hexStringToBytes(commandResult.getResponseCode());
        for (int i = 0; i < ApduConstants.SUCCESS_RESULTS.length; i++) {
            if (equals(ApduConstants.SUCCESS_RESULTS[i], code)) {
                return true;
            }
        }
        return commandResult.canContinueOnFailure();
    }

    @Override
    public String toString() {
        return "ApduExecutionResult{" +
                "state='" + state + '\'' +
                ", packageId='" + packageId + '\'' +
                ", numberOfApduCommandResults='" + apduResponses.size() + '\'' +
                ", errorReason='" + errorReason + '\'' +
                ", executedDuration=" + executedDuration +
                ", executedTsEpoch=" + executedTsEpoch +
                '}';
    }

    private static boolean equals(byte[] a, byte[] a2) {
        if (a == a2) {
            return true;
        }

        if (a == null || a2 == null) {
            return false;
        }

        if (a2.length > 2) {
            return false;
        }

        if (a.length == 1) {
            return a[0] == a2[0];
        }

        for (int i = 0; i < a.length; i++) {
            if (a[i] != a2[i])
                return false;
        }

        return true;
    }
}
