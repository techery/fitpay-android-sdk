package com.fitpay.android.wearable.message;

/**
 * Created by tgs on 3/23/16.
 */
public abstract class ContinuationControlMessage extends BleMessage {

    protected boolean isBeginning;

    public boolean isBeginning() {
        return isBeginning;
    }

    public boolean isEnd() {
        return !isBeginning;
    }

}
