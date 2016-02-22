package com.fitpay.android.api;

import com.fitpay.android.api.models.ECCKeyPair;
import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.OAuthToken;
import com.fitpay.android.api.models.User;
import com.fitpay.android.utils.SecurityHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 */
final class FitPayService extends BaseClient<FitPayClient> {

    private OAuthToken mAuthToken;

    public FitPayService() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder builder = chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json");

                String keyId = SecurityHandler.getInstance().getKeyId();
                if (keyId != null) {
                    builder.header("fp-key-id", keyId);
                }

                if (mAuthToken != null) {
                    builder.header(HEADER_AUTHORIZATION, String.format("%s %s", AUTHORIZATION_BEARER, mAuthToken.getAccessToken()));
                }

                return chain.proceed(builder.build());
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);
        clientBuilder.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .registerTypeAdapter(ECCKeyPair.class, new ModelAdapter.KeyPairSerializer())
                .registerTypeAdapter(User.class, new ModelAdapter.DataSerializer<User>())
                .registerTypeAdapter(Links.class, new ModelAdapter.LinksDeserializer())
                .create();

        mAPIClient = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clientBuilder.build())
                .build()
                .create(FitPayClient.class);
    }

    public void updateToken(OAuthToken token) {
        mAuthToken = token;
    }
}
