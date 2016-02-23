package com.fitpay.android.utils;

import android.text.TextUtils;

import com.fitpay.android.api.models.ECCKeyPair;
import com.fitpay.android.api.models.Links;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 */
class ModelAdapter {

    private static final String ENCRYPTED_DATA = "encryptedData";

    public static class DataSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        public JsonElement serialize(T data, Type typeOfSrc, JsonSerializationContext context) {

            final String encryptedString = SecurityHandler.getInstance()
                    .getEncryptedString(new GsonBuilder().create().toJson(data));

            JsonObject jo = new JsonObject();
            jo.addProperty(ENCRYPTED_DATA, encryptedString);

            return jo;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject() && !TextUtils.isEmpty(json.getAsString())) {

                final String decryptedString = SecurityHandler.getInstance().getDecryptedString(json.getAsString());

                if (!TextUtils.isEmpty(decryptedString)) {
                    Gson gson = new Gson();
                    return gson.fromJson(decryptedString, typeOfT);
                }
            }

            return null;
        }

    }

    public static class KeyPairSerializer implements JsonSerializer<ECCKeyPair> {
        public JsonElement serialize(ECCKeyPair data, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jo = new JsonObject();
            jo.addProperty("clientPublicKey", data.getPublicKey());
            return jo;
        }
    }

    public static class LinksDeserializer implements JsonDeserializer<Links> {

        @Override
        public Links deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Links links = new Links();

            Set<Map.Entry<String, JsonElement>> listsSet = json.getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> entry : listsSet) {
                links.setLink(entry.getKey(), entry.getValue().getAsJsonObject().get("href").getAsString());
            }

            return links;
        }
    }
}
