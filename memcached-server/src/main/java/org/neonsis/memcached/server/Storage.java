package org.neonsis.memcached.server;

import org.neonsis.memcached.model.Status;

public interface Storage extends AutoCloseable {

    Status put(String key, Long ttl, byte[] data);

    byte[] get(String key);

    Status remove(String key);

    Status clear();
}
