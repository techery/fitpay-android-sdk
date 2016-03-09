package com.fitpay.android.utils;

import android.text.TextUtils;

import com.fitpay.android.api.models.ApduPackage;
import com.fitpay.android.api.models.CreditCard;
import com.fitpay.android.api.models.Device;
import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.Payload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
final class ModelAdapter {

    public static final class DataSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        public JsonElement serialize(T data, Type typeOfSrc, JsonSerializationContext context) {

            final String encryptedString = StringUtils.getEncryptedString(KeysManager.KEY_API, new GsonBuilder().create().toJson(data));

            return new JsonParser().parse(encryptedString);
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject() && !TextUtils.isEmpty(json.getAsString())) {

                final String decryptedString = StringUtils.getDecryptedString(KeysManager.KEY_API, json.getAsString());

                if (!TextUtils.isEmpty(decryptedString)) {
                    Gson gson = new Gson();
                    return gson.fromJson(decryptedString, typeOfT);
                }
            }

            return null;
        }

    }

    public static final class PayloadDeserializer implements JsonDeserializer<Payload> {
        @Override
        public Payload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject() && !TextUtils.isEmpty(json.getAsString())) {

                final String decryptedString = StringUtils.getDecryptedString(KeysManager.KEY_API, json.getAsString());

                if (!TextUtils.isEmpty(decryptedString)) {

                    Payload payload = null;
                    Gson gson = new Gson();

                    if(decryptedString.contains("cardType")){
                        CreditCard creditCard = gson.fromJson(decryptedString, CreditCard.class);
                        payload = new Payload(creditCard);
                    } else {
                        ApduPackage apduPackage = gson.fromJson(decryptedString, ApduPackage.class);
                        payload = new Payload(apduPackage);
                    }

                    return payload;
                }
            }

            return null;
        }
    }

    public static final class DeviceSerializer implements JsonSerializer<Device> {
        public JsonElement serialize(Device data, Type typeOfSrc, JsonSerializationContext context) {

            final String encryptedString = StringUtils.getEncryptedString(KeysManager.KEY_API, new GsonBuilder().create().toJson(data));

            JsonObject jo = new JsonObject();
            jo.addProperty("encryptedData", encryptedString);
            return jo;
        }
    }

    public static final class KeyPairSerializer implements JsonSerializer<ECCKeyPair> {
        public JsonElement serialize(ECCKeyPair data, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jo = new JsonObject();
            jo.addProperty("clientPublicKey", data.getPublicKey());
            return jo;
        }
    }

    public static final class LinksDeserializer implements JsonDeserializer<Links> {

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

    public static final class ObjectConverter<T> {

        public Map<String, Object> convertToSimpleMap(T object) {
            Gson gson = new Gson();

            JsonElement objectAsJson = gson.toJsonTree(object);
            LinkedTreeMap objectAsMap = gson.fromJson(objectAsJson, LinkedTreeMap.class);

            Map<String, Object> resultMap = new HashMap<>();
            iterateThroughMap(0, "", objectAsMap, resultMap);

            return resultMap;
        }

        private void iterateThroughMap(int deepLevel, String keyName, LinkedTreeMap treeMap, Map<String, Object> resultMap) {
            for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>) treeMap.entrySet()) {
                if (entry.getValue() instanceof LinkedTreeMap) {
                    if(deepLevel++ > 0) {
                        keyName = keyName + entry.getKey();
                    }
                    iterateThroughMap(deepLevel, keyName, (LinkedTreeMap) entry.getValue(), resultMap);
                } else {
                    if(TextUtils.isEmpty(keyName)){
                        keyName = "/";
                    }
                    resultMap.put(keyName + entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
