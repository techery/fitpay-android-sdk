package com.fitpay.android.api.models.card;


import android.os.Parcel;
import android.os.Parcelable;

import com.fitpay.android.api.enums.CardInitiators;

/**
 * Credit card block/unblock reason
 */
public final class Reason implements Parcelable {

    @CardInitiators.Initiator
    private String causedBy;
    /**
     * description : The reason that the credit card is to deactivated
     */
    private String reason;

    @CardInitiators.Initiator
    public String getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(@CardInitiators.Initiator String causedBy) {
        this.causedBy = causedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.causedBy);
        dest.writeString(this.reason);
    }

    public Reason() {
    }

    protected Reason(Parcel in) {
        @CardInitiators.Initiator String cb = in.readString();
        this.causedBy = cb;
        this.reason = in.readString();
    }

    public static final Parcelable.Creator<Reason> CREATOR = new Parcelable.Creator<Reason>() {
        @Override
        public Reason createFromParcel(Parcel source) {
            return new Reason(source);
        }

        @Override
        public Reason[] newArray(int size) {
            return new Reason[size];
        }
    };
}