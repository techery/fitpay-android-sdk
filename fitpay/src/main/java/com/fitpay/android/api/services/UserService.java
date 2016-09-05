package com.fitpay.android.api.services;

import com.fitpay.android.api.models.security.OAuthToken;
import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.KeysManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


final public class UserService extends BaseClient {

    private OAuthToken authToken;

    private UserClient mClient;

    public UserService(String apiBaseUrl) {

        Interceptor interceptor = chain -> {
            Request.Builder builder = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json");

            String keyId = KeysManager.getInstance().getKeyId(KeysManager.KEY_API);
            if (keyId != null) {
                builder.header(FP_KEY_ID, keyId);
            }

            return chain.proceed(builder.build());
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //TODO remove unsafe once cert issues addressed
        OkHttpClient.Builder clientBuilder = getUnsafeOkHttpClient();
        //OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
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

    public boolean isAuthorized() {
        return authToken != null;
    }


}
