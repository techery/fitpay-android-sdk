package com.fitpay.android.api.models.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User info
 */
public final class UserAuthInfo extends UserInfo implements Parcelable {

    /**
     * description : The user's authentication pin
     */
    String pin;


    UserAuthInfo() {
    }

    @Override
    public String toString() {
        return "UserAuthInfo";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.pin);
    }

    protected UserAuthInfo(Parcel in) {
        super(in);
        this.pin = in.readString();
    }

    public static final Creator<UserAuthInfo> CREATOR = new Creator<UserAuthInfo>() {
        @Override
        public UserAuthInfo createFromParcel(Parcel source) {
            return new UserAuthInfo(source);
        }

        @Override
        public UserAuthInfo[] newArray(int size) {
            return new UserAuthInfo[size];
        }
    };
}
