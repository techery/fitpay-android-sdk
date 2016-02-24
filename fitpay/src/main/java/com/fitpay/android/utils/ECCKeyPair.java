package com.fitpay.android.utils;

import java.util.Date;

import javax.crypto.SecretKey;

/**
 * Created by Vlad on 17.02.2016.
 */
class ECCKeyPair {

    private String keyId;
    private Date createdTs;
    private long createdTsEpoch;
    private String serverPublicKey;
    private String clientPublicKey;

    private transient String privateKey;
    private transient SecretKey secretKey = null;

    public ECCKeyPair(){}

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public Date getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(Date createdTs) {
        this.createdTs = createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }

    public String getPublicKey() {
        return clientPublicKey;
    }

    public void setPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString(){
        return "ECCKeyPair";
    }
}
