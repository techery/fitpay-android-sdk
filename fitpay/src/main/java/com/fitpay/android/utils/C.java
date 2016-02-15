package com.fitpay.android.utils;

import com.google.gson.FieldNamingPolicy;
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

    public static Gson getDefaultGson(){
        if(gson == null){
            gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
        }

        return gson;
    }
}
