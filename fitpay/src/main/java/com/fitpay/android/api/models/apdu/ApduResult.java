package com.fitpay.android.api.models.apdu;

import java.util.List;

/**
 * Created by Vlad on 01.04.2016.
 */
public final class ApduResult {

    private String packageId;
    private String state;
    private String executedTs;
    private int executedDuration;
    private List<ApduResponse> apduResponses;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getExecutedTs() {
        return executedTs;
    }

    public void setExecutedTs(String executedTs) {
        this.executedTs = executedTs;
    }

    public int getExecutedDuration() {
        return executedDuration;
    }

    public void setExecutedDuration(int executedDuration) {
        this.executedDuration = executedDuration;
    }

    public List<ApduResponse> getApduResponses() {
        return apduResponses;
    }

    public void setApduResponses(List<ApduResponse> apduResponses) {
        this.apduResponses = apduResponses;
    }

}
