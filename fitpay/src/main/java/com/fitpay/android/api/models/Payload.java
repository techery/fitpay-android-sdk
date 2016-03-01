package com.fitpay.android.api.models;

import com.fitpay.android.api.enums.CommitTypes;

/**
 * Created by Vlad on 01.03.2016.
 */
public final class Payload {
    private CreditCard creditCard;
    private ApduPackage apduPackage;

    public Payload(CreditCard creditCard){
        this.creditCard = creditCard;
    }

    public Payload(ApduPackage apduPackage){
        this.apduPackage = apduPackage;
    }

    public Object getData(@CommitTypes.Type String type){
        switch (type){
            case CommitTypes.APDU_PACKAGE:
                return apduPackage;

            default:
                return creditCard;
        }
    }
}
