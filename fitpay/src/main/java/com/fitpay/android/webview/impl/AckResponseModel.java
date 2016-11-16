package com.fitpay.android.webview.impl;


public class AckResponseModel {
    private int status;

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "AckResponseModel{" +
                "status='" + status + '\'' +
                '}';
    }

    public static class Builder {
        private int status;

        public Builder status(int status) {
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
