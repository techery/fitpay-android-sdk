package com.fitpay.android.units;

import com.fitpay.android.api.oauth.OAuthConst;
import com.fitpay.android.api.oauth.OAuthService;
import com.fitpay.android.api.oauth.objects.OAuthConfig;
import com.fitpay.android.api.oauth.objects.OAuthToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vlad on 12.02.2016.
 */
public class APIUnit extends Unit {

    private OAuthService mOAuthAPI;

    public APIUnit(OAuthConfig config){
        super();
        mOAuthAPI = new OAuthService(config);
    }

    @Override
    public void onAdd(){
        super.onAdd();

        Call<OAuthToken> getTokenCall = mOAuthAPI.getApi().getAuthToken(OAuthConst.GRANT_TYPE_CLIENT_CREDENTIALS);
        getTokenCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {

            }

            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {

            }
        });
    }
}
