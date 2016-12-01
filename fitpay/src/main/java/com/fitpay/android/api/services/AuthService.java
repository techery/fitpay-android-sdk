package com.fitpay.android.api.services;

import android.support.compat.BuildConfig;

import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.FPLog;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


final public class AuthService extends BaseClient {

    private AuthClient mAuthClient;

    public AuthService(String baseUrl) {

        Interceptor interceptor = chain -> {
            Request.Builder builder = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json");

            return chain.proceed(builder.build());
        };

        //TODO remove unsafe once cert issues addressed
        OkHttpClient.Builder clientBuilder = getUnsafeOkHttpClient();
        //OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);

        if (FPLog.showHttpLogs()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logging);
        }

        mAuthClient = constructClient(baseUrl, clientBuilder.build());

    }

    private AuthClient constructClient(String apiBaseUrl, OkHttpClient okHttpClient) {
        AuthClient client = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(Constants.getGson()))
                .client(okHttpClient)
                .build()
                .create(AuthClient.class);
        return client;
    }

    public AuthClient getClient() {
        return mAuthClient;
    }

}
