package com.fitpay.android.api.models.card;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.device.Device;

/**
 * Credit card relationship object
 */
public final class CreditCardRef extends CreditCardModel{

    /**
     * Retrieve {@link CreditCard} credit card information
     *
     * @param callback result callback
     */
    public void getCreditCard(@NonNull ApiCallback<CreditCard> callback){
        makeGetCall(SELF, null, CreditCard.class, callback);
    }

    /**
     * Retrieve relationship object that contains {@link Device} device and {@link CreditCard} credit card
     *
     * @param callback result callback
     */
    public void getRelationship(@NonNull ApiCallback<Relationship> callback){
        makeGetCall(SELF, null, Relationship.class, callback);
    }
}
