package com.fitpay.android.api.models;


import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.utils.ApiManager;
import com.fitpay.android.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Vlad on 18.02.2016.
 */
public class BaseModel{

    protected static final String SELF = "self";

    @Expose(serialize = false)
    @SerializedName("_links")
    protected Links links;

    public BaseModel(){
    }

    private <T> String getLink(String key, ApiCallback<T> callback){
        String url = links.getLink(key);

        if(StringUtils.isEmpty(url)){
            callback.onFailure(ResultCode.NOT_FOUND, "API endpoint is not available.");
            url = null;
        }

        return url;
    }

    protected <T> void makeGetCall(String key, Map<String, Object> queryMap, Type type, ApiCallback<T> callback){
        String url = getLink(key, callback);
        if(url != null){
            ApiManager.getInstance().get(url, queryMap, type, callback);
        }
    }

    protected <T, U> void makePostCall(String key, U data, Type type, ApiCallback<T> callback){
        String url = getLink(key, callback);
        if(url != null){
            ApiManager.getInstance().post(url, data, type, callback);
        }
    }

    protected<T, U> void makePatchCall(U data, boolean encrypt, Type type, ApiCallback<T> callback) {
        String url = getLink(SELF, callback);
        if (url != null) {
            ApiManager.getInstance().patch(url, data, encrypt, type, callback);
        }
    }

    protected void makeDeleteCall(ApiCallback<Void> callback){
        String url = getLink(SELF,callback);
        if(url != null){
            ApiManager.getInstance().delete(url, callback);
        }
    }

}
