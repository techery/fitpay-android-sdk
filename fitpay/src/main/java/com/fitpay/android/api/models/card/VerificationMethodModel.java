package com.fitpay.android.api.models.card;

import com.fitpay.android.api.models.BaseModel;

/**
 * Verification method model
 */
abstract class VerificationMethodModel extends BaseModel {

    private String verificationId;
    private String state;
    private String methodType;
    private String value;
    private String verificationResult;
    private long createdTsEpoch;
    private long lastModifiedTsEpoch;
    private long verifiedTsEpoch;

    public String getVerificationId() {
        return verificationId;
    }

    public String getState() {
        return state;
    }

    public String getMethodType() {
        return methodType;
    }

    public String getValue() {
        return value;
    }

    public String getVerificationResult() {
        return verificationResult;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public long getLastModifiedTsEpoch() {
        return lastModifiedTsEpoch;
    }

    public long getVerifiedTsEpoch() {
        return verifiedTsEpoch;
    }
}
