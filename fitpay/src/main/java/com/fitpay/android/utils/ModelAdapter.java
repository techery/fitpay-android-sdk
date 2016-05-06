package com.fitpay.android.utils;

import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.Payload;
import com.fitpay.android.api.models.apdu.ApduCommandResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.security.ECCKeyPair;
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

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 */
final class ModelAdapter {

    public static final class DataSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {

        @Override
        public JsonElement serialize(T data, Type typeOfSrc, JsonSerializationContext context) {

            final String encryptedString = StringUtils.getEncryptedString(KeysManager.KEY_API, new GsonBuilder().create().toJson(data));

            return new JsonParser().parse(encryptedString);
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject() && !StringUtils.isEmpty(json.getAsString())) {

                final String decryptedString = StringUtils.getDecryptedString(KeysManager.KEY_API, json.getAsString());

                if (!StringUtils.isEmpty(decryptedString)) {
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

            if (!json.isJsonObject() && !StringUtils.isEmpty(json.getAsString())) {

                final String decryptedString = StringUtils.getDecryptedString(KeysManager.KEY_API, json.getAsString());

                if (!StringUtils.isEmpty(decryptedString)) {

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

    public static final class AdpuCommandResponseSerializer implements JsonSerializer<ApduCommandResult> {
        public JsonElement serialize(ApduCommandResult data, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jo = new JsonObject();
            jo.addProperty("commandId", data.getCommandId());
            jo.addProperty("responseCode", Hex.bytesToHexString(data.getResponseCode()));
            jo.addProperty("responseData", Hex.bytesToHexString(data.getResponseData()));
            return jo;
        }
    }

}
