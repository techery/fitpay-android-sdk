package com.fitpay.android.utils;

import android.util.Log;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class Constants {

    public static final String FIT_PAY_ERROR_TAG = "FitPayError";
    static final String BASE_URL = "https://demo.pagare.me/";
    static final String API_URL = BASE_URL + "api/";
    static final int KEY_API = 0;
    static final int KEY_RTM = KEY_API + 1;
    static final int KEY_WEB = KEY_RTM + 1;
    static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";

    public static void printError(String error) {
        Log.e(Constants.FIT_PAY_ERROR_TAG, error);
    }
}
