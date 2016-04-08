package com.fitpay.android.wearable.constants;

/**
 * Created by Vlad on 07.04.2016.
 */
public final class ApduConstants {
    public final static byte[] SUCCESS = new byte[] { (byte)0x90, (byte)0x00 };
    public final static byte[] INVALID_REQUEST = new byte[] { (byte)0x91, (byte)0x7e };

    public final static byte[] APDU_SUCCESS_NO_CONTINUATION = new byte[] {(byte)0x00};
    public final static byte[] APDU_SUCCESS_CONTINUATION = new byte[] {(byte)0x01};
    public final static byte[] APDU_ERROR_NO_CONTINUATION = new byte[] {(byte)0x02};
    public final static byte[] APDU_ERROR_CONTINUATION = new byte[] {(byte)0x03};
    public final static byte[] APDU_PROTOCOL_ERROR = new byte[] {(byte)0x10};

    public final static byte[] PROTOCOL_ERROR_DUPLICATE_SEQUENCE_NUMBER = new byte[] { 0x00, 0x01};
}
