package com.fitpay.android.models;

public class EncryptionKey {


    private String keyId;
    private String createdTs;
    private long createdTsEpoch;
    private String serverPublicKey;
    private String clientPublicKey;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public void setServerPublicKey(String serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }

    public String getClientPublicKey() {
        return clientPublicKey;
    }
}
