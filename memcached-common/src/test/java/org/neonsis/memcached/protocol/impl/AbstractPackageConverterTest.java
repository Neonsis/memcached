package org.neonsis.memcached.protocol.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neonsis.memcached.exception.MemcachedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AbstractPackageConverterTest {

    private AbstractPackageConverter abstractPackageConverter = new AbstractPackageConverter() {
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getVersionByteCode() {
        assertEquals(16, abstractPackageConverter.getVersionByte());
    }

    @Test
    public void checkProtocolVersionSuccess() {
        try {
            abstractPackageConverter.checkProtocolVersion((byte) 16);
        } catch (Exception e) {
            fail("Supported protocol version should be 1.0");
        }
    }

    @Test
    public void checkProtocolVersionFailure() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage("Unsupported protocol Version: 0");

        abstractPackageConverter.checkProtocolVersion((byte) 0);
    }
}