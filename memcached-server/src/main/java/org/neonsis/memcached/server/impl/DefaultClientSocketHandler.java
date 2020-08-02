package org.neonsis.memcached.server.impl;

import org.neonsis.memcached.model.Request;
import org.neonsis.memcached.model.Response;
import org.neonsis.memcached.protocol.RequestConverter;
import org.neonsis.memcached.protocol.ResponseConverter;
import org.neonsis.memcached.server.ClientSocketHandler;
import org.neonsis.memcached.server.CommandHandler;
import org.neonsis.memcached.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

class DefaultClientSocketHandler implements ClientSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClientSocketHandler.class);
    private final Socket socket;
    private final ServerConfig serverConfig;

    DefaultClientSocketHandler(Socket socket, ServerConfig serverConfig) {
        this.socket = socket;
        this.serverConfig = serverConfig;
    }

    protected boolean interrupted() {
        return Thread.interrupted();
    }

    @Override
    public void run() {
        try {
            RequestConverter requestConverter = serverConfig.getRequestConverter();
            ResponseConverter responseConverter = serverConfig.getResponseConverter();
            CommandHandler commandHandler = serverConfig.getCommandHandler();
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            while (!interrupted()) {
                try {
                    Request request = requestConverter.readRequest(in);
                    Response response = commandHandler.handle(request);
                    responseConverter.writeResponse(out, response);
                    LOGGER.debug("Command {} -> {}", request, response);
                } catch (RuntimeException e) {
                    LOGGER.error("Handle request failed: " + e.getMessage(), e);
                }
            }
        } catch (EOFException | SocketException e) {
            LOGGER.info("Remote client connection closed: " + socket.getRemoteSocketAddress().toString() + ": " + e.getMessage());
        } catch (IOException e) {
            if (!socket.isClosed()) {
                LOGGER.error(("IO Error: " + e.getMessage()));
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error("Close socket failed: " + e.getMessage());
            }
        }
    }
}
