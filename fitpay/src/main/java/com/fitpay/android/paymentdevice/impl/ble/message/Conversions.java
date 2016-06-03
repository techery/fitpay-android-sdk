package com.fitpay.android.paymentdevice.impl.ble.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by tgs on 3/23/16.
 */
public class Conversions {

    private Conversions() {
        //private - static methods only
    }

    public static byte[] getLittleEndianBytes(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    public static byte[] getLittleEndianBytes(long value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
    }

    public static int getIntValueFromLittleEndianBytes(byte[] bytes) {
        if (bytes.length > 2) {
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        } else {
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
        }
    }

    public static long getUnsignedInt(byte[] data) {
        long result = 0;

        for (int i = 0; i < data.length; i++) {
            result += data[i] << 8 * (data.length - 1 - i);
        }
        return result;
    }

    public static byte[] reverseBytes(byte[] bytes) {
        byte[] val = new byte[bytes.length];
        int pos = bytes.length;
        for (int i = 0; i < bytes.length; i++) {
            val[i] = bytes[--pos];
        }
        return val;
    }


}
