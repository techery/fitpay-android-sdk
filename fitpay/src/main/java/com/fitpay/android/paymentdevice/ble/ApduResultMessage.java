package com.fitpay.android.paymentdevice.ble;

import com.fitpay.android.paymentdevice.interfaces.IApduMessage;
import com.fitpay.android.utils.Hex;

import java.util.Arrays;

/**
 * Created by tgs on 3/4/16.
 */
class ApduResultMessage extends BleMessage implements IApduMessage {

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
        this.sequenceId = Hex.sequenceToBytes(sequenceId);
        return this;
    }

    public ApduResultMessage withData(byte[] data) {
        if (enforceLength && data != null && data.length > MAX_MESSAGE_LENGTH - 3) {
            throw new IllegalArgumentException("data is too long.  Max length is: " + (MAX_MESSAGE_LENGTH - 3));
        }
        this.data = data;
        return this;
    }

    public ApduResultMessage withMessage(byte[] msg){
        if (msg == null || msg.length < 3) {
            throw new IllegalArgumentException("message is too short");
        }

        result = msg[0];
        sequenceId = Arrays.copyOfRange(msg, 1, 3);
        data = Arrays.copyOfRange(msg, 3, msg.length);

        return this;
    }

    @Override
    public byte getResult() {
        return result;
    }

    public int getSequenceId(){
        return Conversions.getIntValueFromLittleEndianBytes(sequenceId);
    }

    @Override
    public byte[] getData() {
        return data;
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
