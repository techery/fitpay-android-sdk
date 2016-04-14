package com.fitpay.android.wearable.utils;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Crc32 {

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
