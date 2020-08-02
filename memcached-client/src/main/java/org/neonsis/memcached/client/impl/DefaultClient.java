package org.neonsis.memcached.client.impl;

import org.neonsis.memcached.client.Client;
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
import java.util.concurrent.TimeUnit;

class DefaultClient implements Client {

    private final RequestConverter requestConverter;
    private final ResponseConverter responseConverter;
    private final ObjectSerializer objectSerializer;

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    DefaultClient(ClientConfig clientConfig) throws IOException {
        this.objectSerializer = clientConfig.getObjectSerializer();
        this.requestConverter = clientConfig.getRequestConverter();
        this.responseConverter = clientConfig.getResponseConverter();
        this.socket = createSocket(clientConfig);
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    protected Socket createSocket(ClientConfig clientConfig) throws IOException {
        Socket socket = new Socket(clientConfig.getHost(), clientConfig.getPort());
        socket.setKeepAlive(true);
        return socket;
    }

    protected Response makeRequest(Request request) throws IOException {
        requestConverter.writeRequest(outputStream, request);
        return responseConverter.readResponse(inputStream);
    }

    @Override
    public Status put(String key, Object object) throws IOException {
        return put(key, object, null, null);
    }

    @Override
    public Status put(String key, Object object, Integer ttl, TimeUnit timeUnit) throws IOException {
        byte[] data = objectSerializer.toByteArray(object);
        Long requestTTL = ttl != null && timeUnit != null ? timeUnit.toMillis(ttl) : null;
        Response response = makeRequest(new Request(Command.PUT, key, requestTTL, data));
        return response.getStatus();
    }

    @Override
    public <T> T get(String key) throws IOException {
        Response response = makeRequest(new Request(Command.GET, key));
        return (T) objectSerializer.fromByteArray(response.getData());
    }

    @Override
    public Status remove(String key) throws IOException {
        Response response = makeRequest(new Request(Command.REMOVE, key));
        return response.getStatus();
    }

    @Override
    public Status clear() throws IOException {
        Response response = makeRequest(new Request(Command.CLEAR));
        return response.getStatus();
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}