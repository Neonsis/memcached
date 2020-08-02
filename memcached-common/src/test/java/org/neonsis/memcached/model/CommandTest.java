package org.neonsis.memcached.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neonsis.memcached.exception.MemcachedException;

import static org.junit.Assert.assertEquals;

public class CommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void valueOfSuccess() {
        assertEquals(Command.CLEAR, Command.valueOf((byte) 0));
        assertEquals(Command.PUT, Command.valueOf((byte) 1));
        assertEquals(Command.GET, Command.valueOf((byte) 2));
        assertEquals(Command.REMOVE, Command.valueOf((byte) 3));
    }

    @Test
    public void valueOfFailure() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage("Unsupported byteCode for Command: 127");

        Command.valueOf(Byte.MAX_VALUE);
    }

    @Test
    public void getByteCode() {
        assertEquals(0, Command.CLEAR.getByteCode());
        assertEquals(1, Command.PUT.getByteCode());
        assertEquals(2, Command.GET.getByteCode());
        assertEquals(3, Command.REMOVE.getByteCode());
    }
}