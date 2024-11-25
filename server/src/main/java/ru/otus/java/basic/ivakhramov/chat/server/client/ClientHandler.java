package ru.otus.java.basic.ivakhramov.chat.server.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.ivakhramov.chat.server.*;
import ru.otus.java.basic.ivakhramov.chat.server.auth.Authenticator;
import ru.otus.java.basic.ivakhramov.chat.server.command.CommandProcessor;
import ru.otus.java.basic.ivakhramov.chat.server.command.MessageService;
import ru.otus.java.basic.ivakhramov.chat.server.database.DatabaseManager;
import ru.otus.java.basic.ivakhramov.chat.server.model.EnumRole;
import ru.otus.java.basic.ivakhramov.chat.server.model.Role;
import ru.otus.java.basic.ivakhramov.chat.server.model.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * The ClientHandler class is responsible for managing the communication between the server and a connected client.
 * It handles client authentication, message processing, and disconnection procedures.
 */
public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private DatabaseManager databaseManager;
    private ClientManager clientManager;
    private MessageService messageService;
    private User user;
    private long lastActiveTime;
    private CommandProcessor commandProcessor;
    private Authenticator authenticator;
    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);


    public Server getServer() {
        return server;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    /**
     * Constructor for the ClientHandler class.
     * Initializes the input and output streams, database manager, client manager, message service,
     * command processor, and authenticator. Starts a new thread to handle client communication.
     *
     * @param server        The server instance
     * @param socket        The socket for the client connection
     * @param clientManager The client manager instance
     * @throws IOException If an I/O error occurs when creating input/output streams
     */
    public ClientHandler(Server server, Socket socket, ClientManager clientManager) throws IOException {

        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.databaseManager = new DatabaseManager(server.getConnection());
        this.clientManager = clientManager;
        this.messageService = new MessageService(this.clientManager);
        this.commandProcessor = new CommandProcessor(clientManager, this);
        this.authenticator = new Authenticator(server.getAuthenticatedProvider(), this);
        this.lastActiveTime = System.currentTimeMillis();


        new Thread(() -> {
            try {
                System.out.println("The client has connected");
                LOGGER.info("The client has connected");

                boolean authenticated = false;
                while (!authenticated) {
                    sendMessage("Before working, you must authenticate using the command\n" +
                            "/auth \"login\" \"password\" or register using the command\n" +
                            "/reg \"login\" \"password\" \"name\"");
                    String message = in.readUTF();
                    authenticated = authenticator.authenticate(message);
                }
                System.out.println("Client " + user.getName() + " successfully authenticated");
                LOGGER.info("Client " + user.getName() + " successfully authenticated");
                sendMessage("You can find out the list of commands for working with chat using the command /help");

                while (true) {
                    String message = in.readUTF();
                    commandProcessor.processCommand(message);
                }
            } catch (SocketException e) {
                System.err.println("Error: Lost connection with client: " + e.getMessage());
                LOGGER.error("Error: Lost connection with client", e);
            } catch (EOFException e) {
                System.err.println("Error: Lost connection with client: " + e.getMessage());
                LOGGER.error("Error: Lost connection with client", e);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                LOGGER.error("Error", e);
            } finally {
                disconnect();
            }
        }).start();
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message to be sent
     */
    public void sendMessage(String message) {

        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            LOGGER.error("Error", e);
        }
    }

    /**
     * Sends an exit confirmation message to a specific client.
     *
     * @param name The name of the client to send the exit confirmation to
     */
    public void sendExitok(String name) {
        for (ClientHandler client : clientManager.getClients()) {
            if (client.getUser().getName().equals(name)) {
                sendMessage("/exitok");
                break;
            }
        }
    }

    /**
     * Checks if the authenticated user has an admin role.
     *
     * @return true if the user has an admin role, false otherwise
     */
    public boolean isRoleAdmin() {

        boolean isRoleAdmin = false;
        for (Role role : user.getRoles()) {
            isRoleAdmin = role.getRole().equals(EnumRole.ADMIN);
            break;
        }
        return isRoleAdmin;
    }

    /**
     * Checks if a client with the specified name already exists.
     *
     * @param name The name of the client to check
     * @return true if a client with the specified name exists, false otherwise
     */
    public boolean isNameExist(String name) {

        return clientManager.getClientByName(name) != null;
    }

    /**
     * Updates the last active time of the client to the current system time.
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * Disconnects the client from the server and cleans up resources.
     */
    public void disconnect() {

        clientManager.unsubscribe(this);

        try {
            in.close();
        } catch (IOException e) {
            System.err.println("Error closing input stream: " + e.getMessage());
            LOGGER.error("Error closing input stream", e);
        }

        try {
            out.close();
        } catch (IOException e) {
            System.err.println("Error closing output stream: " + e.getMessage());
            LOGGER.error("Error closing output stream", e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
            LOGGER.error("Error closing socket", e);
        }
    }
}
