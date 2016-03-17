package com.fitpay.android.api.models.user;

/**
 * Created by Vlad on 11.03.2016.
 */
public final class UserInfo {

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
}
