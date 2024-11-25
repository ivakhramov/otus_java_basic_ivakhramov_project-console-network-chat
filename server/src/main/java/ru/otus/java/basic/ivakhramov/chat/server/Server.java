package ru.otus.java.basic.ivakhramov.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.ivakhramov.chat.server.auth.AuthenticatedProvider;
import ru.otus.java.basic.ivakhramov.chat.server.auth.InDatabaseAuthenticationProvider;
import ru.otus.java.basic.ivakhramov.chat.server.client.ClientHandler;
import ru.otus.java.basic.ivakhramov.chat.server.client.ClientManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

/**
 * The Server class represents a server component that accepts and manages incoming connections from clients.
 * It initializes the authentication provider and handles client connections through a dedicated client handler.
 */
public class Server {

    private int port;
    private ClientManager clientManager;
    private AuthenticatedProvider authenticatedProvider;
    private Connection connection;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    /**
     * Gets the database connection used by the server.
     *
     * @return the database connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Constructs a new Server instance with the specified port and database connection.
     *
     * @param port       the port number for the server to listen on.
     * @param connection the database connection for authentication.
     */
    public Server(int port, Connection connection) {
        this.port = port;
        this.connection = connection;
        this.clientManager = new ClientManager();
        authenticatedProvider = new InDatabaseAuthenticationProvider(this, clientManager);
        authenticatedProvider.initialize();
    }

    /**
     * Gets the authenticated provider used by the server.
     *
     * @return the authenticated provider.
     */
    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }

    /**
     * Checks for inactive clients and disconnects them if they have been inactive for more than 20 minutes.
     */
    private void checkInactiveClients() {
        long currentTime = System.currentTimeMillis();
        for (ClientHandler client : clientManager.getClients()) {
            if (currentTime - client.getLastActiveTime() > 20 * 60 * 1000) {
                client.sendMessage("You have been disconnected due to inactivity.");
                client.disconnect();
            }
        }
    }

    /**
     * Starts the server, accepting incoming client connections.
     * Each connection is handled by a new ClientHandler instance.
     * It also starts a separate thread to periodically check for
     * inactive clients and disconnect them if necessary.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("The server is running on port: " + port);
            LOGGER.info("The server is running on port: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket, clientManager);
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            LOGGER.error("Error starting server", e);
        }

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60 * 1000);
                    checkInactiveClients();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Error: " + e.getMessage());
                    LOGGER.error("Error", e);
                    break;
                }
            }
        }).start();
    }
}
