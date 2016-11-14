package com.fitpay.android.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Methods for dealing with timestamps
 */
public class TimestampUtils {

    /**
     * Return combined date and time string for specified date/time
     *
     * @param time time in milliseconds
     * @return String with format "yyyy-MM-dd"
     */
    public static String getReadableDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SIMPLE, Locale.getDefault());
        return dateFormat.format(new Date(time));
    }

    /**
     * Return a date for specified ISO 8601 time
     *
     * @param time time in ISO 8601 format "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * @return Date
     */
    public static Date getDateForISO8601String(String time) {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(time.replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            FPLog.e(e);
        }
        return date;
    }

    /**
     * Return an ISO 8601 combined date and time string for specified date/time
     *
     * @param time time in milliseconds
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     */
    public static String getISO8601StringForTime(long time) {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_ISO8601, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(time);
    }
}
