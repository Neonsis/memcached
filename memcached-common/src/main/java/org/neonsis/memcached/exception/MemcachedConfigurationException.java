package org.neonsis.memcached.exception;

public class MemcachedConfigurationException extends MemcachedException {

    public MemcachedConfigurationException(String message) {
        super(message);
    }

    public MemcachedConfigurationException(Throwable cause) {
        super(cause);
    }
}
