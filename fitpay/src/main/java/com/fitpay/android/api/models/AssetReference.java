package com.fitpay.android.api.models;

/**
 * Created by Vlad on 19.02.2016.
 */
public class AssetReference extends BaseModel {
    private String mimeType;

    public String getMimeType() {
        return mimeType;
    }

    public String getUrl(){
        return links.getLink(SELF);
    }
}
