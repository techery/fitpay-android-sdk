package com.fitpay.android.models;


public class User{

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

    /**
     * description : JSON Web Encrypted compact serialization of the user's information
     * $ref : #definitions/user
     */
    private String encryptedData;

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


    private String id;
    private long termsAcceptedTsEpoch;
    private String createdTs;
    private long createdTsEpoch;
    private long originAccountCreatedTsEpoch;


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTermsVersion(String termsVersion) {
        this.termsVersion = termsVersion;
    }

    public void setTermsAcceptedTs(String termsAcceptedTs) {
        this.termsAcceptedTs = termsAcceptedTs;
    }

    public void setTermsAcceptedTsEpoch(long termsAcceptedTsEpoch) {
        this.termsAcceptedTsEpoch = termsAcceptedTsEpoch;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public void setCreatedTsEpoch(long createdTsEpoch) {
        this.createdTsEpoch = createdTsEpoch;
    }

    public void setOriginAccountCreatedTs(String originAccountCreatedTs) {
        this.originAccountCreatedTs = originAccountCreatedTs;
    }

    public void setOriginAccountCreatedTsEpoch(long originAccountCreatedTsEpoch) {
        this.originAccountCreatedTsEpoch = originAccountCreatedTsEpoch;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
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

    public String getEncryptedData() {
        return encryptedData;
    }
}