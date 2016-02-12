package com.fitpay.android.api;

import com.fitpay.android.api.oauth.OAuthConst;
import com.fitpay.android.models.OAuthToken;
import com.fitpay.android.utils.C;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 */
public class FitPayClient extends BaseClient<FitPayService>{

    public FitPayClient(final OAuthToken token){

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder builder = chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .header(OAuthConst.HEADER_AUTHORIZATION, token.getAuthHeader());

                return chain.proceed(builder.build());
            }
        };

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);

        mAPIService = new Retrofit.Builder()
                .baseUrl(C.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create(FitPayService.class);
    }
}
