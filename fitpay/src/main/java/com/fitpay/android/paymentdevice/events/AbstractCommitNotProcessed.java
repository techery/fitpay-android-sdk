package com.fitpay.android.paymentdevice.events;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.device.Commit;

/**
 * Created by tgs on 5/15/16.
 */
public abstract class AbstractCommitNotProcessed {

    protected Commit commit;
    protected String errorMessage;
    protected int errorCode;

    public AbstractCommitNotProcessed(Commit commit) {
        this.commit = commit;
    }

    public Commit getCommit() {
        return commit;
    }

    public String getCommitId() {
        if (commit != null) {
            return commit.getCommitId();
        }

        return null;
    }

    @CommitTypes.Type
    public String getCommitType() {
        if (commit != null) {
            return commit.getCommitType();
        }

        return null;
    }

    public long getCreatedTs() {
        if (commit != null) {
            return commit.getCreatedTs();
        }

        return -1;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
