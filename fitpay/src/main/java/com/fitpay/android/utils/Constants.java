package com.fitpay.android.utils;

import android.util.Log;

import com.fitpay.android.api.models.Links;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class Constants {

    static final String BASE_URL = "https://demo.pagare.me/";
    static final String API_URL = BASE_URL + "api/";

    static final int KEY_API = 0;
    static final int KEY_RTM = KEY_API + 1;
    static final int KEY_WEB = KEY_RTM + 1;

    public static final String FIT_PAY_ERROR_TAG = "FitPayError";

    public static void printError(String error) {
        Log.e(Constants.FIT_PAY_ERROR_TAG, error);
    }
}
