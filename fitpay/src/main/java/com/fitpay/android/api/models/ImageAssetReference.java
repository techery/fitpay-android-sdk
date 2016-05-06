package com.fitpay.android.api.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Image asset reference
 */
public final class ImageAssetReference extends AssetReference implements Parcelable {
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public ImageAssetReference() {
    }

    protected ImageAssetReference(Parcel in) {
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<ImageAssetReference> CREATOR = new Parcelable.Creator<ImageAssetReference>() {
        @Override
        public ImageAssetReference createFromParcel(Parcel source) {
            return new ImageAssetReference(source);
        }

        @Override
        public ImageAssetReference[] newArray(int size) {
            return new ImageAssetReference[size];
        }
    };
}
