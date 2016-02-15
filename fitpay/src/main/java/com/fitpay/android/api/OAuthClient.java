package com.fitpay.android.api;

import com.fitpay.android.api.oauth.OAuthConst;
import com.fitpay.android.api.oauth.OAuthConfig;
import com.fitpay.android.utils.C;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
public class OAuthClient extends BaseClient<OAuthService>{

    public OAuthClient(final OAuthConfig config){

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

        mAPIService = new Retrofit.Builder()
                .baseUrl(C.BASE_URL)
                .addConverterFactory(getDefaultGsonConverter())
                .client(clientBuilder.build())
                .build()
                .create(OAuthService.class);
    }
}
