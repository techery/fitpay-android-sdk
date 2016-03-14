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
    private String createdTs;
    private long createdTsEpoch;
    private String lastModifiedTs;
    private long lastModifiedTsEpoch;
    private String verifiedTs;
    private long verifiedTsEpoch;

    public String getVerificationId() {
        return verificationId;
    }

    public String getState() {
        return state;
    }

    public String getEthodType() {
        return methodType;
    }

    public String getValue() {
        return value;
    }

    public String getVerificationResult() {
        return verificationResult;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public String getLastModifiedTs() {
        return lastModifiedTs;
    }

    public long getLastModifiedTsEpoch() {
        return lastModifiedTsEpoch;
    }

    public String getVerifiedTs() {
        return verifiedTs;
    }

    public long getVerifiedTsEpoch() {
        return verifiedTsEpoch;
    }
}
