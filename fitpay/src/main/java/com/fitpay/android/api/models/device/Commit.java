package com.fitpay.android.api.models.device;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.Payload;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;

/**
 * Commit
 */
public final class Commit extends CommitModel {
    private static final String APDU_RESPONSE = "apduResponse";


    /**
     * Endpoint to confirm APDU execution.
     *
     * @param apduExecutionResult package confirmation data:(packageId, state, executedTs, executedDuration, apduResponses:(commandId, commandId, responseData))
     * @param callback            result callback
     */
    public void confirm(@NonNull ApduExecutionResult apduExecutionResult, @NonNull ApiCallback<Void> callback) {
        makePostCall(APDU_RESPONSE, apduExecutionResult, Void.class, callback);
    }


    //TODO can be removed after testing complete - needed for ease of apdupackage testing
    public static class Builder {

        private String commitId;
        @CommitTypes.Type
        private String commitType;
        private Long createdTs;
        private Payload payload;

        public Builder commitId(String commitId) {
            this.commitId = commitId;
            return this;
        }

        public Builder commitType(String commitType) {
            this.commitType = commitType;
            return this;
        }

        public Builder createdTs(Long createdTs) {
            this.createdTs = createdTs;
            return this;
        }

        public Builder payload(Payload payload) {
            this.payload = payload;
            return this;
        }

        public Commit build() {
            Commit commit = new Commit();
            commit.commitId = this.commitId;
            commit.commitType = this.commitType;
            commit.createdTs = this.createdTs;
            commit.payload = this.payload;
            return commit;
        }

    }
}