package com.fitpay.android.api.models.device;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.Relationship;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.card.CreditCardRef;

import java.util.ArrayList;

/**
 * Device relationship object
 */
public final class DeviceRef extends DeviceModel implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceIdentifier);
        dest.writeString(this.serialNumber);
        dest.writeString(this.modelNumber);
        dest.writeString(this.hardwareRevision);
        dest.writeString(this.firmwareRevision);
        dest.writeString(this.softwareRevision);
        dest.writeValue(this.createdTsEpoch);
        dest.writeString(this.osName);
        dest.writeString(this.systemId);
        dest.writeString(this.licenseKey);
        dest.writeString(this.bdAddress);
        dest.writeString(this.pairingTs);
        dest.writeString(this.hostDeviceId);
        dest.writeParcelable(this.links, flags);
    }

    public DeviceRef() {
    }

    protected DeviceRef(Parcel in) {
        this.deviceIdentifier = in.readString();
        this.serialNumber = in.readString();
        this.modelNumber = in.readString();
        this.hardwareRevision = in.readString();
        this.firmwareRevision = in.readString();
        this.softwareRevision = in.readString();
        this.createdTsEpoch = (Long) in.readValue(Long.class.getClassLoader());
        this.osName = in.readString();
        this.systemId = in.readString();
        this.licenseKey = in.readString();
        this.bdAddress = in.readString();
        this.pairingTs = in.readString();
        this.hostDeviceId = in.readString();
        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Parcelable.Creator<DeviceRef> CREATOR = new Parcelable.Creator<DeviceRef>() {
        @Override
        public DeviceRef createFromParcel(Parcel source) {
            return new DeviceRef(source);
        }

        @Override
        public DeviceRef[] newArray(int size) {
            return new DeviceRef[size];
        }
    };
}
