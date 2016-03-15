package com.fitpay.android.api.models.device;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.card.CreditCard;

/**
 * Device relationship object
 */
public final class DeviceRef extends DeviceModel {

    /**
     * Retrieve {@link Device} device information
     *
     * @param callback result callback
     */
    public void getDevice(@NonNull ApiCallback<Device> callback){
        makeGetCall(SELF, null, Device.class, callback);
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
