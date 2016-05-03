package com.fitpay.android.paymentdevice.model;

import android.content.Context;

import com.fitpay.android.utils.RxBus;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceService;

/**
 * Base model for wearable payment device
 */
public abstract class PaymentDeviceService implements IPaymentDeviceService {

    protected Context mContext;
    protected String mAddress;
    protected @Connection.State int state;

    public PaymentDeviceService(Context context, String address) {
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
