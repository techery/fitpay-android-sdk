package com.fitpay.android.api.models.user;

import com.fitpay.android.api.models.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * User model
 */
abstract class UserModel extends BaseModel {

    protected String id;

    /**
     * JSON Web Encrypted compact serialization of the user's information from
     * @see UserInfo
     */
    @SerializedName("encryptedData")
    protected UserInfo userInfo;

    /**
     * ISO8601 string providing the date of the creation of original user account.   If not known use the current date
     */
    protected String originAccountCreatedTs;

    /**
     * ISO8601 string providing the date that the FitPay terms and conditions were accepted.
     */
    protected String termsAcceptedTs;

    /**
     * The version of the FitPay terms and conditions that were accepted
     */
    protected String termsVersion;

    protected String createdTs;
    protected Long createdTsEpoch;
    protected Long termsAcceptedTsEpoch;
    protected Long originAccountCreatedTsEpoch;

    protected UserModel() {
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
}
