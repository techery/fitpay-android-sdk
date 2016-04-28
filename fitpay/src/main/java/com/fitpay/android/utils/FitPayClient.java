package com.fitpay.android.utils;

import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.user.User;
import com.google.gson.JsonElement;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

interface FitPayClient {

    //TODO remove - moved to auth service
//    /**
//     * Login user and get auth token
//     */
//    @FormUrlEncoded
//    @POST("oauth/authorize")
//    //@POST(Constants.BASE_URL + "oauth/authorize")
//    Call<OAuthToken> loginUser(@FieldMap Map<String, String> options);

    /**
     * Retrieves the details of an existing user.
     * You need only supply the unique user identifier that was returned upon user creation.
     *
     * @param userId user id
     */
    @GET("users/{userId}")
    Call<User> getUser(@Path("userId") String userId);

    /**
     * Creates a relationship between a device and a creditCard.
     *
     * @param userId       user id
     * @param creditCardId credit card id
     * @param deviceId     device id
     */
    @PUT("users/{userId}/relationships")
    Call<Relationship> createRelationship(@Path("userId") String userId,
                                          @Query("creditCardId") String creditCardId,
                                          @Query("deviceId") String deviceId);

    /**
     * Creates a new encryption key pair
     *
     * @param clientPublicKey client public key
     */
    //TODO review this change
//    @POST(Constants.API_URL_PREFIX + "config/encryptionKeys")
    @POST("config/encryptionKeys")
    Call<ECCKeyPair> createEncryptionKey(@Body ECCKeyPair clientPublicKey);

    /**
     * Retrieve and individual key pair.
     *
     * @param keyId key id
     */
    //TODO review this change
    @GET("config/encryptionKeys/{keyId}")
    Call<ECCKeyPair> getEncryptionKey(@Query("keyId") String keyId);

    /**
     * Delete and individual key pair.
     *
     * @param keyId key id
     */
    //TODO review this change
    @DELETE("config/encryptionKeys/{keyId}")
    Call<Void> deleteEncryptionKey(@Query("keyId") String keyId);

    /**
     * Get webhook
     */
    @GET("config/webhook")
    Call<Object> getWebhook();

    /**
     * Sets the webhook endpoint you would like FitPay to send notifications to, must be a valid URL.
     *
     * @param webhookURL webhook URL
     */
    @PUT("config/webhook")
    Call<Object> setWebhook(@Body String webhookURL);

    /**
     * Removes the current webhook endpoint, unsubscribing you from all Fitpay notifications.
     *
     * @param webhookURL webhook URL
     */
    @DELETE("config/webhook")
    Call<Object> removeWebhook(@Body String webhookURL);

    @GET
    Call<JsonElement> get(@Url String url, @QueryMap Map<String, Object> queryMap);

    @POST
    Call<JsonElement> post(@Url String url);

    @POST
    Call<JsonElement> post(@Url String url, @Body Object data);

    @PUT
    Call<JsonElement> put(@Url String url, @QueryMap Map<String, Object> queryMap);

    @PATCH
    Call<JsonElement> patch(@Url String url, @Body JsonElement data);

    @DELETE
    Call<Void> delete(@Url String url);
}