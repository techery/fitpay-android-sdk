package com.fitpay.android.paymentdevice.events;

/**
 * Created by tgs on 5/15/16.
 */
public class CommitSuccess {

    private String commitId;

    public CommitSuccess(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitId() {
        return commitId;
    }
}
