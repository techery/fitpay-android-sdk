package com.fitpay.android.wearable.utils;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Crc32 {

    private final static String LOG_TAG = Crc32.class.getCanonicalName();

    private Crc32() {
        // only static methods
    }


    public static final byte[] getCRC32Checksum(byte[] bytes) {
        Log.d(LOG_TAG, "calculate checksum on: " + Hex.bytesToHexString(bytes));
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);

        byte[] csBytes = ByteBuffer.allocate(8).putLong(checksum.getValue()).array();
        byte[] trimmed = new byte[4];
        System.arraycopy(csBytes, 4, trimmed, 0, 4);

        Log.d(LOG_TAG, "calculated checksum.  long: " + checksum.getValue() + ", bytes: " + Hex.bytesToHexString(trimmed));
        return trimmed;
    }
}
