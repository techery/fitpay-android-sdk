package com.fitpay.android.models;


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
    private String causedBy;
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


    public static class Address {

        /**
         * description : The billing address street name and number
         */
        private String street1;

        /**
         * description : The billing address unit or suite number, if available
         */
        private String street2;

        /**
         * description : Additional billing address unit or suite number, if available
         */
        private String street3;

        /**
         * description : The billing address city
         */
        private String city;

        /**
         * description : The billing address state
         */
        private String state;

        /**
         * description : The billing address five-digit zip code
         */
        private String postalCode;

        /**
         * description : The billing address country
         */
        private String country;

        /**
         * description : The billing address country code
         */
        private String countryCode;


        public void setStreet1(String street1) {
            this.street1 = street1;
        }

        public void setStreet2(String street2) {
            this.street2 = street2;
        }

        public void setStreet3(String street3) {
            this.street3 = street3;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getStreet1() {
            return street1;
        }

        public String getStreet2() {
            return street2;
        }

        public String getStreet3() {
            return street3;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public String getCountry() {
            return country;
        }
    }
}