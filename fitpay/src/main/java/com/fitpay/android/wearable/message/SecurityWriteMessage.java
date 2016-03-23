package com.fitpay.android.wearable.message;

/**
 * Created by tgs on 3/4/16.
 */
public class SecurityWriteMessage extends BleMessage {

    private byte nfcStatusRequest;

    public SecurityWriteMessage() {
    }

    public SecurityWriteMessage withData(byte[] data) {
        if (data == null || data.length > MAX_MESSAGE_LENGTH || data.length < 1) {
            throw new IllegalArgumentException("security write content is invalid.");
        }
        this.nfcStatusRequest = data[0];
        return this;
    }

    public byte[] getMessage() {
        byte[] message = new byte[1];
        message[0] = this.nfcStatusRequest;
        return message;
    }

}
