package com.fitpay.android.api.models;

import com.fitpay.android.api.enums.CommitTypes;
import com.google.gson.annotations.SerializedName;

public final class Commit extends BaseModel {

    private String commitId;
    @CommitTypes.Type
    private String commitType;
    private Long createdTs;

    @SerializedName("encryptedData")
    private Payload payload;

    @CommitTypes.Type
    public String getCommitType() {
        return commitType;
    }

    public long getCreatedTs() {
        return createdTs;
    }

    public String getCommitId() {
        return commitId;
    }

    public Object getPayload(){
        return payload.getData(commitType);
    }
}