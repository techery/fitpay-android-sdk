package com.fitpay.android.paymentdevice.ble;

import com.fitpay.android.paymentdevice.interfaces.IControlMessage;

/**
 * Created by tgs on 3/4/16.
 */
class ApplicationControlMessage extends BleMessage implements IControlMessage {

    private byte[] data;

    public ApplicationControlMessage withData(byte[] value) {
        this.data = value;
        return this;
    }

    public byte[] getMessage() {
        return data;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
