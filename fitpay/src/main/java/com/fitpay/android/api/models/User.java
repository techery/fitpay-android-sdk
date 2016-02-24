package com.fitpay.android.api.models;

import com.google.gson.annotations.SerializedName;

public final class User extends BaseModel {

    private String id;

    /**
     * description : JSON Web Encrypted compact serialization of the user's information from
     * @see UserInfo
     */

    @SerializedName("encryptedData")
    private UserInfo userInfo;

    /**
     * description : ISO8601 string providing the date of the creation of original user account.   If not known use the current date
     */
    private String originAccountCreatedTs;

    /**
     * description : ISO8601 string providing the date that the FitPay terms and conditions were accepted.
     */
    private String termsAcceptedTs;

    /**
     * description : The version of the FitPay terms and conditions that were accepted
     */
    private String termsVersion;

    private String createdTs;
    private long createdTsEpoch;
    private long termsAcceptedTsEpoch;
    private long originAccountCreatedTsEpoch;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTermsVersion() {
        return termsVersion;
    }

    public void setTermsVersion(String termsVersion) {
        this.termsVersion = termsVersion;
    }

    public String getTermsAcceptedTs() {
        return termsAcceptedTs;
    }

    public void setTermsAcceptedTs(String termsAcceptedTs) {
        this.termsAcceptedTs = termsAcceptedTs;
    }

    public long getTermsAcceptedTsEpoch() {
        return termsAcceptedTsEpoch;
    }

    public void setTermsAcceptedTsEpoch(long termsAcceptedTsEpoch) {
        this.termsAcceptedTsEpoch = termsAcceptedTsEpoch;
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

    public String getOriginAccountCreatedTs() {
        return originAccountCreatedTs;
    }

    public void setOriginAccountCreatedTs(String originAccountCreatedTs) {
        this.originAccountCreatedTs = originAccountCreatedTs;
    }

    public long getOriginAccountCreatedTsEpoch() {
        return originAccountCreatedTsEpoch;
    }

    public void setOriginAccountCreatedTsEpoch(long originAccountCreatedTsEpoch) {
        this.originAccountCreatedTsEpoch = originAccountCreatedTsEpoch;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public static final class UserInfo {

        /**
         * description : The user's username
         */
        private String username;

        /**
         * description : The user's first name
         */
        private String firstName;

        /**
         * description : The user's last name
         */
        private String lastName;

        /**
         * description : The user's birthdate in YYYY-MM-DD format
         */
        private String birthDate;

        /**
         * description : The user's email address, formatted as {string}@{domain}.{extension}
         */
        private String email;

        public UserInfo(){
        }

        public String getUsername(){
            return username;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString(){
            return "UserInfo";
        }
    }
}