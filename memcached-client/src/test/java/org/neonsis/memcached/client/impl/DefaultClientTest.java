package org.neonsis.memcached.client.impl;

import org.junit.Before;
import org.junit.Test;
import org.neonsis.memcached.client.ClientConfig;
import org.neonsis.memcached.model.Command;
import org.neonsis.memcached.model.Request;
import org.neonsis.memcached.model.Response;
import org.neonsis.memcached.model.Status;
import org.neonsis.memcached.protocol.ObjectSerializer;
import org.neonsis.memcached.protocol.RequestConverter;
import org.neonsis.memcached.protocol.ResponseConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultClientTest {

    private DefaultClient client;
    private Socket socket;
    private OutputStream out;
    private InputStream in;

    private ClientConfig clientConfig;
    private RequestConverter requestConverter;
    private ResponseConverter responseConverter;
    private ObjectSerializer serializer;

    @Before
    public void before() throws IOException {
        socket = mock(Socket.class);
        out = mock(OutputStream.class);
        in = mock(InputStream.class);
        when(socket.getInputStream()).thenReturn(in);
        when(socket.getOutputStream()).thenReturn(out);

        clientConfig = mock(ClientConfig.class);
        requestConverter = mock(RequestConverter.class);
        responseConverter = mock(ResponseConverter.class);
        serializer = mock(ObjectSerializer.class);

        when(clientConfig.getObjectSerializer()).thenReturn(serializer);
        when(clientConfig.getRequestConverter()).thenReturn(requestConverter);
        when(clientConfig.getResponseConverter()).thenReturn(responseConverter);

        client = new DefaultClient(clientConfig) {
            @Override
            protected Socket createSocket(ClientConfig clientConfig) {
                return socket;
            }
        };
    }

    @Test
    public void makeRequestSuccess() throws IOException {
        Request request = new Request(Command.GET);
        when(responseConverter.readResponse(in)).thenReturn(new Response(Status.GOTTEN));

        Response response = client.makeRequest(request);

        assertEquals(Status.GOTTEN, response.getStatus());
        verify(requestConverter).writeRequest(out, request);
        verify(responseConverter).readResponse(in);
    }

    @Test
    public void putSimple() throws IOException {
        byte[] array = {1, 2, 3};
        String key = "key";
        Object value = "value";

        when(responseConverter.readResponse(in)).thenReturn(new Response(Status.ADDED));
        when(serializer.toByteArray(value)).thenReturn(array);

        Status status = client.put(key, value);

        assertEquals(Status.ADDED, status);
        verify(serializer).toByteArray(value);
        verify(requestConverter).writeRequest(same(out), equalTo(new Request(Command.PUT, key, null, array)));
    }

    @Test
    public void putFull() throws IOException {
        byte[] array = {1, 2, 3};
        String key = "key";
        Object value = "value";

        when(responseConverter.readResponse(in)).thenReturn(new Response(Status.REPLACED));
        when(serializer.toByteArray(value)).thenReturn(array);

        Status status = client.put(key, value, 1, TimeUnit.MILLISECONDS);

        assertEquals(Status.REPLACED, status);
        verify(serializer).toByteArray(value);
        verify(requestConverter).writeRequest(same(out), equalTo(new Request(Command.PUT, key, 1L, array)));
    }

    @Test
    public void putFullInvalidTTL() throws IOException {
        byte[] array = {1, 2, 3};
        String key = "key";
        Object value = "value";

        when(responseConverter.readResponse(in)).thenReturn(new Response(Status.REPLACED));
        when(serializer.toByteArray(value)).thenReturn(array);

        Status status = client.put(key, value, 1, null);

        assertEquals(Status.REPLACED, status);
        verify(serializer).toByteArray(value);
        verify(requestConverter).writeRequest(same(out), equalTo(new Request(Command.PUT, key, null, array)));
    }

    @Test
    public void getSuccess() throws IOException {
        byte[] array = {1, 2, 3};
        String key = "key";
        Object value = "value";

        when(responseConverter.readResponse(in)).thenReturn(new Response(Status.GOTTEN, array));
        when(serializer.fromByteArray(array)).thenReturn(value);

        String result = client.get(key);

        assertEquals(value, result);
        verify(serializer).fromByteArray(array);
        verify(requestConverter).writeRequest(same(out), equalTo(new Request(Command.GET, key)));
    }

    @Test
    public void removeSuccess() throws IOException {
        String key = "key";
        when(responseConverter.readResponse(in)).thenReturn(new Response(Status.REMOVED));

        Status remove = client.remove(key);

        assertEquals(Status.REMOVED, remove);
        verify(requestConverter).writeRequest(same(out), equalTo(new Request(Command.REMOVE, key)));

    }

    @Test
    public void clearSuccess() throws IOException {
        when(responseConverter.readResponse(in)).thenReturn(new Response(Status.CLEARED));

        Status remove = client.clear();

        assertEquals(Status.CLEARED, remove);
        verify(requestConverter).writeRequest(same(out), equalTo(new Request(Command.CLEAR)));
    }

    @Test
    public void closeSuccess() throws IOException {
        client.close();

        verify(socket).close();
    }

    private Request equalTo(Request request) {
        return argThat(request1 ->
                Objects.equals(request.getCommand(), request1.getCommand()) &&
                        Objects.equals(request.getKey(), request1.getKey()) &&
                        Objects.equals(request.getTtl(), request1.getTtl()) &&
                        Objects.equals(request.getData(), request1.getData())
        );
    }

}