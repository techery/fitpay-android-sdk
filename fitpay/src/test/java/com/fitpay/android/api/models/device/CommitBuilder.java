package com.fitpay.android.api.models.device;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.Payload;

/**
 * Created by tgs on 6/1/16.
 */
public class CommitBuilder {

    private CommitBuilder() {
        // builder only
    }

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
