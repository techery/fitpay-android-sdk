package com.fitpay.android.api.models;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class CreditCard extends BaseModel {

    public static final String INITIATOR_CARDHOLDER = "CARDHOLDER";
    public static final String INITIATOR_ISSUER = "ISSUER";
    private String creditCardId;
    private String userId;
    @SerializedName("default")
    private boolean defaultX;
    private String createdTs;
    private long createdTsEpoch;
    private String lastModifiedTs;
    private long lastModifiedTsEpoch;
    private String state;
    private
    @Initiator
    String causedBy;
    private String cardType;
    private String termsAssetId;
    private String name;
    private String eligibilityExpiration;
    private long eligibilityExpirationEpoch;
    private List<AssetReference> termsAssetReferences;
    private List<Device> deviceRelationships;
    private CardMetaData cardMetaData;
    private List<VerificationMethod> verificationMethods;
    private String externalTokenReference;
    /**
     * description : JSON Web Encrypted compact serialization of the credit card's information from
     *
     * @see CreditCardInfo
     */
    private String encryptedData;

    public String getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDefaultX() {
        return defaultX;
    }

    public void setDefaultX(boolean defaultX) {
        this.defaultX = defaultX;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTermsAssetId() {
        return termsAssetId;
    }

    public void setTermsAssetId(String termsAssetId) {
        this.termsAssetId = termsAssetId;
    }

    public String getEligibilityExpiration() {
        return eligibilityExpiration;
    }

    public void setEligibilityExpiration(String eligibilityExpiration) {
        this.eligibilityExpiration = eligibilityExpiration;
    }

    public long getEligibilityExpirationEpoch() {
        return eligibilityExpirationEpoch;
    }

    public void setEligibilityExpirationEpoch(long eligibilityExpirationEpoch) {
        this.eligibilityExpirationEpoch = eligibilityExpirationEpoch;
    }

    public CardMetaData getCardMetaData() {
        return cardMetaData;
    }

    public void setCardMetaData(CardMetaData cardMetaData) {
        this.cardMetaData = cardMetaData;
    }

    public List<VerificationMethod> getVerificationMethods() {
        return verificationMethods;
    }

    public void setVerificationMethods(List<VerificationMethod> verificationMethods) {
        this.verificationMethods = verificationMethods;
    }

    public List<AssetReference> getTermsAssetReferences() {
        return termsAssetReferences;
    }

    public void setTermsAssetReferences(List<AssetReference> termsAssetReferences) {
        this.termsAssetReferences = termsAssetReferences;
    }

    public List<Device> getDeviceRelationships() {
        return deviceRelationships;
    }

    public void setDeviceRelationships(List<Device> deviceRelationships) {
        this.deviceRelationships = deviceRelationships;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Initiator
    public String getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(@Initiator String causedBy) {
        this.causedBy = causedBy;
    }

    public String getLastModifiedTs() {
        return lastModifiedTs;
    }

    public void setLastModifiedTs(String lastModifiedTs) {
        this.lastModifiedTs = lastModifiedTs;
    }

    public long getLastModifiedTsEpoch() {
        return lastModifiedTsEpoch;
    }

    public void setLastModifiedTsEpoch(long lastModifiedTsEpoch) {
        this.lastModifiedTsEpoch = lastModifiedTsEpoch;
    }

    public String getExternalTokenReference() {
        return externalTokenReference;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    @StringDef({INITIATOR_CARDHOLDER, INITIATOR_ISSUER})
    public @interface Initiator {
    }

    public static final class CreditCardInfo {

        /**
         * description : The credit card cvv2 code
         */
        private String cvv;

        /**
         * description : The credit card number, also known as a Primary Account Number (PAN)
         */
        private String pan;

        /**
         * description : The credit card expiration month
         */
        private int expMonth;

        /**
         * description : The credit card expiration year
         */
        private int expYear;

        private Address address;

        public CreditCardInfo() {
        }

        public String getPan() {
            return pan;
        }

        public void setPan(String pan) {
            this.pan = pan;
        }

        public int getExpMonth() {
            return expMonth;
        }

        public void setExpMonth(int expMonth) {
            this.expMonth = expMonth;
        }

        public int getExpYear() {
            return expYear;
        }

        public void setExpYear(int expYear) {
            this.expYear = expYear;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        @Override
        public String toString(){
            return "CreditCardInfo";
        }
    }
}