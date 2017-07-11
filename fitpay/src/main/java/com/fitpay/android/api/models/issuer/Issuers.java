package com.fitpay.android.api.models.issuer;

import android.os.Parcel;
import android.os.Parcelable;

import com.fitpay.android.api.models.BaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Issuers data (supported countries, card networks, issuers)
 */
public class Issuers extends BaseModel implements Parcelable {

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

    public Issuers() {
    }

    protected Issuers(Parcel in) {
        int countriesSize = in.readInt();
        this.countries = new HashMap<String, Country>(countriesSize);
        for (int i = 0; i < countriesSize; i++) {
            String key = in.readString();
            Country value = in.readParcelable(Country.class.getClassLoader());
            this.countries.put(key, value);
        }
    }

    public static final Creator<Issuers> CREATOR = new Creator<Issuers>() {
        @Override
        public Issuers createFromParcel(Parcel source) {
            return new Issuers(source);
        }

        @Override
        public Issuers[] newArray(int size) {
            return new Issuers[size];
        }
    };
}
