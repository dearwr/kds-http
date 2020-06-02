package com.hchc.kdshttp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-02
 */
public class DatetimeUtil {

    private static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmssSSS";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    public static String now() {
        return new SimpleDateFormat(TIMESTAMP_PATTERN).format(new Date());
    }

    public static String format(Date date) {
        return format(date, DATE_TIME_PATTERN);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String formatDate(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static Date parse(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN);
        return format.parse(date);
    }

    public static Date addDay(Date date, int d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,d);
        return cal.getTime();
    }

    public static Date dayBegin(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Date dayEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public static Date addMinute(Date time, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.MINUTE, offset);
        return c.getTime();
    }

    public static Date addSecond(Date time, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.SECOND, offset);
        return c.getTime();
    }

    public static Date dayTime(Date date, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }

}
