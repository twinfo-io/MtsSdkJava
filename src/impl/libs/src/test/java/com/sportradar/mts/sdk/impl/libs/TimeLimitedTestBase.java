/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

/**
 * @author andrej.resnik on 10/06/16 at 14:47
 */
public class TimeLimitedTestBase {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public Timeout timeout = Timeout.millis(300000);
}
