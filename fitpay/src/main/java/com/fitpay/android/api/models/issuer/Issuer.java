package com.fitpay.android.api.models.issuer;

import android.os.Parcel;
import android.os.Parcelable;

import com.fitpay.android.api.models.BaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Issuer data (supported countries, card networks, issuers)
 */
public class Issuer extends BaseModel implements Parcelable {

    private Map<String, Country> countries;

    public Map<String, Country> getCountries() {
        return countries;
    }

    public Country getCountry(String countryCode) {
        return countries != null ? countries.get(countryCode) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.countries.size());
        for (Map.Entry<String, Country> entry : this.countries.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    public Issuer() {
    }

    protected Issuer(Parcel in) {
        int countriesSize = in.readInt();
        this.countries = new HashMap<String, Country>(countriesSize);
        for (int i = 0; i < countriesSize; i++) {
            String key = in.readString();
            Country value = in.readParcelable(Country.class.getClassLoader());
            this.countries.put(key, value);
        }
    }

    public static final Creator<Issuer> CREATOR = new Creator<Issuer>() {
        @Override
        public Issuer createFromParcel(Parcel source) {
            return new Issuer(source);
        }

        @Override
        public Issuer[] newArray(int size) {
            return new Issuer[size];
        }
    };
}
