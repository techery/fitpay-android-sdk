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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
        if (SecurityHandler.getInstance().getKeyId(Constants.KEY_API) == null) {
            SecurityHandler.getInstance().updateECCKey(Constants.KEY_API, successRunnable, callback);
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

                    Gson gson = new Gson();

                    JsonElement userJson = gson.toJsonTree(user);

                    Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
                    Map<String, String> userMap = gson.fromJson(userJson, mapType);

                    JsonObject updateData = new JsonObject();

//                    LinkedTreeMap userMap = gson.fromJson(userJson, LinkedTreeMap.class);
//                    for(Object entry : userMap.entrySet()){
//
//                    }

                    String userString = updateData.getAsString();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("encryptedData", SecurityHandler.getInstance().getDecryptedString(Constants.KEY_API, userString));

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
                    Call<User> getUserCall = getClient().getUser(SecurityHandler.getInstance().getKeyId(Constants.KEY_API), apiService.getUserId());
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
    }


    /**
     * For a single user, retrieve a pagable collection of tokenized credit cards in their profile.
     *
     * @param userId   user id
     * @param limit    Max number of credit cards per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getCreditCards(String userId, int limit, int offset, ApiCallback<ResultCollection<CreditCard>> callback) {
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
    public void createCreditCard(String userId, CreditCard creditCard, ApiCallback<CreditCard> callback) {
    }

    /**
     * Retrieves the details of an existing credit card.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param callback     result callback
     */
    public void getCreditCard(String userId, String creditCardId, ApiCallback<CreditCard> callback) {
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
    public void updateCreditCard(String userId, String creditCardId, CreditCard creditCard, ApiCallback<CreditCard> callback) {
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
    public void deleteCreditCard(String userId, String creditCardId, ApiCallback<Void> callback) {
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
    public void acceptTerm(String userId, String creditCardId, ApiCallback<CreditCard> callback) {
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
    public void declineTerms(String userId, String creditCardId, ApiCallback<CreditCard> callback) {
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
    public void makeDefault(String userId, String creditCardId, ApiCallback<Void> callback) {
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
    public void deactivate(String userId, String creditCardId, Reason reason, ApiCallback<CreditCard> callback) {
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
    public void reactivate(String userId, String creditCardId, Reason reason, ApiCallback<CreditCard> callback) {
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
    public void selectVerificationType(String userId, String creditCardId, String verificationTypeId,
                                       ApiCallback<VerificationMethod> callback) {
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
    public void verify(String userId, String creditCardId, String verificationTypeId, String verificationCode,
                       ApiCallback<VerificationMethod> callback) {
    }


    /**
     * For a single user, retrieve a pagable collection of devices in their profile.
     *
     * @param userId   user id
     * @param limit    Max number of devices per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getDevices(String userId, int limit, int offset, ApiCallback<ResultCollection<Device>> callback) {
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
    public void createDevice(String userId, Device device, ApiCallback<Device> callback) {
    }

    /**
     * Retrieves the details of an existing device.
     * You need only supply the unique identifier that was returned upon creation.
     *
     * @param userId   user id
     * @param deviceId device id
     * @param callback result callback
     */
    public void getDevice(String userId, String deviceId, ApiCallback<Device> callback) {
    }

    /**
     * Update the details of an existing device.
     *
     * @param userId     user id
     * @param deviceId   device id
     * @param deviceData device data:(firmwareRevision, softwareRevision)
     * @param callback   result callback
     */
    public void updateDevice(String userId, String deviceId, Device deviceData, ApiCallback<Device> callback) {
    }

    /**
     * Delete a single device.
     *
     * @param userId   user id
     * @param deviceId device id
     * @param callback result callback
     */
    public void deleteDevice(String userId, String deviceId, ApiCallback<Void> callback) {
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
    public void getCommits(String userId, String deviceId, String commitsAfter, int limit, int offset,
                           ApiCallback<ResultCollection<Commit>> callback) {
    }

    /**
     * Retrieves an individual commit.
     *
     * @param userId   user id
     * @param deviceId device id
     * @param commitId commit id
     * @param callback result callback
     */
    public void getCommit(String userId, String deviceId, String commitId, ApiCallback<Commit> callback) {
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
    }

    /**
     * Get a single transaction.
     *
     * @param userId        user id
     * @param transactionId transaction id
     * @param callback      result callback
     */
    public void getTransaction(String userId, String transactionId, ApiCallback<Transaction> callback) {
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


    /**
     * Creates a new encryption key pair
     *
     * @param clientPublicKey client public key
     * @param callback        result callback
     */
    public void createEncryptionKey(ECCKeyPair clientPublicKey, ApiCallback<ECCKeyPair> callback) {
    }


    /**
     * Retrieve and individual key pair.
     *
     * @param keyId    key id
     * @param callback result callback
     */
    public void getEncryptionKey(String keyId, ApiCallback<ECCKeyPair> callback) {
    }

    /**
     * Delete and individual key pair.
     *
     * @param keyId    key id
     * @param callback result callback
     */
    public void deleteEncryptionKey(String keyId, ApiCallback<Void> callback) {
    }

}