package com.fitpay.android.wearable.message;

import com.fitpay.android.wearable.utils.Conversions;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by tgs on 3/4/16.
 */
public abstract class BleMessage {

    public static final int MAX_MESSAGE_LENGTH = 20;

    public final static byte[] APDU_SUCCESS_NO_CONTINUATION = new byte[] { 0x00 };
    public final static byte[] APDU_SUCCESS_CONTINUATION = new byte[] { 0x01 };
    public final static byte[] APDU_ERROR_NO_CONTINUATION = new byte[] { 0x02 };
    public final static byte[] APDU_ERROR_CONTINUATION = new byte[] { 0x03 };
    public final static byte[] APDU_PROTOCOL_ERROR = new byte[] { 0x10 };

    public final static byte[] PROTOCOL_ERROR_DUPLICATE_SEQUENCE_NUMBER = new byte[] { 0x00, 0x01};


    abstract public byte[] getMessage();

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
