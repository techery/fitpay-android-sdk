package com.fitpay.android.wearable.model;

import android.content.Context;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.wearable.enums.Connection;
import com.fitpay.android.wearable.interfaces.IWearable;

/**
 * Base model for wearable payment device
 */
public abstract class Wearable implements IWearable {

    protected Context mContext;
    protected String mAddress;
    protected @Connection.State int state;

    public Wearable(Context context, String address) {
        mContext = context;
        mAddress = address;
    }

    @Override
    public @Connection.State int getState() {
        return state;
    }

    @Override
    public void setState(@Connection.State int state) {
        this.state = state;
        RxBus.getInstance().post(new Connection(state));
    }

    @Override
    public String getMacAddress() {
        return mAddress;
    }

}
