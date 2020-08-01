package org.neonsis.memcached.protocol.impl;

import org.neonsis.memcached.exception.MemcachedException;
import org.neonsis.memcached.model.Version;

public abstract class AbstractPackageConverter {

    protected void checkProtocolVersion(byte versionByte) {
        Version version = Version.valueOf(versionByte);
        if (version != Version.VERSION_1_0) {
            throw new MemcachedException("Unsupported protocol version: " + version);
        }
    }

    protected byte getVersionByte() {
        return Version.VERSION_1_0.getByteCode();
    }
}
