package com.fitpay.android.wearable.ble.message;

import com.fitpay.android.wearable.ble.utils.Conversions;
import com.fitpay.android.wearable.interfaces.IMessage;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by tgs on 3/4/16.
 */
public abstract class BleMessage implements IMessage{

    public static final int MAX_MESSAGE_LENGTH = 20;

    public final static byte[] APDU_SUCCESS_NO_CONTINUATION = new byte[] { 0x00 };
    public final static byte[] APDU_SUCCESS_CONTINUATION = new byte[] { 0x01 };
    public final static byte[] APDU_ERROR_NO_CONTINUATION = new byte[] { 0x02 };
    public final static byte[] APDU_ERROR_CONTINUATION = new byte[] { 0x03 };
    public final static byte[] APDU_PROTOCOL_ERROR = new byte[] { 0x10 };

    public final static byte[] PROTOCOL_ERROR_DUPLICATE_SEQUENCE_NUMBER = new byte[] { 0x00, 0x01};


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
