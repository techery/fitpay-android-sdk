package com.fitpay.android.paymentdevice.events;

/**
 * Created by tgs on 5/15/16.
 */
public class CommitSuccess {

    private String commitId;
    private String commitType;
    private long createdTs;

    public CommitSuccess(String commitId) {
        this.commitId = commitId;
    }

    public CommitSuccess(String commitId, String commitType, long createdTs) {
        this.commitId = commitId;
        this.commitType = commitType;
        this.createdTs = createdTs;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getCommitType() {
        return commitType;
    }

    public long getCreatedTs() {
        return createdTs;
    }

    @Override
    public String toString() {
        return "CommitSuccess{" +
                "commitId='" + commitId + '\'' +
                '}';
    }
}
