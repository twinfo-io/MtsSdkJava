/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class MtsDateFormatter {

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_READABLE_SHORT = "yyyy-MM-dd";

    private static final Logger logger = LoggerFactory.getLogger(MtsDateFormatter.class);
    private static final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    private MtsDateFormatter() { throw new IllegalStateException("MtsDateFormatter class"); }

    private static final ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.withInitial(() -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setTimeZone(utcTimeZone);
        return simpleDateFormat;
    });

    private static final ThreadLocal<SimpleDateFormat> formatterReadableShort = ThreadLocal.withInitial(() -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_READABLE_SHORT);
        simpleDateFormat.setTimeZone(utcTimeZone);
        return simpleDateFormat;
    });

    public static SimpleDateFormat get() {
        return formatter.get();
    }

    private static SimpleDateFormat getReadableShortFormat() {
        return formatterReadableShort.get();
    }

    public static TimeZone getTimeZone() {
        return utcTimeZone;
    }

    public static long dateTimeToUnixTime(Date input) { return input.toInstant().toEpochMilli(); }

    public static String dateToString(Date input) {
        if (input == null) {
            return null;
        }
        String dateStr = get().format(input);
        formatter.remove();
        return dateStr;
    }

    public static String dateToReadableShort(Date input) {
        if (input == null) {
            return null;
        }
        String dateStr = getReadableShortFormat().format(input);
        formatterReadableShort.remove();
        return dateStr;
    }

    public static Date stringToDate(String input) {
        try {
            Date date = get().parse(input);
            formatter.remove();
            return date;
        } catch (Exception e) {
            logger.error("invalid date format: {}; required format is: {}", input, DATE_FORMAT);
            return null;
        }
    }

    public static Date stringToDateChecked(String input) throws ParseException {
        Date date = get().parse(input);
        formatter.remove();
        return date;
    }

    public static void unloadThreadLocal(){
        formatter.remove();
        formatterReadableShort.remove();
    }
}
