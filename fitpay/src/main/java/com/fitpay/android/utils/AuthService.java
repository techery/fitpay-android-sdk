package com.fitpay.android.utils;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


final class AuthService {

    private AuthClient mAuthClient;
    private Map<String, String> props;

    public AuthService(String authBaseUrl, Map<String, String> props) {

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder builder = chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json");

                System.out.println("path: " + chain.request().url().encodedPath());
                if (chain.request().url().encodedPath().contains("oauth/token")) {
                    System.out.println("need to add basic auth header");
                    String clientId = props.get(ApiManager.PROPERTY_CLIENT_ID);
                    String clientSecret = props.get(ApiManager.PROPERTY_CLIENT_SECRET);
                    if (null != clientId && null != clientSecret) {
                        String credential = okhttp3.Credentials.basic(clientId, clientSecret);
                        builder.header("Authorization", credential);
                    }
                }

                return chain.proceed(builder.build());
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //        OkHttpClient.Builder clientBuilder = getUnsafeOkHttpClient();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);
        clientBuilder.addInterceptor(logging);

        mAuthClient = constructClient(authBaseUrl, clientBuilder.build());

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
