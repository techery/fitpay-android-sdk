package com.fitpay.android.paymentdevice.events;

import com.fitpay.android.api.models.device.Commit;

/**
 * Created by tgs on 5/15/16.
 */
public class CommitFailed {

    private Commit commit;
    private String errorMessage;
    private int errorCode;

    public CommitFailed(Commit commit) {
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "CommitFailed{" +
                "commitId='" + getCommitId() + '\'' +
                ", commitType='" + getCommitType() + '\'' +
                ", errorCode='" + getErrorCode() + '\'' +
                ", errorMessage='" + getErrorMessage() + '\'' +
                '}';
    }

    public static class Builder {
        private Commit commit;
        private String errorMessage;
        private int errorCode;

        public Builder commit(Commit commit) {
            this.commit = commit;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder errorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public CommitFailed build() {
            CommitFailed commit = new CommitFailed(this.commit);
            commit.errorCode = this.errorCode;
            commit.errorMessage = this.errorMessage;
            return commit;
        }

    }
}
