package org.neonsis.memcached.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neonsis.memcached.exception.MemcachedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class VersionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void valueOfSuccess() {
        assertEquals(Version.VERSION_1_0, Version.valueOf((byte) 16));
    }

    @Test
    public void valueOfFailed() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage(is("Unsupported byteCode for Version: 127"));

        Version.valueOf(Byte.MAX_VALUE);
    }

    @Test
    public void getByteCodeSuccess() {
        assertEquals(16, Version.VERSION_1_0.getByteCode());
    }

    @Test
    public void verifyToString() {
        assertEquals("1.0", Version.VERSION_1_0.toString());
    }
}