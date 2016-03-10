package com.fitpay.android.api.models;

import android.text.TextUtils;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.utils.ApiManager;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Vlad on 18.02.2016.
 */
public class BaseModel{
    @Expose(serialize = false)
    @SerializedName("_links")
    protected Links links;

    public BaseModel(){
    }

    protected <T extends BaseModel> void makeGetCall(String key, Map<String, Object> queryMap, Type type, ApiCallback<T> callback){

        String url = links.getLink(key);
        if(!TextUtils.isEmpty(url)){
            ApiManager.getInstance().get(url, queryMap, type, callback);
        } else {
            callback.onFailure(ResultCode.NOT_FOUND, "API endpoint is not available.");
        }
    }

    protected <T extends BaseModel, U extends BaseModel> void makePostCall(String key, U data, Type type, ApiCallback<T> callback){
        String url = links.getLink(key);
        if(!TextUtils.isEmpty(url)){
            ApiManager.getInstance().post(url, data, type, callback);
        } else {
            callback.onFailure(ResultCode.NOT_FOUND, "API endpoint is not available.");
        }
    }


}
