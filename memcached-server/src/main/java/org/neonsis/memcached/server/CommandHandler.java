package org.neonsis.memcached.server;

import org.neonsis.memcached.model.Request;
import org.neonsis.memcached.model.Response;

public interface CommandHandler {

    Response handle(Request request);
}