package com.fitpay.android.api.models;


import java.util.List;

public final class ApduPackage {

    private String packageId;
    private String state;
    private String executedTs;
    private int executedDuration;
    private List<ApduResponses> apduResponses;

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setExecutedTs(String executedTs) {
        this.executedTs = executedTs;
    }

    public void setExecutedDuration(int executedDuration) {
        this.executedDuration = executedDuration;
    }

    public void setApduResponses(List<ApduResponses> apduResponses) {
        this.apduResponses = apduResponses;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getState() {
        return state;
    }

    public String getExecutedTs() {
        return executedTs;
    }

    public int getExecutedDuration() {
        return executedDuration;
    }

    public List<ApduResponses> getApduResponses() {
        return apduResponses;
    }

    public static class ApduResponses {
        private String commandId;
        private String responseCode;
        private String responseData;

        public void setCommandId(String commandId) {
            this.commandId = commandId;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public void setResponseData(String responseData) {
            this.responseData = responseData;
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
}
