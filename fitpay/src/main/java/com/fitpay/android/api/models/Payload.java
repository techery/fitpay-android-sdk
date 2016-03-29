package com.fitpay.android.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fitpay.android.api.enums.CommitTypes;
import com.fitpay.android.api.models.card.CreditCard;

/***
 * Created by Vlad on 01.03.2016.
 */
public final class Payload implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.creditCard, flags);
        dest.writeParcelable(this.apduPackage, flags);
    }

    protected Payload(Parcel in) {
        this.creditCard = in.readParcelable(CreditCard.class.getClassLoader());
        this.apduPackage = in.readParcelable(ApduPackage.class.getClassLoader());
    }

    public static final Parcelable.Creator<Payload> CREATOR = new Parcelable.Creator<Payload>() {
        @Override
        public Payload createFromParcel(Parcel source) {
            return new Payload(source);
        }

        @Override
        public Payload[] newArray(int size) {
            return new Payload[size];
        }
    };
}
