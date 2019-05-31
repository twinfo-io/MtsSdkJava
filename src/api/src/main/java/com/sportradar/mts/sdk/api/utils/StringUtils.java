/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class StringUtils {

    public static final String EMPTY = "";

    public static String format(String msg, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
        return ft.getMessage();
    }

    public static boolean isNullOrEmpty(String input)
    {
        return input == null || input.isEmpty();
    }
}
