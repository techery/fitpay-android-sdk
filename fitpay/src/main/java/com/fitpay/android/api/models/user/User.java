package com.fitpay.android.api.models.user;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.utils.TimestampUtils;

import java.util.HashMap;
import java.util.Map;

public final class User extends UserModel {

    private static final String GET_DEVICES = "devices";
    private static final String GET_CARDS = "creditCards";


    /**
     * Delete user from your organization.
     *
     * @param callback result callback
     */
    public void deleteUser(@NonNull ApiCallback<Void> callback){
        makeDeleteCall(callback);
    }

    /**
     * Update the details of an existing user.
     *
     * @param user     user data to update: firstName, lastName, birthDate, originAccountCreatedTs, termsAcceptedTs, termsVersion
     * @param callback result callback
     */
    public void updateUser(@NonNull User user, @NonNull ApiCallback<User> callback){
        makePatchCall(user, true, User.class, callback);
    }

    /**
     * Retrieve a pagable collection of tokenized credit cards in their profile.
     *
     * @param limit    Max number of credit cards per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getCreditCards(int limit, int offset, @NonNull ApiCallback<Collections.CreditCardCollection> callback){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("limit", limit);
        queryMap.put("offset", offset);
        makeGetCall(GET_CARDS, queryMap, Collections.CreditCardCollection.class, callback);
    }

    /**
     * retrieve a pagable collection of devices in their profile.
     *
     * @param limit    Max number of devices per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getDevices(int limit, int offset, @NonNull ApiCallback<Collections.DeviceCollection> callback){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("limit", limit);
        queryMap.put("offset", offset);
        makeGetCall(GET_DEVICES, queryMap, Collections.DeviceCollection.class, callback);
    }

    /**
     * Add a single credit card to a user's profile.
     * If the card owner has no default card, then the new card will become the default.
     * However, if the owner already has a default then it will not change.
     * To change the default, you should update the user to have a new "default_source".
     *
     * @param creditCard credit card data:(pan, expMonth, expYear, cvv, name,
     *                   address data:(street1, street2, street3, city, state, postalCode, country))
     * @param callback   result callback
     */
    public void createCreditCard(@NonNull CreditCard creditCard, @NonNull ApiCallback<CreditCard> callback){
        makePostCall(GET_CARDS, creditCard, CreditCard.class, callback);
    }

    /**
     * Add a new device to a user's profile.
     *
     * @param device   device data to create:(deviceType, manufacturerName, deviceName, serialNumber,
     *                 modelNumber, hardwareRevision, firmwareRevision, softwareRevision, systemId,
     *                 osName, licenseKey, bdAddress, secureElementId, pairingTs)
     * @param callback result callback
     */
    public void createDevice(@NonNull Device device, @NonNull ApiCallback<Device> callback){
        makePostCall(GET_DEVICES, device, Device.class, callback);
    }

    public static final class Builder{

        private String firstName;
        private String lastName;
        private String birthDate;
        private long originAccountCreatedAtEpoch;
        private long termsAcceptedAtEpoch;
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
            user.originAccountCreatedTsEpoch = originAccountCreatedAtEpoch;
            user.termsAcceptedTsEpoch = termsAcceptedAtEpoch;
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
            this.originAccountCreatedAtEpoch = originAccountCreatedAt;
//            this.originAccountCreatedAt = TimestampUtils.getISO8601StringForTime(originAccountCreatedAt);
            return this;
        }

        /**
         * Set terms accepted time
         * @param termsAcceptedAt time in milliseconds
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setTermsAcceptedAt(long termsAcceptedAt) {
            this.termsAcceptedAtEpoch = termsAcceptedAt;
//            this.termsAcceptedAt = TimestampUtils.getISO8601StringForTime(termsAcceptedAt);
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