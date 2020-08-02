package org.neonsis.memcached.server;

import org.neonsis.memcached.protocol.RequestConverter;
import org.neonsis.memcached.protocol.ResponseConverter;

import java.net.Socket;
import java.util.concurrent.ThreadFactory;

public interface ServerConfig extends AutoCloseable {

    RequestConverter getRequestConverter();

    ResponseConverter getResponseConverter();

    Storage getStorage();

    CommandHandler getCommandHandler();

    ThreadFactory getWorkerThreadFactor();

    int getClearDataIntervalInMs();

    int getServerPort();

    int getInitThreadCount();

    int getMaxThreadCount();

    ClientSocketHandler buildNewClientSocketHandler(Socket clientSocket);
}
