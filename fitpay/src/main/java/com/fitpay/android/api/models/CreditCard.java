package com.fitpay.android.api.models;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.IllegalFormatException;
import java.util.List;

public final class CreditCard extends BaseModel {

    public static final String INITIATOR_CARDHOLDER = "CARDHOLDER";
    public static final String INITIATOR_ISSUER = "ISSUER";

    @StringDef({INITIATOR_CARDHOLDER, INITIATOR_ISSUER})
    public @interface Initiator {
    }

    private String creditCardId;
    private String userId;
    @SerializedName("default")
    private Boolean defaultX;
    private String createdTs;
    private Long createdTsEpoch;
    private String lastModifiedTs;
    private Long lastModifiedTsEpoch;
    private String state;
    @Initiator
    private String causedBy;
    private String cardType;
    private CardMetaData cardMetaData;
    private String targetDeviceId;
    private String targetDeviceType;
    private String externalTokenReference;
    private List<VerificationMethod> verificationMethods;
    private List<Device> deviceRelationships;
    @SerializedName("encryptedData")
    private CreditCardInfo creditCardInfo;
    private String termsAssetId;
    private String eligibilityExpiration;
    private Long eligibilityExpirationEpoch;
    private List<AssetReference> termsAssetReferences;

    private CreditCard(){
        creditCardInfo = new CreditCardInfo();
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isDefault() {
        return defaultX;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public String getLastModifiedTs() {
        return lastModifiedTs;
    }

    public long getLastModifiedTsEpoch() {
        return lastModifiedTsEpoch;
    }

    public String getState() {
        return state;
    }

    public String getCausedBy() {
        return causedBy;
    }

    public String getCardType() {
        return cardType;
    }

    public CardMetaData getCardMetaData() {
        return cardMetaData;
    }

    public String getTargetDeviceId() {
        return targetDeviceId;
    }

    public String getTargetDeviceType() {
        return targetDeviceType;
    }

    public String getExternalTokenReference() {
        return externalTokenReference;
    }

    public List<VerificationMethod> getVerificationMethods() {
        return verificationMethods;
    }

    public List<Device> getDeviceRelationships() {
        return deviceRelationships;
    }

    public CreditCardInfo getCreditCardInfo() {
        return creditCardInfo;
    }

    public static final class CreditCardInfo {

        /**
         * description : Card holder name
         */
        private String name;

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
        private Integer expMonth;

        /**
         * description : The credit card expiration year
         */
        private Integer expYear;

        /**
         * description : Card holder address
         */
        private Address address;

        private CreditCardInfo() {
        }

        @Override
        public String toString(){
            return "CreditCardInfo";
        }
    }

    public static final class Builder{

        private String name;
        private String cvv;
        private String pan;
        private Integer expMonth;
        private Integer expYear;
        private Address address;

        /**
         * Creates a Builder instance that can be used to build Gson with various configuration
         * settings. Builder follows the builder pattern, and it is typically used by first
         * invoking various configuration methods to set desired options, and finally calling
         * {@link #create()}.
         */
        public Builder(){
        }

        /**
         * Creates a {@link CreditCard} instance based on the current configuration. This method is free of
         * side-effects to this {@code Builder} instance and hence can be called multiple times.
         *
         * @return an instance of {@link CreditCard} configured with the options currently set in this builder
         */
        public CreditCard create(){
            CreditCard card = new CreditCard();
            card.creditCardInfo.name = name;
            card.creditCardInfo.cvv = cvv;
            card.creditCardInfo.pan = pan;
            card.creditCardInfo.expYear = expYear;
            card.creditCardInfo.expMonth = expMonth;
            card.creditCardInfo.address = address;
            return card;
        }

        /**
         * Set card holder name
         * @param name card holder name
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setName(@NonNull String name){
            this.name = name;
            return this;
        }

        /**
         * Set credit card cvv2 code
         * @param cvv cards's cvv2 code. string with 3 digits only
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setCVV(@NonNull String cvv) throws IllegalFormatException{

            String pattern = "\\d{1,3}$";
            if(!cvv.matches(pattern)){
                throw new IllegalArgumentException("incorrect value");
            }

            this.cvv = cvv;
            return this;
        }

        /**
         * Set credit card primary account number (PAN)
         * @param pan cards's PAN. string with 16 digits only
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setPAN(@NonNull String pan) throws IllegalFormatException{

            String pattern = "\\d{1,16}$";
            if(!cvv.matches(pattern)){
                throw new IllegalArgumentException("incorrect value");
            }

            this.pan = pan;
            return this;
        }

        /**
         * Set credit card expiration date
         * @param expYear cards's expiration year
         * @param expMonth cards's expiration month
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setExpDate(int expYear, int expMonth) throws IllegalFormatException{

            Calendar calendar = Calendar.getInstance();
            if(expYear < calendar.get(Calendar.YEAR) && expMonth < calendar.get(Calendar.MONTH) + 1){
                throw new IllegalArgumentException("incorrect expiration date");
            }

            this.expYear = expYear;
            this.expMonth = expMonth;
            return this;
        }

        /**
         * Set card holder address
         * @param address card holder address
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setAddress(@NonNull Address address){
            this.address = address;
            return this;
        }
    }
}