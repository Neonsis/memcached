package org.neonsis.memcached.server.impl;

import org.neonsis.memcached.exception.MemcachedException;
import org.neonsis.memcached.model.Command;
import org.neonsis.memcached.model.Request;
import org.neonsis.memcached.model.Response;
import org.neonsis.memcached.model.Status;
import org.neonsis.memcached.server.CommandHandler;
import org.neonsis.memcached.server.Storage;

class DefaultCommandHandler implements CommandHandler {

    private final Storage storage;

    DefaultCommandHandler(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Response handle(Request request) {
        Status status;
        byte[] data = null;
        if (request.getCommand() == Command.CLEAR) {
            status = storage.clear();
        } else if (request.getCommand() == Command.PUT) {
            status = storage.put(request.getKey(), request.getTtl(), request.getData());
        } else if (request.getCommand() == Command.GET) {
            data = storage.get(request.getKey());
            status = data == null ? Status.NOT_FOUND : Status.GOTTEN;
        } else if (request.getCommand() == Command.REMOVE) {
            status = storage.remove(request.getKey());
        } else {
            throw new MemcachedException("Unsupported command: " + request.getCommand());
        }
        return new Response(status, data);
    }
}
