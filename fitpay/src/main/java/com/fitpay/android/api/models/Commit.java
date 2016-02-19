package com.fitpay.android.api.models;


import android.support.annotation.StringDef;

import java.util.Map;

public class Commit extends BaseModel {

    public static final String CREDITCARD_CREATED = "CREDITCARD_CREATED";
    public static final String CREDITCARD_DEACTIVATED = "CREDITCARD_DEACTIVATED";
    public static final String CREDITCARD_ACTIVATED = "CREDITCARD_ACTIVATED";
    public static final String CREDITCARD_DELETED = "CREDITCARD_DELETED";
    public static final String RESET_DEFAULT_CREDITCARD = "RESET_DEFAULT_CREDITCARD";
    public static final String SET_DEFAULT_CREDITCARD = "SET_DEFAULT_CREDITCARD";
    public static final String APDU_PACKAGE = "APDU_PACKAGE ";

    @Type
    private String commitType;
    private Payload payload;
    private long createdTs;
    private String previousCommit;
    private String commit;

    @Type
    public String getCommitType() {
        return commitType;
    }

    public void setCommitType(@Type String commitType) {
        this.commitType = commitType;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public long getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(long createdTs) {
        this.createdTs = createdTs;
    }

    public String getPreviousCommit() {
        return previousCommit;
    }

    public void setPreviousCommit(String previousCommit) {
        this.previousCommit = previousCommit;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    @StringDef({
            CREDITCARD_CREATED,
            CREDITCARD_ACTIVATED,
            CREDITCARD_DEACTIVATED,
            CREDITCARD_DELETED,
            RESET_DEFAULT_CREDITCARD,
            SET_DEFAULT_CREDITCARD,
            APDU_PACKAGE
    })
    public @interface Type {
    }

    public static class Payload {
        private Map<String, Object> info;

        public Map<String, Object> getInfo() {
            return info;
        }
    }

}