package com.fitpay.android.models;


import java.util.List;

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

        private long createdTsEpoch;
        private String reason;
        private String cvv;
        private Address address;
        private String externalTokenReference;
        private String cardType;
        private String causedBy;
        private String creditCardId;
        private long lastModifiedTsEpoch;
        private String userId;
        private String createdTs;
        private String lastModifiedTs;
        private int expMonth;
        private String targetDeviceType;
        private int expYear;
        private String targetDeviceId;
        private String name;
        private String state;
        private String pan;
        private CardMetaData cardMetaData;
        private List<Relationship> deviceRelationships;

        public void setCreatedTsEpoch(long createdTsEpoch) {
            this.createdTsEpoch = createdTsEpoch;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public void setExternalTokenReference(String externalTokenReference) {
            this.externalTokenReference = externalTokenReference;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public void setCausedBy(String causedBy) {
            this.causedBy = causedBy;
        }

        public void setCreditCardId(String creditCardId) {
            this.creditCardId = creditCardId;
        }

        public void setLastModifiedTsEpoch(long lastModifiedTsEpoch) {
            this.lastModifiedTsEpoch = lastModifiedTsEpoch;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setCreatedTs(String createdTs) {
            this.createdTs = createdTs;
        }

        public void setLastModifiedTs(String lastModifiedTs) {
            this.lastModifiedTs = lastModifiedTs;
        }

        public void setExpMonth(int expMonth) {
            this.expMonth = expMonth;
        }

        public void setTargetDeviceType(String targetDeviceType) {
            this.targetDeviceType = targetDeviceType;
        }

        public void setExpYear(int expYear) {
            this.expYear = expYear;
        }

        public void setTargetDeviceId(String targetDeviceId) {
            this.targetDeviceId = targetDeviceId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setPan(String pan) {
            this.pan = pan;
        }

        public void setCardMetaData(CardMetaData cardMetaData) {
            this.cardMetaData = cardMetaData;
        }

        public void setDeviceRelationships(List<Relationship> deviceRelationships) {
            this.deviceRelationships = deviceRelationships;
        }

        public long getCreatedTsEpoch() {
            return createdTsEpoch;
        }

        public String getReason() {
            return reason;
        }

        public String getCvv() {
            return cvv;
        }

        public Address getAddress() {
            return address;
        }

        public String getExternalTokenReference() {
            return externalTokenReference;
        }

        public String getCardType() {
            return cardType;
        }

        public String getCausedBy() {
            return causedBy;
        }

        public String getCreditCardId() {
            return creditCardId;
        }

        public long getLastModifiedTsEpoch() {
            return lastModifiedTsEpoch;
        }

        public String getUserId() {
            return userId;
        }

        public String getCreatedTs() {
            return createdTs;
        }

        public String getLastModifiedTs() {
            return lastModifiedTs;
        }

        public int getExpMonth() {
            return expMonth;
        }

        public String getTargetDeviceType() {
            return targetDeviceType;
        }

        public int getExpYear() {
            return expYear;
        }

        public String getTargetDeviceId() {
            return targetDeviceId;
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }

        public String getPan() {
            return pan;
        }

        public CardMetaData getCardMetaData() {
            return cardMetaData;
        }

        public List<Relationship> getDeviceRelationships() {
            return deviceRelationships;
        }

    }

}