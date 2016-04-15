package com.fitpay.android.wearable.ble;

import com.fitpay.android.wearable.utils.Crc32;
import com.fitpay.android.wearable.utils.Hex;

/**
 * Created by tgs on 3/4/16.
 */
class ContinuationControlEndMessage extends ContinuationControlMessage {

    private long checksum;
    private byte[] payload;

    public ContinuationControlEndMessage withPayload(byte[] payload) {
        this.isBeginning = true;
        this.payload = payload;
        this.checksum = Crc32.getCRC32Checksum(payload);
        return this;
    }

    public byte[] getMessage() {
        byte[] crc = Conversions.getLittleEndianBytes((int) checksum);
        byte[] message = new byte[1 + crc.length];
        System.arraycopy(MessageConstants.CONTINUATION_END, 0, message, 0, MessageConstants.CONTINUATION_END.length);
        System.arraycopy(crc, 0, message, 1, crc.length);
        return message;
    }

    public ContinuationControlEndMessage withMessage(byte[] message) {
        if (message[0] != 0x01) {
            throw new IllegalArgumentException("Invalid continuation control end message: " + Hex.bytesToHexString(message));
        }
        if (message.length < 5) {
            throw new IllegalArgumentException("Invalid continuation control end message: " + Hex.bytesToHexString(message));
        }
        byte[] crcBytes = new byte[4];
        System.arraycopy(message, 1, crcBytes, 0, 4);
        checksum = Conversions.getIntValueFromLittleEndianBytes(crcBytes);
        if (checksum < 0) {
            checksum = checksum & 0x00000000ffffffffL;
        }
        return this;
    }

    public long getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder
                .append(ContinuationControlEndMessage.class.getSimpleName())
                .append('(')
                .append("checksum: ")
                .append(this.getChecksum());

        return builder.toString();
    }

}
