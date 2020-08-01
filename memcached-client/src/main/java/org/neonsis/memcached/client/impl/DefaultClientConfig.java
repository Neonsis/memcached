package org.neonsis.memcached.client.impl;

import org.neonsis.memcached.client.ClientConfig;
import org.neonsis.memcached.protocol.ObjectSerializer;
import org.neonsis.memcached.protocol.RequestConverter;
import org.neonsis.memcached.protocol.ResponseConverter;
import org.neonsis.memcached.protocol.impl.DefaultObjectSerializer;
import org.neonsis.memcached.protocol.impl.DefaultRequestConverter;
import org.neonsis.memcached.protocol.impl.DefaultResponseConverter;

class DefaultClientConfig implements ClientConfig {

    private final String host;
    private final int port;
    private final RequestConverter requestConverter;
    private final ResponseConverter responseConverter;
    private final ObjectSerializer objectSerializer;

    DefaultClientConfig(String host, int port) {
        this.host = host;
        this.port = port;
        this.requestConverter = new DefaultRequestConverter();
        this.responseConverter = new DefaultResponseConverter();
        this.objectSerializer = new DefaultObjectSerializer();
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public RequestConverter getRequestConverter() {
        return requestConverter;
    }

    @Override
    public ResponseConverter getResponseConverter() {
        return responseConverter;
    }

    @Override
    public ObjectSerializer getObjectSerializer() {
        return objectSerializer;
    }
}
