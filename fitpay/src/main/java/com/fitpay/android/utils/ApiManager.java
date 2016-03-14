package com.fitpay.android.utils;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.ApduPackage;
import com.fitpay.android.api.models.Commit;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.Device;
import com.fitpay.android.api.models.LoginIdentity;
import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.ResultCollection;
import com.fitpay.android.api.models.Transaction;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.api.models.card.VerificationMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Map;

import retrofit2.Call;

/*
 * API manager
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
     * @param identity data for login
     * @param callback result callback
     */
    public void loginUser(LoginIdentity identity, final ApiCallback<Void> callback) {

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

        Call<OAuthToken> getTokenCall = getClient().loginUser(identity.getData());
        getTokenCall.enqueue(getTokenCallback);
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
                    Call<User> getUserCall = getClient().getUser(apiService.getUserId());
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

                    Map<String, Object> userMap = new ObjectConverter().convertToSimpleMap(deviceData);
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
     * Get a single transaction.
     *
     * @param userId        user id
     * @param creditCardId credit card id
     * @param transactionId transaction id
     * @param callback      result callback
     */
    public void getTransaction(String userId, String creditCardId, String transactionId, ApiCallback<Transaction> callback) {
        if(isAuthorized(callback)){
            Call<Transaction> getTransactionCall = getClient().getTransaction(userId, creditCardId, transactionId);
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

    public <T> void get(String url, Map<String, Object> queryMap, final Type type, final ApiCallback<T> callback) {
        Call<JsonElement> getDataCall = getClient().get(url, queryMap);
        getDataCall.enqueue(new CallbackWrapper<>(new ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement result) {
                T response = Constants.getGson().fromJson(result, type);
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                callback.onFailure(errorCode, errorMessage);
            }
        }));
    }

    public <T, U> void post(String url, U data, final Type type, final ApiCallback<T> callback) {
        Call<JsonElement> postDataCall = data != null ?
                getClient().post(url, data) : getClient().post(url);

        postDataCall.enqueue(new CallbackWrapper<>(new ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement result) {
                T response = Constants.getGson().fromJson(result, type);
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                callback.onFailure(errorCode, errorMessage);
            }
        }));
    }

    public <T, U> void patch(String url, U data, boolean encrypt, final Type type, final ApiCallback<T> callback) {

        JsonArray updateData = new JsonArray();

        Map<String, Object> userMap = ObjectConverter.convertToSimpleMap(data);
        for(Map.Entry<String, Object> entry : userMap.entrySet()) {
            JsonObject item = new JsonObject();
            item.addProperty("op", "replace");
            item.addProperty("path", entry.getKey());
            item.addProperty("value", String.valueOf(entry.getValue()));

            updateData.add(item);
        }

        Call<JsonElement> patchDataCall = null;

        if(encrypt) {
            String userString = updateData.toString();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("encryptedData", StringUtils.getEncryptedString(KeysManager.KEY_API, userString));

            patchDataCall = getClient().patch(url, jsonObject);
        } else {
            patchDataCall = getClient().patch(url, updateData);
        }

        patchDataCall.enqueue(new CallbackWrapper<>(new ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement result) {
                T response = Constants.getGson().fromJson(result, type);
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                callback.onFailure(errorCode, errorMessage);
            }
        }));
    }

    public <T, U> void put(String url, U data, final Type type, final ApiCallback<T> callback) {
//        Call<JsonElement> putDataCall = getClient().post(url, data);
//        postDataCall.enqueue(new CallbackWrapper<>(new ApiCallback<JsonElement>() {
//            @Override
//            public void onSuccess(JsonElement result) {
//                T response = Constants.getGson().fromJson(result, type);
//                callback.onSuccess(response);
//            }
//
//            @Override
//            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
//                callback.onFailure(errorCode, errorMessage);
//            }
//        }));
    }

    public void delete(String url, final ApiCallback<Void> callback) {
        Call<Void> deleteDataCall = getClient().delete(url);
        deleteDataCall.enqueue(new CallbackWrapper<>(callback));
    }
}