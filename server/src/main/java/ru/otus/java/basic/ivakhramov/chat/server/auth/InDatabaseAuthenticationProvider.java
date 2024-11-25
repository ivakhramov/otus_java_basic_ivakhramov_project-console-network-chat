package ru.otus.java.basic.ivakhramov.chat.server.auth;

import ru.otus.java.basic.ivakhramov.chat.server.client.ClientHandler;
import ru.otus.java.basic.ivakhramov.chat.server.client.ClientManager;
import ru.otus.java.basic.ivakhramov.chat.server.database.DatabaseManager;
import ru.otus.java.basic.ivakhramov.chat.server.Server;
import ru.otus.java.basic.ivakhramov.chat.server.model.EnumRole;
import ru.otus.java.basic.ivakhramov.chat.server.model.Role;
import ru.otus.java.basic.ivakhramov.chat.server.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * The InDatabaseAuthenticationProvider class implements the AuthenticatedProvider interface
 * and provides authentication and registration services for users in a chat server.
 * It retrieves user data from a database and manages user sessions.
 */
public class InDatabaseAuthenticationProvider implements AuthenticatedProvider {

    private Server server;
    private DatabaseManager databaseManager;
    private ClientManager clientManager;

    List<User> users = new ArrayList<>();

    /**
     * Constructor for InDatabaseAuthenticationProvider.
     *
     * @param server The server instance.
     * @param clientManager The client manager instance.
     */
    public InDatabaseAuthenticationProvider(Server server, ClientManager clientManager) {

        this.server = server;
        this.databaseManager = new DatabaseManager(server.getConnection());
        this.users = databaseManager.getUsers();
        this.clientManager = clientManager;
    }

    /**
     * Initializes the authentication service.
     */
    @Override
    public void initialize() {

        System.out.println("Сервис аутентификации запущен: In database режим");
    }

    /**
     * Retrieves a user by their login and password.
     *
     * @param login The user's login.
     * @param password The user's password.
     * @return The User object if found, otherwise null.
     */
    private User getUserByLoginAndPassword(String login, String password) {

        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Authenticates a client using their login and password.
     *
     * @param clientHandler The client handler for the connected client.
     * @param login The user's login.
     * @param password The user's password.
     * @return True if authentication is successful, otherwise false.
     */
    @Override
    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) {

        User authUser = getUserByLoginAndPassword(login, password);

        if (authUser.getName() == null) {
            clientHandler.sendMessage("Некорректный логин/пароль");
            return false;
        }

        if (clientManager.isNameBusy(authUser.getName())) {
            clientHandler.sendMessage("Учетная запись уже занята");
            return false;
        }

        clientHandler.setUser(authUser);
        clientManager.subscribe(clientHandler);
        clientHandler.sendMessage("/authok " + authUser.getName());
        return true;
    }

    /**
     * Checks if a login already exists in the user list.
     *
     * @param login The login to check.
     * @return True if the login exists, otherwise false.
     */
    private boolean isLoginAlreadyExist(String login) {

        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a name already exists in the user list.
     *
     * @param name The name to check.
     * @return True if the name exists, otherwise false.
     */
    private boolean isNameAlreadyExist(String name) {

        for (User user : users) {
            if (user.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the maximum user ID from the user list.
     *
     * @return The maximum user ID.
     */
    public int getMaxUserIdFromUsers() {

        int maxUserId = -1;
        for (User user : users) {
            if (maxUserId < user.getId()) {
                maxUserId = user.getId();
            }
        }
        return maxUserId;
    }

    /**
     * Registers a new user with the provided login, password, name, and role.
     *
     * @param clientHandler The client handler for the connected client.
     * @param login The user's login.
     * @param password The user's password.
     * @param name The user's name.
     * @param role The user's role.
     * @return True if registration is successful, otherwise false.
     */
    @Override
    public synchronized boolean registration(ClientHandler clientHandler, String login, String password, String name, EnumRole role) {

        List<Role> roles = new ArrayList<>();
        Role newRole = new Role(role == EnumRole.ADMIN ? 1 : 2, role);
        roles.add(newRole);
        User newUser = new User(getMaxUserIdFromUsers() + 1, login, password, name, roles);

        if (login.trim().length() < 3 || password.trim().length() < 6 || name.trim().length() < 2) {
            clientHandler.sendMessage("Требования логин 3+ символа, пароль 6+ символа," +
                    "имя пользователя 2+ символа не выполнены");
            return false;
        }

        if (isLoginAlreadyExist(login)) {
            clientHandler.sendMessage("Указанный логин уже занят");
            return false;
        }

        if (isNameAlreadyExist(name)) {
            clientHandler.sendMessage("Указанное имя пользователя уже занято");
            return false;
        }

        databaseManager.addUser(login, password, name, EnumRole.USER);
        users.add(newUser);
        clientHandler.setUser(newUser);
        clientManager.subscribe(clientHandler);
        clientHandler.sendMessage("/regok " + newUser.getName());

        return true;
    }
}
