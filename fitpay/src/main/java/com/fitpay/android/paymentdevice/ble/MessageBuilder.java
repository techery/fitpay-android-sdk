package com.fitpay.android.paymentdevice.ble;

import android.util.Log;

import com.fitpay.android.utils.Hex;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tgs on 3/8/16.
 */
class MessageBuilder {

    public static final int DATE_TIME_MESSAGE_LENGTH = 10;

    private MessageBuilder() {
        // all static methods
    }

    public static byte[] getDateTimeMessage(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        byte[] msg = new byte[DATE_TIME_MESSAGE_LENGTH];
        int pos = 0;
        Log.d("blop", "year: " + cal.get(Calendar.YEAR));
        byte[] snippet = getIntAsByteArray(cal.get(Calendar.YEAR), 2);
        // reverse byte order - year is little endian
        for (int i = 0; i < snippet.length; i++) {
            msg[pos++] = snippet[snippet.length - i - 1];
        }
        msg[pos++] = getIntAsByteArray(cal.get(Calendar.MONTH) + 1, 1)[0];
        msg[pos++] = getIntAsByteArray(cal.get(Calendar.DAY_OF_MONTH), 1)[0];
        msg[pos++] = getIntAsByteArray(cal.get(Calendar.HOUR_OF_DAY), 1)[0];
        msg[pos++] = getIntAsByteArray(cal.get(Calendar.MINUTE), 1)[0];
        msg[pos++] = getIntAsByteArray(cal.get(Calendar.SECOND), 1)[0];
        int day =  cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7 : cal.get(Calendar.DAY_OF_WEEK) - 1;
        msg[pos++] = getIntAsByteArray(day, 1)[0];
        msg[pos++] = 0x00;  // no 1/256ths of a second
        msg[pos++] = 0x00;  // no adjustment
        return msg;
    }

    public static byte[] getIntAsByteArray(int val, int numberOfBytes) {
        String theVal = Integer.toHexString(val);
        if (theVal.length() > 2 * numberOfBytes) {
            throw new IllegalArgumentException("Insufficient number of bytes to store value");
        }
        while (theVal.length() < 2 * numberOfBytes) {
            theVal = "0" + theVal;
        }
        byte[] msg = new byte[numberOfBytes];
        for (int i = 0; i < numberOfBytes; i++) {
            msg[i] = Hex.hexStringToBytes(theVal.substring(i*2, i*2+2))[0];
        }
        return msg;
    }

}
