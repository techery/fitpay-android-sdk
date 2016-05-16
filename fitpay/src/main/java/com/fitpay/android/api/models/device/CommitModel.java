package com.fitpay.android.api.models.device;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.BaseModel;
import com.fitpay.android.api.models.Payload;
import com.google.gson.annotations.SerializedName;

/**
 * Commit model
 */
abstract class CommitModel extends BaseModel {

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

    public Object getPayload() {
        return payload.getData(commitType);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Commit(")
            .append("commitType=").append(commitType)
                .append(", id=").append(commitId)
            .append(")")
            .toString();
    }
}
