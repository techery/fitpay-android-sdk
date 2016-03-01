package com.fitpay.android.utils;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.callbacks.CallbackWrapper;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.ApduPackage;
import com.fitpay.android.api.models.Commit;
import com.fitpay.android.api.models.CreditCard;
import com.fitpay.android.api.models.Device;
import com.fitpay.android.api.models.Reason;
import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.ResultCollection;
import com.fitpay.android.api.models.Transaction;
import com.fitpay.android.api.models.User;
import com.fitpay.android.api.models.VerificationMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/*
 * Created by andrews on 22.02.16.
 */
public class ApiManager {

    private static ApiManager sInstance;
    private FitPayService apiService;

    private ApiManager() {
        apiService = new FitPayService();
    }

    public static ApiManager getInstance() {
        if (sInstance == null) {
            synchronized (ApiManager.class) {
                if (sInstance == null) {
                    sInstance = new ApiManager();
                }
            }
        }

        return sInstance;
    }

    FitPayClient getClient() {
        return apiService.getClient();
    }

    private boolean isAuthorized(@NonNull ApiCallback callback) {
        if (!apiService.isAuthorized()) {
            callback.onFailure(ResultCode.UNAUTHORIZED, "Unauthorized");

            return false;
        }

        return true;
    }

    private void checkKeyAndMakeCall(@NonNull Runnable successRunnable, @NonNull ApiCallback callback) {
        if (KeysManager.getInstance().getKeyId(KeysManager.KEY_API) == null) {
            KeysManager.getInstance().updateECCKey(KeysManager.KEY_API, successRunnable, callback);
        } else {
            successRunnable.run();
        }
    }

    /**
     * User Login
     *
     * @param login    login
     * @param password password
     * @param callback result callback
     */
    public void loginUser(String login, String password, final ApiCallback<Void> callback) {

        String apiUrl = Constants.BASE_URL;
        String data = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", login, password);

        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("response_type", "token");
        loginMap.put("client_id", "pagare");
        loginMap.put("redirect_uri", apiUrl.substring(0, apiUrl.length() - 1));
        loginMap.put("credentials", data);

        CallbackWrapper<OAuthToken> getTokenCallback = new CallbackWrapper<>(new ApiCallback<OAuthToken>() {
            @Override
            public void onSuccess(OAuthToken result) {
                apiService.updateToken(result);
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });

        Call<OAuthToken> getTokenCall = getClient().loginUser(loginMap);
        getTokenCall.enqueue(getTokenCallback);
    }

//    /**
//     * Returns a list of all users that belong to your organization.
//     * The customers are returned sorted by creation date,
//     * with the most recently created customers appearing first.
//     *
//     * @param limit    Max number of profiles per page, default: 10
//     * @param offset   Start index position for list of entities returned
//     * @param callback result callback
//     */
//    public void getUsers(int limit, int offset, ApiCallback<ResultCollection<User>> callback) {
//    }

//    /**
//     * Creates a new user within your organization.
//     *
//     * @param user     user data (firstName, lastName, birthDate, email)
//     * @param callback result callback
//     */
//    public void createUser(User user, ApiCallback<User> callback) {
//    }

    /**
     * Delete a single user from your organization.
     *
     * @param userId   user id
     * @param callback result callback
     */
    public void deleteUser(String userId, ApiCallback<Void> callback) {
        if(isAuthorized(callback)){
            Call<Void> deleteUserCall = getClient().deleteUser(userId);
            deleteUserCall.enqueue(new CallbackWrapper<>(callback));
        }
    }

    /**
     * Update the details of an existing user.
     *
     * @param userId   user id
     * @param user     user data to update:(firstName, lastName, birthDate, originAccountCreatedTs,
     *                 termsAcceptedTs, termsVersion)
     * @param callback result callback
     */
    public void updateUser(final String userId, final User user, final ApiCallback<User> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    JsonArray updateData = new JsonArray();

                    Map<String, Object> userMap = new ModelAdapter.ObjectConverter().convertToSimpleMap(user);
                    for(Map.Entry<String, Object> entry : userMap.entrySet()) {
                        JsonObject item = new JsonObject();
                        item.addProperty("op", "replace");
                        item.addProperty("path", entry.getKey());
                        item.addProperty("value", String.valueOf(entry.getValue()));

                        updateData.add(item);
                    }

                    String userString = updateData.toString();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("encryptedData", StringUtils.getEncryptedString(KeysManager.KEY_API, userString));

                    Call<User> updateUserCall = getClient().updateUser(userId, jsonObject);
                    updateUserCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Retrieves the details of an existing user.
     * You need only supply the unique user identifier that was returned upon user creation.
     *
     * @param callback result callback
     */
    public void getUser(final ApiCallback<User> callback) {
        if (isAuthorized(callback)) {

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {
                    Call<User> getUserCall = getClient().getUser(KeysManager.getInstance().getKeyId(KeysManager.KEY_API), apiService.getUserId());
                    getUserCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }


    /**
     * Get a single relationship.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param deviceId     device id
     * @param callback     result callback
     */
    public void getRelationship(String userId, String creditCardId, String deviceId, ApiCallback<Relationship> callback) {
        if(isAuthorized(callback)){
            Call<Relationship> getRelationshipCall = getClient().getRelationship(userId, creditCardId, deviceId);
            getRelationshipCall.enqueue(new CallbackWrapper<>(callback));
        }
    }


    /**
     * Creates a relationship between a device and a creditCard.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param deviceId     device id
     * @param callback     result callback
     */
    public void createRelationship(String userId, String creditCardId, String deviceId, ApiCallback<Relationship> callback) {
        if(isAuthorized(callback)){
            Call<Relationship> createRelationshipCall = getClient().createRelationship(userId, creditCardId, deviceId);
            createRelationshipCall.enqueue(new CallbackWrapper<>(callback));
        }
    }

    /**
     * Removes a relationship between a device and a creditCard if it exists.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param deviceId     device id
     * @param callback     result callback
     */
    public void deleteRelationship(String userId, String creditCardId, String deviceId, ApiCallback<Void> callback) {
        if(isAuthorized(callback)){
            Call<Void> deleteRelationshipCall = getClient().deleteRelationship(userId, creditCardId, deviceId);
            deleteRelationshipCall.enqueue(new CallbackWrapper<>(callback));
        }
    }


    /**
     * For a single user, retrieve a pagable collection of tokenized credit cards in their profile.
     *
     * @param userId   user id
     * @param limit    Max number of credit cards per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getCreditCards(final String userId, final int limit, final int offset, final ApiCallback<ResultCollection<CreditCard>> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {
                    Call<ResultCollection<CreditCard>> getCreditCardsCall = getClient().getCreditCards(userId, limit, offset);
                    getCreditCardsCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Add a single credit card to a user's profile.
     * If the card owner has no default card, then the new card will become the default.
     * However, if the owner already has a default then it will not change.
     * To change the default, you should update the user to have a new "default_source".
     *
     * @param userId     user id
     * @param creditCard credit card data:(pan, expMonth, expYear, cvv, name,
     *                   address data:(street1, street2, street3, city, state, postalCode, country))
     * @param callback   result callback
     */
    public void createCreditCard(final String userId, final CreditCard creditCard, final ApiCallback<CreditCard> callback) {
        if (isAuthorized(callback)) {

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {
                    Call<CreditCard> createCreditCardCall = getClient().createCreditCard(userId, creditCard);
                    createCreditCardCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Retrieves the details of an existing credit card.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param callback     result callback
     */
    public void getCreditCard(final String userId, final String creditCardId, final ApiCallback<CreditCard> callback) {
        if (isAuthorized(callback)) {

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {
                    Call<CreditCard> getCreditCardCall = getClient().getCreditCard(userId, creditCardId);
                    getCreditCardCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Update the details of an existing credit card.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param creditCard   credit card data to update:(name (Card holder name), address/street1, address/street2,
     *                     address/city, address/state, address/postalCode, address/countryCode)
     * @param callback     result callback
     */
    public void updateCreditCard(final String userId, final String creditCardId, final CreditCard creditCard, final ApiCallback<CreditCard> callback) {
        if (isAuthorized(callback)) {

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    JsonArray updateData = new JsonArray();

                    Map<String, Object> userMap = new ModelAdapter.ObjectConverter().convertToSimpleMap(creditCard);
                    for(Map.Entry<String, Object> entry : userMap.entrySet()) {
                        JsonObject item = new JsonObject();
                        item.addProperty("op", "replace");
                        item.addProperty("path", entry.getKey());
                        item.addProperty("value", String.valueOf(entry.getValue()));

                        updateData.add(item);
                    }

                    String userString = updateData.toString();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("encryptedData", StringUtils.getEncryptedString(KeysManager.KEY_API, userString));

                    Call<CreditCard> updateCreditCardCall = getClient().updateCreditCard(userId, creditCardId, jsonObject);
                    updateCreditCardCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Delete a single credit card from a user's profile.
     * If you delete a card that is currently the default source,
     * then the most recently added source will become the new default.
     * If you delete a card that is the last remaining source on the customer
     * then the default_source attribute will become null.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param callback     result callback
     */
    public void deleteCreditCard(final String userId, final String creditCardId, final ApiCallback<Void> callback) {
        if (isAuthorized(callback)) {
            Call<Void> deleteCardCall = getClient().deleteCreditCard(userId, creditCardId);
            deleteCardCall.enqueue(new CallbackWrapper<>(callback));
        }
    }


    /**
     * Indicate a user has accepted the terms and conditions presented
     * when the credit card was first added to the user's profile.
     * This link will only be available when the credit card is awaiting the user
     * to accept or decline the presented terms and conditions.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param callback     result callback
     */
    public void acceptTerms(final String userId, final String creditCardId, final ApiCallback<CreditCard> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<CreditCard> acceptTermsCall = getClient().acceptTerms(userId, creditCardId);
                    acceptTermsCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Indicate a user has declined the terms and conditions.
     * Once declined the credit card will be in a final state, no other actions may be taken.
     * This link will only be available when the credit card is awaiting the user to accept
     * or decline the presented terms and conditions.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param callback     result callback
     */
    public void declineTerms(final String userId, final String creditCardId, final ApiCallback<CreditCard> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<CreditCard> declineTermsCall = getClient().declineTerms(userId, creditCardId);
                    declineTermsCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Mark the credit card as the default payment instrument.
     * If another card is currently marked as the default,
     * the default will automatically transition to the indicated credit card.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param callback     result callback
     */
    public void makeDefault(final String userId, final String creditCardId, final ApiCallback<Void> callback) {
        if(isAuthorized(callback)){
            Call<Void> makeDefaultCall = getClient().makeDefault(userId, creditCardId);
            makeDefaultCall.enqueue(new CallbackWrapper<>(callback));
        }
    }

    /**
     * Transition the credit card into a deactivated state so that it may not be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in an active state.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param reason       reason data:(causedBy, reason)
     * @param callback     result callback
     */
    public void deactivate(final String userId, final String creditCardId, final Reason reason, final ApiCallback<CreditCard> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<CreditCard> deactivateCall = getClient().deactivate(userId, creditCardId, reason);
                    deactivateCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Transition the credit card into an active state where it can be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in a deactivated state.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param reason       reason data:(causedBy, reason)
     * @param callback     result callback
     */
    public void reactivate(final String userId, final String creditCardId, final Reason reason, final ApiCallback<CreditCard> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<CreditCard> reactivateCall = getClient().reactivate(userId, creditCardId, reason);
                    reactivateCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * When an issuer requires additional authentication to verify the identity of the cardholder,
     * this indicates the user has selected the specified verification method by the indicated verificationTypeId.
     *
     * @param userId             user id
     * @param creditCardId       credit card id
     * @param verificationTypeId verification type id
     * @param callback           result callback
     */
    public void selectVerificationType(final String userId, final String creditCardId, final String verificationTypeId,
                                       final ApiCallback<VerificationMethod> callback) {
        if(isAuthorized(callback)){
            Call<VerificationMethod> selectCall = getClient().selectVerificationType(userId, creditCardId, verificationTypeId);
            selectCall.enqueue(new CallbackWrapper<>(callback));
        }
    }

    /**
     * If a verification method is selected that requires an entry of a pin code, this transition will be available.
     * Not all verification methods will include a secondary verification step through the FitPay API.
     *
     * @param userId             user id
     * @param creditCardId       credit card id
     * @param verificationTypeId verification type id
     * @param verificationCode   verification code
     * @param callback           result callback
     */
    public void verify(final String userId, final String creditCardId, final String verificationTypeId, final String verificationCode,
                       final ApiCallback<VerificationMethod> callback) {
        if(isAuthorized(callback)){
            Call<VerificationMethod> verifyCall = getClient().verify(userId, creditCardId, verificationTypeId, verificationCode);
            verifyCall.enqueue(new CallbackWrapper<>(callback));
        }
    }


    /**
     * For a single user, retrieve a pagable collection of devices in their profile.
     *
     * @param userId   user id
     * @param limit    Max number of devices per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getDevices(final String userId, final int limit, final int offset, final ApiCallback<ResultCollection<Device>> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<ResultCollection<Device>> getDevicesCall = getClient().getDevices(userId, limit, offset);
                    getDevicesCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * For a single user, create a new device in their profile.
     *
     * @param userId   user id
     * @param device   device data to create:(deviceType, manufacturerName, deviceName, serialNumber,
     *                 modelNumber, hardwareRevision, firmwareRevision, softwareRevision, systemId,
     *                 osName, licenseKey, bdAddress, secureElementId, pairingTs)
     * @param callback result callback
     */
    public void createDevice(final String userId, final Device device, final ApiCallback<Device> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<Device> createDeviceCall = getClient().createDevice(userId, device);
                    createDeviceCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Retrieves the details of an existing device.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId   user id
     * @param deviceId device id
     * @param callback result callback
     */
    public void getDevice(final String userId, final String deviceId, final ApiCallback<Device> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<Device> getDeviceCall = getClient().getDevice(userId, deviceId);
                    getDeviceCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Update the details of an existing device.
     *
     * @param userId     user id
     * @param deviceId   device id
     * @param deviceData device data:(firmwareRevision, softwareRevision)
     * @param callback   result callback
     */
    public void updateDevice(final String userId, final String deviceId, final Device deviceData, final ApiCallback<Device> callback) {
        if (isAuthorized(callback)) {

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    JsonArray updateData = new JsonArray();

                    Map<String, Object> userMap = new ModelAdapter.ObjectConverter().convertToSimpleMap(deviceData);
                    for(Map.Entry<String, Object> entry : userMap.entrySet()) {
                        JsonObject item = new JsonObject();
                        item.addProperty("op", "replace");
                        item.addProperty("path", entry.getKey());
                        item.addProperty("value", String.valueOf(entry.getValue()));

                        updateData.add(item);
                    }

//                    String userString = updateData.toString();

//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty("encryptedData", StringUtils.getEncryptedString(KeysManager.KEY_API, userString));

                    Call<Device> updateCreditCardCall = getClient().updateDevice(userId, deviceId, updateData);
                    updateCreditCardCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Delete a single device.
     *
     * @param userId   user id
     * @param deviceId device id
     * @param callback result callback
     */
    public void deleteDevice(final String userId, final String deviceId, final ApiCallback<Void> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<Void> deleteDeviceCall = getClient().deleteDevice(userId, deviceId);
                    deleteDeviceCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }


    /**
     * Retrieves a collection of all events that should be committed to this device.
     *
     * @param userId       user id
     * @param deviceId     device id
     * @param limit        Max number of events per page, default: 10
     * @param offset       Start index position for list of entities returned
     * @param commitsAfter The last commit successfully applied.
     *                     Query will return all subsequent commits which need to be applied.
     * @param callback     result callback
     */
    public void getCommits(final String userId, final String deviceId, final String commitsAfter, final int limit, final int offset,
                           final ApiCallback<ResultCollection<Commit>> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<ResultCollection<Commit>> getCommitsCall = getClient().getCommits(userId, deviceId, commitsAfter, limit, offset);
                    getCommitsCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Retrieves an individual commit.
     *
     * @param userId   user id
     * @param deviceId device id
     * @param commitId commit id
     * @param callback result callback
     */
    public void getCommit(final String userId, final String deviceId, final String commitId, final ApiCallback<Commit> callback) {
        if(isAuthorized(callback)){

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {

                    Call<Commit> getCommitCall = getClient().getCommit(userId, deviceId, commitId);
                    getCommitCall.enqueue(new CallbackWrapper<>(callback));
                }
            };

            checkKeyAndMakeCall(onSuccess, callback);
        }
    }

    /**
     * Get all transactions.
     *
     * @param userId   user id
     * @param limit    Max number of transactions per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getTransactions(String userId, int limit, int offset, ApiCallback<ResultCollection<Transaction>> callback) {
        if(isAuthorized(callback)){
            Call<ResultCollection<Transaction>> getTransactionsCall = getClient().getTransactions(userId, limit, offset);
            getTransactionsCall.enqueue(new CallbackWrapper<>(callback));
        }
    }

    /**
     * Get a single transaction.
     *
     * @param userId        user id
     * @param transactionId transaction id
     * @param callback      result callback
     */
    public void getTransaction(String userId, String transactionId, ApiCallback<Transaction> callback) {
        if(isAuthorized(callback)){
            Call<Transaction> getTransactionCall = getClient().getTransaction(userId, transactionId);
            getTransactionCall.enqueue(new CallbackWrapper<>(callback));
        }
    }


    /**
     * Endpoint to allow for returning responses to APDU execution.
     *
     * @param packageId   package id
     * @param apduPackage package confirmation data:(packageId, state, executedTs,
     *                    executedDuration, apduResponses:(commandId, commandId, responseData))
     * @param callback    result callback
     */
    public void confirmAPDUPackage(String packageId, ApduPackage apduPackage, ApiCallback<Void> callback) {
        if(isAuthorized(callback)){ //TODO add 200,202 responses
            Call<Void> confirmAPDUPackage = getClient().confirmAPDUPackage(packageId, apduPackage);
            confirmAPDUPackage.enqueue(new CallbackWrapper<>(callback));
        }
    }


    /**
     * Retrieve an individual asset (i.e. terms and conditions)
     *
     * @param adapterData adapter data
     * @param adapterId   adapter id
     * @param assetId     asset id
     * @param callback    result callback
     */
    public void getAssets(String adapterData, String adapterId, String assetId, ApiCallback<Object> callback) {
    }
}