package com.fitpay.android.api;

import android.support.annotation.NonNull;

import com.fitpay.android.api.clients.FitPayClient;
import com.fitpay.android.api.clients.FitPayService;
import com.fitpay.android.api.clients.OAuthClient;
import com.fitpay.android.api.models.OAuthConfig;
import com.fitpay.android.utils.Unit;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class APIUnit extends Unit {

    private OAuthClient mAuthAPI;
    private FitPayClient mFitPayAPI;

    public APIUnit(@NonNull OAuthConfig config) {
        super();

        mAuthAPI = new OAuthClient(config);
        mFitPayAPI = new FitPayClient(null);
    }

    public void setAuthCallback(IAuthCallback callback) {
        mAuthCallback = callback;
    }

    public interface IAuthCallback {
        void onSuccess();
        void onError(String error);
    }
}
