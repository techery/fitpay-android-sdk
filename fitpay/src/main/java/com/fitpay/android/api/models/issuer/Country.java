package com.fitpay.android.api.models.issuer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Issuers country
 */
public class Country implements Parcelable {

    private Map<String, CardNetwork> cardNetworks;

    public Map<String, CardNetwork> getCardNetworks() {
        return cardNetworks;
    }

    public CardNetwork getCardNetwork(String networkName) {
        return cardNetworks != null ? cardNetworks.get(networkName) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cardNetworks.size());
        for (Map.Entry<String, CardNetwork> entry : this.cardNetworks.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    public Country() {
    }

    protected Country(Parcel in) {
        int cardNetworksSize = in.readInt();
        this.cardNetworks = new HashMap<String, CardNetwork>(cardNetworksSize);
        for (int i = 0; i < cardNetworksSize; i++) {
            String key = in.readString();
            CardNetwork value = in.readParcelable(CardNetwork.class.getClassLoader());
            this.cardNetworks.put(key, value);
        }
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
