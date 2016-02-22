package com.fitpay.android.api;

import com.fitpay.android.api.models.Links;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * private constants for FitPay API
 */
final class RestApiConstants {
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
}
