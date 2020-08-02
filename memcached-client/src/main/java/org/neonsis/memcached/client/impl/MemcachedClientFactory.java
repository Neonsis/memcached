package org.neonsis.memcached.client.impl;

import org.neonsis.memcached.client.Client;

import java.io.IOException;

public class MemcachedClientFactory {

    public static Client buildNewClient(String host, int port) throws IOException {
        return new DefaultClient(new DefaultClientConfig(host, port));
    }

    public static Client buildNewClient(String host) throws IOException {
        return buildNewClient(host, 9010);
    }

    public static Client buildNewClient() throws IOException {
        return buildNewClient("localhost");
    }
}
