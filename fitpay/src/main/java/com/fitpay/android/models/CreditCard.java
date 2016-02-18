package com.fitpay.android.models;

import com.fitpay.android.constants.CreditCardInitiator;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditCard {

    private String creditCardId;
    private String userId;
    @SerializedName("default")
    private boolean defaultX;
    private String createdTs;
    private long createdTsEpoch;
    private String state;
    private CreditCardInitiator causedBy;
    private String cardType;
    private String termsAssetId;
    private String pan;
    private int expMonth;
    private int expYear;
    private String cvv;
    private String name;
    private String eligibilityExpiration;
    private long eligibilityExpirationEpoch;
    private List<?> termsAssetReferences;
    private List<Relationship> deviceRelationships;
    private Address address;
    private CardMetaData cardMetaData;
    private List<VerificationMethod> verificationMethods;

    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDefaultX(boolean defaultX) {
        this.defaultX = defaultX;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setTermsAssetId(String termsAssetId) {
        this.termsAssetId = termsAssetId;
    }

    public void setEligibilityExpiration(String eligibilityExpiration) {
        this.eligibilityExpiration = eligibilityExpiration;
    }

    public void setEligibilityExpirationEpoch(long eligibilityExpirationEpoch) {
        this.eligibilityExpirationEpoch = eligibilityExpirationEpoch;
    }

    public void setTermsAssetReferences(List<?> termsAssetReferences) {
        this.termsAssetReferences = termsAssetReferences;
    }

    public void setDeviceRelationships(List<Relationship> deviceRelationships) {
        this.deviceRelationships = deviceRelationships;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setCardMetaData(CardMetaData cardMetaData) {
        this.cardMetaData = cardMetaData;
    }

    public void setVerificationMethods(List<VerificationMethod> verificationMethods) {
        this.verificationMethods = verificationMethods;
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isDefaultX() {
        return defaultX;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public String getState() {
        return state;
    }

    public String getCardType() {
        return cardType;
    }

    public String getTermsAssetId() {
        return termsAssetId;
    }

    public String getPan() {
        return pan;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public String getCvv() {
        return cvv;
    }

    public String getEligibilityExpiration() {
        return eligibilityExpiration;
    }

    public long getEligibilityExpirationEpoch() {
        return eligibilityExpirationEpoch;
    }

    public Address getAddress() {
        return address;
    }

    public CardMetaData getCardMetaData() {
        return cardMetaData;
    }

    public List<VerificationMethod> getVerificationMethods() {
        return verificationMethods;
    }

    public List<?> getTermsAssetReferences() {
        return termsAssetReferences;
    }

    public List<Relationship> getDeviceRelationships() {
        return deviceRelationships;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public CreditCardInitiator getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(CreditCardInitiator causedBy) {
        this.causedBy = causedBy;
    }
}