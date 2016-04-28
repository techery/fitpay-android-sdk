package com.fitpay.android.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


final class UserService {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_BEARER = "Bearer";
    private static final String FP_KEY_ID = "fp-key-id";

    private OAuthToken authToken;

    private UserClient mClient;

    public UserService(String apiBaseUrl) {

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder builder = chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json");

                String keyId = KeysManager.getInstance().getKeyId(KeysManager.KEY_API);
                if (keyId != null) {
                    builder.header(FP_KEY_ID, keyId);
                }

                System.out.println("path: " + chain.request().url().encodedPath());
//
//                if (authToken != null) {
//
//                    final String value = new StringBuilder()
//                            .append(AUTHORIZATION_BEARER)
//                            .append(" ")
//                            .append(authToken.getAccessToken())
//                            .toString();
//
//                    builder.header(HEADER_AUTHORIZATION, value);
//                }

                return chain.proceed(builder.build());
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);
        clientBuilder.addInterceptor(logging);

        mClient = constructClient(apiBaseUrl, clientBuilder.build());

    }

    private UserClient constructClient(String apiBaseUrl, OkHttpClient okHttpClient) {
        UserClient client = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(Constants.getGson()))
                .client(okHttpClient)
                .build()
                .create(UserClient.class);
        return client;
    }

    public UserClient getClient() {
        return mClient;
    }

    public void updateToken(OAuthToken token) {
        authToken = token;
    }

    public boolean isAuthorized(){
        return authToken != null;
    }


}
