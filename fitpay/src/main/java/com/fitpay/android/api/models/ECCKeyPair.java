package com.fitpay.android.api.models;

import java.util.Date;

/**
 * Created by Vlad on 17.02.2016.
 */
public class ECCKeyPair {

    //createdTs:"2015-10-05T20:34:24.350+0000",

    private String keyId;
    private Date createdTs;
    private long createdTsEpoch;
    private String serverPublicKey;
    private String clientPublicKey;

    private transient String privateKey;

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

    public void refreshWithNewData(ECCKeyPair respKey){
        keyId = respKey.getKeyId();
        createdTs = respKey.getCreatedTs();
        createdTsEpoch = respKey.getCreatedTsEpoch();
        serverPublicKey = respKey.getServerPublicKey();
    }
}
