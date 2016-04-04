package com.fitpay.android.api.models.apdu;

import com.fitpay.android.api.enums.ResponseState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 01.04.2016.
 */
public final class ApduPackageResponse {

    private String packageId;
    @ResponseState.ApduState
    private String state;
    private long executedTsEpoch;
    private int executedDuration;
    private List<ApduCommandResult> apduCommandResults;

    public ApduPackageResponse(String packageId){
        this.packageId = packageId;
        apduCommandResults = new ArrayList<>();
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

    public List<ApduCommandResult> getResults() {
        return apduCommandResults;
    }

    public void addResult(ApduCommandResult result){
        apduCommandResults.add(result);
    }
}
