package com.fitpay.android.api.models.card;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.Address;
import com.fitpay.android.api.models.Reason;
import com.fitpay.android.api.models.collection.Collections;

import java.util.Calendar;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

public final class CreditCard extends CreditCardModel {

    private static final String ACCEPT_TERMS = "acceptTerms";
    private static final String DECLINE_TERMS = "declineTerms";
    private static final String REACTIVATE = "reactivate";
    private static final String DEACTIVATE = "deactivate";
    private static final String TRANSACTIONS = "transactions";
    private static final String MAKE_DEFAULT = "makeDefault";

    /**
     * Indicate a user has accepted the terms and conditions presented
     * when the credit card was first added to the user's profile.
     * This link will only be available when the credit card is awaiting the user
     * to accept or decline the presented terms and conditions.
     *
     * @param callback     result callback
     */
    public void acceptTerms(@NonNull ApiCallback<CreditCard> callback){
        makePostCall(ACCEPT_TERMS, null, CreditCard.class, callback);
    }

    /**
     * Indicate a user has declined the terms and conditions.
     * Once declined the credit card will be in a final state, no other actions may be taken.
     * This link will only be available when the credit card is awaiting the user to accept
     * or decline the presented terms and conditions.
     *
     * @param callback     result callback
     */
    public void declineTerms(@NonNull ApiCallback<CreditCard> callback){
        makePostCall(DECLINE_TERMS, null, CreditCard.class, callback);
    }

    /**
     * Transition the credit card into an active state where it can be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in a deactivated state.
     *
     * @param reason       reason data:(causedBy, reason)
     * @param callback     result callback
     */
    public void reactivate(@NonNull Reason reason, @NonNull ApiCallback<CreditCard> callback){
        makePostCall(REACTIVATE, reason, CreditCard.class, callback);
    }

    /**
     * Transition the credit card into a deactivated state so that it may not be utilized for payment.
     * This link will only be available for qualified credit cards that are currently in an active state.
     *
     * @param reason       reason data:(causedBy, reason)
     * @param callback     result callback
     */
    public void deactivate(@NonNull Reason reason, @NonNull ApiCallback<CreditCard> callback){
        makePostCall(DEACTIVATE, reason, CreditCard.class, callback);
    }

    /**
     * Mark the credit card as the default payment instrument.
     * If another card is currently marked as the default,
     * the default will automatically transition to the indicated credit card.
     *
     * @param callback     result callback
     */
    public void makeDefault(@NonNull ApiCallback<Void> callback){
        makePostCall(MAKE_DEFAULT, null, Void.class, callback);
    }

    /**
     * Delete a single credit card from a user's profile.
     * If you delete a card that is currently the default source,
     * then the most recently added source will become the new default.
     * If you delete a card that is the last remaining source on the customer
     * then the default_source attribute will become null.
     *
     * @param callback     result callback
     */
    public void deleteCard(@NonNull ApiCallback<Void> callback){
        makeDeleteCall(callback);
    }

    /**
     * Update the details of an existing credit card.
     *
     * @param creditCard   credit card data to update:(name (Card holder name), address/street1, address/street2,
     *                     address/city, address/state, address/postalCode, address/countryCode)
     * @param callback     result callback
     */
    public void updateCard(@NonNull CreditCard creditCard, @NonNull ApiCallback<CreditCard> callback){
        makePatchCall(creditCard, true, CreditCard.class, callback);
    }

    /**
     * Get all transactions.
     *
     * @param limit    Max number of transactions per page, default: 10
     * @param offset   Start index position for list of entities returned
     * @param callback result callback
     */
    public void getTransactions(int limit, int offset, ApiCallback<Collections.TransactionCollection> callback){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("limit", limit);
        queryMap.put("offset", offset);
        makeGetCall(TRANSACTIONS, queryMap, Collections.TransactionCollection.class, callback);
    }

    public static final class Builder{

        private String name;
        private String cvv;
        private String pan;
        private Integer expMonth;
        private Integer expYear;
        private Address address;

        /**
         * Creates a Builder instance that can be used to build Gson with various configuration
         * settings. Builder follows the builder pattern, and it is typically used by first
         * invoking various configuration methods to set desired options, and finally calling
         * {@link #create()}.
         */
        public Builder(){
        }

        /**
         * Creates a {@link CreditCard} instance based on the current configuration. This method is free of
         * side-effects to this {@code Builder} instance and hence can be called multiple times.
         *
         * @return an instance of {@link CreditCard} configured with the options currently set in this builder
         */
        public CreditCard create(){
            CreditCard card = new CreditCard();
            card.creditCardInfo.name = name;
            card.creditCardInfo.cvv = cvv;
            card.creditCardInfo.pan = pan;
            card.creditCardInfo.expYear = expYear;
            card.creditCardInfo.expMonth = expMonth;
            card.creditCardInfo.address = address;
            return card;
        }

        /**
         * Set card holder name
         * @param name card holder name
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setName(@NonNull String name){
            this.name = name;
            return this;
        }

        /**
         * Set credit card cvv2 code
         * @param cvv cards's cvv2 code. string with 3 digits only
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setCVV(@NonNull String cvv) {//throws IllegalFormatException{

//            String pattern = "\\d{1,3}$";
//            if(!cvv.matches(pattern)){
//                throw new IllegalArgumentException("incorrect value");
//            }

            this.cvv = cvv;
            return this;
        }

        /**
         * Set credit card primary account number (PAN)
         * @param pan cards's PAN. string with 16 digits only
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setPAN(@NonNull String pan) {//throws IllegalFormatException{

//            String pattern = "\\d{1,16}$";
//            if(!cvv.matches(pattern)){
//                throw new IllegalArgumentException("incorrect value");
//            }

            this.pan = pan;
            return this;
        }

        /**
         * Set credit card expiration date
         * @param expYear cards's expiration year
         * @param expMonth cards's expiration month
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setExpDate(int expYear, int expMonth) throws IllegalFormatException{

            Calendar calendar = Calendar.getInstance();
            if(expYear < calendar.get(Calendar.YEAR) && expMonth < calendar.get(Calendar.MONTH) + 1){
                throw new IllegalArgumentException("incorrect expiration date");
            }

            this.expYear = expYear;
            this.expMonth = expMonth;
            return this;
        }

        /**
         * Set card holder address
         * @param address card holder address
         * @return a reference to this {@code Builder} object to fulfill the "Builder" pattern
         */
        public Builder setAddress(@NonNull Address address){
            this.address = address;
            return this;
        }
    }
}