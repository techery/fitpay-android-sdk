package com.fitpay.android.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Generated server links. HATEOS representation
 */
public final class Links implements Parcelable {

    private final Map<String, String> links;

    public Links() {
        links = new HashMap<>();
    }

    public void setLink(String key, String value) {
        links.put(key, value);
    }

    public String getLink(String key) {
        if (links.containsKey(key)) {
            return links.get(key);
        }

        return null;
    }

    public String getReadableKeys() {
        if (links.keySet().size() > 0) {
            String availableLinks = links.keySet().toString();
            if (!availableLinks.contains("self")) {
                availableLinks = "self, " + availableLinks;
            }
            return availableLinks;
        }

        return "self";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final int N = links.size();
        dest.writeInt(N);
        if (N > 0) {
            for (Map.Entry<String, String> entry : links.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        }
    }

    protected Links(Parcel in) {
        final int N = in.readInt();
        links = new HashMap<>();
        for (int i = 0; i < N; i++) {
            String key = in.readString();
            String value = in.readString();
            links.put(key, value);
        }
    }

    public static final Parcelable.Creator<Links> CREATOR = new Parcelable.Creator<Links>() {
        @Override
        public Links createFromParcel(Parcel source) {
            return new Links(source);
        }

        @Override
        public Links[] newArray(int size) {
            return new Links[size];
        }
    };
}
