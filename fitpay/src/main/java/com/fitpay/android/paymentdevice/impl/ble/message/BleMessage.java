package com.fitpay.android.paymentdevice.impl.ble.message;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by tgs on 3/4/16.
 */
abstract class BleMessage {

    public static final int MAX_MESSAGE_LENGTH = 20;

    abstract byte[] getMessage();

    /**
     * Value returned is big endian
     * @param uuid
     * @return
     */
    protected byte[] getUuidBytes(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    protected byte[] getLittleEndianBytes(UUID uuid) {
        byte[] bytes = getUuidBytes(uuid);
        return Conversions.reverseBytes(bytes);
    }

}
