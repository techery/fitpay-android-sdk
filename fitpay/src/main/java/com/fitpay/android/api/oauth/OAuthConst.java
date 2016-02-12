package com.fitpay.android.api.oauth;

import com.fitpay.android.utils.C;

/**
 * Created by Vlad on 12.02.2016.
 */
public class OAuthConst {
    public static final String AUTH_URL = C.BASE_URL + "/oauth/token";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_BASIC = "Basic";
    public static final String AUTHORIZATION_BEARER = "Bearer";
    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
}
