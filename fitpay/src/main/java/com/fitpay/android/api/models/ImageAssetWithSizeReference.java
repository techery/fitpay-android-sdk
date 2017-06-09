package com.fitpay.android.api.models;

import android.net.Uri;
import android.os.Parcel;

import java.util.HashSet;
import java.util.Set;

/**
 * Image asset reference
 */
public final class ImageAssetWithSizeReference extends ImageAssetReference {

    public String getUrlWithWidth(int width) {
        return getUrl(width, 0);
    }

    public String getUrlWithHeight(int height) {
        return getUrl(0, height);
    }

    public String getUrl(int width, int height) {
        Uri uri = Uri.parse(getUrl());

        final Set<String> params = new HashSet<>(uri.getQueryParameterNames());
        final Uri.Builder newUri = uri.buildUpon().clearQuery();

        params.remove("w");
        params.remove("h");

        if (width != 0) {
            newUri.appendQueryParameter("w", String.valueOf(width));
        }

        if (height != 0) {
            newUri.appendQueryParameter("h", String.valueOf(height));
        }

        for (String param : params) {
            newUri.appendQueryParameter(param, uri.getQueryParameter(param));
        }

        return newUri.build().toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public ImageAssetWithSizeReference() {
    }

    protected ImageAssetWithSizeReference(Parcel in) {
        super(in);
    }

    public static final Creator<ImageAssetWithSizeReference> CREATOR = new Creator<ImageAssetWithSizeReference>() {
        @Override
        public ImageAssetWithSizeReference createFromParcel(Parcel source) {
            return new ImageAssetWithSizeReference(source);
        }

        @Override
        public ImageAssetWithSizeReference[] newArray(int size) {
            return new ImageAssetWithSizeReference[size];
        }
    };
}
