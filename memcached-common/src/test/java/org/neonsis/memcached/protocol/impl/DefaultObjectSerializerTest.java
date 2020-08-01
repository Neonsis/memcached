package org.neonsis.memcached.protocol.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neonsis.memcached.exception.MemcachedException;
import org.neonsis.memcached.protocol.ObjectSerializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DefaultObjectSerializerTest {

    private final ObjectSerializer serializer = new DefaultObjectSerializer();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final Object testObject = "Test";

    // Byte array of testObject instance
    private final byte[] testObjectDuringSerialization = {-84, -19, 0, 5, 116, 0, 4, 84, 101, 115, 116};

    // Byte array of class that was not exists already
    private final byte[] testClassNotFoundArray =
            {-84, -19, 0, 5, 118, 114, 0, 44, 111, 114, 103, 46, 110, 101, 111, 110, 115, 105, 115, 46, 109, 101, 109, 99, 97, 99, 104, 101, 100, 46,
                    109, 111, 100, 101, 108, 46, 78, 111, 110, 69, 120, 105, 115, 116, 101, 110, 116, 67, 108, 97, 115, 115, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 112};

    // Byte array of SerializableFailedClass instance
    private final byte[] testIOExceptionDuringDeserialization = {
            -84, -19, 0, 5, 115, 114, 0, 59, 111, 114, 103, 46, 110, 101, 111, 110, 115, 105, 115, 46, 109, 101, 109, 99, 97,
            99, 104, 101, 100, 46, 112, 114, 111, 116, 111, 99, 111, 108, 46, 105, 109, 112, 108, 46, 83, 101, 114, 105, 97,
            108, 105, 122, 97, 98, 108, 101, 70, 97, 105, 108, 101, 100, 67, 108, 97, 115, 115, 16, -86, -11, -87, -121, -7, -118, -58, 2, 0, 0, 120, 112};

    @Test
    public void toByteArraySuccess() {
        byte[] actual = serializer.toByteArray(testObject);
        assertArrayEquals(testObjectDuringSerialization, actual);
    }

    @Test
    public void toByteArrayNull() {
        assertNull(serializer.toByteArray(null));
    }

    @Test
    public void toByteArrayNotImplSerializableFailure() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage(is("Class java.lang.Object should implement java.io.Serializable interface"));

        serializer.toByteArray(new Object());
    }

    @Test
    public void toByteArrayIOException() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage("Cant convert object to byte array: Write IO");
        thrown.expectCause(is(IOException.class));

        serializer.toByteArray(new SerializableFailedClass());
    }

    @Test
    public void fromByteArraySuccess() {
        assertEquals(testObject, serializer.fromByteArray(testObjectDuringSerialization));
    }

    @Test
    public void fromByteArrayNull() {
        assertNull(serializer.fromByteArray(null));
    }

    @Test
    public void fromByteArrayIOException() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage("Cant convert byte array to object: Read IO");
        thrown.expectCause(is(IOException.class));

        serializer.fromByteArray(testIOExceptionDuringDeserialization);
    }

    @Test
    public void fromByteArrayClassNotFoundException() {
        thrown.expect(MemcachedException.class);
        thrown.expectMessage("Cant convert byte array to object: org.neonsis.memcached.model.NonExistentClass");
        thrown.expectCause(is(ClassNotFoundException.class));

        serializer.fromByteArray(testClassNotFoundArray);
    }

}

class SerializableFailedClass implements Serializable {

    private void readObject(ObjectInputStream in) throws IOException {
        throw new IOException("Read IO");
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new IOException("Write IO");
    }
}