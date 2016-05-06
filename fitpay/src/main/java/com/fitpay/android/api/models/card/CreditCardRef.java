package com.fitpay.android.api.models.card;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.CardInitiators;
import com.fitpay.android.api.models.AssetReference;
import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.device.Device;

import java.util.ArrayList;

/**
 * Credit card relationship object
 */
public final class CreditCardRef extends CreditCardModel implements Parcelable {

    /**
     * Retrieve {@link CreditCard} credit card information
     *
     * @param callback result callback
     */
    public void getCreditCard(@NonNull ApiCallback<CreditCard> callback) {
        makeGetCall(SELF, null, CreditCard.class, callback);
    }

    /**
     * Retrieve relationship object that contains {@link Device} device and {@link CreditCard} credit card
     *
     * @param callback result callback
     */
    public void getRelationship(@NonNull ApiCallback<Relationship> callback) {
        makeGetCall(SELF, null, Relationship.class, callback);
    }


    public CreditCardRef() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.creditCardId);
        dest.writeString(this.userId);
        dest.writeValue(this.defaultX);
        dest.writeValue(this.createdTsEpoch);
        dest.writeValue(this.lastModifiedTsEpoch);
        dest.writeString(this.state);
        dest.writeString(this.causedBy);
        dest.writeString(this.cardType);
        dest.writeParcelable(this.cardMetaData, flags);
        dest.writeString(this.targetDeviceId);
        dest.writeString(this.targetDeviceType);
        dest.writeString(this.externalTokenReference);
        dest.writeList(this.verificationMethods);
        dest.writeParcelable(this.creditCardInfo, flags);
        dest.writeString(this.termsAssetId);
        dest.writeValue(this.eligibilityExpirationEpoch);
        dest.writeList(this.termsAssetReferences);
        dest.writeParcelable(this.links, flags);
    }

    protected CreditCardRef(Parcel in) {
        this.creditCardId = in.readString();
        this.userId = in.readString();
        this.defaultX = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.createdTsEpoch = (Long) in.readValue(Long.class.getClassLoader());
        this.lastModifiedTsEpoch = (Long) in.readValue(Long.class.getClassLoader());
        this.state = in.readString();
        @CardInitiators.Initiator String cb = in.readString();
        this.causedBy = cb;
        this.cardType = in.readString();
        this.cardMetaData = in.readParcelable(CardMetaData.class.getClassLoader());
        this.targetDeviceId = in.readString();
        this.targetDeviceType = in.readString();
        this.externalTokenReference = in.readString();
        this.verificationMethods = new ArrayList<>();
        in.readList(this.verificationMethods, VerificationMethod.class.getClassLoader());
        this.creditCardInfo = in.readParcelable(CreditCardInfo.class.getClassLoader());
        this.termsAssetId = in.readString();
        this.eligibilityExpirationEpoch = (Long) in.readValue(Long.class.getClassLoader());
        this.termsAssetReferences = new ArrayList<>();
        in.readList(this.termsAssetReferences, AssetReference.class.getClassLoader());
        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Parcelable.Creator<CreditCardRef> CREATOR = new Parcelable.Creator<CreditCardRef>() {
        @Override
        public CreditCardRef createFromParcel(Parcel source) {
            return new CreditCardRef(source);
        }

        @Override
        public CreditCardRef[] newArray(int size) {
            return new CreditCardRef[size];
        }
    };

}
