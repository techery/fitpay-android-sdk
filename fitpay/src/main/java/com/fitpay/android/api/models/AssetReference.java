package com.fitpay.android.api.models;

import android.os.Parcel;
import android.os.Parcelable;

/***
 * Created by Vlad on 19.02.2016.
 */
public class AssetReference extends BaseModel implements Parcelable {
    private String mimeType;

    public String getMimeType() {
        return mimeType;
    }

    public String getUrl(){
        return links.getLink(SELF);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mimeType);
        dest.writeParcelable(this.links, flags);
    }

    public AssetReference() {
    }

    protected AssetReference(Parcel in) {
        this.mimeType = in.readString();
        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Parcelable.Creator<AssetReference> CREATOR = new Parcelable.Creator<AssetReference>() {
        @Override
        public AssetReference createFromParcel(Parcel source) {
            return new AssetReference(source);
        }

        @Override
        public AssetReference[] newArray(int size) {
            return new AssetReference[size];
        }
    };
}
