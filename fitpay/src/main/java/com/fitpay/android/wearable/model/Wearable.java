package com.fitpay.android.wearable.model;

import android.content.Context;

import com.fitpay.android.wearable.interfaces.IWearable;

/**
 * Created by Vlad on 29.03.2016.
 */
public abstract class Wearable implements IWearable {

    protected Context mContext;
    protected String mAddress;
    protected boolean initialized;

    public Wearable(Context context, String address){
        mContext = context;
        mAddress = address;
    }

    @Override
    public boolean isInitialized(){
        return initialized;
    }
}
