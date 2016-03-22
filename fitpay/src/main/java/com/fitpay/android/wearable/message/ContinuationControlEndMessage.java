package com.fitpay.android.wearable.message;

import com.fitpay.android.wearable.utils.Crc32;

/**
 * Created by tgs on 3/4/16.
 */
public class ContinuationControlEndMessage extends BleMessage {

    private byte[] payload;

    public ContinuationControlEndMessage withPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    public byte[] getMessage() {
        byte[] crc = Crc32.getCRC32Checksum(this.payload);
        byte[] message = new byte[1 + crc.length];
        System.arraycopy(MessageConstants.CONTINUATION_END, 0, message, 0, MessageConstants.CONTINUATION_END.length);
        System.arraycopy(crc, 0, message, 1, crc.length);
        return message;
    }

}
