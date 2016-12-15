package com.fitpay.android.paymentdevice.constants;

/**
 * Apdu constants
 */
public final class ApduConstants {
    public final static byte[] NORMAL_PROCESSING = new byte[]{(byte) 0x90, (byte) 0x00};
    public final static byte[] NORMAL_PROCESSING_WITH_DATA = new byte[]{(byte) 0x61};

    public final static byte[] WARNING_MEMORY_UNCHANGED_NO_INFO = new byte[]{(byte) 0x62, (byte) 0x00};
    public final static byte[] WARNING_MEMORY_UNCHANGED_NOT_CHANGED = new byte[]{(byte) 0x62, (byte) 0x01};
    public final static byte[] WARNING_MEMORY_UNCHANGED_DATA_MAY_BE_CORRUPTED = new byte[]{(byte) 0x62, (byte) 0x81};
    public final static byte[] WARNING_MEMORY_UNCHANGED_END_OF_FILE = new byte[]{(byte) 0x62, (byte) 0x82};
    public final static byte[] WARNING_MEMORY_UNCHANGED_FILE_INVALIDATED = new byte[]{(byte) 0x62, (byte) 0x83};
    public final static byte[] WARNING_MEMORY_UNCHANGED_FCI_NOT_FORMATTED = new byte[]{(byte) 0x62, (byte) 0x84};

    public final static byte[] WARNING_MEMORY_CHANGED_NO_INFO = new byte[]{(byte) 0x63, (byte) 0x00};
    public final static byte[] WARNING_MEMORY_CHANGED_FILE_FILLED_UP = new byte[]{(byte) 0x63, (byte) 0x81};
    public final static byte[] WARNING_MEMORY_CHANGED_C00 = new byte[]{(byte) 0x63, (byte) 0x00};
    public final static byte[] WARNING_MEMORY_CHANGED_C01 = new byte[]{(byte) 0x63, (byte) 0x01};
    public final static byte[] WARNING_MEMORY_CHANGED_C02 = new byte[]{(byte) 0x63, (byte) 0x02};
    public final static byte[] WARNING_MEMORY_CHANGED_C03 = new byte[]{(byte) 0x63, (byte) 0x03};
    public final static byte[] WARNING_MEMORY_CHANGED_C04 = new byte[]{(byte) 0x63, (byte) 0x04};
    public final static byte[] WARNING_MEMORY_CHANGED_C05 = new byte[]{(byte) 0x63, (byte) 0x05};
    public final static byte[] WARNING_MEMORY_CHANGED_C06 = new byte[]{(byte) 0x63, (byte) 0x06};
    public final static byte[] WARNING_MEMORY_CHANGED_C07 = new byte[]{(byte) 0x63, (byte) 0x07};
    public final static byte[] WARNING_MEMORY_CHANGED_C08 = new byte[]{(byte) 0x63, (byte) 0x08};
    public final static byte[] WARNING_MEMORY_CHANGED_C09 = new byte[]{(byte) 0x63, (byte) 0x09};
    public final static byte[] WARNING_MEMORY_CHANGED_C10 = new byte[]{(byte) 0x63, (byte) 0x10};
    public final static byte[] WARNING_MEMORY_CHANGED_C11 = new byte[]{(byte) 0x63, (byte) 0x11};
    public final static byte[] WARNING_MEMORY_CHANGED_C12 = new byte[]{(byte) 0x63, (byte) 0x12};
    public final static byte[] WARNING_MEMORY_CHANGED_C13 = new byte[]{(byte) 0x63, (byte) 0x13};
    public final static byte[] WARNING_MEMORY_CHANGED_C14 = new byte[]{(byte) 0x63, (byte) 0x14};
    public final static byte[] WARNING_MEMORY_CHANGED_C15 = new byte[]{(byte) 0x63, (byte) 0x15};

    public final static byte[][] SUCCESS_RESULTS = {
            NORMAL_PROCESSING,
            NORMAL_PROCESSING_WITH_DATA,
            WARNING_MEMORY_UNCHANGED_NO_INFO,
            WARNING_MEMORY_UNCHANGED_NOT_CHANGED,
            WARNING_MEMORY_UNCHANGED_DATA_MAY_BE_CORRUPTED,
            WARNING_MEMORY_UNCHANGED_END_OF_FILE,
            WARNING_MEMORY_UNCHANGED_FILE_INVALIDATED,
            WARNING_MEMORY_UNCHANGED_FCI_NOT_FORMATTED,
            WARNING_MEMORY_CHANGED_NO_INFO,
            WARNING_MEMORY_CHANGED_FILE_FILLED_UP,
            WARNING_MEMORY_CHANGED_C00,
            WARNING_MEMORY_CHANGED_C01,
            WARNING_MEMORY_CHANGED_C02,
            WARNING_MEMORY_CHANGED_C03,
            WARNING_MEMORY_CHANGED_C04,
            WARNING_MEMORY_CHANGED_C05,
            WARNING_MEMORY_CHANGED_C06,
            WARNING_MEMORY_CHANGED_C07,
            WARNING_MEMORY_CHANGED_C08,
            WARNING_MEMORY_CHANGED_C09,
            WARNING_MEMORY_CHANGED_C10,
            WARNING_MEMORY_CHANGED_C11,
            WARNING_MEMORY_CHANGED_C12,
            WARNING_MEMORY_CHANGED_C13,
            WARNING_MEMORY_CHANGED_C14,
            WARNING_MEMORY_CHANGED_C15
    };

    public final static byte[] INVALID_REQUEST = new byte[]{(byte) 0x91, (byte) 0x7e};

    public final static byte[] APDU_SUCCESS_NO_CONTINUATION = new byte[]{(byte) 0x00};
    public final static byte[] APDU_SUCCESS_CONTINUATION = new byte[]{(byte) 0x01};
    public final static byte[] APDU_ERROR_NO_CONTINUATION = new byte[]{(byte) 0x02};
    public final static byte[] APDU_ERROR_CONTINUATION = new byte[]{(byte) 0x03};
    public final static byte[] APDU_ERROR_SEQUENCE_COUNTER_OFF = new byte[]{(byte) 0x69, (byte) 0x82};
    public final static byte[] APDU_ERROR_FILE_NOT_FOUND = new byte[]{(byte) 0x6a, (byte) 0x82};
    public final static byte[] APDU_ERROR_OUT_OF_MEMORY = new byte[]{(byte) 0x6a, (byte) 0x84};
    public final static byte[] APDU_PROTOCOL_ERROR = new byte[]{(byte) 0x10};

    public final static byte[] PROTOCOL_ERROR_DUPLICATE_SEQUENCE_NUMBER = new byte[]{0x00, 0x01};
}
