package org.neonsis.memcached.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neonsis.memcached.exception.MemcachedException;

import static org.junit.Assert.*;

public class StatusTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void valueOfSuccess() {
        assertEquals(Status.ADDED, Status.valueOf((byte) 0));
        assertEquals(Status.REPLACED, Status.valueOf((byte) 1));
        assertEquals(Status.GOTTEN, Status.valueOf((byte) 2));
        assertEquals(Status.NOT_FOUND, Status.valueOf((byte) 3));
        assertEquals(Status.REMOVED, Status.valueOf((byte) 4));
        assertEquals(Status.CLEARED, Status.valueOf((byte) 5));
    }

    @Test
    public void valueOfFailure() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage("Unsupported byteCode for Status: 127");

        Status.valueOf(Byte.MAX_VALUE);
    }

    @Test
    public void getByteCode() {
        assertEquals(0,Status.ADDED.getByteCode());
        assertEquals(1,Status.REPLACED.getByteCode());
        assertEquals(2,Status.GOTTEN.getByteCode());
        assertEquals(3,Status.NOT_FOUND.getByteCode());
        assertEquals(4,Status.REMOVED.getByteCode());
        assertEquals(5,Status.CLEARED.getByteCode());
    }
}