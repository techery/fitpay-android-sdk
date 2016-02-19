package com.fitpay.android.api;

import android.support.annotation.NonNull;

import com.fitpay.android.api.clients.FitPayClient;
import com.fitpay.android.api.clients.FitPayService;
import com.fitpay.android.api.clients.OAuthClient;
import com.fitpay.android.api.models.OAuthConfig;
import com.fitpay.android.api.models.OAuthToken;
import com.fitpay.android.utils.Unit;
import com.fitpay.android.utils.C;
import com.fitpay.android.utils.SecurityHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class APIUnit extends Unit {

    private OAuthClient mAuthAPI;
    private FitPayClient mFitPayAPI;
    private IAuthCallback mAuthCallback;

    public APIUnit(@NonNull OAuthConfig config) {
        super();

        mAuthAPI = new OAuthClient(config);
        mFitPayAPI = new FitPayClient(null);
    }

    @Override
    public void onAdd() {
        super.onAdd();

        SecurityHandler.getInstance().updateECCKeyPair();

        Call<OAuthToken> getTokenCall = mAuthAPI.getService().getAuthToken();
        getTokenCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
                if (response.isSuccess() && response.body() != null) {
                    mFitPayAPI.updateToken(response.body());

                    if (mAuthCallback != null) {
                        mAuthCallback.onSuccess(mFitPayAPI.getService());
                    }
                } else if (mAuthCallback != null) {
                    if (response.errorBody() != null) {
                        mAuthCallback.onError(response.errorBody().toString());
                    } else {
                        mAuthCallback.onError(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {
                C.printError(t.toString());

                if (mAuthCallback != null) {
                    mAuthCallback.onError(t.toString());
                }
            }
        });
    }

    public void setAuthCallback(IAuthCallback callback) {
        mAuthCallback = callback;
    }

    public FitPayService getFitPayClient() {
        return mFitPayAPI.getService();
    }

    public interface IAuthCallback {
        void onSuccess(FitPayService apiClient);

        void onError(String error);
    }
}
