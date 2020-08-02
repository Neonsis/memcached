package org.neonsis.memcached.server.impl;

import org.neonsis.memcached.server.Server;

import java.util.Properties;

public class MemcachedServerFactory {

    public static Server buildNewServer(Properties overrideApplicationProperties) {
        return new DefaultServer(new DefaultServerConfig(overrideApplicationProperties));
    }
}
