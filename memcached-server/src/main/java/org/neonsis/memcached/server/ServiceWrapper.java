package org.neonsis.memcached.server;

import org.neonsis.memcached.server.impl.MemcachedServerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceWrapper {

    private static final Server server = createServer();

    private static Server createServer() {
        return MemcachedServerFactory.buildNewServer(getServerProperties());
    }

    private static Properties getServerProperties() {
        Properties prop = new Properties();
        String pathToServerProperties = System.getProperty("server-prop");
        try (InputStream in = new FileInputStream(pathToServerProperties)) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static void main(String[] args) {
        if ("start".equals(args[0])) {
            start(args);
        } else if ("stop".equals(args[0])) {
            stop(args);
        }
    }

    public static void start(String[] args) {
        server.start();
    }

    public static void stop(String[] args) {
        server.stop();
    }
}
