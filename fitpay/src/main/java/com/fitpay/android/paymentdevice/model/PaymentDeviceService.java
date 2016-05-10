package com.fitpay.android.paymentdevice.model;

import android.content.Context;
import android.util.Log;

import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceService;
import com.fitpay.android.utils.RxBus;

/**
 * Base model for wearable payment device
 */
public abstract class PaymentDeviceService implements IPaymentDeviceService {

    private final static String TAG = PaymentDeviceService.class.getSimpleName();

    protected Context mContext;
    protected String mAddress;
    protected @Connection.State int state;

    public PaymentDeviceService(Context context, String address) {
        mContext = context;
        mAddress = address;
        state = States.NEW;
    }

    @Override
    public @Connection.State int getState() {
        return state;
    }

    @Override
    public void setState(@Connection.State int state) {
        Log.d(TAG, "connection state changed: " + state);
        this.state = state;
        RxBus.getInstance().post(new Connection(state));
    }

    @Override
    public String getMacAddress() {
        return mAddress;
    }


    @Override
    public void reconnect() {
        connect();
    }

}
