package com.fitpay.android.api.services;

import com.fitpay.android.api.models.security.OAuthToken;
import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.FPLog;
import com.fitpay.android.utils.KeysManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


final public class FitPayService extends BaseClient {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_BEARER = "Bearer";
    private static final String FP_KEY_ID = "fp-key-id";

    private FitPayClient mAPIClient;
    private OAuthToken mAuthToken;

    public FitPayService(String apiBaseUrl) {

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

                if (mAuthToken != null) {

                    final String value = new StringBuilder()
                            .append(AUTHORIZATION_BEARER)
                            .append(" ")
                            .append(mAuthToken.getAccessToken())
                            .toString();

                    builder.header(HEADER_AUTHORIZATION, value);
                }

                return chain.proceed(builder.build());
            }
        };

        //TODO remove once QA cert is issued
        OkHttpClient.Builder clientBuilder = getUnsafeOkHttpClient();
        //OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);

        clientBuilder.connectTimeout(60, TimeUnit.SECONDS);
        clientBuilder.readTimeout(60, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(60, TimeUnit.SECONDS);

        if (FPLog.showHttpLogs()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logging);
        }

        //add timeout modification here if needed - should be configurable
        //clientBuilder.readTimeout(<a value from config>, TimeUnit.SECONDS);

        mAPIClient = constructClient(apiBaseUrl, clientBuilder.build());

    }

    private FitPayClient constructClient(String apiBaseUrl, OkHttpClient okHttpClient) {
        FitPayClient client = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(Constants.getGson()))
                .client(okHttpClient)
                .build()
                .create(FitPayClient.class);
        return client;
    }

    public FitPayClient getClient() {
        return mAPIClient;
    }

    public void updateToken(OAuthToken token) {
        mAuthToken = token;
    }

    public String getUserId() {
        return mAuthToken.getUserId();
    }

    public boolean isAuthorized() {
        return mAuthToken != null;
    }

}
