package com.fitpay.android.wearable.message;

import com.fitpay.android.wearable.utils.Conversions;
import com.fitpay.android.wearable.utils.Hex;

/**
 * Created by tgs on 3/4/16.
 */
public class ApduResultMessage extends BleMessage {

    private byte result;
    private byte[] sequenceId;
    private byte[] data;
    private boolean enforceLength = true;

    public ApduResultMessage withEnforceLength(boolean enforceLength) {
        this.enforceLength = enforceLength;
        return this;
    }

    public ApduResultMessage withResult(byte result) {
        this.result = result;
        return this;
    }

    public ApduResultMessage withResult(byte[] result) {
        if (null == result || result.length != 1) {
            throw new IllegalArgumentException("must be single element byte array");
        }
        this.result = result[0];
        return this;
    }

    public ApduResultMessage withSequenceId(byte[] sequenceId) {
        switch (sequenceId.length) {
            case 0:
                throw new IllegalArgumentException("must define the sequence number");
            case 1:
                this.sequenceId = new byte[] { sequenceId[0], 0x00};  // little endian
                break;
            case 2:
                this.sequenceId = Conversions.reverseBytes(sequenceId);
                break;
            default:
                throw new IllegalArgumentException("must be a two element byte array");
        }
        return this;
    }

    public ApduResultMessage withSequenceId(int sequenceId) {
        String val = Integer.toHexString(sequenceId);
        if ((val.length() & 1) != 0) {
            val = "0" + val;
        }
        return withSequenceId(Hex.hexStringToBytes(val));
    }

    public ApduResultMessage withData(byte[] data) {
        if (enforceLength && data != null && data.length > MAX_MESSAGE_LENGTH - 3) {
            throw new IllegalArgumentException("data is too long.  Max length is: " + (MAX_MESSAGE_LENGTH - 3));
        }
        this.data = data;
        return this;
    }

    public byte[] getMessage() {
        if (null == sequenceId) {
            throw new IllegalStateException("sequenceId must be defined");
        }
        byte[] message = new byte[3 + ((null == data) ? 0 : data.length)];
        message[0] = result;
        System.arraycopy(sequenceId, 0, message, 1, sequenceId.length);
        if (null != data && data.length > 0) {
            System.arraycopy(data, 0, message, 1 + sequenceId.length, data.length);
        }
        return message;
    }

}
