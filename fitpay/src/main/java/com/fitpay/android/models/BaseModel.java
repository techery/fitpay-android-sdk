package com.fitpay.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vlad on 18.02.2016.
 */
public class BaseModel{
    @SerializedName("_links")
    private Links links;

    public BaseModel(){
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
