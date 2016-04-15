package com.fitpay.android.api.models;

/**
 * Transaction
 */
public final class Transaction extends BaseModel {

    private String transactionId;
    private String transactionType;
    private double amount;
    private String currencyCode;
    private String authorizationStatus;
    private long transactionTimeEpoch;
    private String merchantName;
    private String merchantCode;
    private String merchantType;

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
