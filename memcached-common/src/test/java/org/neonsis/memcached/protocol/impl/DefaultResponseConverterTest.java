package org.neonsis.memcached.protocol.impl;

import org.junit.Test;
import org.neonsis.memcached.model.Response;
import org.neonsis.memcached.model.Status;
import org.neonsis.memcached.protocol.ResponseConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class DefaultResponseConverterTest {

    private final ResponseConverter converter = new DefaultResponseConverter();

    @Test
    public void readResponseWithoutDataSuccess() throws IOException {
        Response response = converter.readResponse(new ByteArrayInputStream(new byte[]{
                16, // Version
                0,  // Status ADDED
                0   // Flag that data does not exist
        }));

        assertEquals(Status.ADDED, response.getStatus());
        assertFalse(response.hasData());
    }

    @Test
    public void readResponseWithDataSuccess() throws IOException {
        Response response = converter.readResponse(new ByteArrayInputStream(new byte[]{
                16, // Version
                1,  // Status REPLACED
                1,  // Flag that data exists
                0, 0, 0, 3, // int data length
                1, 2, 3     // byte array of data
        }));

        assertEquals(Status.REPLACED, response.getStatus());
        assertTrue(response.hasData());
        assertArrayEquals(new byte[]{1, 2, 3}, response.getData());
    }

    @Test
    public void writeResponseWithoutDataSuccess() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response response = new Response(Status.ADDED);

        converter.writeResponse(out, response);

        assertArrayEquals(
                new byte[]{
                        16, // Version
                        0,  // Status ADDED
                        0   // Flag that data does not exist
                }, out.toByteArray()
        );
    }

    @Test
    public void writeResponseWithDataSuccess() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response response = new Response(Status.GOTTEN);
        response.setData(new byte[]{1, 2, 3});

        converter.writeResponse(out, response);

        assertArrayEquals(
                new byte[]{
                        16,  // Version
                        2,   // Status GOTTEN
                        1,   // Flag that data exists
                        0, 0, 0, 3, // int data length
                        1, 2, 3     // byte array of data
                }, out.toByteArray()
        );
    }
}