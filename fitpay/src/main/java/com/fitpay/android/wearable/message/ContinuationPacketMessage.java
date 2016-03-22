package com.fitpay.android.wearable.message;

import com.fitpay.wearabledevice.Hex;

/**
 * Created by tgs on 3/4/16.
 */
public class ContinuationPacketMessage extends BleMessage {

    private byte[] sortOrder;
    private byte[] data;

    public ContinuationPacketMessage withSortOrder(byte[] sortOrder) {
        switch (sortOrder.length) {
            case 0:
                throw new IllegalArgumentException("must define the sort order number");
            case 1:
                this.sortOrder = new byte[] { 0x00, sortOrder[0]};
                break;
            case 2:
                this.sortOrder = sortOrder;
                break;
            default:
                throw new IllegalStateException("must be a two element byte array");
        }
        return this;
    }

    public ContinuationPacketMessage withSortOrder(int sortOrder) {
        String val = Integer.toHexString(sortOrder);
        if (val.length() == 1) {
            val = "0" + val;
        }
        return withSortOrder(Hex.hexStringToBytes(val));
    }

    public ContinuationPacketMessage withData(byte[] data) {
        if (data == null || data.length > getMaxDataLength()) {
            throw new IllegalArgumentException("data is too long.  Max length is: " + getMaxDataLength());
        }
        this.data = data;
        return this;
    }

    public byte[] getMessage() {
        if (null == sortOrder) {
            throw new IllegalStateException("sequenceId must be defined");
        }
        byte[] message = new byte[2 + ((null == data) ? 0 : data.length)];
        System.arraycopy(sortOrder, 0, message, 0, sortOrder.length);
        if (null != data && data.length > 0) {
            System.arraycopy(data, 0, message, sortOrder.length, data.length);
        }
        return message;
    }

    public static int getMaxDataLength() {
        return MAX_MESSAGE_LENGTH - 2;
    }

}
