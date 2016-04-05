package com.fitpay.android.wearable.model;

import android.content.Context;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.enums.States;
import com.fitpay.android.wearable.interfaces.IWearable;

/**
 * Created by Vlad on 29.03.2016.
 */
public abstract class Wearable implements IWearable {

    protected Context mContext;
    protected String mAddress;
    protected @States.Wearable int state;

    public Wearable(Context context, String address){
        mContext = context;
        mAddress = address;
    }

    @Override
    public @States.Wearable int getState(){
        return state;
    }

    @Override
    public void setState(@States.Wearable int state){
        this.state = state;
        RxBus.getInstance().post(this);
    }
}
