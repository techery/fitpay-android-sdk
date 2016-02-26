package com.fitpay.android.api.models;

import android.support.annotation.NonNull;

import com.fitpay.android.utils.TimestampUtils;
import com.google.gson.annotations.SerializedName;

public final class User extends BaseModel {

    private String id;

    /**
     * description : JSON Web Encrypted compact serialization of the user's information from
     *
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
    private Long createdTsEpoch;
    private Long termsAcceptedTsEpoch;
    private Long originAccountCreatedTsEpoch;

    private User() {
        userInfo = new UserInfo();
    }

    public String getId() {
        return id;
    }

    public String getTermsVersion() {
        return termsVersion;
    }

    public String getTermsAcceptedTs() {
        return termsAcceptedTs;
    }

    public long getTermsAcceptedTsEpoch() {
        return termsAcceptedTsEpoch;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public long getCreatedTsEpoch() {
        return createdTsEpoch;
    }

    public String getOriginAccountCreatedTs() {
        return originAccountCreatedTs;
    }

    public long getOriginAccountCreatedTsEpoch() {
        return originAccountCreatedTsEpoch;
    }

    public String getUsername() {
        return userInfo.username;
    }

    public String getFirstName() {
        return userInfo.firstName;
    }

    public String getLastName() {
        return userInfo.lastName;
    }

    public String getBirthDate() {
        return userInfo.birthDate;
    }

    public String getEmail() {
        return userInfo.email;
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

        private UserInfo() {
        }

        @Override
        public String toString() {
            return "UserInfo";
        }
    }

    public static final class Builder{

        private String firstName;
        private String lastName;
        private String birthDate;
        private String originAccountCreatedAt;
        private String termsAcceptedAt;
        private String termsVersion;

        /**
         * Creates a Builder instance that can be used to build Gson with various configuration
         * settings. Builder follows the builder pattern, and it is typically used by first
         * invoking various configuration methods to set desired options, and finally calling
         * {@link #create()}.
         */
        public Builder(){
        }

        /**
         * Creates a {@link User} instance based on the current configuration. This method is free of
         * side-effects to this {@code Builder} instance and hence can be called multiple times.
         *
         * @return an instance of User configured with the options currently set in this builder
         */
        public User create(){
            User user = new User();
            user.userInfo.firstName = firstName;
            user.userInfo.lastName = lastName;
            user.userInfo.birthDate = birthDate;
            user.originAccountCreatedTs = originAccountCreatedAt;
            user.termsAcceptedTs = termsAcceptedAt;
            user.termsVersion = termsVersion;
            return user;
        }

        /**
         * Set first name
         * @param firstName the user's first name
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setFirstName(@NonNull String firstName){
            this.firstName = firstName;
            return this;
        }

        /**
         * Set last name
         * @param lastName the user's last name
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setLastName(@NonNull String lastName){
            this.lastName = lastName;
            return this;
        }

        /**
         * Set birthdate
         * @param date time in milliseconds
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setBirthDate(long date){
            this.birthDate = TimestampUtils.getReadableDate(date);
            return this;
        }

        /**
         * Set account creation time
         * @param originAccountCreatedAt time in milliseconds
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setOriginAccountCreatedAt(long originAccountCreatedAt) {
            this.originAccountCreatedAt = TimestampUtils.getISO8601StringForTime(originAccountCreatedAt);
            return this;
        }

        /**
         * Set terms accepted time
         * @param termsAcceptedAt time in milliseconds
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setTermsAcceptedAt(long termsAcceptedAt) {
            this.termsAcceptedAt = TimestampUtils.getISO8601StringForTime(termsAcceptedAt);
            return this;
        }

        /**
         * Set terms version
         * @param termsVersion version name
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setTermsVersion(@NonNull String termsVersion) {
            this.termsVersion = termsVersion;
            return this;
        }
    }
}