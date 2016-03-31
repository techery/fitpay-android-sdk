package com.fitpay.android.wearable.ble.message;

import com.fitpay.android.wearable.interfaces.INotificationMessage;

import java.util.Date;

/**
 * Created by tgs on 3/4/16.
 */
public class NotificationMessage extends BleMessage implements INotificationMessage{

    private Date date;
    private byte[] type;
    private byte[] data;

    private final static int TYPE_LENGTH = 2;

    public NotificationMessage() {
        this.date = new Date();
    }


    public NotificationMessage withDate(Date date) {
        this.date = date;
        return this;
    }

    public NotificationMessage withType(byte[] type) {
        if (null == type) {
            this.type = null;
            return this;
        }
        switch (type.length) {
                case 0:
                    this.type = type;
                    return this;
                case 1:
                    this.type = new byte[] { 0x00, type[0]};
                    break;
                case 2:
                    this.type = type;
                    break;
                default:
                    throw new IllegalStateException("type must be a two element byte array");
            }

        this.type = type;
        return this;
    }

    public NotificationMessage withData(byte[] data) {
        if (data != null && data.length > MAX_MESSAGE_LENGTH - MessageBuilder.DATE_TIME_MESSAGE_LENGTH - TYPE_LENGTH) {
            throw new IllegalArgumentException("data is too long.  Max length is: " + (MessageBuilder.DATE_TIME_MESSAGE_LENGTH - TYPE_LENGTH));
        }
        this.data = data;
        return this;
    }

    public byte[] getMessage() {
        if (null == date) {
            throw new IllegalStateException("date must be defined");
        }
        byte[] dateTime = MessageBuilder.getDateTimeMessage(date);
        int length = MessageBuilder.DATE_TIME_MESSAGE_LENGTH;
        if (null != data) {
            length+=(TYPE_LENGTH + data.length);
        } else if (null != type) {
            length+=TYPE_LENGTH;
        }
        byte[] message = new byte[length];
        System.arraycopy(dateTime, 0, message, 0, dateTime.length);
        if (null != data && data.length > 0) {
            if (type != null) {
                System.arraycopy(type, 0, message, 1 + dateTime.length, type.length);
            } else {
                System.arraycopy(new byte[] { 0x00, 0x00}, 0, message, 1 + dateTime.length, TYPE_LENGTH);
            }
            System.arraycopy(data, 0, message, 1 + dateTime.length + TYPE_LENGTH, data.length);
        } else {
            if (type != null) {
                System.arraycopy(type, 0, message, 1 + dateTime.length, type.length);
            }
        }
        return message;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] getType() {
        return type;
    }
}
