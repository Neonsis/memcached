package org.neonsis.memcached.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.*;

public class RequestTest {

    private Request request;

    @Before
    public void before() {
        request = new Request(Command.CLEAR);
    }

    @Test
    public void hasKeyTrue() {
        request.setKey("key");
        assertTrue(request.hasKey());
    }

    @Test
    public void hasKeyFalse() {
        assertFalse(request.hasKey());
    }

    @Test
    public void hasTLLTrue() {
        request.setTtl(1L);
        assertTrue(request.hasTtl());
    }

    @Test
    public void hasTTLFalse() {
        assertFalse(request.hasTtl());
    }

    @Test
    public void toStringClear() {
        assertEquals("CLEAR", request.toString());
    }

    @Test
    public void toStringRemove() {
        request = new Request(Command.REMOVE);
        request.setKey("key");
        assertEquals("REMOVE[key]", request.toString());
    }

    @Test
    public void toStringPut() {
        request = new Request(Command.PUT);
        request.setKey("key");
        request.setData(new byte[]{1, 2, 3});
        assertEquals("PUT[key]=3 bytes", request.toString());
    }

    @Test
    public void toStringPutWithTTL() {
        request = new Request(Command.PUT);
        request.setKey("key");
        request.setData(new byte[]{1, 2, 3});
        request.setTtl(1596281470493L);
        assertEquals("PUT[key]=3 bytes(Sat Aug 01 14:31:10 MSK 2020)", request.toString());
    }

}