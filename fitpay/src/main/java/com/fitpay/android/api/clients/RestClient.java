package com.fitpay.android.api.clients;

import com.fitpay.android.api.models.ApduPackage;
import com.fitpay.android.api.models.CreditCard;
import com.fitpay.android.api.models.Device;
import com.fitpay.android.api.models.ECCKeyPair;
import com.fitpay.android.api.models.Reason;
import com.fitpay.android.api.models.User;

import java.util.Map;


public interface RestClient {



    public void loginUser(Map<String, String> options);

    /**
     * Returns a list of all users that belong to your organization.
     * The customers are returned sorted by creation date,
     * with the most recently created customers appearing first.
     *
     * @param limit Max number of profiles per page, default: 10
     * @param offset Start index position for list of entities returned
     */
    public void getUsers(int limit, int offset);

    /**
     * Creates a new user within your organization.
     *
     * @param user user data (firstName, lastName, birthDate, email)
     */
    public void createUser(User user);

    /**
     * Delete a single user from your organization.
     *
     * @param userId user id
     */
    public void deleteUser(String userId);

    /**
     * Update the details of an existing user.
     *
     * @param userId user id
     * @param user user data to update:(firstName, lastName, birthDate, originAccountCreatedTs, termsAcceptedTs, termsVersion)
     */
    public void updateUser(String userId, User user);

    /**
     * Retrieves the details of an existing user.
     * You need only supply the unique user identifier that was returned upon user creation.
     *
     * @param userId user id
     */
    public void getUser(String userId);


    /**
     * Get a single relationship.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param deviceId device id
     */
    public void getRelationship(String userId, String creditCardId, String deviceId);



    /**
     * Creates a relationship between a device and a creditCard.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param deviceId device id
     */
    public void createRelationship(String userId, String creditCardId, String deviceId);

    /**
     * Removes a relationship between a device and a creditCard if it exists.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param deviceId device id
     */
    public void deleteRelationship(String userId, String creditCardId, String deviceId);



    /**
     * For a single user, retrieve a pagable collection of tokenized credit cards in their profile.
     *
     * @param userId user id
     * @param limit Max number of credit cards per page, default: 10
     * @param offset Start index position for list of entities returned
     */
    public void getCreditCards(String userId, int limit, int offset);

    /**
     * Add a single credit card to a user's profile.
     * If the card owner has no default card, then the new card will become the default.
     * However, if the owner already has a default then it will not change.
     * To change the default, you should update the user to have a new "default_source".
     *
     * @param userId user id
     * @param creditCard credit card data:(pan, expMonth, expYear, cvv, name,
     *                   address data:(street1, street2, street3, city, state, postalCode, country))
     */
    public void createCreditCard(String userId, CreditCard creditCard);

    /**
     * Retrieves the details of an existing credit card.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    public void getCreditCard(String userId, String creditCardId);

    /**
     * Update the details of an existing credit card.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param creditCard credit card data to update:(name (Card holder name), address/street1, address/street2,
     *                   address/city, address/state, address/postalCode, address/countryCode)
     */
    public void updateCreditCard(String userId, String creditCardId, CreditCard creditCard);

    /**
     * Delete a single credit card from a user's profile.
     * If you delete a card that is currently the default source,
     * then the most recently added source will become the new default.
     * If you delete a card that is the last remaining source on the customer
     * then the default_source attribute will become null.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    public void deleteCreditCard(String userId, String creditCardId);


    /**
     * Indicate a user has accepted the terms and conditions presented
     * when the credit card was first added to the user's profile.
     * This link will only be available when the credit card is awaiting the user
     * to accept or decline the presented terms and conditions.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    public void acceptTerm(String userId, String creditCardId);

    /**
     * Indicate a user has declined the terms and conditions.
     * Once declined the credit card will be in a final state, no other actions may be taken.
     * This link will only be available when the credit card is awaiting the user to accept
     * or decline the presented terms and conditions.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    public void declineTerms(String userId, String creditCardId);

    /**
     * Mark the credit card as the default payment instrument.
     * If another card is currently marked as the default,
     * the default will automatically transition to the indicated credit card.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    public void makeDefault(String userId, String creditCardId);

    /**
     * Transition the credit card into a deactivated state so that it may not be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in an active state.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param reason reason data:(causedBy, reason)
     */
    public void deactivate(String userId, String creditCardId, Reason reason);

    /**
     * Transition the credit card into an active state where it can be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in a deactivated state.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param reason reason data:(causedBy, reason)
     */
    public void reactivate(String userId, String creditCardId, Reason reason);

    /**
     * When an issuer requires additional authentication to verify the identity of the cardholder,
     * this indicates the user has selected the specified verification method by the indicated verificationTypeId.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param verificationTypeId verification type id
     */
    public void selectVerificationType(String userId, String creditCardId, String verificationTypeId);

    /**
     * If a verification method is selected that requires an entry of a pin code, this transition will be available.
     * Not all verification methods will include a secondary verification step through the FitPay API.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param verificationTypeId verification type id
     * @param verificationCode verification code
     */
    public void verify(String userId, String creditCardId, String verificationTypeId, String verificationCode);


    /**
     * For a single user, retrieve a pagable collection of devices in their profile.
     *
     * @param userId user id
     * @param limit Max number of devices per page, default: 10
     * @param offset Start index position for list of entities returned
     */
    public void getDevices(String userId, int limit, int offset);

    /**
     * For a single user, create a new device in their profile.
     *
     * @param userId user id
     * @param device device data to create:(deviceType, manufacturerName, deviceName, serialNumber,
     *               modelNumber, hardwareRevision, firmwareRevision, softwareRevision, systemId,
     *               osName, licenseKey, bdAddress, secureElementId, pairingTs)
     */
    public void createDevice(String userId, Device device);

    /**
     * Retrieves the details of an existing device.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId user id
     * @param deviceId device id
     */
    public void getDevice(String userId, String deviceId);

    /**
     * Update the details of an existing device.
     *
     * @param userId user id
     * @param deviceId device id
     * @param deviceData device data:(firmwareRevision, softwareRevision)
     */
    public void updateDevice(String userId, String deviceId, Device deviceData);

    /**
     * Delete a single device.
     *
     * @param userId user id
     * @param deviceId device id
     */
    public void deleteDevice(String userId, String deviceId);


    /**
     * Retrieves a collection of all events that should be committed to this device.
     *
     * @param userId user id
     * @param deviceId device id
     * @param limit Max number of events per page, default: 10
     * @param offset Start index position for list of entities returned
     * @param commitsAfter The last commit successfully applied.
     *                     Query will return all subsequent commits which need to be applied.
     */
    public void getCommits(String userId, String deviceId, String commitsAfter, int limit, int offset);

    /**
     * Retrieves an individual commit.
     *
     * @param userId user id
     * @param deviceId device id
     * @param commitId commit id
     */
    public void getCommit(String userId, String deviceId, String commitId);

    /**
     * Get all transactions.
     *
     * @param userId user id
     * @param limit Max number of transactions per page, default: 10
     * @param offset Start index position for list of entities returned
     * */
    public void getTransactions(String userId, int limit, int offset);

    /**
     * Get a single transaction.
     *
     * @param userId user id
     * @param transactionId transaction id
     * */
    public void getTransaction(String userId, String transactionId);


    /**
     * Endpoint to allow for returning responses to APDU execution.
     *
     * @param packageId package id
     * @param apduPackage package confirmation data:(packageId, state, executedTs,
     *                            executedDuration, apduResponses:(commandId, commandId, responseData))
     * */
    public void confirmAPDUPackage(String packageId, ApduPackage apduPackage);


    /**
     * Retrieve an individual asset (i.e. terms and conditions)
     *
     * @param adapterData adapter data
     * @param adapterId adapter id
     * @param assetId asset id
     * */
    public void getAssets(String adapterData, String adapterId, String assetId);


    /**
     * Creates a new encryption key pair
     *
     * @param clientPublicKey client public key
     * */
    public void createEncryptionKey(ECCKeyPair clientPublicKey);


    /**
     * Retrieve and individual key pair.
     *
     * @param keyId key id
     * */
    public void getEncryptionKey(String keyId);

    /**
     * Delete and individual key pair.
     *
     * @param keyId key id
     * */
    public void deleteEncryptionKey(String keyId);


}