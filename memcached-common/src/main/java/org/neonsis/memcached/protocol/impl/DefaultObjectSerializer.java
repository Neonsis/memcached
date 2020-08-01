package org.neonsis.memcached.protocol.impl;

import org.neonsis.memcached.exception.MemcachedException;
import org.neonsis.memcached.protocol.ObjectSerializer;

import java.io.*;

public class DefaultObjectSerializer implements ObjectSerializer {

    @Override
    public byte[] toByteArray(Object object) {
        if (object == null) {
            return null;
        }

        if (!(object instanceof Serializable)) {
            throw new MemcachedException("Class " + object.getClass().getName() + " should implement java.io.Serializable interface");
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(object);
            oos.flush();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new MemcachedException("Cant convert object to byte array: " + e.getMessage(), e);
        }

    }

    @Override
    public Object fromByteArray(byte[] array) {
        if (array == null) {
            return null;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array));

            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new MemcachedException("Cant convert byte array to object: " + e.getMessage(), e);
        }
    }
}
