package com.fitpay.android.webview.impl;

/**
 * Created by Ross on 5/13/2016.
 */
public class SyncResponseModel {
    private int status;
    private String reason;

    public String getReason() {
        return reason;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SyncResponseModel{" +
                "reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static class Builder {
        private int status;
        private String reason;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public SyncResponseModel build() {
            SyncResponseModel response = new SyncResponseModel();
            response.status = this.status;
            response.reason = this.reason;
            return response;
        }

    }
}
