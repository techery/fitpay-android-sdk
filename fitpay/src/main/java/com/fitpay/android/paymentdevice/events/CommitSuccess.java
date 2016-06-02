package com.fitpay.android.paymentdevice.events;

import com.fitpay.android.api.models.device.Commit;

/**
 * Created by tgs on 5/15/16.
 */
public class CommitSuccess {

    private Commit commit;

    public CommitSuccess(Commit commit) {
        this.commit = commit;
    }

    public Commit getCommit() {
        return commit;
    }

    public String getCommitId() {
        return commit.getCommitId();
    }

    public String getCommitType() {
        return commit.getCommitType();
    }

    public long getCreatedTs() {
        return commit.getCreatedTs();
    }

    @Override
    public String toString() {
        return "CommitSuccess{" +
                "commitId='" + getCommitId() + '\'' +
                ", commitType='" + getCommitType() + '\'' +
                '}';
    }
}
