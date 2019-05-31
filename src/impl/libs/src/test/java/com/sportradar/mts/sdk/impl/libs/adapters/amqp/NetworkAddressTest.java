/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author andrej.resnik on 09/08/16 at 14:54
 */
public class NetworkAddressTest extends TimeLimitedTestBase {

    private String host;
    private int port;
    private NetworkAddress networkAddress;

    @Before
    public void setUp() {
        host = "testHost";
        port = 1000;
        networkAddress = new NetworkAddress(host, port);
    }

    @Test
    public void getHostTest() {
        assertThat(networkAddress.getHost(), is(host));
    }

    @Test
    public void getPortTest() {
        assertThat(networkAddress.getPort(), is(port));
    }

    @Test
    public void initializeNewInstancePassingHost() {
        networkAddress = new NetworkAddress(host);
        int defaultPort = -1;
        assertThat(networkAddress.getHost(), is(host));
        assertThat(networkAddress.getPort(), is(defaultPort));
    }

    @Test
    public void hashCodeTest() {
        int expected = 31 * this.host.hashCode() + this.port;
        assertThat(networkAddress.hashCode(), is(expected));
    }

    @Test
    public void equalsTest() {
        NetworkAddress other = new NetworkAddress(host, port);
        boolean result = networkAddress.equals(other);
        assertTrue(result);

        other = new NetworkAddress("someHost", port);
        result = networkAddress.equals(other);
        assertFalse(result);

        other = new NetworkAddress(host, 1111);
        result = networkAddress.equals(other);
        assertFalse(result);

        other = new NetworkAddress(null, 0);
        result = networkAddress.equals(other);
        assertFalse(result);
    }

    @Test
    public void toStringTest() {
        String expected = host + ":" + port;
        String result = networkAddress.toString();
        assertEquals(result, expected);
    }
}
