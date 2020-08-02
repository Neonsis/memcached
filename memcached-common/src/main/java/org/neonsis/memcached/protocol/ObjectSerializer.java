package org.neonsis.memcached.protocol;

public interface ObjectSerializer {

    byte[] toByteArray(Object object);

    Object fromByteArray(byte[] array);
}
