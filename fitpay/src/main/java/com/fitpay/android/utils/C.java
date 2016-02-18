package com.fitpay.android.utils;

import android.util.Log;

import com.fitpay.android.models.Links;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Vlad on 12.02.2016.
 */
public class C {
    public static final String BASE_URL = "https://demo.pagare.me/";
    public static final String API_URL = BASE_URL + "api/";
    public static final String FIT_PAY_ERROR_TAG = "FitPayError";

    private static Gson gson;

    public static Gson getDefaultGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .registerTypeAdapter(Links.class, new ModelAdapter.LinksDeserializer())
                    .create();
        }

        return gson;
    }

    public static void printError(String error) {
        Log.e(C.FIT_PAY_ERROR_TAG, error);
    }
}
