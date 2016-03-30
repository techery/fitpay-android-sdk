package com.fitpay.android.wearable.ble.message;

import java.util.Random;

/**
 * Created by tgs on 3/4/16.
 */
public class ApplicationControlMessage extends BleMessage {

    private byte result;
    private byte[] message;

    public ApplicationControlMessage withDeviceReset(byte[] value) {
        final int numberOfBytes = 36;
        this.message = new byte[numberOfBytes];
        new Random().nextBytes(this.message);
        return this;
    }

    public byte[] getMessage() {
        return message;
    }

}
