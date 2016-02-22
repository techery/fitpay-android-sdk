package com.fitpay.android.utils;

import android.util.Log;

import com.fitpay.android.api.models.Links;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Vlad on 12.02.2016.
 */
public class Constants {
    public static final String FIT_PAY_ERROR_TAG = "FitPayError";
    public static void printError(String error) {
        Log.e(Constants.FIT_PAY_ERROR_TAG, error);
    }
}
