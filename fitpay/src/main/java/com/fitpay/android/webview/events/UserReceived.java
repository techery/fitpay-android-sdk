package com.fitpay.android.webview.events;

public class UserReceived {

    private String userId;
    private String email;

    public UserReceived(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public String userId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
