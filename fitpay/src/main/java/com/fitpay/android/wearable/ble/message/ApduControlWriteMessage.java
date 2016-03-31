package com.fitpay.android.wearable.ble.message;

import com.fitpay.android.wearable.ble.utils.Conversions;
import com.fitpay.android.wearable.ble.utils.Hex;

import java.util.Arrays;

/**
 * Created by tgs on 3/4/16.
 */
public class ApduControlWriteMessage extends BleMessage {

    private int sequenceId;
    private byte[] apduCommand;
    private byte[] message;

    public ApduControlWriteMessage() {
    }

    public ApduControlWriteMessage withMessage(byte[] msg) {
        if (msg == null || msg.length < 4) {
            throw new IllegalArgumentException("apdu control write content is invalid.");
        }
        this.message = msg;
        // byte[0] not used
        sequenceId = Conversions.getIntValueFromLittleEndianBytes(Arrays.copyOfRange(msg, 1, 3));
        apduCommand = Arrays.copyOfRange(msg, 3, msg.length);
        return this;
    }

    public ApduControlWriteMessage withSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
        return this;
    }

    public ApduControlWriteMessage withData(byte[] data) {
        apduCommand = data;

        message = new byte[3 + ((null == data) ? 0 : data.length)];

        String val = Integer.toHexString(sequenceId);
        if ((val.length() & 1) != 0) {
            val = "0" + val;
        }

        byte[] byteSequenceId = new byte[] { Hex.hexStringToBytes(val)[0], 0x00};
        System.arraycopy(byteSequenceId, 0, message, 1, byteSequenceId.length);

        if (null != data && data.length > 0) {
            System.arraycopy(data, 0, message, 1 + byteSequenceId.length, data.length);
        }

        return this;
    }

    public byte[] getMessage() {
        return message;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public byte[] getApduCommand() {
        return apduCommand;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder
                .append(ApduControlWriteMessage.class.getSimpleName())
                .append('(')
                .append("sequenceId: ")
                .append(this.getSequenceId())
                .append(", apduCommand: ");
        if (null != this.getMessage()) {
            builder.append(Hex.bytesToHexString(this.getApduCommand()));
        } else {
            builder.append("null");
        }
        builder.append(')');

        return builder.toString();
    }

}
