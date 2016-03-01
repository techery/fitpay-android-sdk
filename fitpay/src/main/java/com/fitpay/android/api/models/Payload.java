package com.fitpay.android.api.models;

import java.util.Map;

/**
 * Created by Vlad on 01.03.2016.
 */
public class Payload {

    private Map<String, Object> info;

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    @Override
    public String toString(){
        return "Payload";
    }
}
