package com.fitpay.android.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vlad on 25.02.2016.
 */
public class TimeUtils {
    public static String getReadableDate(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SIMPLE, Locale.getDefault());
        return dateFormat.format(new Date(time));
    }

    public static String getReadableDateISO8601(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_ISO8601, Locale.getDefault());
        return dateFormat.format(new Date(time));
    }
}
