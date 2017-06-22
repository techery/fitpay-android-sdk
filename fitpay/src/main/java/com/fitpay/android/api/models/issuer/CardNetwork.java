package com.fitpay.android.api.models.issuer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Issuers Card network
 */
public class CardNetwork implements Parcelable {

    private List<String> issuers = new ArrayList<>();

    public List<String> getIssuers() {
        return issuers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.issuers);
    }

    public CardNetwork() {
    }

    protected CardNetwork(Parcel in) {
        this.issuers = in.createStringArrayList();
    }

    public static final Creator<CardNetwork> CREATOR = new Creator<CardNetwork>() {
        @Override
        public CardNetwork createFromParcel(Parcel source) {
            return new CardNetwork(source);
        }

        @Override
        public CardNetwork[] newArray(int size) {
            return new CardNetwork[size];
        }
    };
}
