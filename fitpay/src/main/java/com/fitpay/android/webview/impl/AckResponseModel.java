package com.fitpay.android.webview.impl;


public class AckResponseModel {
    private String status;

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "AckResponseModel{" +
                "status='" + status + '\'' +
                '}';
    }

    public static class Builder {
        private String status;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public AckResponseModel build() {
            AckResponseModel response = new AckResponseModel();
            response.status = this.status;
            return response;
        }
    }
}
