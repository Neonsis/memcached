package org.neonsis.memcached.model;

import org.neonsis.memcached.exception.MemcachedException;

public enum Command {
    CLEAR(0),

    PUT(1),

    GET(2),

    REMOVE(3);

    private byte code;

    Command(int code) {
        this.code = (byte) code;
    }

    public static Command valueOf(byte byteCode) {
        for (Command command : Command.values()) {
            if (command.getByteCode() == byteCode) {
                return command;
            }
        }
        throw new MemcachedException("Unsupported byteCode for Command: " + byteCode);
    }

    public byte getByteCode() {
        return code;
    }
}
