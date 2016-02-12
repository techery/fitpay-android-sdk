package com.fitpay.android.api.oauth;

import com.fitpay.android.api.oauth.objects.OAuthConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * OAuth2.0 service. Provides methods for requesting auth tokens.
 */
public class OAuthService {

    private OAuthAPI api;

    public OAuthService(final OAuthConfig config){

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                final String basic_auth = String.format("%s %s", OAuthConst.AUTHORIZATION_BASIC, config.getEncodedString());

                Request.Builder builder = chain.request().newBuilder()
                        .header("Accept", "application/json")
                        .header(OAuthConst.HEADER_AUTHORIZATION, basic_auth);

                return chain.proceed(builder.build());
            }
        };

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);

        api = new Retrofit.Builder()
                .baseUrl(OAuthConst.AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create(OAuthAPI.class);
    }

    public OAuthAPI getApi(){
        return api;
    }

}
