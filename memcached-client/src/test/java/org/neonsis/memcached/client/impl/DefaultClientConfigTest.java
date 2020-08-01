package org.neonsis.memcached.client.impl;

import org.junit.Test;
import org.neonsis.memcached.protocol.impl.DefaultObjectSerializer;
import org.neonsis.memcached.protocol.impl.DefaultRequestConverter;
import org.neonsis.memcached.protocol.impl.DefaultResponseConverter;

import static org.junit.Assert.assertEquals;

public class DefaultClientConfigTest {

    private final DefaultClientConfig config = new DefaultClientConfig("localhost", 9010);

    @Test
    public void getHost() {
        assertEquals("localhost", config.getHost());
    }

    @Test
    public void getPort() {
        assertEquals(9010, config.getPort());
    }

    @Test
    public void getRequestConverter() {
        assertEquals(DefaultRequestConverter.class, config.getRequestConverter().getClass());
    }

    @Test
    public void getResponseConverter() {
        assertEquals(DefaultResponseConverter.class, config.getResponseConverter().getClass());
    }

    @Test
    public void getObjectSerializer() {
        assertEquals(DefaultObjectSerializer.class, config.getObjectSerializer().getClass());
    }
}