package com.fitpay.android.api.models.card;


import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public final class VerificationMethod extends VerificationMethodModel{
    private static final String SELECT = "select";
    private static final String VERIFY = "verify";

    /**
     * When an issuer requires additional authentication to verify the identity of the cardholder,
     * this indicates the user has selected the specified verification method.
     */
    public void select(@NonNull ApiCallback<VerificationMethod> callback){
        Type type = new TypeToken<VerificationMethod>(){}.getType();
        makePostCall(SELECT, null, type, callback);
    }

    /**
     * If a verification method is selected that requires an entry of a pin code, this transition will be available.
     * Not all verification methods will include a secondary verification step through the FitPay API.
     *
     * @param callback           result callback
     */
    public void verify(@NonNull String verificationCode, @NonNull ApiCallback<VerificationMethod> callback){
        Type type = new TypeToken<VerificationMethod>(){}.getType();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verificationCode", verificationCode);
        makePostCall(VERIFY, jsonObject, type, callback);
    }
}