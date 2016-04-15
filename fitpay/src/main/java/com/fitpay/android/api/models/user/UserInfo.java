package com.fitpay.android.api.models.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User info
 */
public final class UserInfo implements Parcelable {

    /**
     * description : The user's username
     */
    String username;

    /**
     * description : The user's first name
     */
    String firstName;

    /**
     * description : The user's last name
     */
    String lastName;

    /**
     * description : The user's birthdate in YYYY-MM-DD format
     */
    String birthDate;

    /**
     * description : The user's email address, formatted as {string}@{domain}.{extension}
     */
    String email;

    UserInfo() {
    }

    @Override
    public String toString() {
        return "UserInfo";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.birthDate);
        dest.writeString(this.email);
    }

    protected UserInfo(Parcel in) {
        this.username = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.birthDate = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
