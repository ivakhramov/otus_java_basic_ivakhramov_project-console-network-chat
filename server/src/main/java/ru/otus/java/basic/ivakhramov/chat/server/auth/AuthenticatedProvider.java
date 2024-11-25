package ru.otus.java.basic.ivakhramov.chat.server.auth;

import ru.otus.java.basic.ivakhramov.chat.server.client.ClientHandler;
import ru.otus.java.basic.ivakhramov.chat.server.model.EnumRole;

/**
 * The AuthenticatedProvider interface defines the methods required for user authentication and registration
 * in a chat server application. Implementations of this interface should provide the necessary logic to
 * initialize the authentication system, authenticate users, and register new users.
 */
public interface AuthenticatedProvider {

    /**
     * Initializes the authentication provider.
     */
    void initialize();

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param clientHandler The client handler associated with the user attempting to authenticate.
     * @param login The login identifier of the user.
     * @param password The password associated with the user's login.
     * @return true if the authentication is successful; false otherwise.
     */
    boolean authenticate(ClientHandler clientHandler, String login, String password);

    /**
     * Registers a new user in the system.
     *
     * @param clientHandler The client handler associated with the user being registered.
     * @param login The login identifier for the new user.
     * @param password The password for the new user.
     * @param name The name of the new user.
     * @param role The role assigned to the new user, represented as an EnumRole.
     * @return true if the registration is successful; false otherwise.
     */
    boolean registration(ClientHandler clientHandler, String login, String password, String name, EnumRole role);
}
