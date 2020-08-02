package org.neonsis.memcached.server.impl;

import org.neonsis.memcached.exception.MemcachedException;
import org.neonsis.memcached.server.Server;
import org.neonsis.memcached.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

class DefaultServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServer.class);
    private final ServerConfig serverConfig;
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Thread mainServerThread;
    private volatile boolean serverStopped;

    public DefaultServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.serverSocket = createServerSocket();
        this.executorService = createExecutorService();
        this.mainServerThread = createMainServerThread(createServerRunnable());
    }

    protected ExecutorService createExecutorService() {
        ThreadFactory threadFactory = serverConfig.getWorkerThreadFactor();
        int initThreadCount = serverConfig.getInitThreadCount();
        int maxThreadCount = serverConfig.getMaxThreadCount();
        return new ThreadPoolExecutor(initThreadCount, maxThreadCount, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    protected ServerSocket createServerSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverConfig.getServerPort());
            serverSocket.setReuseAddress(true);
            return serverSocket;
        } catch (IOException e) {
            throw new MemcachedException("Can't create server socket with port=" + serverConfig.getServerPort());
        }
    }

    protected Thread createMainServerThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "Main Server Thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(false);
        return thread;
    }

    protected Runnable createServerRunnable() {
        return () -> {
            while (!mainServerThread.isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    try {
                        executorService.submit(serverConfig.buildNewClientSocketHandler(clientSocket));
                        LOGGER.info("A new client connection established: " + clientSocket.getRemoteSocketAddress().toString());
                    } catch (RejectedExecutionException e) {
                        LOGGER.error("All worker thread are busy. A new connection rejected: " + e.getMessage());
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        LOGGER.error("Can't accept client socket: " + e.getMessage(), e);
                    }
                    destroyMemcachedServer();
                    break;
                }
            }
        };
    }

    protected Thread getShutdownHook() {
        return new Thread(() -> {
            if (!serverStopped) {
                destroyMemcachedServer();
            }
        }, "ShutdownHook");
    }

    private void destroyMemcachedServer() {
        try {
            serverConfig.close();
        } catch (Exception e) {
            LOGGER.error("Close serverConfig failed: " + e.getMessage(), e);
        }
        executorService.shutdownNow();
        LOGGER.info("Server stopped");
        serverStopped = true;
    }

    @Override
    public void start() {
        if (mainServerThread.getState() != Thread.State.NEW) {
            throw new MemcachedException("Current Memcached server already started or stopped! Please create new server instance");
        }
        Runtime.getRuntime().addShutdownHook(getShutdownHook());
        mainServerThread.start();
        LOGGER.info("Server started: " + serverConfig);
    }

    @Override
    public void stop() {
        LOGGER.info("Detected stop command");
        mainServerThread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.warn("Error during close server socket: " + e.getMessage(), e);
        }
    }
}
