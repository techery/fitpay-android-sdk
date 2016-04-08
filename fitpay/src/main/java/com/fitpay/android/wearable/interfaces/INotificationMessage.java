package com.fitpay.android.wearable.interfaces;

import java.util.Date;

/**
 * abstract interface for Transaction object
 */
public interface INotificationMessage {
    Date getDate();
    byte[] getData();
    byte[] getType();
}
