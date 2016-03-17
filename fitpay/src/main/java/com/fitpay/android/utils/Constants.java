package com.fitpay.android.utils;

import android.util.Log;

import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.Payload;
import com.fitpay.android.api.models.card.CreditCardInfo;
import com.fitpay.android.api.models.user.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Vlad on 12.02.2016.
 */
public final class Constants {

    public static final String FIT_PAY_ERROR_TAG = "FitPayError";

    static final String BASE_URL = "https://gi-de.pagare.me/";
    static final String API_URL = BASE_URL + "api/";
    static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";

    static final String PUSHER_KEY = "ef31c4c7ce55c574d8f9";

    static Gson gson;

    static Gson getGson() {
        if (gson == null){
            gson = new GsonBuilder()
                    .setDateFormat(Constants.DATE_FORMAT)
                    .registerTypeAdapter(ECCKeyPair.class, new ModelAdapter.KeyPairSerializer())
                    .registerTypeAdapter(Links.class, new ModelAdapter.LinksDeserializer())
                    .registerTypeAdapter(UserInfo.class, new ModelAdapter.DataSerializer<>())
                    .registerTypeAdapter(CreditCardInfo.class, new ModelAdapter.DataSerializer<>())
                    .registerTypeAdapter(Payload.class, new ModelAdapter.PayloadDeserializer())
                    .create();
        }
        return gson;
    }


    public static void printError(Throwable error) {
        printError(error.toString());
    }

    public static void printError(String error) {
        Log.e(Constants.FIT_PAY_ERROR_TAG, error);
    }
}

