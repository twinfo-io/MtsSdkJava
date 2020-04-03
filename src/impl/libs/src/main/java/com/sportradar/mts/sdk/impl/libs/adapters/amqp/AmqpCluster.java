/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class AmqpCluster {

    private final String un;
    private final String pwd;
    private final boolean useSslProtocol;
    private final String description;

    private final String vhost;
    private final NetworkAddress[] addresses;

    private final String environment;

    private AmqpCluster(String un,
                        String pwd,
                        String vhost,
                        boolean useSslProtocol,
                        NetworkAddress address) {
        Preconditions.checkNotNull(un, "un");
        Preconditions.checkNotNull(pwd, "pwd");
        Preconditions.checkNotNull(vhost, "vhost");
        Preconditions.checkNotNull(address, "address");
        Preconditions.checkNotNull(address.getHost(), "address.host");

        this.un = un;
        this.pwd = pwd;
        this.vhost = vhost;
        this.useSslProtocol = useSslProtocol;
        this.addresses = new NetworkAddress[]{address};
        this.description = composeDescription(this.addresses, this.vhost);

        if (vhost.contains("tradinggate"))
        {
            environment = "PROD";
        }
        else if (vhost.contains("integration"))
        {
            environment = "CI";
        }
        else
        {
            environment = "CUSTOM";
        }
    }

    private AmqpCluster(String un,
                        String pwd,
                        String vhost,
                        boolean useSslProtocol,
                        Set<NetworkAddress> addresses) {
        Preconditions.checkNotNull(un, "un");
        Preconditions.checkNotNull(pwd, "pwd");
        Preconditions.checkNotNull(vhost, "vhost");
        Preconditions.checkNotNull(addresses, "addresses");
        Preconditions.checkArgument(addresses.size() > 0, "addresses.size");
        for (final NetworkAddress address : addresses) {
            Preconditions.checkNotNull(address, "address");
            Preconditions.checkNotNull(address.getHost(), "address.host");
        }

        this.un = un;
        this.pwd = pwd;
        this.vhost = vhost;
        this.useSslProtocol = useSslProtocol;

        NetworkAddress[] addressesTmp = addresses.toArray(new NetworkAddress[addresses.size()]);
        Arrays.sort(addressesTmp, (o1, o2) -> {
            int t = o1.getHost().compareTo(o2.getHost());
            if (t != 0) {
                return t;
            }
            return Integer.compare(o1.getPort(), o2.getPort());
        });
        this.description = composeDescription(addressesTmp, this.vhost);

        shuffleArray(addressesTmp);
        this.addresses = addressesTmp;

        if (vhost.contains("tradinggate"))
        {
            environment = "PROD";
        }
        else if (vhost.contains("integration"))
        {
            environment = "CI";
        }
        else
        {
            environment = "CUSTOM";
        }
    }

    public String getUsername() { return un; }

    public String getPassword() {
        return pwd;
    }

    public String getVhost() {
        return vhost;
    }

    public boolean useSslProtocol() {
        return useSslProtocol;
    }

    public NetworkAddress[] getAddresses() {
        return addresses;
    }

    public String getDescription() {
        return description;
    }

    public String getEnvironment() { return environment; }

    public static AmqpCluster from(String username,
                                   String pwd,
                                   String vhost,
                                   boolean useSslProtocol,
                                   NetworkAddress address) {
        return new AmqpCluster(username, pwd, cleanVHost(vhost), useSslProtocol, address);
    }

    public static AmqpCluster from(String username,
                                   String pwd,
                                   String vhost,
                                   boolean useSslProtocol,
                                   Set<NetworkAddress> addresses) {
        return new AmqpCluster(username, pwd, cleanVHost(vhost), useSslProtocol, addresses);
    }

    public static AmqpCluster fromConnectionString(final String connectionString) {
        try {
            return fromURI(new URI(connectionString));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static AmqpCluster fromURI(final URI uri) {

        String hostTmp = "127.0.0.1";
        int portTmp = 5672;
        String unTmp = "test";
        String pwdTmp = "test";
        String vhostTmp = "/";
        boolean useSslProtocolTmp = false;

        if (!"amqp".equals(uri.getScheme().toLowerCase())) {
            if (!"amqps".equals(uri.getScheme().toLowerCase())) {
                throw new IllegalArgumentException("Wrong scheme in AMQP URI: " + uri.getScheme());
            }

            portTmp = 5671;
            useSslProtocolTmp = true;
        }

        { // get host
            String host = uri.getHost();
            if (host != null) {
                hostTmp = host;
            }
        }

        { // get port
            int port = uri.getPort();
            if (port != -1) {
                portTmp = port;
            }
        }

        { // get un and pwd
            String userInfo = uri.getRawUserInfo();
            if (userInfo != null) {
                String[] path = userInfo.split(":");
                if (path.length > 2) {
                    throw new IllegalArgumentException("Bad user info in AMQP URI: " + userInfo);
                }

                unTmp = uriDecode(path[0]);
                if (path.length == 2) {
                    pwdTmp = uriDecode(path[1]);
                }
            }
        }

        { // get vhost
            String path = uri.getRawPath();
            if (path != null && path.length() > 0) {
                if (path.indexOf(47, 1) != -1) {
                    throw new IllegalArgumentException("Multiple segments in path: " + path);
                }

                String tmp = uriDecode(uri.getPath());
                if (tmp != null) {
                    vhostTmp = cleanVHost(tmp);
                }
            }
        }

        return new AmqpCluster(unTmp, pwdTmp, vhostTmp, useSslProtocolTmp, new NetworkAddress(hostTmp, portTmp));
    }

    @Override
    public int hashCode() {
        int result = un.hashCode();
        result = 31 * result + pwd.hashCode();
        result = 31 * result + (useSslProtocol ? 1 : 0);
        result = 31 * result + description.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AmqpCluster mqCluster = (AmqpCluster) o;

        if (useSslProtocol != mqCluster.useSslProtocol) return false;
        if (!un.equals(mqCluster.un)) return false;
        if (!pwd.equals(mqCluster.pwd)) return false;
        return description.equals(mqCluster.description);
    }

    private static void shuffleArray(final NetworkAddress[] input) {
        final Random rnd = ThreadLocalRandom.current();
        for (int i = input.length - 1; i > 0; i--) {
            final int index = rnd.nextInt(i + 1);
            if (i == index) {
                continue;
            }
            final NetworkAddress address = input[index];
            input[index] = input[i];
            input[i] = address;
        }
    }

    private static String composeDescription(final NetworkAddress[] input,
                                             final String vh) {
        StringBuilder sb = new StringBuilder();
        sb.append("vhost: '").append(vh).append("', address(es): '");
        sb.append(input[0].toString());
        for (int i = 1; i < input.length; i++) {
            sb.append(",").append(input[i].toString());
        }
        return sb.append("'").toString();
    }

    private static String cleanVHost(String vhost) {
        if ((vhost == null) || vhost.isEmpty()) {
            return "/";
        }
        String tmp = vhost;
        while (tmp.startsWith("/")) {
            tmp = tmp.substring(1);
        }
        return ("/" + tmp);
    }

    private static String uriDecode(String s) {
        try {
            return URLDecoder.decode(s.replace("+", "%2B"), "US-ASCII");
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }
}
