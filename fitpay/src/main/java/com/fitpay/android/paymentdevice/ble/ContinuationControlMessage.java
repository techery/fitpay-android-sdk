package com.fitpay.android.paymentdevice.ble;

/**
 * Created by tgs on 3/23/16.
 */
abstract class ContinuationControlMessage extends BleMessage {

    protected boolean isBeginning;

    public boolean isBeginning() {
        return isBeginning;
    }

    public boolean isEnd() {
        return !isBeginning;
    }

}
