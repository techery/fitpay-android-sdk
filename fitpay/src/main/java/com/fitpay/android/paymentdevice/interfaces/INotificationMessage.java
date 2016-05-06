package com.fitpay.android.paymentdevice.interfaces;

import java.util.Date;

/**
 * abstract interface for Notification object
 */
public interface INotificationMessage {
    Date getDate();
    byte[] getData();
    byte[] getType();
}
