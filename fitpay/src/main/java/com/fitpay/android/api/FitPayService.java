package com.fitpay.android.api;


import com.fitpay.android.models.AuthenticatedUser;
import com.fitpay.android.models.CreditCard;
import com.fitpay.android.models.CreditCardsCollection;
import com.fitpay.android.models.Relationship;
import com.fitpay.android.models.User;
import com.fitpay.android.models.UsersCollection;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FitPayService {

    @FormUrlEncoded
    @POST("/users/login")
    Call<AuthenticatedUser> loginUser(@FieldMap Map<String, String> options);

    @POST("/users")
    Call<User> createUser(@Body User user);

    @GET("users")
    Call<UsersCollection> getUsers(@Query("limit") int limit, @Query("offset") int offset);

    @DELETE("/users/{userId}")
    Call<Object> deleteUser(@Path("userId") String userId);

    @PATCH("/users/{userId}")
    Call<User> updateUser(@Path("userId") String userId, @Body User user);

    @GET("/users/{userId}")
    Call<User> getUser(@Path("userId") String userId);



    @GET("/users/{userId}/relationships")
    Call<Relationship> getRelationship(@Path("userId") String userId,
                                       @Query("creditCardId") String creditCardId,
                                       @Query("deviceId") String deviceId);

    @PUT("/users/{userId}/relationships")
    Call<Relationship> createRelationship(@Path("userId") String userId,
                                          @Query("creditCardId") String creditCardId,
                                          @Query("deviceId") String deviceId);

    @DELETE("/users/{userId}/relationships")
    Call<Object> deleteRelationship(@Path("userId") String userId);



    @GET("/users/{userId}/creditCards")
    Call<CreditCardsCollection> getCreditCards(@Path("userId") String userId,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset);

    @POST("/users/{userId}/creditCards")
    Call<CreditCard> addCard(@Path("userId") String userId, @Body CreditCard creditCard);

    @GET("/users/{userId}/creditCards/{creditCardId}")
    Call<CreditCard> getCreditCard(@Path("userId") String userId,
                                   @Path("creditCardId") String creditCardId,
                                   @Body CreditCard creditCard);

    @DELETE("/users/{userId}/creditCards/{creditCardId}")
    Call<Object> deleteCreditCard(@Path("userId") String userId, @Path("creditCardId") String creditCardId);


    @POST("/users/{userId}/creditCards/{creditCardId}/acceptTerms")
    Call<CreditCard> acceptTerm(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

    @POST("/users/{userId}/creditCards/{creditCardId}/declineTerms")
    Call<CreditCard> declineTerms(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

    @POST("/users/{userId}/creditCards/{creditCardId}/makeDefault")
    Call<CreditCard> makeDefault(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

    @POST("/users/{userId}/creditCards/{creditCardId}/deactivate")
    Call<CreditCard> deactivate(@Path("userId") String userId, @Path("creditCardId") String creditCardId);

}