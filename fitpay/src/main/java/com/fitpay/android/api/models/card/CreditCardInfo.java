package com.fitpay.android.api.models.card;

import com.fitpay.android.api.models.device.Address;

/**
 * Created by Vlad on 11.03.2016.
 */
public final class CreditCardInfo {

    /**
     * description : Card holder name
     */
    String name;

    /**
     * description : The credit card cvv2 code
     */
    String cvv;

    /**
     * description : The credit card number, also known as a Primary Account Number (PAN)
     */
    String pan;

    /**
     * description : The credit card expiration month
     */
    Integer expMonth;

    /**
     * description : The credit card expiration year
     */
    Integer expYear;

    /**
     * description : Card holder address
     */
    Address address;

    CreditCardInfo() {
    }

    @Override
    public String toString() {
        return "CreditCardInfo";
    }
}
