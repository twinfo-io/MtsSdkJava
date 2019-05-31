/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class MtsDateDeserializer extends JsonDeserializer<Date> {

    private static final Logger logger = LoggerFactory.getLogger(MtsDateDeserializer.class);
    private static final Pattern onlyDigits = Pattern.compile("\\D+");

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        if (jp.getText() == null) {
            return null;
        }
        Date d = parseFullDate(jp.getText());
        if (d != null) {
            return d;
        }
        String clean = onlyDigits.matcher(jp.getText()).replaceAll("");
        d = parseFullDate(clean);
        if (d != null) {
            return d;
        }
        d = parseDate(clean);
        if (d != null) {
            return d;
        }
        d = parseMonth(clean);
        if (d != null) {
            return d;
        }
        d = parseYear(clean);
        if (d != null) {
            return d;
        }
        logger.debug("Failed to decode Date for input: {}", clean);
        return null;
    }

    private static Date parseFullDate(String s) {
        try {
            return MtsDateFormatter.get().parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    private static Date parseDate(String s) {
        SimpleDateFormat formatterTmp = new SimpleDateFormat("yyyyMMdd");
        formatterTmp.setTimeZone(MtsDateFormatter.getTimeZone());
        try {
            return formatterTmp.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    private static Date parseMonth(String s) {
        SimpleDateFormat formatterTmp = new SimpleDateFormat("yyyyMM");
        formatterTmp.setTimeZone(MtsDateFormatter.getTimeZone());
        try {
            return formatterTmp.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    private static Date parseYear(String s) {
        SimpleDateFormat formatterTmp = new SimpleDateFormat("yyyy");
        formatterTmp.setTimeZone(MtsDateFormatter.getTimeZone());
        try {
            return formatterTmp.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }
}
