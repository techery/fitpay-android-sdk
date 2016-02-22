package com.fitpay.android.api;

import android.text.TextUtils;

import com.fitpay.android.api.models.ECCKeyPair;
import com.fitpay.android.api.models.Links;
import com.fitpay.android.utils.SecurityHandler;
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
import java.util.Set;

/**
 */
class ModelAdapter {

    private static final String ENCRYPTED_DATA = "encryptedData";

    public static class DataSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        public JsonElement serialize(T data, Type typeOfSrc, JsonSerializationContext context) {

            final String encryptedString = SecurityHandler.getInstance()
                    .getEncryptedString(RestApiConstants.getDefaultGson().toJson(data));

            JsonObject jo = new JsonObject();
            jo.addProperty(ENCRYPTED_DATA, encryptedString);

            return jo;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject finalJson = json.getAsJsonObject();

            if (finalJson.has(ENCRYPTED_DATA)) {
                JsonElement encryptedObject = finalJson.get(ENCRYPTED_DATA);
                if(!encryptedObject.isJsonObject() && !TextUtils.isEmpty(encryptedObject.getAsString())){

                    final String decryptedString = SecurityHandler.getInstance().getDecryptedString(encryptedObject.getAsString());
                    if(decryptedString != null) {
                        JsonElement decryptedJson = new JsonParser().parse(decryptedString);

                        if (decryptedJson != null) {
                            finalJson.remove(ENCRYPTED_DATA);

                            for (Map.Entry<String, JsonElement> entry : decryptedJson.getAsJsonObject().entrySet()) {
                                finalJson.add(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
            }

            return RestApiConstants.getDefaultGson().fromJson(finalJson, typeOfT);
        }
    }

    public static class KeyPairSerializer implements JsonSerializer<ECCKeyPair> {
        public JsonElement serialize(ECCKeyPair data, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jo = new JsonObject();
            jo.addProperty("clientPublicKey", data.getPublicKey());
            return jo;
        }
    }

    public static class LinksDeserializer implements JsonDeserializer<Links>{

        @Override
        public Links deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Links links = new Links();

            Set<Map.Entry<String, JsonElement>> listsSet = json.getAsJsonObject().entrySet();
            for(Map.Entry<String, JsonElement> entry : listsSet){
                links.setLink(entry.getKey(), entry.getValue().getAsJsonObject().get("href").getAsString());
            }
            
            return links;
        }
    }
}
