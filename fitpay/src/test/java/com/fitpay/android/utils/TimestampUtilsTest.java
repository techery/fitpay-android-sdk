package com.fitpay.android.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Created by ssteveli on 10/12/17.
 */

public class TimestampUtilsTest {

    @Test
    public void shouldParseISO8601TimeCorrectly() throws Exception {
        Date expected = new Date(1505257442000l);
        String ts = "2017-09-12T23:04:02.000Z";
        Date d = TimestampUtils.getDateForISO8601String(ts);

        System.out.println(d.getTime());
        Assert.assertNotNull(d);
        Assert.assertEquals(expected, d);
    }

    @Test
    public void shouldOutputISO8601TimeCorrectly() throws Exception {
        String ts = "2017-09-12T23:04:02.000Z";
        String formattedString = TimestampUtils.getISO8601StringForTime(1505257442000l);

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(ts, formattedString);
    }

}
