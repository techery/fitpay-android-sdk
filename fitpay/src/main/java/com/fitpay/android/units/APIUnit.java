package com.fitpay.android.units;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fitpay.android.api.FitPayClient;
import com.fitpay.android.api.FitPayService;
import com.fitpay.android.api.OAuthClient;
import com.fitpay.android.api.OAuthService;
import com.fitpay.android.api.oauth.OAuthConfig;
import com.fitpay.android.models.OAuthToken;
import com.fitpay.android.utils.C;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vlad on 12.02.2016.
 */
public class APIUnit extends Unit {

    private OAuthService mAuthAPI;
    private FitPayService mFitPayAPI;
    private IAuthCallback mAuthCallback;

    public APIUnit(@NonNull OAuthConfig config) {
        super();
        mAuthAPI = new OAuthClient(config).getService();
    }

    @Override
    public void onAdd() {
        super.onAdd();

        Call<OAuthToken> getTokenCall = mAuthAPI.getAuthToken();
        getTokenCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
                if (response.isSuccess() && response.body() != null) {
                    mFitPayAPI = new FitPayClient(response.body()).getService();

                    if (mAuthCallback != null) {
                        mAuthCallback.onSuccess(mFitPayAPI);
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
                Log.e(C.FIT_PAY_ERROR_TAG, t.toString());

                if(mAuthCallback != null){
                    mAuthCallback.onError(t.toString());
                }
            }
        });
    }

    public void setAuthCallback(IAuthCallback callback) {
        mAuthCallback = callback;
    }

    public FitPayService getFitPayClient(){
        return mFitPayAPI;
    }

    public interface IAuthCallback {
        void onSuccess(FitPayService apiClient);
        void onError(String error);
    }
}
