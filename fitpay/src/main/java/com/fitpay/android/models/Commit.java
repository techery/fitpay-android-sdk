package com.fitpay.android.models;


public class Commit {


    private String commitType;
    private Payload payload;
    private long createdTs;
    private String previousCommit;
    private String commit;

    public void setCommitType(String commitType) {
        this.commitType = commitType;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public void setCreatedTs(long createdTs) {
        this.createdTs = createdTs;
    }

    public void setPreviousCommit(String previousCommit) {
        this.previousCommit = previousCommit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getCommitType() {
        return commitType;
    }

    public Payload getPayload() {
        return payload;
    }

    public long getCreatedTs() {
        return createdTs;
    }

    public String getPreviousCommit() {
        return previousCommit;
    }

    public String getCommit() {
        return commit;
    }

    public static class Payload {
    }
}
