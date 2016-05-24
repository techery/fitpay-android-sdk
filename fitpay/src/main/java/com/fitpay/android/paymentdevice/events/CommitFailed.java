package com.fitpay.android.paymentdevice.events;

/**
 * Created by tgs on 5/15/16.
 */
public class CommitFailed {

    private String commitId;

    public CommitFailed(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitId() {
        return commitId;
    }

    @Override
    public String toString() {
        return "CommitFailed{" +
                "commitId='" + commitId + '\'' +
                '}';
    }
}
