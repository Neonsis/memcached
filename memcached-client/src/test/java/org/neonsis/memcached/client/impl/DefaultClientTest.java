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

}