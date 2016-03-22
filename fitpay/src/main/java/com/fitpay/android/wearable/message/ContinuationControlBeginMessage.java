package com.fitpay.android.wearable.message;

import java.util.UUID;

/**
 * Created by tgs on 3/4/16.
 */
public class ContinuationControlBeginMessage extends BleMessage {

    private UUID uuid;

    public ContinuationControlBeginMessage withUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public ContinuationControlBeginMessage withUuid(String uuidValue) {
        this.uuid = UUID.fromString(uuidValue);
        return this;
    }

    public byte[] getMessage() {
        byte[] id = getLittleEndianBytes(this.uuid);
        byte[] message = new byte[1 + id.length];
        System.arraycopy(MessageConstants.CONTINUATION_BEGIN, 0, message, 0, MessageConstants.CONTINUATION_BEGIN.length);
        System.arraycopy(id, 0, message, 1, id.length);
        return message;
    }


}
