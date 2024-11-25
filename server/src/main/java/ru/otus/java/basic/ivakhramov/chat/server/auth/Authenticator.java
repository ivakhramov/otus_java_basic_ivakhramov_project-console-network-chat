package ru.otus.java.basic.ivakhramov.chat.server.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.ivakhramov.chat.server.client.ClientHandler;
import ru.otus.java.basic.ivakhramov.chat.server.model.EnumRole;

import java.io.IOException;

/**
 * The Authenticator class is responsible for handling user authentication and registration
 * in the chat server. It processes commands related to user authentication and registration,
 * and interacts with the AuthenticatedProvider to validate user credentials.
 */
public class Authenticator {

    private AuthenticatedProvider authenticatedProvider;
    private ClientHandler clientHandler;
    private static final Logger LOGGER = LogManager.getLogger(Authenticator.class);

    /**
     * Constructs an Authenticator with the specified AuthenticatedProvider and ClientHandler.
     *
     * @param authenticatedProvider the provider for managing authenticated users
     * @param clientHandler the handler for managing client connections
     */
    public Authenticator(AuthenticatedProvider authenticatedProvider, ClientHandler clientHandler) {
        this.authenticatedProvider = authenticatedProvider;
        this.clientHandler = clientHandler;
    }

    /**
     * Authenticates a user based on the provided message.
     * The message can contain commands for authentication, registration, or exit.
     *
     * @param message the command message from the client
     * @return true if the authentication or registration was successful, false otherwise
     * @throws IOException if an I/O error occurs while sending messages
     */
    public boolean authenticate(String message) throws IOException {
            if (message.startsWith("/")) {
                if (message.startsWith("/exit")) {
                    clientHandler.sendMessage("/exitok");
                    return false;
                }

                if (message.startsWith("/auth ")) {
                    return auth(message);
                } else if (message.startsWith("/reg ")) {
                    return reg(message);
                }
            }
            return false;
    }

    /**
     * Handles user authentication based on the provided message.
     *
     * @param message the authentication command message
     * @return true if authentication is successful, false otherwise
     */
    private boolean auth(String message) {
            String[] elements = message.split(" ");

            if (elements.length != 3) {
                clientHandler.sendMessage("Invalid command format /auth ");
                return false;
            }

            if (clientHandler.getServer().getAuthenticatedProvider()
                    .authenticate(clientHandler, elements[1], elements[2])) {
                return true;
            } else {
                return false;
            }

    }

    /**
     * Handles user registration based on the provided message.
     *
     * @param message the registration command message
     * @return true if registration is successful, false otherwise
     */
    private boolean reg(String message) {
        String[] elements = message.split(" ");
        if (elements.length != 4) {
            clientHandler.sendMessage("Invalid command format /reg ");
            return false;
        }

        if (clientHandler.getServer().getAuthenticatedProvider()
                .registration(clientHandler, elements[1], elements[2], elements[3], EnumRole.USER)) {
            return true;
        } else {
            return false;
        }
    }

}
