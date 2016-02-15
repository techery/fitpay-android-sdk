package com.fitpay.android.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Vlad on 15.02.2016.
 */
public class DataAdapter {

    private static final String ENCRYPTED_DATA = "encrypted_data";

    public static class DataSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        public JsonElement serialize(T data, Type typeOfSrc, JsonSerializationContext context) {

            final String encryptedString = ModelUtils.getEncryptedString(C.getDefaultGson().toJson(data));

            JsonObject jo = new JsonObject();
            jo.addProperty(ENCRYPTED_DATA, encryptedString);

            return jo;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject finalJson = json.getAsJsonObject();

            if (finalJson.has(ENCRYPTED_DATA)) {
                JsonObject encryptedObject = finalJson.getAsJsonObject(ENCRYPTED_DATA);
                if (encryptedObject != null && !encryptedObject.isJsonNull()) {
                    final String decryptedString = ModelUtils.getDecryptedString(encryptedObject.toString());
                    JsonObject decryptedJson = new JsonParser().parse(decryptedString).getAsJsonObject();

                    if (decryptedJson != null) {
                        finalJson.remove(ENCRYPTED_DATA);

                        for (Map.Entry<String, JsonElement> entry : decryptedJson.entrySet()) {
                            finalJson.add(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

            return C.getDefaultGson().fromJson(finalJson, typeOfT);
        }
    }
}
