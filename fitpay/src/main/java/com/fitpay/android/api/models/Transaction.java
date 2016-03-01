package com.fitpay.android.api.models;


public final class Transaction extends BaseModel{

    private String transactionId;
    private String transactionType;
    private double amount;
    private String currencyCode;
    private String authorizationStatus;
    private String transactionTime;
    private long transactionTimeEpoch;
    private String merchantName;
    private String merchantCode;
    private String merchantType;

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setAuthorizationStatus(String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public void setTransactionTimeEpoch(long transactionTimeEpoch) {
        this.transactionTimeEpoch = transactionTimeEpoch;
    }

    public void setErchantName(String erchantName) {
        this.merchantName = erchantName;
    }

    public void setErchantCode(String erchantCode) {
        this.merchantCode = erchantCode;
    }

    public void setErchantType(String erchantType) {
        this.merchantType = erchantType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public long getTransactionTimeEpoch() {
        return transactionTimeEpoch;
    }

    public String getErchantName() {
        return merchantName;
    }

    public String getErchantCode() {
        return merchantCode;
    }

    public String getErchantType() {
        return merchantType;
    }
}
