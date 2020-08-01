package org.neonsis.memcached.protocol.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neonsis.memcached.exception.MemcachedException;
import org.neonsis.memcached.model.Command;
import org.neonsis.memcached.model.Request;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultRequestConverterTest {

    private final DefaultRequestConverter converter = new DefaultRequestConverter();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void readRequestEmpty() throws IOException {
        Request request = converter.readRequest(new ByteArrayInputStream(new byte[]{
                16,  // Version
                1,   // Command.PUT
                0,   // Flag which means that key,ttl and data does not exist
        }));

        assertEquals(Command.PUT, request.getCommand());
        assertFalse(request.hasData());
        assertFalse(request.hasKey());
        assertFalse(request.hasTtl());
    }

    @Test
    public void readRequestAll() throws IOException {
        Request request = converter.readRequest(new ByteArrayInputStream(new byte[]{
                16,  // Version
                1,   // Command.PUT
                7,   // Flag which means that key,ttl and data exists
                3,   // Key length
                107, 101, 121, // Bytes of word "key"
                0, 0, 1, 2, 11, 80, 50, 120, // Long(8-bytes) value of "1108291367544" - TTL
                0, 0, 0, 3, // Int(4-bytes) data length
                1, 2, 3  // Data
        }));

        assertEquals(Command.PUT, request.getCommand());
        assertTrue(request.hasKey());
        assertEquals("key", request.getKey());
        assertEquals(1108291367544L, request.getTtl().longValue());
        assertTrue(request.hasData());
        assertArrayEquals(new byte[]{1, 2, 3}, request.getData());

    }

    @Test
    public void writeRequestEmpty() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Request request = new Request(Command.CLEAR);

        converter.writeRequest(out, request);

        assertArrayEquals(
                new byte[]{
                        16,  // Version
                        0,   // Command.CLEAR
                        0,   // No flags
                }, out.toByteArray()
        );
    }

    @Test
    public void writeRequestAll() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Request request = new Request(Command.PUT, "key", 1108291367544L, new byte[]{1});

        converter.writeRequest(out, request);

        assertArrayEquals(
                new byte[]{
                        16,  // Version
                        1,   // Command.CLEAR
                        7,   // All Flags exists
                        3,   // Key length
                        107, 101, 121, // Bytes of word "key"
                        0, 0, 1, 2, 11, 80, 50, 120, // Long(8-bytes) value of "1108291367544" - TTL
                        0, 0, 0, 1, // Int(4-bytes) data length
                        1 // Data

                }, out.toByteArray()
        );
    }

    @Test
    public void getFlagsByteEmpty() {
        Request request = new Request(Command.REMOVE);
        byte flagsByte = converter.getFlagsByte(request);

        assertEquals(0, flagsByte);
    }

    @Test
    public void getFlagsByteAll() {
        Request request = new Request(Command.REMOVE, "key", System.currentTimeMillis(), new byte[]{1});
        byte flagsByte = converter.getFlagsByte(request);

        assertEquals(7, flagsByte);
    }

    @Test
    public void getFlagsByteWithKey() {
        Request request = new Request(Command.REMOVE, "key");
        byte flagsByte = converter.getFlagsByte(request);

        assertEquals(1, flagsByte);
    }

    @Test
    public void getFlagsByteWithTTL() {
        Request request = new Request(Command.REMOVE);
        request.setTtl(System.currentTimeMillis());
        byte flagsByte = converter.getFlagsByte(request);

        assertEquals(2, flagsByte);
    }

    @Test
    public void getFlagsByteWithData() {
        Request request = new Request(Command.REMOVE);
        request.setData(new byte[]{1});
        byte flagsByte = converter.getFlagsByte(request);

        assertEquals(4, flagsByte);
    }

    @Test
    public void writeKeySuccess() throws IOException {
        DataOutputStream dos = spy(new DataOutputStream(mock(OutputStream.class)));
        String key = "key";

        Request request = new Request(Command.REMOVE, key);
        converter.writeKey(dos, request);

        verify(dos).write(key.getBytes(StandardCharsets.US_ASCII));
        verify(dos).writeByte(3);
    }

    @Test
    public void writeKeyFailed() throws IOException {
        String key = StringUtils.repeat("a", 128);

        thrown.expect(MemcachedException.class);
        thrown.expectMessage("Key length should be <= 127 bytes for key=" + key);

        DataOutputStream dos = new DataOutputStream(null);

        Request request = new Request(Command.REMOVE, key);
        request.setKey(key);

        converter.writeKey(dos, request);
    }

}