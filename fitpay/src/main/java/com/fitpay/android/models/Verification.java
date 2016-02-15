package com.fitpay.android.models;


public class Verification {


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

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setEthodType(String ethodType) {
        this.methodType = ethodType;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVerificationResult(String verificationResult) {
        this.verificationResult = verificationResult;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public void setLastModifiedTs(String lastModifiedTs) {
        this.lastModifiedTs = lastModifiedTs;
    }

    public void setLastModifiedTsEpoch(long lastModifiedTsEpoch) {
        this.lastModifiedTsEpoch = lastModifiedTsEpoch;
    }

    public void setVerifiedTs(String verifiedTs) {
        this.verifiedTs = verifiedTs;
    }

    public void setVerifiedTsEpoch(long verifiedTsEpoch) {
        this.verifiedTsEpoch = verifiedTsEpoch;
    }

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