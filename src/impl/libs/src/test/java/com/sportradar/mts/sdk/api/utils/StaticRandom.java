/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StaticRandom {

    public static ThreadLocalRandom Instance = ThreadLocalRandom.current();

    public static String S(int limit)
    {
        int result = limit > 1 ? Instance.nextInt(1, limit) : Instance.nextInt();
        return String.valueOf(result);
    }

    public static int I(int limit)
    {
        return limit > 1 ? Instance.nextInt(1, limit) : Instance.nextInt();
    }

    public static long L(int limit)
    {
        return limit > 1 ? Instance.nextLong(1, limit) : Instance.nextLong();
    }

    public static String S = S(0);

    public static int I = I(0);

    public static String S1000 = S(1000);

    public static int I1000 = I(1000);

    public static int I1000P = I(100000) + 10000;

    public static String S100 = S(100);

    public static int I100 = I(100);

    public static boolean B = I100 > 49;

    public static long L1000 = L(1000);

    public static long L1000P = L(100000) + 10000;

    public static String SL(int length)
    {
        String result = "";
        if (length <= 0)
        {
            return result;
        }
        while (result.length() < length)
        {
            result += UUID.randomUUID().toString().replace("-", "");
        }
        return result.substring(0, length);
    }
}
