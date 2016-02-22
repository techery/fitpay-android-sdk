package com.fitpay.android.api;

import com.fitpay.android.api.models.ApduPackage;
import com.fitpay.android.api.models.Commit;
import com.fitpay.android.api.models.CreditCard;
import com.fitpay.android.api.models.Device;
import com.fitpay.android.api.models.ECCKeyPair;
import com.fitpay.android.api.models.OAuthToken;
import com.fitpay.android.api.models.Reason;
import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.ResultCollection;
import com.fitpay.android.api.models.Transaction;
import com.fitpay.android.api.models.User;
import com.fitpay.android.api.models.VerificationMethod;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

interface FitPayClient {

    /**
     * Login user and get auth token
     */
    @GET(BaseClient.BASE_URL + "oauth/authorize")
    Call<OAuthToken> loginUser(@QueryMap Map<String, String> options);

    /**
     * Returns a list of all users that belong to your organization.
     * The customers are returned sorted by creation date,
     * with the most recently created customers appearing first.
     *
     * @param limit Max number of profiles per page, default: 10
     * @param offset Start index position for list of entities returned
     */
    @GET("users")
    Call<ResultCollection<User>> getUsers(@Header("fp-api-key")String fpKey, @Query("limit") int limit, @Query("offset") int offset);

    /**
     * Creates a new user within your organization.
     *
     * @param user user data (firstName, lastName, birthDate, email)
     */
    @POST("users")
    Call<User> createUser(@Body User user);

    /**
     * Delete a single user from your organization.
     *
     * @param userId user id
     */
    @DELETE("users/{userId}")
    Call<Object> deleteUser(@Path("userId") String userId);

    /**
     * Update the details of an existing user.
     *
     * @param userId user id
     * @param user user data to update:(firstName, lastName, birthDate, originAccountCreatedTs, termsAcceptedTs, termsVersion)
     */
    @PATCH("users/{userId}")
    Call<User> updateUser(@Path("userId") String userId, @Body User user);

    /**
     * Retrieves the details of an existing user.
     * You need only supply the unique user identifier that was returned upon user creation.
     *
     * @param userId user id
     */
    @GET("users/{userId}")
    Call<User> getUser(@Path("userId") String userId);


    /**
     * Get a single relationship.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param deviceId device id
     */
    @GET("users/{userId}/relationships")
    Call<Relationship> getRelationship(@Path("userId") String userId,
                                       @Query("creditCardId") String creditCardId,
                                       @Query("deviceId") String deviceId);

    /**
     * Creates a relationship between a device and a creditCard.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param deviceId device id
     */
    @PUT("users/{userId}/relationships")
    Call<Relationship> createRelationship(@Path("userId") String userId,
                                          @Query("creditCardId") String creditCardId,
                                          @Query("deviceId") String deviceId);

    /**
     * Removes a relationship between a device and a creditCard if it exists.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param deviceId device id
     */
    @DELETE("users/{userId}/relationships")
    Call<Object> deleteRelationship(@Path("userId") String userId,
                                    @Query("creditCardId") String creditCardId,
                                    @Query("deviceId") String deviceId);



    /**
     * For a single user, retrieve a pagable collection of tokenized credit cards in their profile.
     *
     * @param userId user id
     * @param limit Max number of credit cards per page, default: 10
     * @param offset Start index position for list of entities returned
     */
    @GET("users/{userId}/creditCards")
    Call<ResultCollection<CreditCard>> getCreditCards(@Path("userId") String userId,
                                                      @Query("limit") int limit,
                                                      @Query("offset") int offset);

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
    @POST("users/{userId}/creditCards")
    Call<CreditCard> addCard(@Path("userId") String userId, @Body CreditCard creditCard);

    /**
     * Retrieves the details of an existing credit card.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    @GET("users/{userId}/creditCards/{creditCardId}")
    Call<CreditCard> getCreditCard(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

    /**
     * Update the details of an existing credit card.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param creditCard credit card data to update:(name (Card holder name), address/street1, address/street2,
     *                   address/city, address/state, address/postalCode, address/countryCode)
     */
    @PATCH("users/{userId}/creditCards/{creditCardId}")
    Call<CreditCard> updateCreditCard(@Path("userId") String userId,
                                      @Path("creditCardId") String creditCardId,
                                      @Body CreditCard creditCard);

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
    @DELETE("users/{userId}/creditCards/{creditCardId}")
    Call<Object> deleteCreditCard(@Path("userId") String userId, @Path("creditCardId") String creditCardId);


    /**
     * Indicate a user has accepted the terms and conditions presented
     * when the credit card was first added to the user's profile.
     * This link will only be available when the credit card is awaiting the user
     * to accept or decline the presented terms and conditions.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    @POST("users/{userId}/creditCards/{creditCardId}/acceptTerms")
    Call<CreditCard> acceptTerm(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

    /**
     * Indicate a user has declined the terms and conditions.
     * Once declined the credit card will be in a final state, no other actions may be taken.
     * This link will only be available when the credit card is awaiting the user to accept
     * or decline the presented terms and conditions.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    @POST("users/{userId}/creditCards/{creditCardId}/declineTerms")
    Call<CreditCard> declineTerms(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

    /**
     * Mark the credit card as the default payment instrument.
     * If another card is currently marked as the default,
     * the default will automatically transition to the indicated credit card.
     *
     * @param userId user id
     * @param creditCardId credit card id
     */
    @POST("users/{userId}/creditCards/{creditCardId}/makeDefault")
    Call<CreditCard> makeDefault(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

    /**
     * Transition the credit card into a deactivated state so that it may not be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in an active state.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param reason reason data:(causedBy, reason)
     */
    @POST("users/{userId}/creditCards/{creditCardId}/deactivate")
    Call<CreditCard> deactivate(@Path("userId") String userId,
                                @Path("creditCardId") String creditCardId,
                                @Body Reason reason);

    /**
     * Transition the credit card into an active state where it can be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in a deactivated state.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param reason reason data:(causedBy, reason)
     */
    @POST("users/{userId}/creditCards/{creditCardId}/reactivate")
    Call<CreditCard> reactivate(@Path("userId") String userId,
                                @Path("creditCardId") String creditCardId,
                                @Body Reason reason);

    /**
     * When an issuer requires additional authentication to verify the identity of the cardholder,
     * this indicates the user has selected the specified verification method by the indicated verificationTypeId.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param verificationTypeId verification type id
     */
    @POST("users/{userId}/creditCards/{creditCardId}/verificationMethods/{verificationTypeId}/select")
    Call<VerificationMethod> selectVerificationType(@Path("userId") String userId,
                                              @Path("creditCardId") String creditCardId,
                                              @Path("verificationTypeId") String verificationTypeId);

    /**
     * If a verification method is selected that requires an entry of a pin code, this transition will be available.
     * Not all verification methods will include a secondary verification step through the FitPay API.
     *
     * @param userId user id
     * @param creditCardId credit card id
     * @param verificationTypeId verification type id
     * @param verificationCode verification code
     */
    @POST("users/{userId}/creditCards/{creditCardId}/verificationMethods/{verificationTypeId}/verify")
    Call<VerificationMethod> verify(@Path("userId") String userId,
                              @Path("creditCardId") String creditCardId,
                              @Path("verificationTypeId") String verificationTypeId,
                              @Body String verificationCode);


    /**
     * For a single user, retrieve a pagable collection of devices in their profile.
     *
     * @param userId user id
     * @param limit Max number of devices per page, default: 10
     * @param offset Start index position for list of entities returned
     */
    @GET("users/{userId}/devices")
    Call<ResultCollection<Device>> getDevices(@Path("userId") String userId,
                                       @Query("limit") int limit,
                                       @Query("offset") int offset);

    /**
     * For a single user, create a new device in their profile.
     *
     * @param userId user id
     * @param device device data to create:(deviceType, manufacturerName, deviceName, serialNumber,
     *               modelNumber, hardwareRevision, firmwareRevision, softwareRevision, systemId,
     *               osName, licenseKey, bdAddress, secureElementId, pairingTs)
     */
    @POST("users/{userId}/devices")
    Call<Device> createDevice(@Path("userId") String userId, @Body Device device);

    /**
     * Retrieves the details of an existing device.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId user id
     * @param deviceId device id
     */
    @GET("users/{userId}/devices/{deviceId}")
    Call<Device> getDevice(@Path("userId") String userId, @Path("deviceId") String deviceId);

    /**
     * Update the details of an existing device.
     *
     * @param userId user id
     * @param deviceId device id
     * @param deviceData device data:(firmwareRevision, softwareRevision)
     */
    @PATCH("users/{userId}/devices/{deviceId}")
    Call<Device> updateDevice(@Path("userId") String userId,
                              @Path("deviceId") String deviceId,
                              @Body Device deviceData);

    /**
     * Delete a single device.
     *
     * @param userId user id
     * @param deviceId device id
     */
    @DELETE("users/{userId}/devices/{deviceId}")
    Call<Object> deleteDevice(@Path("userId") String userId, @Path("deviceId") String deviceId);


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
    @GET("users/{userId}/devices/{deviceId}/commits")
    Call<ResultCollection<Commit>> getCommits(@Path("userId") String userId,
                                              @Path("deviceId") String deviceId,
                                              @Query("commitsAfter") String commitsAfter,
                                              @Query("limit") int limit,
                                              @Query("offset") int offset);

    /**
     * Retrieves an individual commit.
     *
     * @param userId user id
     * @param deviceId device id
     * @param commitId commit id
     */
    @GET("users/{userId}/devices/{deviceId}/commits/{commitId}")
    Call<Commit> getCommit(@Path("userId") String userId,
                           @Path("deviceId") String deviceId,
                           @Path("commitId") String commitId);

    /**
     * Get all transactions.
     *
     * @param userId user id
     * @param limit Max number of transactions per page, default: 10
     * @param offset Start index position for list of entities returned
     * */
    @GET("users/{userId}/transactions")
    Call<ResultCollection<Transaction>> getTransactions(@Path("userId") String userId,
                                                        @Query("limit") int limit,
                                                        @Query("offset") int offset);

    /**
     * Get a single transaction.
     *
     * @param userId user id
     * @param transactionId transaction id
     * */
    @GET("users/{userId}/transactions/{transactionId}")
    Call<Transaction> getTransaction(@Path("userId") String userId, @Path("transactionId") String transactionId);


    /**
     * Endpoint to allow for returning responses to APDU execution.
     *
     * @param packageId package id
     * @param apduPackage package confirmation data:(packageId, state, executedTs,
     *                            executedDuration, apduResponses:(commandId, commandId, responseData))
     * */
    @POST("apduPackages/{packageId}/confirm")
    Call<Object> confirmAPDUPackage(@Path("packageId") String packageId, @Body ApduPackage apduPackage);


    /**
     * Retrieve an individual asset (i.e. terms and conditions)
     *
     * @param adapterData adapter data
     * @param adapterId adapter id
     * @param assetId asset id
     * */
    @GET("assets")
    Call<Object> getAssets(@Query("adapterData") String adapterData,
                          @Query("adapterId") String adapterId,
                          @Query("assetId") String assetId);


    /**
     * Creates a new encryption key pair
     *
     * @param clientPublicKey client public key
     * */
    @POST("config/encryptionKeys")
    Call<ECCKeyPair> createEncryptionKey(@Body ECCKeyPair clientPublicKey);


    /**
     * Retrieve and individual key pair.
     *
     * @param keyId key id
     * */
    @GET("config/encryptionKeys/{keyId}")
    Call<ECCKeyPair> getEncryptionKey(@Query("keyId") String keyId);

    /**
     * Delete and individual key pair.
     *
     * @param keyId key id
     * */
    @DELETE("config/encryptionKeys/{keyId}")
    Call<Void> deleteEncryptionKey(@Query("keyId") String keyId);


    /**
     * //TODO: add description when it becomes available on API documentation page
     *
     * */
    @GET("config/webhook")
    Call<Object> getWebhook();

    /**
     * Sets the webhook endpoint you would like FitPay to send notifications to, must be a valid URL.
     *
     * @param webhookURL webhook URL
     * */
    @PUT("config/webhook")
    Call<Object> setWebhook(@Body String webhookURL);

    /**
     * Removes the current webhook endpoint, unsubscribing you from all Fitpay notifications.
     *
     * @param webhookURL webhook URL
     * */
    @DELETE("config/webhook")
    Call<Object> removeWebhook(@Body String webhookURL);

}