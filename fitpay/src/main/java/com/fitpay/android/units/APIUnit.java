package com.fitpay.android.units;

import com.fitpay.android.api.FitPayClient;
import com.fitpay.android.api.oauth.OAuthConst;
import com.fitpay.android.api.OAuthClient;
import com.fitpay.android.api.oauth.OAuthConfig;
import com.fitpay.android.models.OAuthToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vlad on 12.02.2016.
 */
public class APIUnit extends Unit {

    private OAuthClient mOAuthClient;
    private FitPayClient mFitPayClient;

    public APIUnit(OAuthConfig config){
        super();
        mOAuthClient = new OAuthClient(config);
    }

    @Override
    public void onAdd(){
        super.onAdd();

        Call<OAuthToken> getTokenCall = mOAuthClient.getService().getAuthToken(OAuthConst.GRANT_TYPE_CLIENT_CREDENTIALS);
        getTokenCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
                if(response.isSuccess() && response.body() != null){
                    mFitPayClient = new FitPayClient(response.body());
                }
            }

            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {

            }
        });
    }
}
