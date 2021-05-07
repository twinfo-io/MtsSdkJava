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

    public static String dateToString(Date input) {
        if (input == null) {
            return null;
        }
        return get().format(input);
    }

    public static String dateToReadableShort(Date input) {
        if (input == null) {
            return null;
        }
        return getReadableShortFormat().format(input);
    }

    public static SimpleDateFormat get() {
        return formatter.get();
    }

    public static TimeZone getTimeZone() {
        return utcTimeZone;
    }

    public static Date stringToDate(String input) {
        try {
            return get().parse(input);
        } catch (Exception e) {
            logger.error("invalid date format: {}; required format is: {}", input, DATE_FORMAT);
            return null;
        }
    }

    public static Date stringToDateChecked(String input) throws ParseException { return get().parse(input); }

    private static SimpleDateFormat getReadableShortFormat() {
        return formatterReadableShort.get();
    }

    public static long dateTimeToUnixTime(Date input) { return input.toInstant().toEpochMilli(); }
}
