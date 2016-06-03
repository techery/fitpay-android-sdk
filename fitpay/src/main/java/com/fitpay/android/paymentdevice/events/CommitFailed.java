package com.fitpay.android.paymentdevice.events;

import com.fitpay.android.api.models.device.Commit;

/**
 * Created by tgs on 5/15/16.
 */
public class CommitFailed extends AbstractCommitNotProcessed {

    public CommitFailed(Commit commit) {
        super(commit);
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
