package com.fitpay.android.webview.events;

public class UserReceived {

    private String email;

    public UserReceived(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
