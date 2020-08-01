package org.neonsis.memcached.protocol;

import org.neonsis.memcached.model.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ResponseConverter {

    Response readResponse(InputStream inputStream) throws IOException;

    void writeRequest(OutputStream outputStream, Response request) throws IOException;
}
