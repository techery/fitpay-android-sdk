package com.fitpay.android.wearable.ble.message;

import com.fitpay.android.wearable.ble.utils.Conversions;
import com.fitpay.android.wearable.ble.utils.Hex;

/**
 * Created by tgs on 3/4/16.
 */
public class ContinuationPacketMessage extends BleMessage {

    private int sortOrder;
    private byte[] data;

    public ContinuationPacketMessage withSortOrder(byte[] sortOrder) {
        switch (sortOrder.length) {
            case 0:
                throw new IllegalArgumentException("must define the sort order number");
            case 1:
                this.sortOrder = Conversions.getIntValueFromLittleEndianBytes(new byte[] { sortOrder[0] });
                break;
            case 2:
                this.sortOrder = Conversions.getIntValueFromLittleEndianBytes(sortOrder);
                break;
            default:
                throw new IllegalStateException("must be a two element byte array");
        }
        return this;
    }

    public ContinuationPacketMessage withSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public ContinuationPacketMessage withData(byte[] data) {
        if (data == null || data.length > getMaxDataLength()) {
            throw new IllegalArgumentException("data is too long.  Max length is: " + getMaxDataLength());
        }
        this.data = data;
        return this;
    }

    public ContinuationPacketMessage withMessage(byte[] msg) {
        if (msg.length < 3) {
            throw new IllegalArgumentException("invalid continuation pasket message: " + Hex.bytesToHexString(msg));
        }
        byte[] sortOrderBytes = new byte[] { msg[0], msg[1]};
        this.sortOrder = Conversions.getIntValueFromLittleEndianBytes(sortOrderBytes);
        this.data = new byte[msg.length-2];
        System.arraycopy(msg, 2, this.data, 0, msg.length-2);
        return this;
    }

    public byte[] getMessage() {
        byte[] message = new byte[2 + ((null == data) ? 0 : data.length)];
        byte[] sortOrderBytes = Conversions.getLittleEndianBytes(sortOrder);
        System.arraycopy(sortOrderBytes, 0, message, 0, sortOrderBytes.length);
        if (null != data && data.length > 0) {
            System.arraycopy(data, 0, message, 2, data.length);
        }
        return message;
    }

    public static int getMaxDataLength() {
        return MAX_MESSAGE_LENGTH - 2;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder
                .append(ContinuationPacketMessage.class.getSimpleName())
                .append('(')
                .append("sortOrder: ")
                .append(this.getSortOrder())
                .append(", data: ")
                .append(Hex.bytesToHexString(this.getData()));

        return builder.toString();
    }

}
