package org.neonsis.memcached.exception;

public class MemcachedException extends RuntimeException {

    public MemcachedException(String message) {
        super(message);
    }

    public MemcachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemcachedException(Throwable cause) {
        super(cause);
    }
}
