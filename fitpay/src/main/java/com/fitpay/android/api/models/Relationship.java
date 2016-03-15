package com.fitpay.android.api.models;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.card.CreditCardRef;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.device.DeviceRef;

public final class Relationship extends BaseModel {

    private static final String CARD = "card";
    private static final String DEVICE = "device";

    private CreditCardRef card;
    private DeviceRef device;

    /**
     * Removes a relationship between a device and a creditCard if it exists.
     *
     * @param callback result callback
     */
    public void deleteRelationship(@NonNull ApiCallback<Void> callback) {
        makeDeleteCall(callback);
    }

    /**
     * Retrieve credit card information
     *
     * @param callback result callback
     */
    public void getCreditCard(@NonNull ApiCallback<CreditCard> callback) {
        makeGetCall(CARD, null, CreditCard.class, callback);
    }

    /**
     * Retrieve device information
     *
     * @param callback result callback
     */
    public void getDevice(@NonNull ApiCallback<Device> callback) {
        makeGetCall(DEVICE, null, Device.class, callback);
    }

    public DeviceRef getDeviceRef() {
        return device;
    }

    public CreditCardRef getCardRef() {
        return card;
    }
}