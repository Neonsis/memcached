package org.neonsis.memcached.model;

public class Response extends AbstractPackage {

    private final Status status;

    public Response(Status status, byte[] data) {
        super(data);
        this.status = status;
    }

    public Response(Status status) {
        this.status = status;
    }
}
