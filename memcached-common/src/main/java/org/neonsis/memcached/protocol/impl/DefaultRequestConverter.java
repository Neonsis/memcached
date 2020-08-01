package org.neonsis.memcached.protocol.impl;

import org.neonsis.memcached.exception.MemcachedException;
import org.neonsis.memcached.model.Command;
import org.neonsis.memcached.model.Request;
import org.neonsis.memcached.protocol.RequestConverter;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DefaultRequestConverter extends AbstractPackageConverter implements RequestConverter {

    @Override
    public Request readRequest(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(inputStream);
        checkProtocolVersion(dis.readByte());
        byte cmd = dis.readByte();
        byte flags = dis.readByte();
        boolean hasKey = (flags & 1) != 0;
        boolean hasTTL = (flags & 2) != 0;
        boolean hasData = (flags & 4) != 0;

        return readRequest(cmd, hasKey, hasTTL, hasData, dis);
    }

    protected Request readRequest(byte cmd, boolean hasKey, boolean hasTTL, boolean hasData, DataInputStream dis) throws IOException {
        Request request = new Request(Command.valueOf(cmd));
        if (hasKey) {
            byte keyLength = dis.readByte();
            byte[] keyBytes = IOUtils.readFully(dis, keyLength, false);
            request.setKey(new String(keyBytes, StandardCharsets.US_ASCII));
        }
        if (hasTTL) {
            request.setTtl(dis.readLong());
        }
        if (hasData) {
            int dataLength = dis.readInt();
            request.setData(IOUtils.readFully(dis, dataLength, false));
        }
        return request;
    }

    @Override
    public void writeRequest(OutputStream outputStream, Request request) throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeByte(getVersionByte());
        dos.writeByte(request.getCommand().getByteCode());
        dos.writeByte(getFlagsByte(request));
        if (request.hasKey()) {
            writeKey(dos, request);
        }
        if (request.hasTtl()) {
            dos.writeLong(request.getTtl());
        }
        if (request.hasData()) {
            dos.writeInt(request.getData().length);
            dos.write(request.getData());
        }
        dos.flush();
    }

    protected byte getFlagsByte(Request request) {
        byte flags = 0;
        if (request.hasKey()) {
            flags |= 1;
        }
        if (request.hasTtl()) {
            flags |= 2;
        }
        if (request.hasData()) {
            flags |= 4;
        }
        return flags;
    }

    protected void writeKey(DataOutputStream dos, Request request) throws IOException {
        byte[] key = request.getKey().getBytes(StandardCharsets.US_ASCII);
        if (key.length > 127) {
            throw new MemcachedException("Key length should be <= 127 bytes for key=" + request.getKey());
        }
        dos.writeByte(key.length);
        dos.write(key);
    }
}
