package com.fitpay.android.api.models.device;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.BaseModel;
import com.fitpay.android.api.models.Payload;
import com.google.gson.annotations.SerializedName;

/***
 * Created by Vlad on 16.03.2016.
 */
public class CommitModel extends BaseModel {

    protected String commitId;
    protected String previousCommitId;
    @CommitTypes.Type
    protected String commitType;
    protected Long createdTs;
    @SerializedName("encryptedData")
    protected Payload payload;

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

    public String getPreviousCommitId() {
        return previousCommitId;
    }

    public Object getPayload() {
        return payload.getData(commitType);
    }
}
