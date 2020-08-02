package org.neonsis.memcached.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResponseTest {

    @Test
    public void toStringWithoutData() {
        Response response = new Response(Status.ADDED);
        assertEquals("ADDED", response.toString());
    }

    @Test
    public void toStringWithData() {
        Response response = new Response(Status.ADDED);
        response.setData(new byte[]{1, 2, 3});
        assertEquals("ADDED [3 bytes]", response.toString());
    }
}